package com.vinylteam.vinyl.util.impl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VinylUaParserITest {

    private VinylUaParser vinylUaParser = new VinylUaParser();
    private HashSet<String> oneGenreTestLink = new HashSet<>();
    private HashSet<String> onePageTestLink = new HashSet<>();
    private HashSet<String> offerLinksSet = new HashSet<>();
    private String validOfferLink = "http://vinyl.ua/release/3372/georg-levin-everything-must-change%20%7D%7D";
    private String invalidOfferLink = "http://vinyl.ua/release/0";
    private Document validLinkDocument;
    private Document invalidLinkDocument;

    @BeforeAll
    void beforeAll() throws IOException {
        oneGenreTestLink.add("http://vinyl.ua/showcase/reggae");
        onePageTestLink.add("http://vinyl.ua/showcase/reggae?page=1");
        offerLinksSet.add(validOfferLink);
        offerLinksSet.add(invalidOfferLink);
        validLinkDocument = Jsoup.connect(validOfferLink).get();
        invalidLinkDocument = Jsoup.connect(invalidOfferLink).get();
    }

    @Test
    @DisplayName("Checks that returned hashset of genres links isn't empty after parsing.")
    void getGenresLinksTest() {
        assertFalse(vinylUaParser.getGenresLinks().isEmpty());
    }

    @Test
    @DisplayName("Checks that returned hashset of pages' links in one genre isn't empty after parsing.")
    void getPageLinksTest() {
        assertFalse(vinylUaParser.getPageLinks(oneGenreTestLink).isEmpty());
    }

    @Test
    @DisplayName("Checks that returned hashset of offers' links in one page isn't empty after parsing.")
    void getOfferLinksTest() {
        assertFalse(vinylUaParser.getOfferLinks(onePageTestLink).isEmpty());
    }

    @Test
    @DisplayName("Checks that getReleaseFrom-Valid-Document returns not empty release")
    void getReleaseFromValidDocument() {
        assertNotEquals("", vinylUaParser.getReleaseFromDocument(validLinkDocument));
    }

    @Test
    @DisplayName("Checks that getReleaseFrom-Not Valid-Document returns empty release")
    void getReleaseFromNotValidDocument() {
        assertEquals("", vinylUaParser.getReleaseFromDocument(invalidLinkDocument));
    }

    @Test
    @DisplayName("Checks that getArtistFrom-Valid-Document returns artist != Various Artists")
    void getArtistFromValidDocument() {
        assertNotEquals("Various Artists", vinylUaParser.getArtistFromDocument(validLinkDocument));
    }

    @Test
    @DisplayName("Checks that getArtistFrom-Not Valid-Document returns artist == Various Artists")
    void getArtistFromNotValidDocument() {
        assertEquals("Various Artists", vinylUaParser.getArtistFromDocument(invalidLinkDocument));
    }

    @Test
    @DisplayName("Checks that getPriceFrom-Valid-Document returns price != 0.")
    void getPriceFromValidDocument() {
        assertTrue(vinylUaParser.getPriceFromDocument(validLinkDocument) > 0);
    }

    @Test
    @DisplayName("Checks that getPriceFrom-Not Valid-Document returns price == 0.")
    void getPriceFromNotValidDocument() {
        assertEquals(0., vinylUaParser.getPriceFromDocument(invalidLinkDocument));
    }

    @Test
    @DisplayName("Checks that getOptionalCurrencyFrom-Valid-Document returns not-empty optional")
    void getOptionalCurrencyFromValidDocument() {
        assertTrue(vinylUaParser.getOptionalCurrencyFromDocument(validLinkDocument).isPresent());
    }

    @Test
    @DisplayName("Checks that getOptionalCurrencyFrom-Valid-Document returns empty optional")
    void getOptionalCurrencyFromNotValidDocument() {
        assertTrue(vinylUaParser.getOptionalCurrencyFromDocument(invalidLinkDocument).isEmpty());
    }

    @Test
    @DisplayName("Checks that getHighResImageLinkFrom-Valid-Document returns not default link to high resolution image")
    void getHighResImageLinkFromValidDocument() {
        assertNotEquals("img/goods/no_image.jpg", vinylUaParser.getHighResImageLinkFromDocument(validLinkDocument));
    }

    @Test
    @DisplayName("Checks that getHighResImageLinkFrom-Not Valid-Document returns null link to high resolution image")
    void getHighResImageLinkFromNotValidDocument() {
        assertEquals("img/goods/no_image.jpg", vinylUaParser.getHighResImageLinkFromDocument(invalidLinkDocument));
    }

    @Test
    @DisplayName("Checks that getGenreFrom-Valid-Document returns not empty genre")
    void getGenreFromValidDocument() {
        assertNotEquals("", vinylUaParser.getGenreFromDocument(validLinkDocument));
    }

    @Test
    @DisplayName("Checks that getGenreFrom-Not Valid-Document returns empty genre")
    void getGenreFromNotValidDocument() {
        assertEquals("", vinylUaParser.getGenreFromDocument(invalidLinkDocument));
    }

    @Test
    @DisplayName("Checks that returned raw offer is filled after parsing offer link.")
    void getRawOfferFromInvalidOfferLinkTest() {
        assertEquals("img/goods/no_image.jpg", vinylUaParser.getRawOfferFromOfferLink(invalidOfferLink).getImageLink());
    }

    @Test
    @DisplayName("Checks that returned hashset of raw offers isn't empty and doesn't contain invalid offers after parsing.")
    void readRawOffersFromAllOfferLinksTest() {
        assertFalse(vinylUaParser.readRawOffersFromAllOfferLinks(offerLinksSet).isEmpty());
    }

}
