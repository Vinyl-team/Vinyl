package com.vinylteam.vinyl.util.impl;

import com.vinylteam.vinyl.entity.Currency;
import com.vinylteam.vinyl.entity.RawOffer;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DecksParserITest {

    private final DecksParser decksParser = new DecksParser();
    private Document testDocument;

    @BeforeAll
    void beforeAll() {
        String testUrl = "https://www.decks.de/track/sault-untitled/ci7-9g";
        Optional<Document> testOptionalDocument = decksParser.getDocument(testUrl);

        if (testOptionalDocument.isPresent()) {
            testDocument = testOptionalDocument.get();
        } else {
            throw new RuntimeException("Haven`t get document from link: ".concat(testUrl));
        }
    }

    @Test
    @DisplayName("Returns offer link from offer link")
    void getRawOfferFromOfferLink() {
        //prepare
        String testOfferLink = "https://www.decks.de/track/industries_of_the_blend-the_folly_of_molly_ep/cho-t8";
        //when
        RawOffer actualRawOfferFromOfferLink = decksParser.getRawOfferFromOfferLink(testOfferLink);
        //then
        assertNotNull(actualRawOfferFromOfferLink);
    }

    @Test
    @DisplayName("Get genre links")
    void getGenresLinks() {
        //when
        HashSet<String> actualGenresLinks = decksParser.getGenresLinks();
        //then
        assertNotNull(actualGenresLinks);
    }

    @Test
    @DisplayName("Get genre from document")
    void getGenreFromDocument() {
        //when
        String actualGenreFromDocument = decksParser.getGenreFromDocument(testDocument);
        //then
        assertNotNull(actualGenreFromDocument);
    }

    @Test
    @DisplayName("Get optional currency from document")
    void getOptionalCurrencyFromDocument() {
        //when
        Optional<Currency> actualOptionalCurrencyFromDocument = decksParser.getOptionalCurrencyFromDocument(testDocument);
        //then
        assertTrue(actualOptionalCurrencyFromDocument.isPresent());
    }

    @Test
    @DisplayName("Get price from document")
    void getPriceFromDocument() {
        //when
        double actualPriceFromDocument = decksParser.getPriceFromDocument(testDocument);
        //then
        assertNotEquals(0.0d, actualPriceFromDocument);
    }

    @Test
    @DisplayName("Get artist from documents")
    void getArtistFromDocument() {
        //when
        String actualArtistFromDocument = decksParser.getArtistFromDocument(testDocument);
        //then
        assertNotNull(actualArtistFromDocument);
    }

    @Test
    @DisplayName("Get release from document")
    void getReleaseFromDocument() {
        //when
        String actualReleaseFromDocument = decksParser.getReleaseFromDocument(testDocument);
        //then
        assertNotNull(actualReleaseFromDocument);
    }

    @Test
    @DisplayName("Get catalog number from document")
    void getCatNumberFromDocument() {
        //when
        String actualCatalogNumberFromDocument = decksParser.getCatNumberFromDocument(testDocument);
        //then
        assertNotNull(actualCatalogNumberFromDocument);
    }

    @Test
    @DisplayName("Get link to image")
    void getHighResImageLinkFromDocument() {
        //when
        String imageLink = decksParser.getHighResImageLinkFromDocument(testDocument);
        //then
        assertNotNull(imageLink);
    }

    @Test
    @DisplayName("Read raw offer from offer links")
    void readRawOffersFromAllOfferLinks() {
        //prepare
        HashSet<String> testOfferLinks = new HashSet<>();
        testOfferLinks.add("https://www.decks.de/track/industries_of_the_blend-the_folly_of_molly_ep/cho-t8");
        //when
        HashSet<RawOffer> actualRawOffers = decksParser.readRawOffersFromAllOfferLinks(testOfferLinks);
        //then
        assertNotNull(actualRawOffers);
    }

    @Test
    @DisplayName("Returns offer links")
    void getOfferLinks() {
        //prepare
        HashSet<String> testGenreLinks = new HashSet<>();
        testGenreLinks.add("https://www.decks.de/decks/workfloor/lists/list_db.php/decks/workfloor/lists/list_db.php?wo=ten&now_Sub=hc&now_Was=news&now_Date=nodate&aktuell=0");
        //when
        HashSet<String> actualOfferLinks = decksParser.getOfferLinks(testGenreLinks);
        //then
        assertNotNull(actualOfferLinks);
    }

    @Test
    @DisplayName("Returns page links")
    void getPageLinks() {
        //prepare
        HashSet<String> testGenreLinks = new HashSet<>();
        testGenreLinks.add("https://www.decks.de/decks/workfloor/lists/list_db.php?wo=ten&now_Sub=hc&now_Was=news&now_Date=nodate&aktuell=0");
        //when
        HashSet<String> actualPageLinks = decksParser.getPageLinks(testGenreLinks);
        //then
        assertNotNull(actualPageLinks);
    }

    @Test
    @DisplayName("Returns document from https://www.decks.de")
    void getDocument() {
        //prepare
        String testUrl = "https://www.decks.de/decks/workfloor/lists/list_db.php";
        //when
        Optional<Document> actualDocument = decksParser.getDocument(testUrl);
        //then
        assertTrue(actualDocument.isPresent());
    }

}