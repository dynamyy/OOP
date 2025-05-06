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
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class BarboraScraper extends WebScraper {
    /**
     * Seadistab chromedriveri
     *
     * @throws URISyntaxException
     */

    private final PoodRepository poodRepository;
    private String url = "https://barbora.ee/";

    public BarboraScraper(PoodRepository poodRepository) {
        super("Barbora");
        this.poodRepository = poodRepository;
    }


    //Esilehe html
    @Override
    String hangiDynamicSource() throws ScrapeFailedException {
        WebDriver chromedriver = getChromedriver();

        getUrl(url);

        WebDriverWait wait = new WebDriverWait(chromedriver, Duration.ofSeconds(10));
        wait.until(driver -> !driver.findElements(By.cssSelector(".category-item--title")).isEmpty());

        return chromedriver.getPageSource();
    }

    //Vahelehtede html leidmine
    public String html(String url) {
        WebDriver chromedriver = getChromedriver();
        chromedriver.get(url);

        WebDriverWait wait = new WebDriverWait(chromedriver, Duration.ofSeconds(10));
        wait.until(driver -> !driver.findElements(By.cssSelector(".category-item--title")).isEmpty());

        return chromedriver.getPageSource();
    }


    //Leian kõik URL-d
    public List<String> URLikirjed() throws ScrapeFailedException {
        List<String> info = new ArrayList<>();
        String s = hangiDynamicSource();

        // Vea korral tagastatakse null
        if (s.isEmpty()) {
            return null;
        }

        Document doc = Jsoup.parse(s);
        Elements links = doc.select("a.category-item--title");

        for (int i = 1; i < links.size(); i++) {
            Element link = links.get(i);
            String href = link.attr("href");
            info.add("https://barbora.ee" + href);
        }

        return info;
    }



    //Kasutan kõiki URL-e, et leida igal vahelele olevad tooted
    //Barbora lehtedele tuleb lisada ?page=nr, kus nr tähendab vahelehte(alates 2)
    //Kuna kõiki vahelehti ei saanud kohe alguses kätte, tuleb siin kasutada for loopi, et leida vahelehed, kus üldse tooted on.
    //Kui juhtub, et tooteid ei ole lehel või toodet pole hetkel valikus, võime loopi lõpetada sest mittesaadaval olevad tooted on viimased
    @Override
    public List<Toode> scrape(WebDriver chromedriver) throws ScrapeFailedException {
        setChromedriver(chromedriver);
        List<String> urlid = URLikirjed();
        List<Toode> tooted = new ArrayList<>();

        // Vea korral tagastatakse tühi list.
        if (urlid == null) {
            return tooted;
        }

        for (String url : urlid){
            int i = 2;

            String vaheleht = url;
            while (true) {

                String html = html(vaheleht);
                Document doc = Jsoup.parse(html);

                Elements kaardid = doc.select("li[data-testid^=product-card]");

                if (kaardid.isEmpty()) {
                    break;
                }

                for (Element kaart : kaardid) {
                    Element hindElement = kaart.selectFirst("meta[itemprop=price]");
                    if (hindElement == null) {
                        System.out.println("Hinnainfo puudub. Katkestan selle lehe töötlemise.");
                        break;
                    }

                    String hindStr = hindElement.attr("content").replace(",", ".");
                    double tykiHind = Double.parseDouble(hindStr);

                    Element nimiElement = kaart.selectFirst("span.tw-block");
                    String tooteNimi = nimiElement.text();


                    Element yhikuHindElement = kaart.selectFirst("div.tw-text-\\[10px\\]");
                    String hindKoosYhikuga = yhikuHindElement != null ? yhikuHindElement.text() : "";
                    double yhikuHind = hindTekstist(hindKoosYhikuga.split("€")[0]);
                    String yhik = hindKoosYhikuga.split("/")[1];



                    double kliendiYhikuHind = yhikuHind;
                    double kliendiTykiHind = tykiHind;


                    //Barbora leht veidi kahtlaselt üles ehitatud, kui tootel on kliendikaardi soodustus siis see pannakse html-s sinna,
                    //kus muidu on tavahind ning tavahinnale tehakse oma div
                    try{
                        Element yhikuHindKlientElement = kaart.selectFirst("div.tw-relative.tw-text-\\[10px\\]");
                        yhikuHind = hindTekstist(yhikuHindKlientElement.text());

                        Element tykiHindKlientElementTaisosa = kaart.select("span.tw-pr-\\[2px\\]").get(1);
                        Element tykiHindKlientElementMurdosa = kaart.select("span.tw-pr-\\[1px\\]").get(1);
                        tykiHind = Double.parseDouble(tykiHindKlientElementTaisosa.text() + "." + tykiHindKlientElementMurdosa.text());

                    }catch (Exception e){
                        //System.out.println("Puudub kliendihind");
                    }


                    Element piltElement = kaart.selectFirst("img");
                    String pildiURL = "";
                    if (piltElement != null) {
                        pildiURL = piltElement.attr("src");
                        if (pildiURL.isEmpty()) {
                            pildiURL = piltElement.attr("data-srcset");
                        }
                    }


                    /*
                    System.out.println("Nimi: " + tooteNimi +
                            ", tükihind: " + tykiHind +
                            ", ühikuhind: " + yhikuHind +
                            ", kliendihind: " + kliendiTykiHind +
                            ", kliendi ühikuhind: " + kliendiYhikuHind +
                            ", ühik: " + yhik +
                            ", piltURL: " + pildiURL);
                    */

                    Toode uusToode = new Toode(tooteNimi,
                            yhik,
                            kliendiTykiHind,
                            kliendiYhikuHind,
                            poodRepository.findPoodByNimi("Maxima"),
                            yhikuHind,
                            tykiHind,
                            pildiURL);
                    tooted.add(uusToode);


                }

                vaheleht = url + "?page=" + i;
                i++;
            }
            //break;//Vaatab ainult 1 alamkategooria, kustuta see ära kui päriselt scrapeda tahad
        }
        return tooted;//Leht tuvastab scraperi, seega tuleks scrapemine teha osade kaupa või leida mõni muu lahendus
    }

    public static double hindTekstist(String hindStr) {
        if (hindStr == null || hindStr.isEmpty()) {
            return 0.0;
        }

        hindStr = hindStr.replaceAll("[^\\d,\\.]", "");

        hindStr = hindStr.replace(",", ".");

        try {
            return Double.parseDouble(hindStr);
        } catch (NumberFormatException e) {
            System.err.println("Vigane hind: " + hindStr);
            return 0.0;
        }
    }
}
