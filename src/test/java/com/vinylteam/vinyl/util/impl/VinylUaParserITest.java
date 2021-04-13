package com.vinylteam.vinyl.util.impl;

import com.vinylteam.vinyl.entity.Vinyl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
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
    void getGenresLinksTest() throws IOException {
        assertFalse(vinylUaParser.getGenresLinks().isEmpty());
    }

    @Test
    @DisplayName("Checks that returned hashset of pages links in one genre isn't empty after parsing.")
    void getPageLinksTest() throws IOException {
        assertFalse(vinylUaParser.getPageLinks(oneTestLink).isEmpty());
    }

    @Test
    @DisplayName("Checks that returned hashset of vinyls isn't empty after parsing.")
    void readVinylsDataFromAllPagesTest() throws IOException {
        assertFalse(vinylUaParser.readVinylsDataFromAllPages(oneTestLink).isEmpty());
    }

    @Test
    @DisplayName("Checks that returned list of vinyls isn't empty after parsing.")
    void getAllVinylsFromShopListTest() throws IOException {
        List<Vinyl> actualVinyls = vinylUaParser.getAllVinylsFromShopList();
        assertFalse(actualVinyls.isEmpty());
    }

}