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
class TriplevisionParserITest {

    private final TriplevisionParser triplevisionParser = new TriplevisionParser();
    private Document testDocument;

    @BeforeAll
    void beforeAll() {
        String testUrl = "https://www.triplevision.nl/release/WAHWAH025RP/";
        Optional<Document> testOptionalDocument = triplevisionParser.getDocument(testUrl);

        if (testOptionalDocument.isPresent()) {
            testDocument = testOptionalDocument.get();
        } else {
            throw new RuntimeException("Haven`t get document from link: ".concat(testUrl));
        }
    }

    @Test
    @DisplayName("Returns offer from offer link")
    void getRawOfferFromOfferLink() {
        //prepare
        String testOfferLink = "https://www.triplevision.nl/release/MOANV09/";
        //when
        RawOffer actualRawOfferFromOfferLink = triplevisionParser.getRawOfferFromOfferLink(testOfferLink);
        //then
        assertNotNull(actualRawOfferFromOfferLink);
    }

//    @Test
//    @DisplayName("Get genre links")
//    void getGenresLinks() {
//        //when
//        HashSet<String> actualGenresLinks = triplevisionParser.getGenresLinks();
//        //then
//        assertNotNull(actualGenresLinks);
//    }

    @Test
    @DisplayName("Get genre from document")
    void getGenreFromDocument() {
        //when
        String actualGenreFromDocument = triplevisionParser.getGenreFromDocument(testDocument);
        //then
        assertNotNull(actualGenreFromDocument);
    }

    @Test
    @DisplayName("Get optional currency from document")
    void getOptionalCurrencyFromDocument() {
        //when
        Optional<Currency> actualOptionalCurrencyFromDocument = triplevisionParser.getOptionalCurrencyFromDocument(testDocument);
        //then
        assertTrue(actualOptionalCurrencyFromDocument.isPresent());
    }

    @Test
    @DisplayName("Get price from document")
    void getPriceFromDocument() {
        //when
        double actualPriceFromDocument = triplevisionParser.getPriceFromDocument(testDocument);
        //then
        assertNotEquals(0.0d, actualPriceFromDocument);
    }

    @Test
    @DisplayName("Get artist from documents")
    void getArtistFromDocument() {
        //when
        String actualArtistFromDocument = triplevisionParser.getArtistFromDocument(testDocument);
        //then
        assertNotNull(actualArtistFromDocument);
    }

    @Test
    @DisplayName("Get release from document")
    void getReleaseFromDocument() {
        //when
        String actualReleaseFromDocument = triplevisionParser.getReleaseFromDocument(testDocument);
        //then
        assertNotNull(actualReleaseFromDocument);
    }

    @Test
    @DisplayName("Get catalog number from document")
    void getCatNumberFromDocument() {
        //when
        String actualCatalogNumberFromDocument = triplevisionParser.getCatNumberFromDocument(testDocument);
        //then
        assertNotNull(actualCatalogNumberFromDocument);
    }

    @Test
    @DisplayName("Get link to image")
    void getHighResImageLinkFromDocument() {
        //when
        String imageLink = triplevisionParser.getHighResImageLinkFromDocument(testDocument);
        //then
        assertNotNull(imageLink);
    }

    @Test
    @DisplayName("Read raw offer from offer links")
    void readRawOffersFromAllOfferLinks() {
        //prepare
        HashSet<String> testOfferLinks = new HashSet<>();
        testOfferLinks.add("https://www.triplevision.nl/release/BAUMUZIK001/");
        //when
        HashSet<RawOffer> actualRawOffers = triplevisionParser.readRawOffersFromAllOfferLinks(testOfferLinks);
        //then
        assertNotNull(actualRawOffers);
    }

    @Test
    @DisplayName("Returns offer links")
    void getOfferLinks() {
        //prepare
        HashSet<String> testGenreLinks = new HashSet<>();
        testGenreLinks.add("https://www.triplevision.nl/releases/");
        //when
        HashSet<String> actualOfferLinks = triplevisionParser.getOfferLinks(testGenreLinks);
        //then
        assertNotNull(actualOfferLinks);
    }

    @Test
    @DisplayName("Returns page links")
    void getPageLinks() {
        //when
        HashSet<String> actualPageLinks = triplevisionParser.getPageLinks();
        //then
        assertNotNull(actualPageLinks);
    }

    @Test
    @DisplayName("Returns document from https://www.decks.de")
    void getDocument() {
        //prepare
        String testUrl = "https://www.triplevision.nl/releases/";
        //when
        Optional<Document> actualDocument = triplevisionParser.getDocument(testUrl);
        //then
        assertTrue(actualDocument.isPresent());
    }

}