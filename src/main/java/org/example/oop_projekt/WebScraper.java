package org.example.oop_projekt;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;

/**
 * Peaklass kõigi webscrape'imise tegevuste jaoks.
 * Iga poe scraper peaks laiendama seda klassi.
 */
public abstract class WebScraper {
    private WebDriver chromedriver;

    WebDriver getChromedriver() {
        return chromedriver;
    }


    /**
     * Seadistab chromedriveri
     * @throws URISyntaxException
     */
    public WebScraper() throws URISyntaxException {
        // Viitab chromedriver.exe failile resources kaustas
        URL cdResource = WebScraper.class.getClassLoader().getResource("chromedriver.exe");
        // Failitee teisendamine, et toetada utf-8 kaustade nimesid
        assert cdResource != null;
        String cdPath = Paths.get(cdResource.toURI()).toString();

        System.setProperty("webdriver.chrome.driver", cdPath);

        // Sean chromedriveri jooksma peidetult
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");

        chromedriver = new ChromeDriver(options);
    }

    /**
     * Kasutab Seleniumi ja chromedriverit, et saada lehe lähtekood.
     * See võimaldab saada andmeid lehtdelet, mis vajavad javascripti
     * tööriistu või muusugust kasutaja sisendit
     */
    abstract String hangiDynamicSource();

    /**
     * Meetod scrape'imisloogika käivitamiseks
     */
    abstract void scrape();


    /**
     * Leiab kõik viidatud elemendi laste teksti
     * @param lahtekood Lehe lähtekood
     * @param cssSelector cssSelector viide vanema elemendile
     * @return Kõikide viidatud elemendi laste tekst.
     * Iga lapse kohta moodustatakse üks sõne listis.
     */
    List<String> leiaLapsed(String lahtekood, String cssSelector) {
        Document doc = Jsoup.parse(lahtekood);

        Elements lapsed = doc.select(cssSelector).first().children();

        return lapsed.stream().map(Element::text).toList();
    }
}