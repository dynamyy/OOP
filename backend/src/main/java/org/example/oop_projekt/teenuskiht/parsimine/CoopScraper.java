package org.example.oop_projekt.teenuskiht.parsimine;

import org.example.oop_projekt.Erindid.ScrapeFailedException;
import org.example.oop_projekt.repository.PoodRepository;
import org.example.oop_projekt.mudel.Toode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    private final PoodRepository poodRepository;
    private final String url;

    public CoopScraper(PoodRepository poodRepository) {
        super("COOP");
        url = "https://hiiumaa.ecoop.ee/et/tooted";
        this.poodRepository = poodRepository;
    }

    @Override
    String hangiDynamicSource() throws ScrapeFailedException {
        WebDriver chromedriver = getChromedriver();

        // Veebilehe avamine
        getUrl(url);

        // Ootan kuni leht laeb, et ei tekiks vigu
        ootaLeheLaadimist("span.option:nth-child(1)");

        // Vajutab nuppu "Ühel lehel", et kuvataks kõik tooted
        chromedriver.findElement(By.cssSelector("span.option:nth-child(1)")).click();

        // Loeb mitu toodet lehel on, et teada kui kaua peaks lehel alla scrollima
        WebElement tootearvuSilt = chromedriver.findElement(By.cssSelector(".count"));
        // Üleliigne tekst eemaldatakse split meetodiga
        int toodeteArv = Integer.parseInt(tootearvuSilt.getText().split(" ")[0]);

        scrolliLeheLoppu(300, "app-product-card.item");

        return chromedriver.getPageSource();
    }

    @Override
    public List<Toode> scrape(WebDriver chromedriver) throws ScrapeFailedException{
        setChromedriver(chromedriver);

        List<Toode> tooted = new ArrayList<>();
        String lahtekood = hangiDynamicSource();

        // Saan lähtekoodist kõik toodete elemendid
        Document doc = Jsoup.parse(lahtekood);
        Elements lapsed = Objects.requireNonNull(doc.select(".products-wrapper").first()).children();

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


            // Võimalike lehe muutuste püüdmine
            if (hindadeList.length < 7 && hindadeList.length != 3) {
                System.out.println("Midagi on valesti. hindadelist liiga lühike");
                System.out.println("\tToote nimi: " + tooteNimi);
                System.out.println("\tSaadud hindadelist: " + java.util.Arrays.toString(hindadeList));
                continue;
            }


            tkHindKlient = tkHind = Double.parseDouble(hindadeList[0] + "." + hindadeList[1]);

            // On väga üksikud tooted, millel pole märgitud ühikuhinda
            // Sellisel juhul määran kõik hinnad samaks ja ühikuks tk
            if (hindadeList.length == 3) {
                uhikuHindKlient = uhikuHind = tkHind;
                uhik = "tk";
            } else {
                uhikuHindKlient = uhikuHind = Double.parseDouble(hindadeList[3]);
                uhik = hindadeList[6];
            }


            // Kui tootel on säästukaardiga erinev hind, siis tavakliendi hind on märgitud lisa hinnasilti
            lisaHind = toode.select(".prices-info").text();
            if (!lisaHind.isEmpty()) {
                lisaHindadeList = lisaHind.split(" ");

                // Lisahinnasilti märgitakse ka pant.
                // Kui panti pole, siis on tavakliendi hind
                if (!lisaHindadeList[1].equals("Pant")) {
                    // Võimalike lehe muutuste püüdmine
                    if (lisaHindadeList.length < 8) {
                        System.out.println("Midagi on valesti. lisaHindadelist liiga lühike");
                        System.out.println("\tToote nimi: " + tooteNimi);
                        System.out.println("\tSaadud lisaHindadelist: " + java.util.Arrays.toString(lisaHindadeList));
                        continue;
                    }

                    tkHind = Double.parseDouble(lisaHindadeList[2]);
                    uhikuHind = Double.parseDouble(lisaHindadeList[7]);
                }
            }

            Toode uusToode = new Toode(tooteNimi,
                                    uhik,
                                    tkHindKlient,
                                    uhikuHindKlient,
                                    poodRepository.findPoodByNimi("Coop"),
                                    uhikuHind,
                                    tkHind,
                                    "");
            tooted.add(uusToode);
        }

        return tooted;
    }
}
