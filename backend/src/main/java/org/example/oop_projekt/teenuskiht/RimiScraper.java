package org.example.oop_projekt.teenuskiht;

import org.example.oop_projekt.andmepääsukiht.PoodRepository;
import org.example.oop_projekt.andmepääsukiht.Toode;
import org.jsoup.Jsoup;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.*;

public class RimiScraper extends WebScraper{


    private final PoodRepository poodRepository;
    private String url = "https://rimi.ee/epood";
    /**
     * Seadistab chromedriveri
     *
     * @throws URISyntaxException
     */
    public RimiScraper(PoodRepository poodRepository) throws URISyntaxException {
        super("Rimi");
        this.poodRepository = poodRepository;
    }


    @Override
    String hangiDynamicSource() {
        WebDriver chromedriver = getChromedriver();

        if (!getUrl(url)) {
            return "";
        }

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
        try {
            chromedriver.get(url);

            WebDriverWait wait = new WebDriverWait(chromedriver, Duration.ofSeconds(10));
            wait.until(driver -> driver.findElements(By.cssSelector(".section-header__wrapper")).size() > 0);

            return chromedriver.getPageSource();
        } finally {
        }
    }

    /*
    Leian kõik URL-d
    Urlide leidmiseks pean info saama button class="trigger gtm" klassist
    <button role="menuitem" class="trigger gtm" href="/epood/ee/tooted/puuviljad-koogiviljad-lilled/c/SH-12">
     */
    public List<String> URLikirjed() throws IOException {
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
    List<Toode> scrape(WebDriver chromedriver) throws IOException {
        setChromedriver(chromedriver);
        int pageNr = 1;
        List<String> urlid = URLikirjed();
        List<Toode> tooted = new ArrayList<>();

        if (urlid == null) {
            return tooted;
        }


        for (String url : urlid) {
            while (true) {
                String urliLisa = "?currentPage=" + pageNr + "&pageSize=80&query=%3Arelevance%3AallCategories%3ASH-19%3AassortmentStatus%3AinAssortment";
                String vaheleht = url + urliLisa;

                String html = html(vaheleht);
                Document doc = Jsoup.parse(html);

                Elements info = doc.select("li.product-grid__item");

                // Kui ei leitud üldse tooteid, katkesta while
                if (info.isEmpty()) {
                    System.out.println("Leht " + pageNr + " tühjaks jäänud – katkestan selle kategooria.");
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

                    Element tooteNimiElement = toode.selectFirst("div.card__details p.card__name");
                    String tooteNimi = tooteNimiElement.text();

                    Element yhikuHindElement = toode.selectFirst("p.card__price-per");
                    String yhikuHind = yhikuHindElement.text();
                    String yhik = yhikuHind.split("/")[1];


                    Element hindElement = toode.selectFirst("div.price-tag.card__price");

                    String täisarvulineOsa = hindElement.selectFirst("span").text();
                    String komakoht = hindElement.selectFirst("sup").text();

                    String hindStr = täisarvulineOsa + "." + komakoht;
                    double hind = Double.parseDouble(hindStr);

                    double kliendiHind = hind;
                    String kliendiYhikuhind = yhikuHind;
                    try{
                        Element kliendiHindElement = toode.selectFirst("div.price-label__price");

                        String täisarv = kliendiHindElement.selectFirst("span.major").text();
                        String komad = kliendiHindElement.selectFirst("span.cents").text();

                        String hindSt = täisarv + "." + komad;
                        kliendiHind = Double.parseDouble(hindSt);

                        Element yhikuhindElement = toode.selectFirst("div.price-per-unit");

                        String yhikuHindStr = yhikuhindElement.text();
                        kliendiYhikuhind = yhikuHindStr.replace(",", ".");

                    }catch (Exception e){
                        System.out.println("Puudub kliendihind");
                    }


                    System.out.println("Nimi: " + tooteNimi + " Tükihind: " + hind + " Ühikuhind: " + yhikuHind + ", Kliendihind: " + kliendiHind + " kliendiühikuhind: " + kliendiYhikuhind + " Ühik: " + yhik);
                }

                if (katkesta) {
                    System.out.println("Lehel oli toode, mis polnud saadaval. Katkestan");
                    break;
                }


                pageNr++;
            }

            break; // testimiseks ainult esimene kategooria
        }




        return tooted;
    }

}
