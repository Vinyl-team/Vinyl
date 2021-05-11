package com.vinylteam.vinyl.util.impl;

import com.vinylteam.vinyl.entity.RawOffer;
import org.junit.jupiter.api.*;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VinylUaParserITest {

    private VinylUaParser vinylUaParser = new VinylUaParser();
    private HashSet<String> oneGenreTestLink = new HashSet<>();
    private HashSet<String> onePageTestLink = new HashSet<>();
    private HashSet<String> oneOfferTestLink = new HashSet<>();
    private String offerLink = "http://vinyl.ua/release/3372/georg-levin-everything-must-change%20%7D%7D";

    @BeforeAll
    void beforeAll() {
        oneGenreTestLink.add("http://vinyl.ua/showcase/reggae");
        onePageTestLink.add("http://vinyl.ua/showcase/reggae?page=1");
        oneOfferTestLink.add(offerLink);
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
    @DisplayName("Checks that returned raw offer is right after parsing offer link.")
    void getRawOfferFromOfferLinkTest() {
        assertFalse(vinylUaParser.getOfferLinks(onePageTestLink).isEmpty());
    }

    @Test
    @DisplayName("Checks that returned raw offer is right after parsing offer link.")
    void getRawOfferFromInvalidOfferLinkTest() {
        assertFalse(vinylUaParser.getOfferLinks(onePageTestLink).isEmpty());
    }

    @Test
    @DisplayName("Checks that returned hashset of raw offers is right after parsing.")
    void readRawOffersFromAllOfferLinksTest() {
        assertFalse(vinylUaParser.readRawOffersFromAllOfferLinks(oneOfferTestLink).isEmpty());
    }

    @Test
    @DisplayName("Checks that returned list of raw offers isn't empty after parsing.")
    void getRawOffersListTest() {
        List<RawOffer> actualRawOffers = vinylUaParser.getRawOffersList();
        assertFalse(actualRawOffers.isEmpty());
    }

}
