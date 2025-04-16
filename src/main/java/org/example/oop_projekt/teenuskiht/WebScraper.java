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
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
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
    private String poeNimi;
    private WebDriverWait driverWait;
    private JavascriptExecutor jsExec;

    public String getPoeNimi() {
        return poeNimi;
    }

    public void setChromedriver(WebDriver chromedriver) {
        this.chromedriver = chromedriver;
        driverWait = new WebDriverWait(chromedriver, Duration.ofSeconds(10));
        jsExec = (JavascriptExecutor) chromedriver;
    }

    public WebDriver getChromedriver() {
        return chromedriver;
    }

    public WebScraper(String poeNimi) {
        this.poeNimi = poeNimi;
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
    abstract List<Toode> scrape(WebDriver chromedriver) throws IOException;


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
     * Scrollib kuni etteantud arv lapselemente on laetud.
     * @param oodatavLasteArv Kui palju elemente peaks laadima
     * @param lapseCss Viide lapsele endale (üldine mitte mingile kindlale elemendile)
     * @return true kui scrollimine oli edukas, false kui ei õnnestunud laadida
     * etteantud arv elemente
     */
    boolean scrolliLeheLoppu(int oodatavLasteArv, String lapseCss) {
        int lasteArv = chromedriver.findElements(By.cssSelector(lapseCss)).size();

        // Scrollin nii kaua kuni kõik lapsed on laetud
        while (lasteArv < oodatavLasteArv) {
            // Scrollin lehe lõppu
            jsExec.executeScript("window.scrollBy(0,document.body.scrollHeight)");

            // Ootan, et elemendid laeks
            try {
                driverWait.until(ExpectedConditions.numberOfElementsToBeMoreThan(
                        By.cssSelector(lapseCss), lasteArv
                ));
            } catch (TimeoutException e) {
                System.out.println("\u001B[31mLehe lõppu scrollimine ebaõnnestus liiga kaua ootamise tõttu" +
                        "\n\toodatavLasteArv:" + oodatavLasteArv + "; Leidsin vaid:" + lasteArv +
                        "; lapseCss:" + lapseCss + "\u001B[0m");
                return false;
            }

            // Leian uue laste arvu
            lasteArv = chromedriver.findElements(By.cssSelector(lapseCss)).size();
        }
        return true;
    }

    /**
     * Ootab kuni leht on laetud.
     * Kontrollib kindla elementi olemasolu, mis on
     * dünaamilistel lehtedel (sisuliselt kõik tänapäevased lehed).
     * Tuvastab, kas laadimine oli edukas või mitte.
     * kindlam kui document.readyState kontrollimine
     * @param cssSelector elemendi CSS Selector, mille olemasolu kontrollida
     * @return true kui element laadis, false kui oodati 15sec ja element ei laadinud
     */
    boolean ootaLeheLaadimist(String cssSelector) {
        try {
            driverWait.until((ExpectedConditions.presenceOfElementLocated(By.cssSelector(cssSelector))));
            return true;
        } catch (TimeoutException e) {
            System.out.println("\u001B[31mOotamine kestis liiga kaua, cssSelector: " + cssSelector + "\u001B[0m");
            return false;
        }
    }
}