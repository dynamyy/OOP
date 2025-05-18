package org.example.oop_projekt.teenuskiht.parsimine;

import io.github.bonigarcia.wdm.WebDriverManager;
import jakarta.transaction.Transactional;
import org.example.oop_projekt.Erindid.ChromeDriverFailException;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Component
public class ScraperController{
    private final List<WebScraper> scraperid;
    private final ToodeTeenus toodeTeenus;
    private final Logger logger;
    private String chromeKasutajaDir;

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
        int failedKatseid;

        // Scrapib kõik poed ja lisab tooted andmebaasi
        for (WebScraper pood : scraperid) {
            failedKatseid = 0;
            while (failedKatseid < 3) {
                logger.info("Alustan {} scrapemist", pood.getPoeNimi());

                try {
                    tooted = pood.scrape(chromedriver);
                    if (!tooted.isEmpty()) {
                        logger.info("Sain {} andmed, lisan andmebaasi ({}) toodet", pood.getPoeNimi(), tooted.size());
                        this.toodeTeenus.lisaTootedAndmebaasi(tooted);
                    } else {
                        logger.warn("Scrape õnnestus, kuid ei saanud {} andmeid (0 toodet)", pood.getPoeNimi());
                    }
                    break;
                } catch (ChromeDriverFailException e) {
                    failedKatseid++;
                    logger.error("{} scrape failis {}. korda: {}", pood.getPoeNimi(), failedKatseid, e.getMessage());

                    if (failedKatseid < 3) {
                        logger.info("Teen uue Chromedriveri ja proovin scrape'imist jätkata");
                        cleanupChromedriver();
                        chromedriver = uusDriver();
                    }
                } catch (ScrapeFailedException e) {
                    logger.error("{} scrape failis: {}", pood.getPoeNimi(), e.getMessage());
                    break;
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        logger.info("Kõik scrapetud ja andmebaasi lisatud");
        chromedriver.quit();
        cleanupChromedriver();
    }

    /**
     * Loob uue ChromeDriver objekti. Kuna igal objektil saab
     * vaid korra kutsuda .quit() meetodit, siis tuleks igaks
     * scrapemiseks luua uus objekt.
     * @return uus ChromeDriver objekt
     */
    private ChromeDriver uusDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        options.addArguments("--accept-language=en-US,en;q=0.9");
        options.addArguments("Accept-Encoding", "gzip, deflate, br");
        options.addArguments("Connection", "keep-alive");

        options.addArguments("--proxy-server=socks4://51.75.242.182:80");

        chromeKasutajaDir = "/tmp/chrome-user-data-" + UUID.randomUUID();
        options.addArguments("--user-data-dir=" + chromeKasutajaDir);
        options.addArguments("--headless=new"); // peidetult jooksmine
        options.addArguments("window-size=1920,1080");

        options.setExperimentalOption("excludeSwitches", List.of("enable-automation"));
        options.setExperimentalOption("useAutomationExtension", false);
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:138.0) Gecko/20100101 Firefox/138.0");

        WebDriverManager.chromedriver().setup();
        ChromeDriver driver = new ChromeDriver(options);

        driver.executeScript(
                "Object.defineProperty(navigator, 'webdriver', {get: () => undefined})"
        );
        return driver;
    }

    /**
     * Kustutab suvaliselt genereeritud user data kaustast andmed peale sessiooni
     */
    private void cleanupChromedriver() {
        try (Stream<Path> walk = Files.walk(Paths.get(chromeKasutajaDir))) {
            int[] kustutamisStaatused = {0, 0};
            walk.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(file -> {
                if (file.delete()) {
                    kustutamisStaatused[0]++;
                } else {
                    kustutamisStaatused[1]++;
                }
            });
            logger.info("Chrome'i kasutaja kaust {} puhastatud. Edukaid kustutusi: {}; Ebaõnnestunud kustutusi: {}",
                    chromeKasutajaDir, kustutamisStaatused[0], kustutamisStaatused[1]);
        } catch (IOException e) {
            logger.error("Ei õnnestunud puhastada Chrome'i kasutaja kausta {}: {}", chromeKasutajaDir, e.getMessage());
        }
    }
}