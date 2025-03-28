package org.example.oop_projekt;

import org.openqa.selenium.WebDriver;

import java.net.URISyntaxException;
import java.util.List;

/**
 * COOPi epoe scraper. Kuna COOP on vaikselt oma
 * epoe teenust teistele ettevõtetele nagu Bolt ja Wolt
 * üle kandnud, siis tuleb andmeid lugeda Hiiumaa epoest.
 * (Boltis ja Woltis pakutakse väga väheseid tooteid)
 *
 * Hiiumaa eCOOPi koduleht:
 * https://hiiumaa.ecoop.ee/et
 */
public class CoopScraper extends WebScraper{
    private String url;

    public CoopScraper() throws URISyntaxException {
        super();
        url = "https://hiiumaa.ecoop.ee/et/tooted";
    }

    @Override
    String hangiDynamicSource() {
        WebDriver chromedriver = getChromedriver();
        String leheHTML;
        try {
            chromedriver.get(url);
            leheHTML = chromedriver.getPageSource();
        } finally {
            chromedriver.quit();
        }

        return leheHTML;
    }

    @Override
    void scrape() {
        String lahtekood = hangiDynamicSource();
        List<String> lapsed = leiaLapsed(lahtekood, ".products-wrapper");

        lapsed.forEach(System.out::println);
    }
}
