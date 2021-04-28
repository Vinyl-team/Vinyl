package com.vinylteam.vinyl.util;

import com.vinylteam.vinyl.entity.Offer;
import com.vinylteam.vinyl.entity.RawOffer;
import com.vinylteam.vinyl.entity.UniqueVinyl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RawOffersSorterTest {

    private final RawOffersSorter sorter = new RawOffersSorter();
    private final ListPreparerForTests listPreparer = new ListPreparerForTests();
    private final List<RawOffer> rawOffers = new ArrayList<>();
    private final List<UniqueVinyl> uniqueVinyls = new ArrayList<>();
    private final List<Offer> offers = new ArrayList<>();

    @BeforeAll
    void beforeAll() {
        listPreparer.fillListsForRawOffersSorterTest(rawOffers, uniqueVinyls, offers);
    }

    @Test
    @DisplayName("Passing filled rawOffers and filled uniqueVinyls that match some but not all raw offers " +
            "leaves uniqueVinyls the same, gets right offers, and leaves rawOffers empty.")
    void getOffersUpdateAllMatchesVinylsTest() {
        //prepare
        List<RawOffer> actualRawOffers = new ArrayList<>(rawOffers);
        List<UniqueVinyl> actualUniqueVinyls = new ArrayList<>(uniqueVinyls);
        List<UniqueVinyl> expectedUniqueVinyls = new ArrayList<>(actualUniqueVinyls);
        List<Offer> expectedOffers = new ArrayList<>(offers);
        //when
        List<Offer> actualOffers = sorter.getOffersUpdateUniqueVinyls(actualRawOffers, actualUniqueVinyls);
        //then
        assertEquals(expectedOffers, actualOffers);
        assertEquals(expectedUniqueVinyls, actualUniqueVinyls);
        assertTrue(actualRawOffers.isEmpty());
    }

    @Test
    @DisplayName("Passing filled rawOffers and filled uniqueVinyls that match some but not all raw offers " +
            "fills uniqueVinyls right, gets right offers, and leaves rawOffers empty.")
    void getOffersUpdateSomeMatchesVinylsTest() {
        //prepare
        List<RawOffer> actualRawOffers = new ArrayList<>(List.of(rawOffers.get(4), rawOffers.get(5)));
        List<UniqueVinyl> actualUniqueVinyls = new ArrayList<>(List.of(uniqueVinyls.get(0), uniqueVinyls.get(1)));
        List<UniqueVinyl> expectedUniqueVinyls = new ArrayList<>(uniqueVinyls);
        List<Offer> expectedOffers = new ArrayList<>(List.of(offers.get(4), offers.get(5)));
        //when
        List<Offer> actualOffers = sorter.getOffersUpdateUniqueVinyls(actualRawOffers, actualUniqueVinyls);
        //then
        assertEquals(expectedOffers, actualOffers);
        assertEquals(expectedUniqueVinyls, actualUniqueVinyls);
        assertTrue(actualRawOffers.isEmpty());
    }

    @Test
    @DisplayName("Passing filled rawOffers and filled uniqueVinyls that match no raw offers " +
            "fills uniqueVinyls right, gets right offers, and leaves rawOffers empty.")
    void getOffersUpdateNoMatchesVinylsTest() {
        //prepare
        List<RawOffer> actualRawOffers = new ArrayList<>(List.of(rawOffers.get(4), rawOffers.get(5)));
        List<UniqueVinyl> actualUniqueVinyls = new ArrayList<>(List.of(uniqueVinyls.get(0), uniqueVinyls.get(1)));
        List<UniqueVinyl> expectedUniqueVinyls = new ArrayList<>(uniqueVinyls);
        List<Offer> expectedOffers = new ArrayList<>(List.of(offers.get(4), offers.get(5)));
        //when
        List<Offer> actualOffers = sorter.getOffersUpdateUniqueVinyls(actualRawOffers, actualUniqueVinyls);
        //then
        assertEquals(expectedOffers, actualOffers);
        assertEquals(expectedUniqueVinyls, actualUniqueVinyls);
        assertTrue(actualRawOffers.isEmpty());
    }

    @Test
    @DisplayName("Passing filled rawOffers and empty uniqueVinyls fills uniqueVinyls right, gets right offers, and leaves rawOffers empty.")
    void getOffersUpdateEmptyVinylsTest() {
        //prepare
        List<RawOffer> actualRawOffers = new ArrayList<>(rawOffers);
        List<UniqueVinyl> actualUniqueVinyls = new ArrayList<>();
        List<UniqueVinyl> expectedUniqueVinyls = new ArrayList<>(uniqueVinyls);
        List<Offer> expectedOffers = new ArrayList<>(offers);
        //when
        List<Offer> actualOffers = sorter.getOffersUpdateUniqueVinyls(actualRawOffers, actualUniqueVinyls);
        //then
        assertEquals(expectedOffers, actualOffers);
        assertEquals(expectedUniqueVinyls, actualUniqueVinyls);
        assertTrue(actualRawOffers.isEmpty());
    }

    @Test
    @DisplayName("Passing empty rawOffers leaves uniqueVinyls the same and gets empty offers.")
    void getOffersUpdateVinylsWithEmptyRawOffersTest() {
        //prepare
        List<UniqueVinyl> actualUniqueVinyls = new ArrayList<>(uniqueVinyls);
        List<UniqueVinyl> expectedUniqueVinyls = new ArrayList<>(actualUniqueVinyls);
        //when
        List<Offer> actualOffers = sorter.getOffersUpdateUniqueVinyls(new ArrayList<>(), actualUniqueVinyls);
        //then
        assertEquals(expectedUniqueVinyls, actualUniqueVinyls);
        assertTrue(actualOffers.isEmpty());
    }

    @Test
    @DisplayName("Passing null rawOffers causes RuntimeException")
    void updateVinylsAndOffersWithNullRawOffersTest() {
        //when
        assertThrows(NullPointerException.class, () -> sorter.getOffersUpdateUniqueVinyls(null, new ArrayList<>()));
    }

    @Test
    @DisplayName("Passing null uniqueVinyls causes RuntimeException")
    void updateNullVinylsAndOffersTest() {
        //when
        assertThrows(NullPointerException.class, () -> sorter.getOffersUpdateUniqueVinyls(new ArrayList<>(), null));
    }

}