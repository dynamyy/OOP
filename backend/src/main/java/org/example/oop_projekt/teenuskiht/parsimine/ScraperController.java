package org.example.oop_projekt.teenuskiht.parsimine;

import io.github.bonigarcia.wdm.WebDriverManager;
import jakarta.transaction.Transactional;
import org.example.oop_projekt.Erindid.ScrapeFailedException;
import org.example.oop_projekt.mudel.Toode;
import org.example.oop_projekt.teenuskiht.äriloogika.ToodeTeenus;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class ScraperController{
    private final List<WebScraper> scraperid;
    private final ToodeTeenus toodeTeenus;

    public ScraperController(List<WebScraper> scraperid, ToodeTeenus toodeTeenus) {
        this.scraperid = scraperid;
        this.toodeTeenus = toodeTeenus;
    }


    /**
     * Scrapeb kõik poed ja lisab saadud tooted andmebaasi
     * @throws IOException -
     */
    @Transactional
    public void scrapeAll() throws IOException {
        WebDriver chromedriver = uusDriver();
        List<Toode> tooted;

        // Scrapib kõik poed ja lisab tooted andmebaasi
        for (WebScraper pood : scraperid) {
            System.out.println("\u001B[32mAlustan " + pood.getPoeNimi() + " scrapemist\u001B[0m");

            try {
                tooted = pood.scrape(chromedriver);

                if (!tooted.isEmpty()) {
                    System.out.println("\u001B[32mSain " + pood.getPoeNimi() +
                            " andmed, lisan andmebaasi (" + tooted.size() + ") toodet\u001B[0m");
                    this.toodeTeenus.lisaTootedAndmebaasi(tooted);
                } else {
                    System.out.println("\u001B[31mScrape õnnestus, kuid ei saanud " + pood.getPoeNimi() + " andmeid (0 toodet)\u001B[0m");
                }

            } catch (ScrapeFailedException e) {
                System.out.println("\u001B[31m" + pood.getPoeNimi() + " scrape failis.");
                System.out.println(e.getMessage() + "\u001B[0m");
            }


        }

        System.out.println("\u001B[32mKõik scrapetud ja andmebaasi lisatud\u001B[0m");
        chromedriver.quit();
    }

    /**
     * Loob uue ChromeDriver objekti. Kuna igal objektil saab
     * vaid korra kutsuda .quit() meetodit, siis tuleks igaks
     * scrapemiseks luua uus objekt.
     * @return uus ChromeDriver objekt
     */
    private ChromeDriver uusDriver() {
        // Sean chromedriveri jooksma peidetult
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");

        WebDriverManager.chromedriver().setup();
        //return new ChromeDriver(options); //Peidetud
        return new ChromeDriver(); //Mittepeidetud
    }
}
