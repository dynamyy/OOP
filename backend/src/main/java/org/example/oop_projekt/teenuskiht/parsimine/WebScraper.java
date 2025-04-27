package org.example.oop_projekt.teenuskiht.parsimine;

import lombok.Getter;
import org.example.oop_projekt.Erindid.ScrapeFailedException;
import org.example.oop_projekt.andmepääsukiht.Toode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

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
    private WebDriverWait driverWait;
    private JavascriptExecutor jsExec;

    public void setChromedriver(WebDriver chromedriver) {
        this.chromedriver = chromedriver;
        driverWait = new WebDriverWait(chromedriver, Duration.ofSeconds(10));
        jsExec = (JavascriptExecutor) chromedriver;
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
     * @throws ScrapeFailedException Viga kui kui ei õnnestunud laadida etteantud arv elemente
     */
    void scrolliLeheLoppu(int oodatavLasteArv, String lapseCss) throws ScrapeFailedException {
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
                throw new ScrapeFailedException("\u001B[31mLehe lõppu scrollimine ebaõnnestus liiga kaua ootamise tõttu" +
                        "\n\toodatavLasteArv:" + oodatavLasteArv + "; Leidsin vaid:" + lasteArv +
                        "; lapseCss:" + lapseCss + "\u001B[0m");
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
            throw new ScrapeFailedException("\u001B[31mOotamine kestis liiga kaua, cssSelector: " + cssSelector + "\u001B[0m");
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
            throw new ScrapeFailedException("\u001B[31mTekkis viga URLi laadimisel: " + url + "\u001B[0m");
        }
    }
}