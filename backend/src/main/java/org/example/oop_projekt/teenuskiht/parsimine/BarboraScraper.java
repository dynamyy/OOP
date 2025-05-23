package org.example.oop_projekt.teenuskiht.parsimine;

import org.example.oop_projekt.Erindid.ScrapeFailedException;
import org.example.oop_projekt.repository.PoodRepository;
import org.example.oop_projekt.mudel.Toode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.awt.Robot;
import java.awt.AWTException;



import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class BarboraScraper extends WebScraper {
    private final PoodRepository poodRepository;
    private String url = "https://barbora.ee/";
    private final Logger logger;

    public BarboraScraper(PoodRepository poodRepository) {
        super("Barbora");
        this.poodRepository = poodRepository;
        this.logger = LoggerFactory.getLogger(BarboraScraper.class);
    }


    //Esilehe html
    @Override
    String hangiDynamicSource() throws ScrapeFailedException {
        WebDriver chromedriver = getChromedriver();

        getUrl(url);

        WebDriverWait wait = new WebDriverWait(chromedriver, Duration.ofSeconds(10));
        wait.until(driver -> !driver.findElements(By.cssSelector(".category-item--title")).isEmpty());


        return chromedriver.getPageSource();
    }


    //Vahelehtede html leidmine
    public String html(String url) {
        WebDriver chromedriver = getChromedriver();
        chromedriver.get(url);

        WebDriverWait wait = new WebDriverWait(chromedriver, Duration.ofSeconds(10));
        wait.until(driver -> !driver.findElements(By.cssSelector(".category-item--title")).isEmpty());

        return chromedriver.getPageSource();
    }


    //Leian kõik URL-d
    @Override
    public List<String> URLiKirjed() throws ScrapeFailedException {
        List<String> info = new ArrayList<>();
        String s = hangiDynamicSource();

        // Vea korral tagastatakse null
        if (s.isEmpty()) {
            return null;
        }

        Document doc = Jsoup.parse(s);
        Elements links = doc.select("a.category-item--title");

        for (int i = 0; i < links.size(); i++) {
            Element link = links.get(i);
            String href = link.attr("href");
            info.add("https://barbora.ee" + href);
        }

        return info;
    }



    //Kasutan kõiki URL-e, et leida igal vahelele olevad tooted
    //Barbora lehtedele tuleb lisada ?page=nr, kus nr tähendab vahelehte(alates 2)
    //Kuna kõiki vahelehti ei saanud kohe alguses kätte, tuleb siin kasutada for loopi, et leida vahelehed, kus üldse tooted on.
    //Kui juhtub, et tooteid ei ole lehel või toodet pole hetkel valikus, võime loopi lõpetada sest mittesaadaval olevad tooted on viimased
    @Override
    public List<Toode> scrape(WebDriver chromedriver) throws ScrapeFailedException, InterruptedException {
        setChromedriver(chromedriver);
        List<String> urlid = URLiKirjed();
        List<Toode> tooted = new ArrayList<>();

        // Vea korral tagastatakse tühi list.
        if (urlid == null) {
            return tooted;
        }

        for (String url : urlid){
            int i = 2;

            /*
            Random random = new Random();
            int sleepTime = 500 + random.nextInt(3500);
            Thread.sleep(sleepTime);

             */

            String vaheleht = url;
            while (true) {

                String html = html(vaheleht);
                Document doc = Jsoup.parse(html);


                Elements kaardid = doc.select("li[data-testid^=product-card]");

                if (kaardid.isEmpty()) {
                    break;
                }

                for (Element kaart : kaardid) {
                    Element hindElement = kaart.selectFirst("meta[itemprop=price]");
                    if (hindElement == null) {
                        logger.info("Lõpetasin vahelehe {} scrapemise", vaheleht);
                        break;
                    }

                    String hindStr = hindElement.attr("content").replace(",", ".");
                    double tykiHind = Double.parseDouble(hindStr);

                    Element nimiElement = kaart.selectFirst("span.tw-block");
                    String tooteNimi = nimiElement.text();



                    Element yhikuHindElement = kaart.selectFirst("div.tw-text-\\[10px\\]");
                    String hindKoosYhikuga = yhikuHindElement != null ? yhikuHindElement.text() : "";
                    double yhikuHind = hindTekstist(hindKoosYhikuga.split("€")[0]);
                    String yhik = hindKoosYhikuga.split("/")[1];



                    double kliendiYhikuHind = yhikuHind;
                    double kliendiTykiHind = tykiHind;


                    //Barbora leht veidi kahtlaselt üles ehitatud, kui tootel on kliendikaardi soodustus siis see pannakse html-s sinna,
                    //kus muidu on tavahind ning tavahinnale tehakse oma div
                    try{
                        Element yhikuHindKlientElement = kaart.selectFirst("div.tw-relative.tw-text-\\[10px\\]");
                        yhikuHind = hindTekstist(yhikuHindKlientElement.text());

                        Element tykiHindKlientElementTaisosa = kaart.select("span.tw-pr-\\[2px\\]").get(1);
                        Element tykiHindKlientElementMurdosa = kaart.select("span.tw-pr-\\[1px\\]").get(1);
                        tykiHind = Double.parseDouble(tykiHindKlientElementTaisosa.text() + "." + tykiHindKlientElementMurdosa.text());

                    }catch (Exception e){
                        //System.out.println("Puudub kliendihind");
                    }


                    Element piltElement = kaart.selectFirst("img");
                    String pildiURL = "";
                    if (piltElement != null) {
                        pildiURL = piltElement.attr("src");
                        if (pildiURL.isEmpty()) {
                            pildiURL = piltElement.attr("data-srcset");
                        }
                    }

                    /*
                    System.out.println("Nimi: " + tooteNimi +
                            ", tükihind: " + tykiHind +
                            ", ühikuhind: " + yhikuHind +
                            ", kliendihind: " + kliendiTykiHind +
                            ", kliendi ühikuhind: " + kliendiYhikuHind +
                            ", ühik: " + yhik +
                            ", piltURL: " + pildiURL);

                     */

                    Toode uusToode = new Toode(tooteNimi,
                            yhik,
                            kliendiTykiHind,
                            kliendiYhikuHind,
                            poodRepository.findPoodByNimi("Maxima"),
                            yhikuHind,
                            tykiHind,
                            pildiURL,
                            "",
                            null);
                    tooted.add(uusToode);

                }

                vaheleht = url + "?page=" + i;
                i++;
                // Robot, et Barbora ei tuvastaks scraperit
                Random random = new Random();
                int randomX = 1 + random.nextInt(500); // 1–500
                int randomY = 1 + random.nextInt(500); // 1–500

                Actions actions = new Actions(chromedriver);
                actions.moveByOffset(randomX, randomY).perform();
                actions.moveByOffset(-randomX, -randomY).perform();
                //Thread.sleep(sleepTime - 500);
            }
            //break;//Vaatab ainult 1 alamkategooria, kustuta see ära kui päriselt scrapeda tahad
        }
        return tooted;
    }


}
