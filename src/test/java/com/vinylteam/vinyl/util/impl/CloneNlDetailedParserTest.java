package com.vinylteam.vinyl.util.impl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class CloneNlDetailedParserTest {

    private CloneNlParser.DetailedVinylParser detailedParser = new CloneNlParser.DefaultDetailedVinylParser();

    static Element getVinylElement(String elementResource) {
        File testHtml = new File(CloneNlDetailedParserTest.class.getClassLoader().getResource(elementResource).getPath());
        try {
            return Jsoup.parse(testHtml, null).select("DIV").first();
        } catch (IOException e) {
            return null;
        }
    }

    static Stream<Arguments> getTestData() {
        return Stream.of(
                Arguments.of(new CloneNlParser.DefaultDetailedVinylParser(), getVinylElement("HtmlPages/cloneNLItem.html")),
                Arguments.of(new CloneNlParser.OnePageDetailedVinylParser(), getVinylElement("HtmlPages/cloneNLItemOnePage.html"))
                );
    }

    @ParameterizedTest(name = "#{index} - Run test with args={0}")
    @MethodSource("getTestData")
    @DisplayName("Checks whether genres are received from HTML Element that represents one vinyl Item")
    void getGenreFromDocument(CloneNlParser.DetailedVinylParser detailedParser, Element vinylElement) {
        var genre = detailedParser.getGenreFromDocument(vinylElement);
        assertNotNull(genre);
    }
    @ParameterizedTest(name = "#{index} - Run test with args={0}")
    @MethodSource("getTestData")
    @DisplayName("Checks whether release is received from HTML Element that represents one vinyl Item")
    void getReleaseFromDocument(CloneNlParser.DetailedVinylParser detailedParser, Element vinylElement) {
        String release = detailedParser.getReleaseFromDocument(vinylElement);
        assertEquals("The Paul Breitner EP", release);
    }

    @ParameterizedTest(name = "#{index} - Run test with args={0}")
    @MethodSource("getTestData")
    @DisplayName("Checks whether artist is received from HTML Element that represents one vinyl Item")
    void getArtistFromDocument(CloneNlParser.DetailedVinylParser detailedParser, Element vinylElement) {
        String artist = detailedParser.getArtistFromDocument(vinylElement);
        assertNotNull(artist);
    }

    @ParameterizedTest(name = "#{index} - Run test with args={0}")
    @MethodSource("getTestData")
    @DisplayName("Checks whether release is received from HTML Element that represents one vinyl Item")
    void getCatNumberFromDocument(CloneNlParser.DetailedVinylParser detailedParser, Element vinylElement) {
        String catalogNumber = detailedParser.getCatNumberFromDocument(vinylElement);
        assertEquals("Rothmans7", catalogNumber);
    }

    @ParameterizedTest(name = "#{index} - Run test with args={0}")
    @MethodSource("getTestData")
    @DisplayName("Checks whether release is received from HTML Element that represents one vinyl Item")
    void getInStockInfoFromDocument(CloneNlParser.DetailedVinylParser detailedParser, Element vinylElement) {
        boolean inStock = detailedParser.getInStockInfoFromDocument(vinylElement);
        assertTrue(inStock);
    }

    @ParameterizedTest(name = "#{index} - Run test with args={0}")
    @MethodSource("getTestData")
    @DisplayName("Checks whether currency is received from HTML Element that represents one vinyl Item")
    void getOptionalCurrencyFromDocumentTest(CloneNlParser.DetailedVinylParser detailedParser, Element vinylElement) {
        var currency = detailedParser.getOptionalCurrencyFromDocument(vinylElement);
        assertTrue(currency.isPresent());
    }

    @ParameterizedTest(name = "#{index} - Run test with args={0}")
    @MethodSource("getTestData")
    @DisplayName("Checks whether price is received from HTML Element that represents one vinyl Item")
    void getPriceFromDocumentTest(CloneNlParser.DetailedVinylParser detailedParser, Element vinylElement) {
        var price = detailedParser.getPriceFromDocument(vinylElement);
        assertEquals(11.49d, price);
    }

    @ParameterizedTest(name = "#{index} - Run test with args={0}")
    @MethodSource("getTestData")
    @DisplayName("Checks whether high resolution image link is received from HTML Element that represents one vinyl Item")
    void getHighResImageLinkFromDocumentTest(CloneNlParser.DetailedVinylParser detailedParser, Element vinylElement) throws IOException {
        String highResolutionImageLink = detailedParser.getHighResImageLinkFromDocument(vinylElement);
        assertNotNull(highResolutionImageLink);
        assertFalse(highResolutionImageLink.isEmpty());
    }

    @Test
    @DisplayName("Checks whether offer link is received from HTML Element that is a part of list")
    void getOfferLinkFromDocumentTest()  {
        var vinylElement = getVinylElement("HtmlPages/cloneNLItem.html");
        String offerLink = new CloneNlParser.DefaultDetailedVinylParser().getOfferLinkFromDocument(vinylElement);
        assertNotNull(offerLink);
        assertFalse(offerLink.isEmpty());
    }

    @Test
    @DisplayName("Checks whether offer link is empty from HTML Element that represents one vinyl Item")
    void getOfferLinkFromOnePAgeDocumentTest()  {
        var vinylElement = getVinylElement("HtmlPages/cloneNLItemOnePage.html");
        String offerLink = new CloneNlParser.OnePageDetailedVinylParser().getOfferLinkFromDocument(vinylElement);
        assertNotNull(offerLink);
        assertTrue(offerLink.isEmpty());
    }

}
