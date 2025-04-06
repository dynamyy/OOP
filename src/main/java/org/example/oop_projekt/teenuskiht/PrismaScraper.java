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

import java.net.URISyntaxException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class PrismaScraper extends WebScraper {

    private final PoodRepository poodRepository;
    private String url;

    public PrismaScraper(PoodRepository poodRepository) throws URISyntaxException {
        super();
        url = "https://www.prismamarket.ee/tooted/";
        this.poodRepository = poodRepository;
    }

    @Override
    String hangiDynamicSource() {
        WebDriver chromedriver = getChromedriver();
        String leheHTML;

        try {
            chromedriver.get(url);

            // Ootan kuni leht laeb, et ei tekiks vigu
            WebDriverWait wait = new WebDriverWait(chromedriver, Duration.ofSeconds(15));
            wait.until((ExpectedConditions.presenceOfElementLocated(By.cssSelector("[data-test-id='product-list'] > div"))));

            // Loeb mitu toodet lehel on, et teada kui kaua peaks lehel alla scrollima
            WebElement tootearvuSilt = chromedriver.findElement(By.cssSelector("[data-test-id='product-result-total']"));
            // Üleliigne tekst eemaldatakse split meetodiga
            int toodeteArv = Integer.parseInt(tootearvuSilt.getText().split(" ")[0]);

            scrolliLeheLoppu(200, "[data-test-id='product-list'] > div", "[data-test-id='product-list-item']");
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
        Elements lapsed = doc.select("[data-test-id='product-list'] > div").first().children();

        // Ühik määrab, mis ühikutes peaks hiljem ühikuhinda kuvama (l / kg)
        String tooteNimi, uhik = "tk";

        // Kliendihind on Prisma puhul hind säästukaardiga
        // tkHind ja uhikuHind on tavakliendi hind ehk ilma säästukaardita
        // Kui säästukaardiga erihinda pole (enamasti pole), siis tavakliendi hind == kliendi hind
        double tkHind, uhikuHind = 0, tkHindKlient = 0, uhikuHindKlient = 0;
        for (Element toode : lapsed) {
            tooteNimi = toode.select("[data-test-id='product-card__productName'] span").text();

            // Elements tooted sisaldab mõningaid üleliigseid ridu, skipin need
            if (tooteNimi.isEmpty()) continue;

            // Võtan hinnainfo konteineri, kust saan css-selectorite abil kõik vajaliku info kätte.
            Elements hinnaInfo = toode.select("[data-test-id='product-card__productPrice']");

            tkHind = tkHindKlient = Double.parseDouble(hinnaInfo.select("[data-test-id='product-price__unitPrice']")
                    .text().split(" ")[0]
                    .replace("~", "")
                    .replace(",", "."));

            Elements uhikuHindElement = hinnaInfo.select("[data-test-id='product-card__productPrice__comparisonPrice']");
            if (!uhikuHindElement.isEmpty()) {
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

            System.out.print(tooteNimi + " " + tkHind + "€ " + uhikuHind + "€/" + uhik);
            if (tkHind != tkHindKlient) System.out.println(" Säästukaardiga: " + tkHindKlient + "€ " + uhikuHindKlient + "€/" + uhik);
            else System.out.println();

            Toode uusToode = new Toode(tooteNimi,
                    uhik,
                    tkHindKlient,
                    uhikuHindKlient,
                    poodRepository.findPoodByNimi("Prisma"),
                    uhikuHind,
                    tkHind);
            tooted.add(uusToode);
            System.out.println();
        }
        return tooted;
    }
}