package org.example.oop_projekt.Kontrollerid;

import org.example.oop_projekt.teenuskiht.parsimine.CoopScraper;
import org.example.oop_projekt.teenuskiht.parsimine.ScraperController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ScrapeScheduler {
    private final ScraperController scraper;
    private final Logger logger;

    @Autowired
    public ScrapeScheduler(ScraperController scraper) {
        this.scraper = scraper;
        this.logger = LoggerFactory.getLogger(ScrapeScheduler.class);
    }

    @Scheduled(cron = "00 36 17 * * *", zone = "Europe/Tallinn")
    public void runScrape() throws IOException {
        logger.info("Alustan scheduler scrapimist");
        scraper.scrapeAll();
    }
}