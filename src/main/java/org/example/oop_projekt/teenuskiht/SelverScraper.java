package org.example.oop_projekt.teenuskiht;

import org.example.oop_projekt.andmepääsukiht.PoodRepository;
import org.example.oop_projekt.andmepääsukiht.Toode;
import org.jsoup.Jsoup;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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

    /*
    Leian esilehe HTML-i
     */
    @Override
    String hangiDynamicSource(WebDriver chromedriver) {
        String leheHTML;

        chromedriver.get(url);

        // Ootan kuni leht laeb, et ei tekiks vigu
        WebDriverWait wait = new WebDriverWait(chromedriver, Duration.ofSeconds(10));

        return chromedriver.getPageSource();
    }


    //Vahelehtede html leidmind
    public static String html(WebDriver chromedriver, String url) {
        chromedriver.get(url);

        WebDriverWait wait = new WebDriverWait(chromedriver, Duration.ofSeconds(10));
        wait.until(driver -> driver.findElements(By.cssSelector(".ProductCard__info")).size() > 0);

        return chromedriver.getPageSource();
    }



    /*
    Leian kõik URL-d
     */
    public List<String> URLiKirjed(WebDriver chromedriver) throws IOException {
        List<String> info = new ArrayList<>();
        String s = hangiDynamicSource(chromedriver);
        Document doc = Jsoup.parse(s);

        Elements links = doc.select("a.SidebarMenu__link");


        for (int i = 21; i < links.size(); i++) {
            Element link = links.get(i);
            String href = link.attr("href");

            long slashCount = href.chars().filter(ch -> ch == '/').count();

            if (slashCount <= 2) {
                info.add("https://www.selver.ee" + href + "?limit=10000");
            }
        }
        return info;
    }

    /*
    Kasutan kõiki URL-e, et leida igal lehel olevate toodete info
     */
    @Override
    public List<Toode> scrape(WebDriver chromedriver) throws IOException {
        List<Toode> tooted = new ArrayList<>();
        List<String> urlid = URLiKirjed(chromedriver);
        int count = 0;

        for (String url : urlid){
            String html = html(chromedriver, url);

            Document doc = Jsoup.parse(html);

            Elements info = doc.select("div.ProductCard__info");

            for(Element tooteInfo : info){
                if (count == 10) return tooted;

                Element elemendiNimi = tooteInfo.selectFirst("a.ProductCard__link");
                String nimi = elemendiNimi.ownText().trim();

                Element elemendiHind = tooteInfo.selectFirst("div.ProductPrice");
                String hind = elemendiHind.ownText().trim();


                Element elemendiTykiHind = tooteInfo.selectFirst("span.ProductPrice__unit-price");
                String tykihind = elemendiTykiHind.text().trim();

                System.out.println(nimi + " " + hind + " " + tykihind);

                count++;
                //tooted.add(new Toode(nimi, hind, tykihind);
            }
        }

        return tooted;
    }
}
