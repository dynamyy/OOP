package org.example.oop_projekt.teenuskiht;

import org.example.oop_projekt.andmepääsukiht.Toode;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;

public class ScraperController {
    private final List<WebScraper> scraperid;
    private static WebDriver chromedriver;
    private final ToodeTeenus toodeTeenus;

    public ScraperController(List<WebScraper> scraperid, ToodeTeenus toodeTeenus) throws URISyntaxException {
        this.scraperid = scraperid;
        this.toodeTeenus = toodeTeenus;

        // Viitab chromedriver.exe failile resources kaustas
        URL cdResource = WebScraper.class.getClassLoader().getResource("chromedriver.exe");
        // Failitee teisendamine, et toetada utf-8 kaustade nimesid
        assert cdResource != null;
        String cdPath = Paths.get(cdResource.toURI()).toString();

        System.setProperty("webdriver.chrome.driver", cdPath);

        // Sean chromedriveri jooksma peidetult
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");

        chromedriver = new ChromeDriver();
    }


    /**
     * Scrapeb kõik poed ja lisab saadud tooted andmebaasi
     * @throws IOException -
     */
    public void scrapeAll() throws IOException {
        List<Toode> tooted;

        // Scrapib kõik poed ja lisab tooted andmebaasi
        for (WebScraper pood : scraperid) {
            System.out.println("Alustan " + pood.getPoeNimi() + " scrapemist");
            tooted = pood.scrape(chromedriver);

            System.out.println("Sain " + pood.getPoeNimi() + " andmed, lisan andmebaasi (" + tooted.size() + ") toodet");
            this.toodeTeenus.lisaTootedAndmebaasi(tooted);
        }

        System.out.println("Kõik scrapetud ja andmebaasi lisatud");
        chromedriver.quit();
    }

}
