package org.example.oop_projekt.teenuskiht;

import java.net.URISyntaxException;

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
    public void scrape() {

    }
}
