package com.vinylteam.vinyl.util.impl;

import com.vinylteam.vinyl.entity.Vinyl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

class VinylUaParserITest {
    private VinylUaParser vinylUaParser;
    private HashSet<String> oneTestLink;

    @BeforeEach
    void setUp() {
        vinylUaParser = new VinylUaParser();
        oneTestLink = new HashSet<>();
        oneTestLink.add("http://vinyl.ua/showcase/reggae");
    }

    @Test
    void getGenresLinksTest() throws IOException {
        assertFalse(vinylUaParser.getGenresLinks().isEmpty());
    }

    @Test
    void getPageLinksTest() throws IOException {
        assertFalse(vinylUaParser.getPageLinks(oneTestLink).isEmpty());
    }

    @Test
    void readProductDataFromPageTest() throws IOException {
        assertFalse(vinylUaParser.readProductDataFromPage(oneTestLink).isEmpty());
    }

    @Test
    void getDataProduct() throws IOException {
        List<Vinyl> actualVinyls = vinylUaParser.getDataProduct();
        assertFalse(actualVinyls.isEmpty());
    }
}