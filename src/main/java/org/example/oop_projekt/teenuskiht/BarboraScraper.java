package org.example.oop_projekt.teenuskiht;

import org.example.oop_projekt.andmepääsukiht.PoodRepository;
import org.example.oop_projekt.andmepääsukiht.Toode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class BarboraScraper extends WebScraper{
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
    String hangiDynamicSource() {
        WebDriver chromedriver = getChromedriver();

        if (!getUrl(url)) {
            return "";
        }

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
    public List<String> URLikirjed() {
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
    public List<Toode> scrape(WebDriver chromedriver) throws IOException {
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
                    double hind = Double.parseDouble(hindStr);

                    Element nimiElement = kaart.selectFirst("span.tw-block");
                    String nimi = nimiElement.text();


                    Element yhikuHindElement = kaart.selectFirst("div.tw-text-\\[10px\\]");
                    String yhikuHind = yhikuHindElement != null ? yhikuHindElement.text() : "";
                    String yhik = yhikuHind.split("/")[1];


                    Element yhikuHindKlientElement;
                    String yhikuHindKlient = "";
                    Element tykiHindKlientElement;
                    String tykiHindKlient = "";


                    try{


                    }catch (Exception e){
                        System.out.println("Puudub kliendihind");
                    }


                    //Lisa andmed uue tootena (oma Toode klassi järgi kohenda vajadusel)
                    //Toode toode = new Toode(nimi, yhik, tykiHindKlient, yhikuHindKlient, new HashSet<>(), yhikuHind, tykiHind);
                    //toode.lisaPood(poodRepository.findPoodByNimi("Barbora")


                    System.out.println("Nimi: " + nimi +
                            ", tükihind: " + hind +
                            ", ühikuhind: " + yhikuHind +
                            ", kliendihind: " + tykiHindKlient +
                            ", kliendi ühikuhind: " + yhikuHindKlient +
                            ", ühik: " + yhik);
                }

                vaheleht = url + "?page=" + i;
                i++;
            }
            break;//Vaatab ainult 1 alamkategooria
        }
        return tooted;
    }
}