package com.vinylteam.vinyl.util.impl;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class JunoVinylParserTest {

    private JunoVinylParser parser = new JunoVinylParser();

    @Test
    void getPageLinks() throws IOException {
        var pageLinks = parser.getPresentPageLinks();
        assertFalse(pageLinks.isEmpty());
    }

    @Test
    void givenLinksToOnePage_whenVinylsAreReceived_thenCorrect() throws IOException {
        var pageLink = "https://www.juno.co.uk/all/back-cat/2/?media_type=vinyl";
        var items = parser.readVinylsDataFromAllPages(Set.of(pageLink));
    }

    @Test
    void givenLinksToPages_whenFullListFromOneToMAxPAgeISReceived_thenCorrect() throws IOException {
        var items = parser.getFullPageLinksList(Set.of("https://www.juno.co.uk/all/back-cat/3/?media_type=vinyl", "https://www.juno.co.uk/all/back-cat/12/?media_type=vinyl"));
        assertEquals(12, items.size());
    }

    @Test
    void givenLinksToPages_whenVinylsAreReceived_thenCorrect() throws IOException {
        var items = parser.readVinylsDataFromAllPages(Set.of("https://www.juno.co.uk/all/back-cat/3/?media_type=vinyl", "https://www.juno.co.uk/all/back-cat/12/?media_type=vinyl"));
        assertFalse(items.isEmpty());
    }

}