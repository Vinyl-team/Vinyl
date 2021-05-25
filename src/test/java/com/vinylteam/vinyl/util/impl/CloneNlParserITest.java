package com.vinylteam.vinyl.util.impl;

import com.vinylteam.vinyl.entity.Currency;
import com.vinylteam.vinyl.util.VinylParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CloneNlParserITest {

    private CloneNlParser parser = new CloneNlParser();

    @Test
    @DisplayName("Checks whether genre links can be received")
    void getAllGenreLinks() {
        var allGenres = parser.getAllGenreLinks();
        assertFalse(allGenres.isEmpty());
        for (String genreLink : allGenres) {
            assertTrue(genreLink.startsWith(CloneNlParser.BASE_LINK));
        }
    }

    @Test
    @DisplayName("Checks whether page links can be received by the set of genre links, at least one for each genre")
    void getAllPagesByGenresTest() {
        Set<String> genreLink = Set.of("https://clone.nl/all/genre/Disco?sort=datum&order=desc", "https://clone.nl/all/genre/Dubstep?sort=datum&order=desc");
        var allGenrePages = parser.getAllPagesByGenres(genreLink);
        assertFalse(allGenrePages.isEmpty());
        assertTrue(allGenrePages.size() >= 2);
    }

    @Test
    @DisplayName("Checks whether at least one page link can be received by one genre link")
    void getAllPagesByGenreTest() {
        String genreLink = "https://clone.nl/all/genre/Dubstep?sort=datum&order=desc";
        var allGenrePages = parser.getAllPagesByGenre(genreLink);
        assertFalse(allGenrePages.isEmpty());
        assertTrue(allGenrePages.size() >= 1);
    }

    @Test
    @DisplayName("Checks whether RawOffer is received from HTML Element that represents one vinyl Item")
    void getRawOffersFromAllOfferLinksTest() {
        var rawOffer = parser.getRawOfferFromOfferLink("https://clone.nl/item36449.html");
        assertNotNull(rawOffer);
        assertEquals("The Paul Breitner EP", rawOffer.getRelease());
        assertEquals("https://clone.nl/item36449.html", rawOffer.getOfferLink());
        assertEquals("https://clone.nl/platen/artwork/large/plaatimage36037.jpg", rawOffer.getImageLink());
        assertEquals(Currency.EUR, rawOffer.getCurrency().get());
        assertEquals(11.49d, rawOffer.getPrice());
        assertEquals("Various Artists", rawOffer.getArtist());
        assertTrue(rawOffer.getGenre().contains("House"));
    }

}