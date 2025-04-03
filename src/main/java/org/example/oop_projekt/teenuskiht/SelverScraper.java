package org.example.oop_projekt.teenuskiht;

import org.example.oop_projekt.andmepääsukiht.Toode;

import java.net.URISyntaxException;
import java.util.List;

public class SelverScraper extends WebScraper{

    /**
     * Seadistab chromedriveri
     *
     * @throws URISyntaxException
     */
    public SelverScraper() throws URISyntaxException {
        super();

    }

    public static void main(String[] args) {

    }

    @Override
    String hangiDynamicSource() {
        return "";
    }

    @Override
    public List<Toode> scrape() {
        return null;
    }
}
