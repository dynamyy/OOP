package org.example.oop_projekt.teenuskiht.parsimine;

import org.example.oop_projekt.Erindid.ScrapeFailedException;
import org.example.oop_projekt.andmepääsukiht.PoodRepository;
import org.example.oop_projekt.andmepääsukiht.Toode;
import org.jsoup.Jsoup;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;
import java.time.Duration;
import java.util.*;

@Service
public class SelverScraper extends WebScraper {

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

    public SelverScraper(PoodRepository poodRepository) {
        super("Selver");
        this.poodRepository = poodRepository;
    }

    /*
    Leian esilehe HTML-i
     */
    @Override
    String hangiDynamicSource() throws ScrapeFailedException {
        WebDriver chromedriver = getChromedriver();

        getUrl(url);

        // Ootan kuni leht laeb, et ei tekiks vigu
        ootaLeheLaadimist("li.SidebarMenu__item");

        return chromedriver.getPageSource();
    }


    //Vahelehtede html leidmine
    public String html(String url) throws ScrapeFailedException{
        WebDriver chromedriver = getChromedriver();

        // Lehe avamine
        getUrl(url);

        WebDriverWait wait = new WebDriverWait(chromedriver, Duration.ofSeconds(10));
        wait.until(driver -> !driver.findElements(By.cssSelector(".ProductCard__info")).isEmpty());

        return chromedriver.getPageSource();
    }



    /*
    Leian kõik URL-d
     */
    public List<String> URLiKirjed() throws ScrapeFailedException {
        List<String> info = new ArrayList<>();
        String s = hangiDynamicSource();

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
    public List<Toode> scrape(WebDriver chromedriver) throws ScrapeFailedException {
        setChromedriver(chromedriver);
        List<Toode> tooted = new ArrayList<>();
        List<String> urlid = URLiKirjed();

        if (urlid == null) {
            return tooted;
        }

        int count = 0;

        for (String url : urlid){
            String html = html(url);

            // Vigase urli korral tagastatakse 0 toodet
            if (html.isEmpty()) {
                return new ArrayList<>();
            }

            Document doc = Jsoup.parse(html);

            Elements info = doc.select("div.ProductCard__info");

            for(Element tooteInfo : info){
                if (count == 10) return tooted;

                Element elemendiNimi = tooteInfo.selectFirst("a.ProductCard__link");
                String nimi = elemendiNimi.ownText().trim();

                Element elemendiTykiHind = tooteInfo.selectFirst("div.ProductPrice");
                String tykihind = elemendiTykiHind.ownText().trim();

                Element elemendiYhikuHind = tooteInfo.selectFirst("span.ProductPrice__unit-price");
                String yhikuhind = elemendiYhikuHind.text().trim();
                String yhik = yhikuhind.split("/")[1];


                String kliendiHind = "";
                try {
                    Element elemendiKliendiHind = tooteInfo.selectFirst("span.ProductBadge__badge--label");
                    kliendiHind = elemendiKliendiHind.text().trim();
                } catch (Exception e){
                    System.out.println("Puudub partnerkaardi soodustus");
                }

                String yhikuHindKlient;//Selle jaoks oleks vaja leida toote kogus

                System.out.println("Nimi: " + nimi +
                        ", tükihind: " + tykihind +
                        ", ühikuhind: " + yhikuhind +
                        ", kliendihind: " + kliendiHind +
                        ", ühik: " + yhik);

                count++;

                //Toode toode = new Toode(nimi, yhik, tykiHindKlient, yhikuHindKlient, new HashSet<>(), yhikuHind, tykiHind);
                //toode.lisaPood(poodRepository.findPoodByNimi("Selver");
                //tooted.add(toode);
            }
        }

        return tooted;
    }
}
