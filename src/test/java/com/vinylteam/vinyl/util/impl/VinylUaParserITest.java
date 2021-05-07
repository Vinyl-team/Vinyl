package com.vinylteam.vinyl.util.impl;

import com.vinylteam.vinyl.entity.RawOffer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

class VinylUaParserITest {

    private VinylUaParser vinylUaParser;
    private HashSet<String> oneTestLink;

    @BeforeEach
    void beforeEach() {
        vinylUaParser = new VinylUaParser();
        oneTestLink = new HashSet<>();
        oneTestLink.add("http://vinyl.ua/showcase/reggae");
    }

    @Test
    @DisplayName("Checks that returned hashset of genres links isn't empty after parsing.")
    void getGenresLinksTest() {
        assertFalse(vinylUaParser.getGenresLinks().isEmpty());
    }

    @Test
    @DisplayName("Checks that returned hashset of pages links in one genre isn't empty after parsing.")
    void getPageLinksTest() {
        assertFalse(vinylUaParser.getPageLinks(oneTestLink).isEmpty());
    }

    @Test
    @DisplayName("Checks that returned hashset of raw offers isn't empty after parsing.")
    void readRawOffersFromAllPagesTest() {
        assertFalse(vinylUaParser.readRawOffersFromAllPages(oneTestLink).isEmpty());
    }

    @Test
    @DisplayName("Checks that returned list of raw offers isn't empty after parsing.")
    void getRawOffersListTest() {
        List<RawOffer> actualRawOffers = vinylUaParser.getRawOffersList();
        assertFalse(actualRawOffers.isEmpty());
    }

}
