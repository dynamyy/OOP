package org.example.oop_projekt;

import jakarta.transaction.Transactional;
import org.example.oop_projekt.repository.PoodRepository;
import org.example.oop_projekt.teenuskiht.parsimine.*;
import org.example.oop_projekt.teenuskiht.ariloogika.ToodeTeenus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableScheduling
public class OopProjektApplication {

    private final ToodeTeenus toodeTeenus;
    private final PoodRepository poodRepository;

    @Autowired
    public OopProjektApplication(ToodeTeenus toodeTeenus,
                                 PoodRepository poodRepository) {
        this.toodeTeenus = toodeTeenus;
        this.poodRepository = poodRepository;
    }

    public static void main(String[] args) {
        // Veebileht
        SpringApplication.run(OopProjektApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initializeApp() throws Exception {

        // Scraperite loomine
        List<WebScraper> scraperid = new ArrayList<>();
        scraperid.add(new CoopScraper(this.poodRepository));
        //scraperid.add(new PrismaScraper(this.poodRepository));
        scraperid.add(new SelverScraper(this.poodRepository));
        //scraperid.add(new BarboraScraper(this.poodRepository));
        //scraperid.add(new RimiScraper(this.poodRepository));


        ScraperController scraper = new ScraperController(scraperid, this.toodeTeenus);
        scraper.scrapeAll();
    }
}