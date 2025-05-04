package org.example.oop_projekt;

import org.example.oop_projekt.repository.PoodRepository;
import org.example.oop_projekt.repository.ToodeRepository;
import org.example.oop_projekt.teenuskiht.parsimine.*;
import org.example.oop_projekt.teenuskiht.äriloogika.PoodTeenus;
import org.example.oop_projekt.teenuskiht.äriloogika.ToodeTeenus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableScheduling
public class OopProjektApplication {

    private ToodeTeenus toodeTeenus;
    private PoodRepository poodRepository;
    private ToodeRepository toodeRepository;
    private PoodTeenus poodTeenus;
    private ScraperController scraper;

    @RequestMapping
    public String index() {
        return "index.html";
    }

    @Autowired
    public OopProjektApplication(ToodeTeenus toodeTeenus,
                                 PoodRepository poodRepository,
                                 ToodeRepository toodeRepository,
                                 PoodTeenus poodTeenus) {
        this.toodeTeenus = toodeTeenus;
        this.poodRepository = poodRepository;
        this.toodeRepository = toodeRepository;
        this.poodTeenus = poodTeenus;
    }

    public static void main(String[] args) {
        // Veebileht
        SpringApplication.run(OopProjektApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initializeApp() throws Exception {

        // Scraperite loomine
        List<WebScraper> scraperid = new ArrayList<>();
        //scraperid.add(new CoopScraper(this.poodRepository));
        //scraperid.add(new PrismaScraper(this.poodRepository));
        //scraperid.add(new SelverScraper(this.poodRepository));
        //scraperid.add(new BarboraScraper(this.poodRepository));
        //scraperid.add(new RimiScraper(this.poodRepository));



        scraper = new ScraperController(scraperid, this.toodeTeenus);

        scraper.scrapeAll();
    }

}