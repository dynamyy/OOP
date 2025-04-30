package org.example.oop_projekt.teenuskiht.parsimine;

import org.example.oop_projekt.Erindid.ScrapeFailedException;
import org.example.oop_projekt.andmepääsukiht.PoodRepository;
import org.example.oop_projekt.andmepääsukiht.Toode;
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

@Service
public class PrismaScraper extends WebScraper {

    private final PoodRepository poodRepository;
    private String url;

    public PrismaScraper(PoodRepository poodRepository) {
        super("Prisma");
        url = "https://www.prismamarket.ee/tooted/";
        this.poodRepository = poodRepository;
    }

    @Override
    String hangiDynamicSource() throws ScrapeFailedException {
        WebDriver chromedriver = getChromedriver();

        // Veebilehe avamine
        getUrl(url);

        // Ootan kuni leht laeb, et ei tekiks vigu
        ootaLeheLaadimist("[data-test-id='product-list'] > div");

        // Loeb mitu toodet lehel on, et teada kui kaua peaks lehel alla scrollima
        WebElement tootearvuSilt = chromedriver.findElement(By.cssSelector("[data-test-id='product-result-total']"));
        // Üleliigne tekst eemaldatakse split meetodiga
        int toodeteArv = Integer.parseInt(tootearvuSilt.getText().split(" ")[0]);

        scrolliLeheLoppu(100, "[data-test-id='product-list-item']");

        return chromedriver.getPageSource();
    }

    @Override
    public List<Toode> scrape(WebDriver chromedriver) throws ScrapeFailedException{
        setChromedriver(chromedriver);
        List<Toode> tooted = new ArrayList<>();
        String lahtekood = hangiDynamicSource();

        // Saan lähtekoodist kõik toodete elemendid
        Document doc = Jsoup.parse(lahtekood);
        Elements lapsed = Objects.requireNonNull(doc.select("[data-test-id='product-list'] > div").first()).children();

        // Ühik määrab, mis ühikutes peaks hiljem ühikuhinda kuvama (l / kg)
        String tooteNimi, uhik = "tk";

        // Kliendihind on Prisma puhul hind säästukaardiga
        // tkHind ja uhikuHind on tavakliendi hind ehk ilma säästukaardita
        // Kui säästukaardiga erihinda pole (enamasti pole), siis tavakliendi hind == kliendi hind
        double tkHind, uhikuHind = 0, tkHindKlient = 0, uhikuHindKlient = 0;
        String tootePiltUrl;
        for (Element toode : lapsed) {
            tooteNimi = toode.select("[data-test-id='product-card__productName'] span").text();

            tootePiltUrl = toode.select("[data-test-id='product-card__productImage'] > img").attr("srcset").split(" ")[0];

            // Elements tooted sisaldab mõningaid üleliigseid ridu, skipin need
            if (tooteNimi.isEmpty()) continue;

            // Võtan hinnainfo konteineri, kust saan css-selectorite abil kõik vajaliku info kätte.
            Elements hinnaInfo = toode.select("[data-test-id='product-card__product-price']");

            Elements tkHindProov = hinnaInfo.select("[data-test-id='product-price__unitPrice']");

            if (tkHindProov.isEmpty()) { // Vaatan, kas eelmine data-test-id select on tühi, sest Prisma muudab neid id-sid
                tkHindProov = hinnaInfo.select("[data-test-id='product-price__dynamic-unitPrice']");
            }

            tkHind = tkHindKlient = Double.parseDouble(tkHindProov
                    .text().split(" ")[0]
                    .replace("~", "")
                    .replace(",", "."));

            Elements uhikuHindElement = hinnaInfo.select("[data-test-id='product-card__productPrice__comparisonPrice']");
            if (!uhikuHindElement.isEmpty() && uhikuHindElement.text().split(" ")[1].split("/").length > 1) {
                String[] uhikuHinnaInfo = uhikuHindElement.text().split(" ");
                uhikuHind = uhikuHindKlient = Double.parseDouble(uhikuHinnaInfo[0].replace(",", "."));
                uhik = uhikuHinnaInfo[1].split("/")[1];
            }

            Elements soodukaTavahindElement = hinnaInfo.select("[data-test-id='product-price__lowest30DayPrice']");
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
                    tootePiltUrl);
            tooted.add(uusToode);
        }
        return tooted;
    }
}