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

    private final JunoVinylParser parser = new JunoVinylParser();

    @Test
    void getPageLinks() {
        var pageLinks = parser.getPresentPageLinks();
        assertFalse(pageLinks.isEmpty());
    }

    @Test
    public void countPageLinksTest() {
        Set<String> links = Set.of("https://www.juno.co.uk/all/back-cat/3/?media_type=vinyl", "https://www.juno.co.uk/all/back-cat/333/?media_type=vinyl");
        Integer number = parser.countPageLinks(links);
        assertEquals(333, number);
    }

    @Test
    void givenLinksToOnePage_whenRawOffersAreReceived_thenCorrect() {
        var pageLink = "https://www.juno.co.uk/all/back-cat/2/?media_type=vinyl";
        var items = parser.readVinylsDataFromAllPages(Set.of(pageLink));
        assertFalse(items.isEmpty());
    }

    @Test
    void givenLinksToPages_whenFullListFromOneToMAxPageISReceived_thenCorrect() {
        var items = parser.getFullPageLinksList(Set.of("https://www.juno.co.uk/all/back-cat/3/?media_type=vinyl", "https://www.juno.co.uk/all/back-cat/12/?media_type=vinyl"));
        assertEquals(12, items.size());
    }

    @Test
    void givenLinksToPages_whenRawOffersAreReceived_thenCorrect() {
        var items = parser.readVinylsDataFromAllPages(Set.of("https://www.juno.co.uk/all/back-cat/3/?media_type=vinyl", "https://www.juno.co.uk/all/back-cat/12/?media_type=vinyl"));
        assertFalse(items.isEmpty());
    }

    @Test
    public void givenHtmlImgElement_whenRawOfferISCreated_thenCorrect() throws IOException {
        File testHtml = new File(this.getClass().getClassLoader().getResource("vinylITem.html").getPath());
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
    public void givenVinylImageTag_whenLinkIsReturnedEitherFRomSrcOrDataSrcAttr_thenCorrect() {
        String htmlImgSrcTag = "<IMG src='http://google.com/image.jpg' title='some image title'>";
        String htmlImgDataSrcTag = "<IMG data-src='http://google.com/data-image.jpg' title='some image title'>";
        String srcLink = getImgLink(htmlImgSrcTag);
        assertEquals("http://google.com/image.jpg", srcLink);
        String dataSrcLink = getImgLink(htmlImgDataSrcTag);
        assertEquals("http://google.com/data-image.jpg", dataSrcLink);
    }

    @Test
    public void givenPriceStringFRomHtml_whenNumericPriceReturned_thenCorrect(){
        double price = parser.extractPrice("£5.77");
        assertEquals(5.77, price);
    }

    private String getImgLink(String htmlImgTag) {
        var documentSrc = Jsoup.parse(htmlImgTag);
        var imageElement = documentSrc.select("img").get(0);
        return parser.resolveVinylImageLink(imageElement);
    }
}