package org.example.oop_projekt.teenuskiht.parsimine;

import org.example.oop_projekt.Erindid.ScrapeFailedException;
import org.example.oop_projekt.repository.PoodRepository;
import org.example.oop_projekt.mudel.Toode;
import org.jsoup.Jsoup;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.*;

import static java.lang.Math.round;

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
    @Override
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


        for (String url : urlid){
            String html = html(url);


            // Vigase urli korral tagastatakse 0 toodet
            if (html.isEmpty()) {
                return new ArrayList<>();
            }

            Document doc = Jsoup.parse(html);

            Elements info = doc.select("div.ProductCard");

            for(Element tooteInfo : info){
                //if (count == 10) return tooted;//Kui päris tööle paned asja võta see ära

                Element elemendiNimi = tooteInfo.selectFirst("div.ProductCard__name a.ProductCard__link");
                String tooteNimi = elemendiNimi.text().trim();

                Element elemendiTykiHind = tooteInfo.selectFirst("div.ProductPrice");
                String tykihindStr = elemendiTykiHind.ownText().trim();
                double tykiHind = hindTekstist(tykihindStr);

                Element elemendiYhikuHind = tooteInfo.selectFirst("span.ProductPrice__unit-price");
                String hindKoosYhikuga = elemendiYhikuHind.text().trim();
                double yhikuHind = hindTekstist(hindKoosYhikuga.split(" ")[0]);
                String yhik = hindKoosYhikuga.split("/")[1];


                double kliendiTykiHind = 0;

                try {
                    Element elemendiKliendiHind = tooteInfo.selectFirst("span.ProductBadge__badge--label");
                    kliendiTykiHind = hindTekstist(elemendiKliendiHind.text().trim());
                }catch (NullPointerException e){

                }
                if (kliendiTykiHind == 0){
                    kliendiTykiHind = tykiHind;
                    //System.out.println("Puudub partnerkaardi soodustus, kliendihind on: " + kliendiTykiHind);
                }



                Element imgElem = tooteInfo.selectFirst("div.ProductCard__image-wrapper img");
                String pildiURL = "";
                if (imgElem != null) {
                    pildiURL = imgElem.hasAttr("data-src") ? imgElem.attr("data-src").trim() : imgElem.attr("src").trim();
                }


                double kliendiYhikuHind = yhikuHind;

                if (tykiHind != kliendiTykiHind){
                    double protsent = kliendiTykiHind / tykiHind;
                    kliendiYhikuHind = new BigDecimal(yhikuHind * protsent).setScale(2, RoundingMode.HALF_UP).doubleValue();
                }

                /*
                System.out.println("Nimi: " + tooteNimi +
                        ", tükihind: " + tykiHind +
                        ", ühikuhind: " + yhikuHind +
                        ", kliendihind: " + kliendiTykiHind +
                        ", kliendiühikuhind: " + kliendiYhikuHind +
                        ", ühik: " + yhik +
                        ", pildi URL: " + pildiURL);

                 */


                Toode uusToode = new Toode(tooteNimi,
                        yhik,
                        kliendiTykiHind,
                        kliendiYhikuHind,
                        poodRepository.findPoodByNimi("Selver"),
                        yhikuHind,
                        tykiHind,
                        pildiURL,
                        "",
                        null);
                tooted.add(uusToode);

            }
        }

        return tooted;
    }
}
