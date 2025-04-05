package org.example.oop_projekt.teenuskiht;

import org.example.oop_projekt.andmepääsukiht.Toode;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class PrismaScraper extends WebScraper{

    /**
     * Seadistab chromedriveri
     *
     * @throws URISyntaxException
     */
    public PrismaScraper() throws URISyntaxException {
    }

    @Override
    String hangiDynamicSource() {
        return "";
    }



    @Override
    List<Toode> scrape() throws IOException {
        return List.of();
    }
}
