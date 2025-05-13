package org.example.oop_projekt.teenuskiht.parsimine;

import jakarta.transaction.Transactional;
import lombok.Getter;
import org.example.oop_projekt.Erindid.ScrapeFailedException;
import org.example.oop_projekt.Erindid.TuhiElementideTagastusException;
import org.example.oop_projekt.mudel.Toode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

/**
 * Peaklass kõigi webscrape'imise tegevuste jaoks.
 * Iga poe scraper peaks laiendama seda klassi.
 */
public abstract class WebScraper{
    @Getter
    private WebDriver chromedriver;
    @Getter
    private final String poeNimi;
    @Getter
    private WebDriverWait driverWait;
    private JavascriptExecutor jsExec;
    private final Logger logger;

    public void setChromedriver(WebDriver chromedriver) {
        this.chromedriver = chromedriver;
        driverWait = new WebDriverWait(chromedriver, Duration.ofSeconds(60));
        jsExec = (JavascriptExecutor) chromedriver;
    }

    public WebScraper(String poeNimi) {
        this.poeNimi = poeNimi;
        this.logger = LoggerFactory.getLogger(CoopScraper.class);
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
    @Transactional
    abstract List<Toode> scrape(WebDriver chromedriver) throws IOException, InterruptedException;

    /**
     * Leiab lehelt kõik kategooriad, et neid ükshaaval läbi vaadata.
     * @return List URL-dest
     * @throws ScrapeFailedException Kui scrape ebaõnnestub
     */
    abstract List<String> URLiKirjed() throws ScrapeFailedException;


    /**
     * Scrollib kuni etteantud arv lapselemente on laetud.
     * @param oodatavLasteArv Kui palju elemente peaks laadima
     * @param lapseCss Viide lapsele endale (üldine mitte mingile kindlale elemendile)
     * @throws ScrapeFailedException Viga kui kui ei õnnestunud laadida etteantud arv elemente
     */
    void scrolliLeheLoppu(int oodatavLasteArv, String lapseCss) throws ScrapeFailedException {
        int lasteArv = chromedriver.findElements(By.cssSelector(lapseCss)).size();
        int kordiProovitud = 0;

        // Scrollin nii kaua kuni kõik lapsed on laetud
        while (lasteArv < oodatavLasteArv) {
            // Scrollin lehe lõppu
            jsExec.executeScript("window.scrollBy(0,document.body.scrollHeight)");


            // Ootan, et elemendid laeks
            try {
                driverWait.until(ExpectedConditions.numberOfElementsToBeMoreThan(
                        By.cssSelector(lapseCss), lasteArv
                ));
                kordiProovitud = 0;
            } catch (TimeoutException e) {
                // Timeouti korral proovin kuni 3x uuesti
                if (kordiProovitud < 3) {
                    logger.warn("Lehe lõppu scrollimine ebaõnnestus liiga kaua ootamise tõttu. {}. viga, proovin uuesti laadida.\n\toodatavLasteArv:{}; Leidsin vaid:{}; lapseCss:{}", kordiProovitud + 1, oodatavLasteArv, lasteArv, lapseCss);
                    kordiProovitud++;
                    continue;
                }
                throw new ScrapeFailedException("Lehe lõppu scrollimine ebaõnnestus liiga kaua ootamise tõttu" +
                        "\n\toodatavLasteArv:" + oodatavLasteArv + "; Leidsin vaid:" + lasteArv +
                        "; lapseCss:" + lapseCss);
            } catch (WebDriverException e) {
                throw new ScrapeFailedException("Lehe lõppu scrollimine ebaõnnestus chromedriveri vea tõttu: " + e.getMessage());
            }

            // Leian uue laste arvu
            lasteArv = chromedriver.findElements(By.cssSelector(lapseCss)).size();
        }
    }

    /**
     * Ootab kuni leht on laetud.
     * Kontrollib kindla elementi olemasolu, mis on
     * dünaamilistel lehtedel (sisuliselt kõik tänapäevased lehed).
     * Tuvastab, kas laadimine oli edukas või mitte.
     * kindlam kui document.readyState kontrollimine
     * @param cssSelector elemendi CSS Selector, mille olemasolu kontrollida
     * @throws ScrapeFailedException Viga kui oodati 15sec ja element ei laadinud
     */
    void ootaLeheLaadimist(String cssSelector) throws ScrapeFailedException {
        try {
            driverWait.until((ExpectedConditions.presenceOfElementLocated(By.cssSelector(cssSelector))));
        } catch (TimeoutException e) {
            throw new ScrapeFailedException("Ootamine kestis liiga kaua, cssSelector: " + cssSelector);
        } catch (WebDriverException e) {
            throw new ScrapeFailedException("Elemendi ootamine ebaõnnestus chromedriveri vea tõttu, cssSelector: "
                    + cssSelector + ": " + e.getMessage());
        }
    }

    /**
     * Laeb chromedriveriga etteantud veebilehe.
     * @param url veebileht, kuhu minna
     * @throws ScrapeFailedException Viga tekib kui url ei lae.
     */
    void getUrl(String url) throws ScrapeFailedException {
        try {
            chromedriver.get(url);
        } catch (WebDriverException e) {
            throw new ScrapeFailedException("Tekkis viga URLi laadimisel: " + url);
        }
    }

    /**
     * Leiab ja tagastab otsitava elemendi kasutades chromedriverit
     * @param cssSelector Elemendi cssSelector
     * @return Otsitud WebElement
     * @throws ScrapeFailedException Viga kui elementi ei leita, sisend on vigane
     * või on probleem chromedriveriga
     */
    WebElement leiaElement(String cssSelector) throws ScrapeFailedException {
        try {
            return chromedriver.findElement(By.cssSelector(cssSelector));
        } catch (WebDriverException e) {
            throw new ScrapeFailedException("Viga elemendi leidmisel chromedriverist. cssSelector " + cssSelector + ": " + e.getMessage());
        }
    }

    /**
     * Leiab ja tagastab otsitavad elemendid kasutades chromedriverit
     * @param cssSelector Elementide cssSelector
     * @return List otsitud WebElementidest
     * @throws ScrapeFailedException Viga kui elemente ei leita, sisend on vigane
     * või on probleem chromedriveriga
     */
    List<WebElement> leiaElemendid(String cssSelector) throws ScrapeFailedException {
        try {
            return chromedriver.findElements(By.cssSelector(cssSelector));
        } catch (WebDriverException e) {
            throw new ScrapeFailedException("Viga elementide leidmisel chromedriverist. cssSelector " + cssSelector + ": " + e.getMessage());
        }
    }

    /**
     * Leiab ja tagastab otsitavad elemendid võttes aluseks Element objekti
     * @param vanem Element, millest otsida
     * @param cssQuery otsitavate Elementide cssSelector
     * @return Otsitud Elements
     * @throws ScrapeFailedException Viga kui elemente ei leita, sisend on vigane
     * või on probleem chromedriveriga
     */
    Elements valiElement(Element vanem, String cssQuery) throws ScrapeFailedException{
        try {
            Elements tulemus = vanem.select(cssQuery);

            if (tulemus.isEmpty()) {
                throw new TuhiElementideTagastusException("Viga elemendi leidmisel vanema kaudu. Leiti 0 elementi. cssQuery: " + cssQuery);
            }

            return tulemus;

        } catch (IllegalStateException e) {
            throw new ScrapeFailedException("Viga elemendi leidmisel vanema kaudu. cssQuery " + cssQuery + ": " + e.getMessage());
        }
    }

    /**
     * Leiab ja tagastab otsitavad elemendid võttes aluseks Element objekti
     * @param vanem Element, millest otsida
     * @param cssQuery otsitavate Elementide cssSelector
     * @param lubaNull Kas pidada tühja vastust veaks või mitte
     * @return Otsitud Elements
     * @throws ScrapeFailedException Viga kui elemente ei leita, sisend on vigane
     * või on probleem chromedriveriga
     */
    Elements valiElement(Elements vanem, String cssQuery, boolean lubaNull) throws ScrapeFailedException{
        try {
            Elements tulemus = vanem.select(cssQuery);

            if (!lubaNull && tulemus.isEmpty()) {
                throw new TuhiElementideTagastusException("Viga elemendi leidmisel vanema kaudu. Leiti 0 elementi. cssQuery: " + cssQuery);
            }

            return tulemus;

        } catch (IllegalStateException e) {
            throw new ScrapeFailedException("Viga elemendi leidmisel vanema kaudu. cssQuery " + cssQuery + ": " + e.getMessage());
        }
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