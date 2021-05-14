package com.vinylteam.vinyl.util.impl;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    @DisplayName("Checks that returned raw offer is filled after parsing offer link.")
    void getRawOfferFromOfferLinkTest() {
        assertTrue(vinylUaParser.getRawOfferFromOfferLink(offerLink).getRelease() != null);
    }

    @Test
    @DisplayName("Checks that returned hashset of raw offers isn't emty after parsing.")
    void readRawOffersFromAllOfferLinksTest() {
        assertFalse(vinylUaParser.readRawOffersFromAllOfferLinks(oneOfferTestLink).isEmpty());
    }

}
