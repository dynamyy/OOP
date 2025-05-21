package org.example.oop_projekt.teenuskiht.parsimine;

import org.example.oop_projekt.Erindid.ScrapeFailedException;
import org.example.oop_projekt.Erindid.TuhiElementideTagastusException;
import org.example.oop_projekt.repository.PoodRepository;
import org.example.oop_projekt.mudel.Toode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * COOPi epoe scraper. Kuna COOP on vaikselt oma
 * epoe teenust teistele ettevõtetele nagu Bolt ja Wolt
 * üle kandnud, siis tuleb andmeid lugeda Hiiumaa epoest.
 * (Boltis ja Woltis pakutakse väga väheseid tooteid)
 * Hiiumaa eCOOPi koduleht:
 * <a href="https://hiiumaa.ecoop.ee/et">https://hiiumaa.ecoop.ee/et</a>
 */
@Service
public class CoopScraper extends WebScraper {

    private final PoodRepository poodRepository;
    private final String url;
    private final Logger logger;

    public CoopScraper(PoodRepository poodRepository) {
        super("COOP");
        url = "https://vandra.ecoop.ee/et/tooted";
        this.poodRepository = poodRepository;
        this.logger = LoggerFactory.getLogger(CoopScraper.class);
    }

    @Override
    String hangiDynamicSource() throws ScrapeFailedException {
        WebDriver chromedriver = getChromedriver();

        // Veebilehe avamine
        getUrl(url);

        // Ootan kuni leht laeb, et ei tekiks vigu
        ootaLeheLaadimist("span.option:nth-child(1)");

        // Vajutab nuppu "Ühel lehel", et kuvataks kõik tooted
        leiaElement("span.option:nth-child(1)").click();

        // Loeb mitu toodet lehel on, et teada kui kaua peaks lehel alla scrollima
        WebElement tootearvuSilt = leiaElement(".count");

        // Üleliigne tekst eemaldatakse split meetodiga
        int toodeteArv = Integer.parseInt(tootearvuSilt.getText().split(" ")[0]);

        scrolliLeheLoppu(toodeteArv, "app-product-card.item");

        return chromedriver.getPageSource();
    }

    @Override
    public List<Toode> scrape(WebDriver chromedriver) throws ScrapeFailedException{
        setChromedriver(chromedriver);

        List<Toode> tooted = new ArrayList<>();
        String lahtekood = hangiDynamicSource();

        // Saan lähtekoodist kõik toodete elemendid
        Document doc = Jsoup.parse(lahtekood);
        Element toodeteWrapper = valiElement(doc, ".products-wrapper").first();
        Elements lapsed;
        if (toodeteWrapper != null) {
            lapsed = toodeteWrapper.children();
        } else {
            throw new ScrapeFailedException("Toodete wrapperist ei leitud ühtegi toodet");
        }

        // Ühik määrab, mis ühikutes peaks hiljem ühikuhinda kuvama (l / kg)
        String tooteNimi, uhik, lisaHind, pildiUrl;
        String[] hindadeList, lisaHindadeList;
        // Kliendihind on Coopi puhul hind säästukaardiga
        // tkHind ja uhikuHind on tavakliendi hind ehk ilma säästukaardita
        // Kui säästukaardiga erihinda pole (enamasti pole), siis tavakliendi hind == kliendi hind
        double tkHind, uhikuHind, tkHindKlient, uhikuHindKlient;
        for (Element toode : lapsed) {
            try {
                tooteNimi = valiElement(toode, ".product-name").text();
            } catch (TuhiElementideTagastusException e) {
                // Elements tooted sisaldab mõningaid üleliigseid ridu, skipin need
                continue;
            }

            pildiUrl = valiElement(toode, ".product-img-wp img").attr("src");

            // Saan toote tükihinna ja ühikuhinna
            // See hind kehtib kindlasti säästukaardi omanikele.
            // Kui säästukaardiga pole sätestatud erihinda (enamasti pole),
            // siis tavakliendi hind == kliendi hind
            hindadeList = valiElement(toode, "app-price-tag:nth-child(1) > div:nth-child(2)").text().split(" ");

            // Võimalike lehe muutuste püüdmine
            if (hindadeList.length < 7 && hindadeList.length != 3) {
                logger.warn("Midagi on valesti. hindadelist liiga lühike. toote nimi: {}; saadud hindadeList: {}",
                            tooteNimi, java.util.Arrays.toString(hindadeList));
                continue;
            }

            try {
                tkHindKlient = tkHind = Double.parseDouble(hindadeList[0] + "." + hindadeList[1]);
            } catch (NullPointerException | NumberFormatException e) {
                logger.warn("Ei saanud teisendada tükihinda arvuks. tootenimi:{}; tkHindKlient:{}; tkHind:{}",
                        tooteNimi, hindadeList[0], hindadeList[1]);
                continue;
            }

            // On väga üksikud tooted, millel pole märgitud ühikuhinda
            // Sellisel juhul määran kõik hinnad samaks ja ühikuks tk
            if (hindadeList.length == 3) {
                uhikuHindKlient = uhikuHind = tkHind;
                uhik = "tk";
            } else {
                try {
                    uhikuHindKlient = uhikuHind = Double.parseDouble(hindadeList[3]);
                    uhik = hindadeList[6];
                }
                catch (NullPointerException | NumberFormatException e) {
                    logger.warn("Ei saanud teisendada ühikuhinda arvuks. tootenimi:{}; uhikuHind:{}",
                            tooteNimi, hindadeList[3]);
                    continue;
                }
            }


            // Kui tootel on säästukaardiga erinev hind, siis tavakliendi hind on märgitud lisa hinnasilti
            lisaHind = valiElement(toode, ".prices-info").text();
            if (!lisaHind.isEmpty()) {
                lisaHindadeList = lisaHind.split(" ");

                // Lisahinnasilti märgitakse ka pant.
                // Kui panti pole, siis on tavakliendi hind
                if (!lisaHindadeList[1].equals("Pant")) {
                    // Võimalike lehe muutuste püüdmine
                    if (lisaHindadeList.length < 8) {
                        logger.warn("Midagi on valesti. lisahindadelist liiga lühike. toote nimi: {}; saadud hindadeList: {}",
                                tooteNimi, java.util.Arrays.toString(lisaHindadeList));
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
                                    pildiUrl,
                                    "",
                                    null);
            tooted.add(uusToode);
        }

        return tooted;
    }

    @Override
    List<String> URLiKirjed() throws ScrapeFailedException {
        return List.of();
    }
}
