package org.example.oop_projekt.teenuskiht;

import org.example.oop_projekt.andmepääsukiht.Toode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * COOPi epoe scraper. Kuna COOP on vaikselt oma
 * epoe teenust teistele ettevõtetele nagu Bolt ja Wolt
 * üle kandnud, siis tuleb andmeid lugeda Hiiumaa epoest.
 * (Boltis ja Woltis pakutakse väga väheseid tooteid)
 *
 * Hiiumaa eCOOPi koduleht:
 * https://hiiumaa.ecoop.ee/et
 */
@Service
public class CoopScraper extends WebScraper {
    private String url;

    public CoopScraper() throws URISyntaxException {
        super();
        url = "https://hiiumaa.ecoop.ee/et/tooted";
    }

    @Override
    String hangiDynamicSource() {
        WebDriver chromedriver = getChromedriver();
        String leheHTML;

        try {
            chromedriver.get(url);

            // Vajutab nuppu "Ühel lehel", et
            // kuvataks kõik tooted
            chromedriver.findElement(By.cssSelector("span.option:nth-child(1)")).click();

            // Loeb mitu toodet lehel on, et teada kui kaua peaks lehel alla scrollima
            WebElement tootearvuSilt = chromedriver.findElement(By.cssSelector(".count"));
            // Üleliigne tekst eemaldatakse split meetodiga
            int toodeteArv = Integer.parseInt(tootearvuSilt.getText().split(" ")[0]);

            scrolliLeheLoppu(300, ".products-wrapper", "app-product-card.item");

            leheHTML = chromedriver.getPageSource();
        } finally {
            chromedriver.quit();
        }

        return leheHTML;
    }

    @Override
    public List<Toode> scrape() {
        List<Toode> tooted = new ArrayList<>();
        String lahtekood = hangiDynamicSource();

        // Saan lähtekoodist kõik toodete elemendid
        Document doc = Jsoup.parse(lahtekood);
        Elements lapsed = doc.select(".products-wrapper").first().children();

        // Ühik määrab, mis ühikutes peaks hiljem ühikuhinda kuvama (l / kg)
        String tooteNimi, uhik, lisaHind;
        String[] hindadeList, lisaHindadeList;
        // Kliendihind on Coopi puhul hind säästukaardiga
        // tkHind ja uhikuHind on tavakliendi hind ehk ilma säästukaardita
        // Kui säästukaardiga erihinda pole (enamasti pole), siis tavakliendi hind == kliendi hind
        double tkHind, uhikuHind, tkHindKlient, uhikuHindKlient;
        for (Element toode : lapsed) {
            tooteNimi = toode.select(".product-name").text();

            // Elements tooted sisaldab mõningaid üleliigseid ridu, skipin need
            if (tooteNimi.isEmpty()) continue;

            // Saan toote tükihinna ja ühikuhinna
            // See hind kehtib kindlasti säästukaardi omanikele.
            // Kui säästukaardiga pole sätestatud erihinda (enamasti pole),
            // siis tavakliendi hind == kliendi hind
            hindadeList = toode.select("app-price-tag:nth-child(1) > div:nth-child(2)").text().split(" ");
            tkHindKlient = tkHind = Double.parseDouble(hindadeList[0] + "." + hindadeList[1]);
            uhikuHindKlient = uhikuHind = Double.parseDouble(hindadeList[3]);
            uhik = hindadeList[6];


            // Kui tootel on säästukaardiga erinev hind, siis tavakliendi hind on märgitud lisa hinnasilti
            lisaHind = toode.select(".prices-info").text();
            //if (!lisaHind.isEmpty()) {
            //    lisaHindadeList = lisaHind.split(" ");
            //    tkHind = Double.parseDouble(lisaHindadeList[2]);
            //    uhikuHind = Double.parseDouble(lisaHindadeList[7]);
            //}

            System.out.print(tooteNimi + " " + tkHind + "€ " + uhikuHind + "€/" + uhik);
            if (tkHind != tkHindKlient) System.out.println(" Säästukaardiga: " + tkHindKlient + "€ " + uhikuHindKlient + "€/" + uhik);
            else System.out.println();

            /*
            Kõik andmed on nüüd peaaegu korrektselt muutujates
            Read 94-98 on välja kommenteeritud, sest see koht failib kui
            lisahinnasilti pole märgitud mitte tavakliendi hind vaid pandihind,
            ehk see case tuleks veel lahendada, muus osas töötab


            Järgmisena tuleks luua klassi Toode objektid nende andmetega
            ja lisada Listi: List<Toode>
            Seejärel lihtsalt tagastada List<Toode>

            ja peakski olema COOPi scraperiga kõik

            (Praegu ei loo Tooted objekte, sest Tooted klass pole vist veel lõplik)

             */
        }

        return tooted;
    }
}
