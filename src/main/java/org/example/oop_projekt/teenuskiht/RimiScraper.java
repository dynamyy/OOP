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

public class RimiScraper extends WebScraper{


    private final PoodRepository poodRepository;
    private String url = "https://rimi.ee/epood";
    /**
     * Seadistab chromedriveri
     *
     * @throws URISyntaxException
     */
    public RimiScraper(PoodRepository poodRepository) throws URISyntaxException {
        super("Rimi");
        this.poodRepository = poodRepository;
    }

    static WebDriver chromedriver = getChromedriver();

    @Override
    String hangiDynamicSource() {
        try {
            chromedriver.get(url);

            // Ootan kuni leht laeb, et ei tekiks vigu
            WebDriverWait wait = new WebDriverWait(chromedriver, Duration.ofSeconds(10));

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
            wait.until(driver -> driver.findElements(By.cssSelector(".section-header__wrapper")).size() > 0);

            return chromedriver.getPageSource();
        } finally {
        }
    }

    /*
    Leian kõik URL-d
    Urlide leidmiseks pean info saama button class="trigger gtm" klassist
    <button role="menuitem" class="trigger gtm" href="/epood/ee/tooted/puuviljad-koogiviljad-lilled/c/SH-12">
     */
    public List<String> URLikirjed() throws IOException {
        List<String> info = new ArrayList<>();
        String s = hangiDynamicSource();
        Document doc = Jsoup.parse(s);

        // Valib kõik <button> elemendid klassiga "trigger gtm"
        Elements buttons = doc.select("button.trigger.gtm");

        for (Element button : buttons) {
            String href = button.attr("href");
            if (href != null && !href.isEmpty()) {
                info.add("https://rimi.ee" + href);
            }
        }
        System.out.println(info);
        return info;
    }

    @Override
    List<Toode> scrape() throws IOException {
        return List.of();
    }

}
