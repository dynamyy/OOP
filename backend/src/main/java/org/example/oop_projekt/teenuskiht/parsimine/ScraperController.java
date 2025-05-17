package org.example.oop_projekt.teenuskiht.parsimine;

import io.github.bonigarcia.wdm.WebDriverManager;
import jakarta.transaction.Transactional;
import org.example.oop_projekt.Erindid.ScrapeFailedException;
import org.example.oop_projekt.mudel.Toode;
import org.example.oop_projekt.teenuskiht.ariloogika.ToodeTeenus;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class ScraperController{
    private final List<WebScraper> scraperid;
    private final ToodeTeenus toodeTeenus;
    private final Logger logger;

    public ScraperController(List<WebScraper> scraperid, ToodeTeenus toodeTeenus) {
        this.scraperid = scraperid;
        this.toodeTeenus = toodeTeenus;
        this.logger = LoggerFactory.getLogger(ScraperController.class);
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

            logger.info("Alustan {} scrapemist", pood.getPoeNimi());

            try {
                tooted = pood.scrape(chromedriver);

                if (!tooted.isEmpty()) {
                    logger.info("Sain {} andmed, lisan andmebaasi ({}) toodet", pood.getPoeNimi(), tooted.size());
                    this.toodeTeenus.lisaTootedAndmebaasi(tooted);
                } else {
                    logger.warn("Scrape õnnestus, kuid ei saanud {} andmeid (0 toodet)", pood.getPoeNimi());
                }

            } catch (ScrapeFailedException e) {
                logger.error("{} scrape failis: {}", pood.getPoeNimi(), e.getMessage());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }


        }

        logger.info("Kõik scrapetud ja andmebaasi lisatud");
        chromedriver.quit();
    }

    /**
     * Loob uue ChromeDriver objekti. Kuna igal objektil saab
     * vaid korra kutsuda .quit() meetodit, siis tuleks igaks
     * scrapemiseks luua uus objekt.
     * @return uus ChromeDriver objekt
     */
    private ChromeDriver uusDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // peidetult jooksmine
        options.addArguments("window-size=1920,1080");

        WebDriverManager.chromedriver().setup();
        return new ChromeDriver(options);
    }
}