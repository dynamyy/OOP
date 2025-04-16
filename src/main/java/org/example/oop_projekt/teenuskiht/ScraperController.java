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
            System.out.println("\u001B[32mAlustan " + pood.getPoeNimi() + " scrapemist\u001B[0m");
            tooted = pood.scrape(chromedriver);


            if (!tooted.isEmpty()) {
                System.out.println("\u001B[32mSain " + pood.getPoeNimi() +
                        " andmed, lisan andmebaasi (" + tooted.size() + ") toodet\u001B[0m");
                this.toodeTeenus.lisaTootedAndmebaasi(tooted);
            } else {
                System.out.println("\u001B[31mEi saanud " + pood.getPoeNimi() + " andmeid (0 toodet)\u001B[0m");
            }
        }

        System.out.println("\u001B[32mKõik scrapetud ja andmebaasi lisatud\u001B[0m");
        chromedriver.quit();
    }

}
