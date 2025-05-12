package org.example.oop_projekt.teenuskiht.parsimine;

import org.example.oop_projekt.Erindid.ScrapeFailedException;
import org.example.oop_projekt.Erindid.TuhiElementideTagastusException;
import org.example.oop_projekt.repository.PoodRepository;
import org.example.oop_projekt.mudel.Toode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PrismaScraper extends WebScraper {

    private final PoodRepository poodRepository;
    private String url;
    private int toodeteArv;

    public PrismaScraper(PoodRepository poodRepository) {
        super("Prisma");
        url = "https://www.prismamarket.ee/tooted";
        this.poodRepository = poodRepository;
    }

    @Override
    String hangiDynamicSource() throws ScrapeFailedException {
        WebDriver chromedriver = getChromedriver();

        // Veebilehe avamine
        getUrl(url);

        // Ootan kuni leht laeb, et ei tekiks vigu
        ootaLeheLaadimist("[data-test-id='product-list'] > div");

        scrolliLeheLoppu(toodeteArv, "[data-test-id='product-list-item']");

        return chromedriver.getPageSource();
    }

    @Override
    List<String> URLiKirjed() throws ScrapeFailedException {
        //Elements kategooriad = doc.select("[product-result-filter]");
        return leiaElemendid("[data-test-id='product-result-filter']")
                .stream().map(kategooria -> kategooria.getAttribute("href"))
                .collect(Collectors.toList());
    }

    @Override
    public List<Toode> scrape(WebDriver chromedriver) throws ScrapeFailedException{
        setChromedriver(chromedriver);
        List<Toode> tooted = new ArrayList<>();
        scrapeRek(tooted, url);
        return tooted;
    }

    public void scrapeRek(List<Toode> tooted, String uusUrl) {
        getUrl(uusUrl);
        toodeteArv = 0;

        try {
            getDriverWait().until(driver -> !driver
                    .findElement(By.cssSelector("[data-test-id='product-result-total']"))
                    .getText().trim().isEmpty());
        } catch (TimeoutException e) {
            throw new ScrapeFailedException("Ootamine scrapeRek meetodis kestis liiga kaua");
        } catch (WebDriverException e) {
            throw new ScrapeFailedException("Elemendi ootamine scrapeRek meetodis ebaõnnestus chromedriveri vea tõttu");
        }

        WebElement tootearvuSilt = leiaElement("[data-test-id='product-result-total']");

        try {
            toodeteArv = Integer.parseInt(tootearvuSilt.getText().split(" ")[0]);
        } catch (NullPointerException | NumberFormatException e) {
            throw new ScrapeFailedException("Ei suutnud scrapeRek meetodis toodetearvu silti numbriks muuta");
        }

        if (toodeteArv > 5000) {
            List<String> URLd = URLiKirjed();

            for (String u : URLd) {
                scrapeRek(tooted, u);
            }
        } else {
            url = uusUrl;
            Document doc = Jsoup.parse(hangiDynamicSource());
            scrapeLehekulg(tooted, doc);
        }
    }

    public void scrapeLehekulg(List<Toode> tooted, Document doc) {
        Element toodeteWrapper = valiElement(doc, "[data-test-id='product-list'] > div").first();
        Elements lapsed;
        if (toodeteWrapper != null) {
            lapsed = toodeteWrapper.children();
        } else {
            throw new ScrapeFailedException("Toodete wrapperist ei leitud ühtegi toodet");
        }

        // Ühik määrab, mis ühikutes peaks hiljem ühikuhinda kuvama (l / kg)
        String tooteNimi, uhik = "tk";

        // Kliendihind on Prisma puhul hind säästukaardiga
        // tkHind ja uhikuHind on tavakliendi hind ehk ilma säästukaardita
        // Kui säästukaardiga erihinda pole (enamasti pole), siis tavakliendi hind == kliendi hind
        double tkHind, uhikuHind = 0, tkHindKlient = 0, uhikuHindKlient = 0;
        String tootePiltUrl, tooteKood;
        for (Element toode : lapsed) {
            try {
                tooteNimi = valiElement(toode, "[data-test-id='product-card__productName'] span").text();
            } catch (TuhiElementideTagastusException e) {
                // Elements tooted sisaldab mõningaid üleliigseid ridu, skipin need
                continue;
            }

            tootePiltUrl = valiElement(toode, "[data-test-id='product-card__productImage'] > img")
                    .attr("srcset")
                    .split(" ")[0];
            tooteKood = valiElement(toode, "article").attr("data-product-id");


            // Võtan hinnainfo konteineri, kust saan css-selectorite abil kõik vajaliku info kätte.
            Elements hinnaInfo = valiElement(toode, "[data-test-id='product-card__product-price']");

            Elements tkHindProov = valiElement(hinnaInfo, "[data-test-id='product-price__unitPrice']", true);

            if (tkHindProov.isEmpty()) { // Vaatan, kas eelmine data-test-id select on tühi, sest Prisma muudab neid id-sid
                tkHindProov = hinnaInfo.select("[data-test-id='product-price__dynamic-unitPrice']");
            }

            tkHind = tkHindKlient = Double.parseDouble(tkHindProov
                    .text().split(" ")[0]
                    .replace("~", "")
                    .replace("umbes", "")
                    .replace("Umbes", "")
                    .replace(",", "."));

            Elements uhikuHindElement = valiElement(hinnaInfo, "[data-test-id='product-card__product-price__comparisonPrice']", true);
            if (!uhikuHindElement.isEmpty() && uhikuHindElement.text().split(" ")[1].split("/").length > 1) {
                String[] uhikuHinnaInfo = uhikuHindElement.text().split(" ");
                uhikuHind = uhikuHindKlient = Double.parseDouble(uhikuHinnaInfo[0].replace(",", "."));
                uhik = uhikuHinnaInfo[1].split("/")[1].replace("Võrdlushind", "");
            }

            Elements soodukaTavahindElement = valiElement(hinnaInfo, "[data-test-id='product-price__lowest30DayPrice']", true);
            if (!soodukaTavahindElement.isEmpty()) {
                tkHindKlient = tkHind;
                tkHind = Double.parseDouble(soodukaTavahindElement
                        .text().split(" ")[0]
                        .replace(",", "."));
                if (uhikuHind != 0) uhikuHind = uhikuHindKlient / tkHindKlient * tkHind;
            }

            Toode uusToode = new Toode(tooteNimi,
                    uhik,
                    tkHindKlient,
                    uhikuHindKlient,
                    poodRepository.findPoodByNimi("Prisma"),
                    uhikuHind,
                    tkHind,
                    tootePiltUrl,
                    tooteKood,
                    null);
            tooted.add(uusToode);
        }
    }
}