package org.example.oop_projekt.teenuskiht.parsimine;

import org.example.oop_projekt.Erindid.ScrapeFailedException;
import org.example.oop_projekt.Erindid.TuhiElementideTagastusException;
import org.example.oop_projekt.repository.PoodRepository;
import org.example.oop_projekt.mudel.Toode;
import org.jsoup.Jsoup;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class RimiScraper extends WebScraper {

    private final PoodRepository poodRepository;
    private final Logger logger;

    public RimiScraper(PoodRepository poodRepository) {
        super("Rimi");
        this.poodRepository = poodRepository;
        this.logger = LoggerFactory.getLogger(RimiScraper.class);
    }


    @Override
    String hangiDynamicSource() throws ScrapeFailedException {
        WebDriver chromedriver = getChromedriver();

        getUrl("https://rimi.ee/epood"); // Lisa siia ootamine

        // Ootan kuni leht laeb, et ei tekiks vigu. Siia pole teda otseselt vaja
        /*
        if (!ootaLeheLaadimist("main.main")) {//Leia element mida oodata
            return "";
        }
        */


        return chromedriver.getPageSource();

    }

    //Vahelehtede html leidmine
    public String html(String url) {
        WebDriver chromedriver = getChromedriver();
        chromedriver.get(url);

        WebDriverWait wait = new WebDriverWait(chromedriver, Duration.ofSeconds(10));
        wait.until(driver -> !driver.findElements(By.cssSelector(".section-header__wrapper")).isEmpty());

        return chromedriver.getPageSource();
    }

    /*
    Leian kõik URL-d
    Urlide leidmiseks pean info saama button class="trigger gtm" klassist
    <button role="menuitem" class="trigger gtm" href="/epood/ee/tooted/puuviljad-koogiviljad-lilled/c/SH-12">
     */
    public List<String> URLiKirjed() throws ScrapeFailedException {
        List<String> info = new ArrayList<>();
        String s = hangiDynamicSource();
        Document doc = Jsoup.parse(s);

        // Valib kõik <button> elemendid klassiga "trigger gtm"
        Elements buttons = doc.select("button.trigger.gtm");

        for (Element button : buttons) {
            String href = button.attr("href");
            if (href != null && !href.isEmpty()) {
                info.add("https://rimi.ee" + href);
            }
        }
        return info;
    }

    @Override
    List<Toode> scrape(WebDriver chromedriver) throws ScrapeFailedException {
        setChromedriver(chromedriver);
        List<String> urlid = URLiKirjed();
        List<Toode> tooted = new ArrayList<>();

        if (urlid == null) {
            return tooted;
        }

        for (String url : urlid) {

            if (url.contains("teenused")){
                System.out.println("Lõpetan Rimi töötlemise");
                break;
            }

            int pageNr = 1;
            String nr = extractNumber(url);
            while (true) {
                String urliLisa = "?currentPage=" + pageNr + "&pageSize=80&query=%3Arelevance%3AallCategories%3ASH-" +
                        nr + "%3AassortmentStatus%3AinAssortment";
                String vaheleht = url + urliLisa;

                String html = html(vaheleht);
                Document doc = Jsoup.parse(html);

                Elements info = doc.select("li.product-grid__item");

                // Kui ei leitud üldse tooteid, katkesta while
                if (info.isEmpty()) {
                    logger.info("Leht {} on tühi – lõpetan selle kategooria töötlemise.", pageNr);
                    break;
                }

                boolean katkesta = false;

                for (Element toode : info) {
                    try {//Kui jõuan lehele, kus pole ühtegi elementi, liigun järgmise alamkategooria juurde
                        Element kasSaadaval = toode.selectFirst("div.card__price-wrapper p.card__price-per");
                        if (kasSaadaval != null) {
                            String saadavus = kasSaadaval.text();
                            if (saadavus.contains("Ei ole")) {
                                katkesta = true;
                                break;
                            }
                        }
                    } catch (Exception e) {
                        katkesta = true;
                        break;
                    }

                    Element tooteNimiElement = valiEsimene(toode, "div.card__details p.card__name");
                    String tooteNimi = tooteNimiElement.text();

                    double yhikuHind = 0;
                    String yhik = "puudub";
                    try{
                        Element yhikuHindElement = valiEsimene(toode, "p.card__price-per");
                        String hindKoosYhikuga = yhikuHindElement.text();
                        yhikuHind = hindTekstist(hindKoosYhikuga.split(" ")[0]);
                        yhik = hindKoosYhikuga.split("/")[1];
                    }catch (Exception e){
                        logger.warn("Ühik puudub, {}", e.getMessage());
                    }



                    Element hindElement = valiEsimene(toode, "div.price-tag.card__price");

                    String taisarvulineOsa = valiEsimene(hindElement, "span").text();
                    String komakoht = valiEsimene(hindElement, "sup").text();

                    String hindStr = taisarvulineOsa + "." + komakoht;
                    double tykiHind = Double.parseDouble(hindStr);

                    double kliendiTykiHind = tykiHind;
                    double kliendiYhikuHind = yhikuHind;

                    try {
                        Element kliendiHindElement = valiEsimene(toode, "div.price-label__price");
                        String taisarv = valiEsimene(kliendiHindElement, "span.major").text();
                        String komad = valiEsimene(kliendiHindElement, "span.cents").text();

                        String hindSt = taisarv + "." + komad;
                        kliendiTykiHind = Double.parseDouble(hindSt);
                        Element yhikuhindElement = valiEsimene(toode, "div.price-per-unit");
                        kliendiYhikuHind = hindTekstist(yhikuhindElement.text());
                    } catch (TuhiElementideTagastusException ignore) {} // Kõikidel toodetel pole kliendihinda


                    Element imgElement = toode.selectFirst("img");
                    String pildiURL = "";
                    if (imgElement != null) {
                        if (imgElement.hasAttr("data-src")) {
                            pildiURL = imgElement.attr("data-src").trim();
                        }
                    }

                    Toode uusToode = new Toode(tooteNimi,
                            yhik,
                            kliendiTykiHind,
                            kliendiYhikuHind,
                            poodRepository.findPoodByNimi("Rimi"),
                            yhikuHind,
                            tykiHind,
                            pildiURL,
                            "",
                            null);
                    tooted.add(uusToode);

                    /*
                    System.out.println("Nimi: " + tooteNimi +
                            ", Tükihind: " + tykiHind +
                            ", Ühikuhind: " + yhikuHind +
                            ", Kliendihind: " + kliendiTykiHind +
                            " kliendiühikuhind: " + kliendiYhikuHind +
                            " Ühik: " + yhik +
                            ", Pildi URL:" + pildiURL);

                     */

                }
                if (katkesta) {
                    System.out.println("Lehel oli toode, mis polnud saadaval. Katkestan");
                    break;
                }
                pageNr++;
            }
            //break; // testimiseks ainult esimene kategooria
        }

        return tooted;
    }

    public static String extractNumber(String url) {
        Pattern pattern = Pattern.compile("SH-(\\d+)$");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return null; // või viska erind, kui muster ei leidu
        }
    }
}
