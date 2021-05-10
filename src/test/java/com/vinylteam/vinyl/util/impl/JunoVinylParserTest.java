package com.vinylteam.vinyl.util.impl;

import com.vinylteam.vinyl.entity.Currency;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class JunoVinylParserTest {

    private JunoVinylParser parser = new JunoVinylParser();

    @Test
    void getPageLinks() throws IOException {
        var pageLinks = parser.getPresentPageLinks();
        assertFalse(pageLinks.isEmpty());
    }

    @Test
    public void countPageLinksTest(){
       Set<String> links = Set.of("https://www.juno.co.uk/all/back-cat/3/?media_type=vinyl", "https://www.juno.co.uk/all/back-cat/333/?media_type=vinyl");
        Integer number = parser.countPageLinks(links);
        assertEquals(333, number);
    }

    @Test
    void givenLinksToOnePage_whenVinylsAreReceived_thenCorrect() throws IOException {
        var pageLink = "https://www.juno.co.uk/all/back-cat/2/?media_type=vinyl";
        var items = parser.readVinylsDataFromAllPages(Set.of(pageLink));
    }

    @Test
    void givenLinksToPages_whenFullListFromOneToMAxPAgeISReceived_thenCorrect() throws IOException {
        var items = parser.getFullPageLinksList(Set.of("https://www.juno.co.uk/all/back-cat/3/?media_type=vinyl", "https://www.juno.co.uk/all/back-cat/12/?media_type=vinyl"));
        assertEquals(12, items.size());
    }

    @Test
    void givenLinksToPages_whenVinylsAreReceived_thenCorrect() throws IOException {
        var items = parser.readVinylsDataFromAllPages(Set.of("https://www.juno.co.uk/all/back-cat/3/?media_type=vinyl", "https://www.juno.co.uk/all/back-cat/12/?media_type=vinyl"));
        assertFalse(items.isEmpty());
    }

    @Test
    public void itemToRawOfferTest() throws IOException {
        var testHtml = new File(this.getClass().getClassLoader().getResource("vinylITem.html").getPath());
        Document document = Jsoup.parse(testHtml, null);
        Element itemElement = document.select("div.dv-item").get(0);
        var vinylItem = parser.itemToRawOffer(itemElement).orElse(null);
        assertNotNull(vinylItem);
        assertNotNull(vinylItem.getRelease());
        assertFalse(vinylItem.getRelease().isEmpty());
        assertNotNull(vinylItem.getArtist());
        assertFalse(vinylItem.getArtist().isEmpty());
        assertNotNull(vinylItem.getShopId());
        assertNotNull(vinylItem.getOfferLink());
        assertFalse(vinylItem.getOfferLink().isEmpty());
        assertNotNull(vinylItem.getCurrency());
        assertEquals(Currency.getCurrency("GBP"), vinylItem.getCurrency());
        assertNotNull(vinylItem.getGenre());
        assertFalse(vinylItem.getGenre().isEmpty());
        assertEquals("Minimal/Tech House", vinylItem.getGenre());
        assertNotNull(vinylItem.getPrice());
        assertEquals(8.75, vinylItem.getPrice());
        assertNotNull(vinylItem.getImageLink());
        assertFalse(vinylItem.getImageLink().isEmpty());
    }
}