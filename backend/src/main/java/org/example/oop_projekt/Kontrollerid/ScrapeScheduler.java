package org.example.oop_projekt.Kontrollerid;

import org.example.oop_projekt.teenuskiht.ScraperController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ScrapeScheduler {
    private final ScraperController scraper;

    @Autowired
    public ScrapeScheduler(ScraperController scraper) {
        this.scraper = scraper;
    }

    @Scheduled(cron = "0 0 3 * * *", zone = "Europe/Tallinn")
    public void runScrape() throws IOException {
        System.out.println("\u001B[32mAlustan scheduler scrapimist\u001B[0m");
        scraper.scrapeAll();
    }
}
