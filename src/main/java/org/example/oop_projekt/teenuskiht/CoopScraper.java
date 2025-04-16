package org.example.oop_projekt.teenuskiht;

import org.example.oop_projekt.andmepääsukiht.PoodRepository;
import org.example.oop_projekt.andmepääsukiht.Toode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
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
    String hangiDynamicSource() {
        WebDriver chromedriver = getChromedriver();
        String leheHTML;

        chromedriver.get(url);

        // Ootan kuni leht laeb, et ei tekiks vigu
        if (!ootaLeheLaadimist("span.option:nth-child(1)")) {
            return "";
        }

        // Vajutab nuppu "Ühel lehel", et kuvataks kõik tooted
        chromedriver.findElement(By.cssSelector("span.option:nth-child(1)")).click();

        // Loeb mitu toodet lehel on, et teada kui kaua peaks lehel alla scrollima
        WebElement tootearvuSilt = chromedriver.findElement(By.cssSelector(".count"));
        // Üleliigne tekst eemaldatakse split meetodiga
        int toodeteArv = Integer.parseInt(tootearvuSilt.getText().split(" ")[0]);

        if (!scrolliLeheLoppu(300, "app-product-card.item")) {
            return "";
        }

        leheHTML = chromedriver.getPageSource();


        return leheHTML;
    }

    @Override
    public List<Toode> scrape(WebDriver chromedriver) {
        setChromedriver(chromedriver);

        List<Toode> tooted = new ArrayList<>();
        String lahtekood = hangiDynamicSource();

        // Tühja lähtekoodi korral on tekkinud viga,
        // tagastan tühja listi
        if (lahtekood.isEmpty()) {
            return tooted;
        }

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
        double pant, kogus;
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
            if (!lisaHind.isEmpty()) {
                lisaHindadeList = lisaHind.split(" ");

                // Lisahinnasilti märgitakse ka pant, mille kokkuleppeliselt liidame hinnale
                if (lisaHindadeList[1].equals("Pant")) {
                    pant = Double.parseDouble(lisaHindadeList[2]);

                    // Arvutan toote koguse, et korrektselt suurendada liitrihinda
                    // Siin kliendihind ja tavakliendi hind on samad, seega ei pea korduvalt arvutama
                    kogus = (tkHind / uhikuHind) / (pant / 0.1);

                    tkHindKlient = tkHind += pant;
                    // Kuna jagamistehe pant / kogus võib tekitada rohkem kui 2 komakohta, siis on vaja ümardada
                    // Kasutan BidDecimal, et vältida kümnendmurdude ja ümardamisega seotud vigu
                    // Tegelikult on siin arvutuses endiselt ümardamisvead, sest kogus pole juba täpne
                    uhikuHindKlient = uhikuHind += (new BigDecimal(pant / kogus).setScale(2, RoundingMode.HALF_UP)).doubleValue();
                }

                // Kui panti pole, siis on tavakliendi hind
                else {
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
                                    tkHind);
            tooted.add(uusToode);
        }

        return tooted;
    }
}
