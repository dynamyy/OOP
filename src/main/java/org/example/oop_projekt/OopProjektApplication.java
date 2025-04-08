package org.example.oop_projekt;

import org.example.oop_projekt.andmepääsukiht.PoodRepository;
import org.example.oop_projekt.andmepääsukiht.Toode;
import org.example.oop_projekt.andmepääsukiht.ToodeRepository;
import org.example.oop_projekt.teenuskiht.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@SpringBootApplication
public class OopProjektApplication {

    ToodeTeenus toodeTeenus;
    PoodRepository poodRepository;
    ToodeRepository toodeRepository;
    PoodTeenus poodTeenus;

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

//        COOPi webScraper
//        CoopScraper coop = new CoopScraper(this.poodRepository);
//        List<Toode> coopTooted = coop.scrape();
//        System.out.println("Toodete andmebaasi lisamine");
//        this.toodeTeenus.lisaTootedAndmebaasi(coopTooted);
//        System.out.println("Kõik tooted lisatud!");

        PrismaScraper prisma = new PrismaScraper(this.poodRepository);
        List<Toode> prismaTooted = prisma.scrape();
        System.out.println("Toodete andmebaasi lisamine");
        this.toodeTeenus.lisaTootedAndmebaasi(prismaTooted);
        System.out.println("Kõik tooted lisatud!");

        //SelverScraper selver = new SelverScraper(this.poodRepository);
        //selver.scrape();

//        BarboraScraper barbora = new BarboraScraper(this.poodRepository);
//        barbora.scrape();


    }

}