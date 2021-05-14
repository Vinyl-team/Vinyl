package com.vinylteam.vinyl.util.impl;

import com.vinylteam.vinyl.entity.Currency;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class JunoVinylParserITest {

    private final JunoVinylParser parser = new JunoVinylParser();

    @Test
    @DisplayName("Checks whether the set of page links that are present on the start page contains elements (links)")
    void getPageLinks() {
        var pageLinks = parser.getPresentPageLinks();
        assertFalse(pageLinks.isEmpty());
    }

    @Test
    @DisplayName("Checks whether pages links are counted correctly")
    void countPageLinksTest() {
        Set<String> links = Set.of("https://www.juno.co.uk/all/back-cat/3/?media_type=vinyl", "https://www.juno.co.uk/all/back-cat/333/?media_type=vinyl");
        Integer number = parser.countPageLinks(links);
        assertEquals(333, number);
    }

    @Test
    @DisplayName("Checks whether Raw Offers set is returned as a result of parsing one page link")
    void givenLinksToOnePageWhenRawOffersAreReceivedThenCorrect() {
        var pageLink = "https://www.juno.co.uk/all/back-cat/2/?media_type=vinyl";
        var items = parser.readVinylsDataFromAllPages(Set.of(pageLink));
        assertFalse(items.isEmpty());
    }

    @Test
    @DisplayName("Checks whether gaps in sequenced page links are filled")
    void givenLinksToPagesWhenFullListFromOneToMAxPageIsReceivedThenCorrect() {
        var items = parser.getFullPageLinksList(Set.of("https://www.juno.co.uk/all/back-cat/3/?media_type=vinyl", "https://www.juno.co.uk/all/back-cat/12/?media_type=vinyl"));
        assertEquals(12, items.size());
    }

    @Test
    @DisplayName("Checks whether RawOffers set is returned non empty as a result of parsing specified links")
    void givenLinksToPagesWhenRawOffersAreReceivedThenCorrect() {
        var items = parser.readVinylsDataFromAllPages(Set.of("https://www.juno.co.uk/all/back-cat/3/?media_type=vinyl", "https://www.juno.co.uk/all/back-cat/12/?media_type=vinyl"));
        assertFalse(items.isEmpty());
    }

    @Test
    @DisplayName("Checks whether RawOffer instance is created and fields are set based on HTML fragment that represents vinyl item")
    void givenHtmlImgElementWhenRawOfferIsCreatedThenCorrect() throws IOException {
        File testHtml = new File(this.getClass().getClassLoader().getResource("vinylItem.html").getPath());
        Document document = Jsoup.parse(testHtml, null);
        Element itemElement = document.select("div.dv-item").get(0);
        var rawOffer = parser.itemToRawOffer(itemElement).orElse(null);
        assertNotNull(rawOffer);
        assertNotNull(rawOffer.getRelease());
        assertFalse(rawOffer.getRelease().isEmpty());
        assertNotNull(rawOffer.getArtist());
        assertFalse(rawOffer.getArtist().isEmpty());
        assertTrue(rawOffer.getShopId() > 0);
        assertNotNull(rawOffer.getOfferLink());
        assertTrue(rawOffer.getOfferLink().startsWith(JunoVinylParser.BASE_LINK));
        assertFalse(rawOffer.getOfferLink().isEmpty());
        assertNotNull(rawOffer.getCurrency());
        assertEquals(Currency.getCurrency("GBP"), rawOffer.getCurrency());
        assertNotNull(rawOffer.getGenre());
        assertFalse(rawOffer.getGenre().isEmpty());
        assertEquals("Minimal/Tech House", rawOffer.getGenre());
        assertTrue(rawOffer.getPrice() > 0);
        assertEquals(8.75, rawOffer.getPrice());
        assertNotNull(rawOffer.getImageLink());
        assertFalse(rawOffer.getImageLink().isEmpty());
    }

    @Test
    @DisplayName("Checks returned image link since it can be set in 2 different attributes: src or data-src")
    void givenVinylImgTagWhenLinkIsReturnedEitherFromSrcOrDataSrcAttrThenCorrect() {
        String htmlImgSrcTag = "<IMG src='http://google.com/image.jpg' title='some image title'>";
        String htmlImgDataSrcTag = "<IMG data-src='http://google.com/data-image.jpg' title='some image title'>";
        String srcLink = getImgLink(htmlImgSrcTag);
        assertEquals("http://google.com/image.jpg", srcLink);
        String dataSrcLink = getImgLink(htmlImgDataSrcTag);
        assertEquals("http://google.com/data-image.jpg", dataSrcLink);
    }

    @Test
    @DisplayName("Checks extractPrice() method which returns numeric price from the string received in HTML")
    void givenPriceStringFromHtmlWhenNumericPriceReturnedThenCorrect() {
        double price = parser.extractPrice("£5.77");
        assertEquals(5.77, price);
    }

    private String getImgLink(String htmlImgTag) {
        var documentSrc = Jsoup.parse(htmlImgTag);
        var imageElement = documentSrc.select("img").get(0);
        return parser.resolveVinylImageLink(imageElement);
    }
}