package org.example.oop_projekt.teenuskiht;

import org.example.oop_projekt.andmepääsukiht.Pood;
import org.example.oop_projekt.andmepääsukiht.PoodRepository;
import org.example.oop_projekt.andmepääsukiht.Toode;
import org.jsoup.Jsoup;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.*;

public class SelverScraper extends WebScraper{

    /**
     * Seadistab chromedriveri
     *
     * @throws URISyntaxException
     */

    /*
    ProductCard_link - nimi, firma, kogus

    <div data-v-691fd582="" class="ProductCard__row ProductCard__name"><h3 data-v-691fd582="" class="ProductCard__title"><a data-v-691fd582="" href="/kodune-hakkliha-maks-moorits-500-g" class="ProductCard__link" data-testid="productLink">
          Kodune hakkliha, MAKS&amp;MOORITS, 500 g
    </a></h3></div>


    ProductPrice klassist saan hinna
    <div data-v-6f524057="" class="ProductPrice">3,55 €
           <span data-v-6f524057="" class="ProductPrice__unit-price">7,10 €/kg
        </span>

    </div>

    Saan ise valida, kui palju mingit sorti tooteid on lehel läbi urli


    Targem oleks ilmselt teha nii, et võtan SidebarMenu_title klassist üldkategooria, mis läheb urli: selver.ee/üldkategooria

    siis võtan SidebarMenu__name klassist nime, mis läheb urli selver.ee/üldkategooria/nimi

    Koguse saaaks ka html-st võtta, kuid lihtsam on panna igale poole sama, näiteks 10000:

    selver.ee/üldkategooria/nimi?limit=10000

    sellelt lehelt saan hakata lugema kõiki tooteid
     */


    private final PoodRepository poodRepository;
    private String url = "https://www.selver.ee/";

    public SelverScraper(PoodRepository poodRepository) throws URISyntaxException {
        super();
        this.poodRepository = poodRepository;
    }

    public static void main(String[] args) {
    }

    /*
    Leian esilehe HTML-i
     */
    @Override
    String hangiDynamicSource() {
        WebDriver chromedriver = getChromedriver();
        String leheHTML;

        try {
            chromedriver.get(url);

            // Ootan kuni leht laeb, et ei tekiks vigu
            WebDriverWait wait = new WebDriverWait(chromedriver, Duration.ofSeconds(10));

            leheHTML = chromedriver.getPageSource();
        } finally {
            chromedriver.quit();
        }

        return leheHTML;
    }

    public static String html(String url){
        WebDriver chromedriver = getChromedriver();
        String leheHTML;

        try {
            chromedriver.get(url);

            // Ootan kuni leht laeb, et ei tekiks vigu
            WebDriverWait wait = new WebDriverWait(chromedriver, Duration.ofSeconds(10));

            leheHTML = chromedriver.getPageSource();
        } finally {
            chromedriver.quit();
        }

        return leheHTML;
    }


    /*
    Leian kõik URL-d
     */
    public List<String> URLiKirjed() throws IOException {
        List<String> info = new ArrayList<>();
        String s = hangiDynamicSource();
        Document doc = Jsoup.parse(s);

        Elements links = doc.select("a.SidebarMenu__link");


        for (int i = 21; i < links.size(); i++) {
            Element link = links.get(i);
            String href = link.attr("href");

            long slashCount = href.chars().filter(ch -> ch == '/').count();

            if (slashCount <= 2) {
                info.add("https://www.selver.ee/" + href + "?limit=10000");
            }
        }
        return info;
    }

    /*
    Kasutan kõiki URL-e, et leida igal lehel olevate toodete info
     */
    @Override
    public List<Toode> scrape() throws IOException {
        List<Toode> tooted = new ArrayList<>();
        List<String> urlid = URLiKirjed();

        for (String url : urlid){
            String html = html(url);

            Document doc = Jsoup.parse(html);


        }



        return tooted;
    }
}
