package org.example.oop_projekt.teenuskiht;

import org.example.oop_projekt.andmepääsukiht.PoodRepository;
import org.example.oop_projekt.andmepääsukiht.Toode;
import org.example.oop_projekt.andmepääsukiht.ToodeRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;

/**
 * Peaklass kõigi webscrape'imise tegevuste jaoks.
 * Iga poe scraper peaks laiendama seda klassi.
 */
public abstract class WebScraper {

    private WebDriver chromedriver;

    WebDriver getChromedriver() {
        return chromedriver;
    }


    /**
     * Seadistab chromedriveri
     * @throws URISyntaxException
     */
    public WebScraper() throws URISyntaxException {
        // Viitab chromedriver.exe failile resources kaustas
        URL cdResource = WebScraper.class.getClassLoader().getResource("chromedriver.exe");
        // Failitee teisendamine, et toetada utf-8 kaustade nimesid
        assert cdResource != null;
        String cdPath = Paths.get(cdResource.toURI()).toString();

        System.setProperty("webdriver.chrome.driver", cdPath);

        // Sean chromedriveri jooksma peidetult
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");

        chromedriver = new ChromeDriver(options);
    }

    /**
     * Kasutab Seleniumi ja chromedriverit, et saada lehe lähtekood.
     * See võimaldab saada andmeid lehtdelet, mis vajavad javascripti
     * tööriistu või muusugust kasutaja sisendit
     */
    abstract String hangiDynamicSource();

    /**
     * Meetod scrape'imisloogika käivitamiseks
     */
    abstract List<Toode> scrape();


    /**
     * Leiab kõik viidatud elemendi laste teksti
     * @param lahtekood Lehe lähtekood
     * @param cssSelector cssSelector viide vanema elemendile
     * @return Kõikide viidatud elemendi laste tekst.
     * Iga lapse kohta moodustatakse üks sõne listis.
     */
    List<String> leiaLapsed(String lahtekood, String cssSelector) {
        Document doc = Jsoup.parse(lahtekood);

        Elements lapsed = doc.select(cssSelector).first().children();

        return lapsed.stream().map(Element::text).toList();
    }

    /**
     * Scrollib kuni etteantud arv lapselemente on laetud
     * @param oodatavLasteArv Kui palju elemente peaks laadima
     * @param VanemaCss Viide elemendile, mis on laste vanem
     * @param lapseCss Viide lapsele endale (üldine mitte mingile kindlale elemendile)
     */
    void scrolliLeheLoppu(int oodatavLasteArv, String VanemaCss, String lapseCss) {
        WebDriverWait wait = new WebDriverWait(chromedriver, Duration.ofSeconds(5));
        JavascriptExecutor js = (JavascriptExecutor) chromedriver;

        int lasteArv = leiaLapsed(chromedriver.getPageSource(), VanemaCss).size();

        // Scrollin nii kaua kuni kõik lapsed on laetud
        while (lasteArv < oodatavLasteArv) {
            // Scrollin lehe lõppu
            js.executeScript("window.scrollBy(0,document.body.scrollHeight)");

            // Ootan, et elemendid laeks
            wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(
                    By.cssSelector(lapseCss), lasteArv
            ));

            // Leian uue laste arvu
            lasteArv = leiaLapsed(chromedriver.getPageSource(), VanemaCss).size();
        }
    }
}