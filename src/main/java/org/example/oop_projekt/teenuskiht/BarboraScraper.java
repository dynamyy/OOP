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

    public BarboraScraper(PoodRepository poodRepository) throws URISyntaxException {
        super();
        this.poodRepository = poodRepository;
    }

    static WebDriver chromedriver = getChromedriver();


    //Esilehe html
    @Override
    String hangiDynamicSource() {
        String leheHTML;

        try{
            chromedriver.get(url);


            WebDriverWait wait = new WebDriverWait(chromedriver, Duration.ofSeconds(10));
            wait.until(driver -> driver.findElements(By.cssSelector(".category-item--title")).size() > 0);


            return chromedriver.getPageSource();
        } finally {
        }
    }

    //Vahelehtede html leidmine
    public static String html(String url) {
        WebDriver chromedriver = getChromedriver();
        try {
            chromedriver.get(url);

            WebDriverWait wait = new WebDriverWait(chromedriver, Duration.ofSeconds(10));
            wait.until(driver -> driver.findElements(By.cssSelector(".category-item--title")).size() > 0);
            return chromedriver.getPageSource();
        } finally {
        }
    }


    //Leian kõik URL-d
    public List<String> URLikirjed() throws IOException {
        List<String> info = new ArrayList<>();
        String s = hangiDynamicSource();
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
    //Barbora lehtedele tuleb lisada ?page=nr, kus nr tähendab vahelehte(alates teisest)
    //Kuna kõiki vahelehti ei saanud kohe alguses kätte, tuleb siin kasutada for loopi, et leida vahelehed, kus üldse tooted on.
    //Kui juhtub, et tooteid ei ole lehel või toodet pole hetkel valikus, võime loopi lõpetada sest mittesaadaval olevad tooted on viimased
    @Override
    List<Toode> scrape() throws IOException {
        return List.of();
    }
}
