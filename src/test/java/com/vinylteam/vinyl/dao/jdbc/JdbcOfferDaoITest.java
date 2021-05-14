package com.vinylteam.vinyl.dao.jdbc;

import com.vinylteam.vinyl.dao.OfferDao;
import com.vinylteam.vinyl.entity.Offer;
import com.vinylteam.vinyl.entity.Shop;
import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.util.DataFinderFromDBForITests;
import com.vinylteam.vinyl.util.DatabasePreparerForITests;
import com.vinylteam.vinyl.util.DataGeneratorForTests;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JdbcOfferDaoITest {

    private final DatabasePreparerForITests databasePreparer = new DatabasePreparerForITests();
    private final DataFinderFromDBForITests dataFinder = new DataFinderFromDBForITests(databasePreparer.getDataSource());
    private final DataGeneratorForTests dataGenerator = new DataGeneratorForTests();
    private final OfferDao offerDao = new JdbcOfferDao(databasePreparer.getDataSource());
    private final List<Shop> shops = dataGenerator.getShopsList();
    private final List<UniqueVinyl> uniqueVinyls = dataGenerator.getUniqueVinylsList();
    private final List<Offer> offers = dataGenerator.getOffersList();

    @BeforeAll
    void beforeAll() throws SQLException {
        databasePreparer.truncateAllVinylTables();
    }

    @AfterAll
    void afterAll() throws SQLException {
        databasePreparer.truncateAllVinylTables();
    }

    @BeforeEach
    void beforeEach() throws SQLException {
        databasePreparer.insertShops(shops);
        databasePreparer.insertUniqueVinyls(uniqueVinyls);
        databasePreparer.insertOffers(offers);
    }

    @AfterEach
    void afterEach() throws SQLException {
        databasePreparer.truncateAllVinylTables();
    }

    @Test
    @DisplayName("Returns list of offers with uniqueVinylId-s matching passed uniqueVinylId")
    void findManyByUniqueVinylIdTest() {
        //prepare
        List<Offer> expectedOffers = dataGenerator.getOffersList();
        expectedOffers.subList(2, 6).clear();
        //when
        List<Offer> actualOffers = offerDao.findManyByUniqueVinylId(1);
        //then
        assertEquals(expectedOffers, actualOffers);
    }

    @Test
    @DisplayName("Throws RuntimeException when passed uniqueVinylId has no matches")
    void findManyByUniqueVinylIdNoMatchTest() {
        //when
        assertThrows(RuntimeException.class, () -> offerDao.findManyByUniqueVinylId(4));
    }

    @Test
    @DisplayName("Throws RuntimeException when finding many by uniqueVinylId in empty table")
    void findManyByUniqueVinylIdEmptyTableTest() throws SQLException {
        //prepare
        databasePreparer.truncateOffers();
        //when
        assertThrows(RuntimeException.class, () -> offerDao.findManyByUniqueVinylId(1));
    }

    @Test
    @DisplayName("Throws Runtime Exception when null uniqueVinyls is passed")
    void updateUniqueVinylsRewriteAllNullUniqueVinylsTest() {
        //when
        assertThrows(RuntimeException.class, () -> offerDao.updateUniqueVinylsRewriteAll(null, offers));
    }

    @Test
    @DisplayName("Throws Runtime Exception when null offers is passed")
    void updateUniqueVinylsRewriteAllNullOffersTest() {
        //when
        assertThrows(RuntimeException.class, () -> offerDao.updateUniqueVinylsRewriteAll(uniqueVinyls, null));
    }

    @Test
    @DisplayName("Throws Runtime Exception when empty uniqueVinyls is passed")
    void updateUniqueVinylsRewriteAllEmptyUniqueVinylsTest() {
        //when
        assertThrows(RuntimeException.class, () -> offerDao.updateUniqueVinylsRewriteAll(new ArrayList<>(), offers));
    }

    @Test
    @DisplayName("Throws Runtime Exception when empty offers is passed")
    void updateUniqueVinylsRewriteAllEmptyOffersTest() {
        //when
        assertThrows(RuntimeException.class, () -> offerDao.updateUniqueVinylsRewriteAll(uniqueVinyls, new ArrayList<>()));
    }

    @Test
    @DisplayName("Returns empty list of unadded offers on updating filled db with two unique vinyls now without offers (has_offers==false or false vinyls) and without four offers that were referencing them")
    void updateUniqueVinylsRewriteAllRemovedOffersForTwoUniqueVinylsTest() {
        //prepare
        List<UniqueVinyl> expectedUpdatedUniqueVinyls = dataGenerator.getUniqueVinylsList();
        expectedUpdatedUniqueVinyls.get(1).setHasOffers(false);
        expectedUpdatedUniqueVinyls.get(2).setHasOffers(false);
        List<Offer> expectedUpdatedOffers = dataGenerator.getOffersList();
        expectedUpdatedOffers.subList(2, 6).clear();
        //when
        List<Offer> actualUnaddedOffers = offerDao.updateUniqueVinylsRewriteAll(expectedUpdatedUniqueVinyls, expectedUpdatedOffers);
        //then
        assertTrue(actualUnaddedOffers.isEmpty());
        List<UniqueVinyl> actualUpdatedUniqueVinyls = dataFinder.findAllUniqueVinyls();
        List<Offer> actualUpdatedOffers = dataFinder.findAllOffers();
        assertEquals(expectedUpdatedUniqueVinyls, actualUpdatedUniqueVinyls);
        assertEquals(expectedUpdatedOffers, actualUpdatedOffers);
    }

    @Test
    @DisplayName("Returns empty list of unadded offers on updating filled db with one vinyl turned true and one new offer referencing it")
    void updateUniqueVinylsRewriteAllNewOfferToPreviouslyFalseUniqueVinylTest() {
        //prepare
        List<UniqueVinyl> expectedUpdatedUniqueVinyls = dataGenerator.getUniqueVinylsList();
        expectedUpdatedUniqueVinyls.get(3).setHasOffers(true);
        List<Offer> expectedUpdatedOffers = dataGenerator.getOffersList();
        Offer newOffer = new Offer(expectedUpdatedOffers.get(0));
        newOffer.setId(7);
        newOffer.setUniqueVinylId(4);
        newOffer.setPrice(41.);
        newOffer.setGenre("genre4");
        newOffer.setOfferLink("shop1/release4");
        expectedUpdatedOffers.add(newOffer);
        //when
        List<Offer> actualUnaddedOffers = offerDao.updateUniqueVinylsRewriteAll(expectedUpdatedUniqueVinyls, expectedUpdatedOffers);
        //then
        assertTrue(actualUnaddedOffers.isEmpty());
        List<UniqueVinyl> actualUpdatedUniqueVinyls = dataFinder.findAllUniqueVinyls();
        List<Offer> actualUpdatedOffers = dataFinder.findAllOffers();
        assertEquals(expectedUpdatedUniqueVinyls, actualUpdatedUniqueVinyls);
        assertEquals(expectedUpdatedOffers, actualUpdatedOffers);
    }

    @Test
    @DisplayName("Returns empty list of unadded offers when database is initially filled, and on update there's one new unique vinyl and one new offer for it are added")
    void updateUniqueVinylsRewriteAllNewUniqueVinylNewOfferTest() {
        //prepare
        List<UniqueVinyl> expectedUpdatedUniqueVinyls = dataGenerator.getUniqueVinylsList();
        List<Offer> expectedUpdatedOffers = dataGenerator.getOffersList();
        UniqueVinyl newUniqueVinyl = new UniqueVinyl(expectedUpdatedUniqueVinyls.get(0));
        newUniqueVinyl.setId(5);
        newUniqueVinyl.setRelease("release5");
        newUniqueVinyl.setArtist("artist5");
        newUniqueVinyl.setFullName(newUniqueVinyl.getRelease() + " - " + newUniqueVinyl.getArtist());
        newUniqueVinyl.setImageLink("/image5");
        newUniqueVinyl.setHasOffers(true);
        expectedUpdatedUniqueVinyls.add(newUniqueVinyl);
        Offer newOffer = new Offer(expectedUpdatedOffers.get(0));
        newOffer.setId(7);
        newOffer.setUniqueVinylId(5);
        newOffer.setPrice(51.);
        newOffer.setGenre("genre5");
        newOffer.setOfferLink("shop1/release5");
        expectedUpdatedOffers.add(newOffer);
        //when
        List<Offer> actualUnaddedOffers = offerDao.updateUniqueVinylsRewriteAll(expectedUpdatedUniqueVinyls, expectedUpdatedOffers);
        //then
        assertTrue(actualUnaddedOffers.isEmpty());
        List<UniqueVinyl> actualUpdatedUniqueVinyls = dataFinder.findAllUniqueVinyls();
        List<Offer> actualUpdatedOffers = dataFinder.findAllOffers();
        assertEquals(expectedUpdatedUniqueVinyls, actualUpdatedUniqueVinyls);
        assertEquals(expectedUpdatedOffers, actualUpdatedOffers);
    }

    @Test
    @DisplayName("Returns list of unadded offers with all offers on updating filled db with all false vinyls with offers referencing them")
    void updateUniqueVinylsRewriteAllUniqueVinylsAllFalseWithOffersReferencingTest() {
        //prepare
        List<UniqueVinyl> expectedUpdatedUniqueVinyls = dataGenerator.getUniqueVinylsList();
        for (UniqueVinyl expectedUpdatedUniqueVinyl : expectedUpdatedUniqueVinyls) {
            expectedUpdatedUniqueVinyl.setHasOffers(false);
        }
        List<Offer> expectedUnaddedOffers = dataGenerator.getOffersList();
        //when
        List<Offer> actualUnaddedOffers = offerDao.updateUniqueVinylsRewriteAll(expectedUpdatedUniqueVinyls, dataGenerator.getOffersList());
        //then
        assertEquals(expectedUnaddedOffers, actualUnaddedOffers);
        List<UniqueVinyl> actualUpdatedUniqueVinyls = dataFinder.findAllUniqueVinyls();
        List<Offer> actualUpdatedOffers = dataFinder.findAllOffers();
        assertEquals(expectedUpdatedUniqueVinyls, actualUpdatedUniqueVinyls);
        assertTrue(actualUpdatedOffers.isEmpty());
    }

    @Test
    @DisplayName("Returns list of unadded offers with one offer on updating filled db with one of the offers referencing false vinyl")
    void updateUniqueVinylsRewriteAllSomeOffersReferencingFalseUniqueVinylTest() {
        //prepare
        List<UniqueVinyl> expectedUpdatedUniqueVinyls = dataGenerator.getUniqueVinylsList();
        List<Offer> expectedUpdatedOffers = dataGenerator.getOffersList();
        List<Offer> updatedOffersFalseUniqueVinylId = dataGenerator.getOffersList();
        Offer newOfferFalseUniqueVinylId = new Offer(expectedUpdatedOffers.get(0));
        newOfferFalseUniqueVinylId.setId(7);
        newOfferFalseUniqueVinylId.setUniqueVinylId(4);
        newOfferFalseUniqueVinylId.setPrice(41.);
        newOfferFalseUniqueVinylId.setGenre("genre4");
        newOfferFalseUniqueVinylId.setOfferLink("shop1/release4");
        updatedOffersFalseUniqueVinylId.add(newOfferFalseUniqueVinylId);
        List<Offer> expectedUnaddedOffers = updatedOffersFalseUniqueVinylId.subList(6, 7);
        //when
        List<Offer> actualUnaddedOffers = offerDao.updateUniqueVinylsRewriteAll(expectedUpdatedUniqueVinyls, updatedOffersFalseUniqueVinylId);
        //then
        assertEquals(expectedUnaddedOffers, actualUnaddedOffers);
        List<UniqueVinyl> actualUpdatedUniqueVinyls = dataFinder.findAllUniqueVinyls();
        List<Offer> actualUpdatedOffers = dataFinder.findAllOffers();
        assertEquals(expectedUpdatedUniqueVinyls, actualUpdatedUniqueVinyls);
        assertEquals(expectedUpdatedOffers, actualUpdatedOffers);
    }

    @Test
    @DisplayName("Returns list of unadded offers with offer with nonexistent uniqueVinylIds when database is initially filled, and one of the offers has uniqueVinylId that does not exist")
    void updateUniqueVinylsRewriteAllUniqueVinylsSomeOffersFalseUniqueVinylIdTest() {
        //prepare
        List<UniqueVinyl> expectedUpdatedUniqueVinyls = dataGenerator.getUniqueVinylsList();
        List<Offer> expectedUpdatedOffers = dataGenerator.getOffersList();
        List<Offer> updatedOffersNonExistentUniqueVinylId = dataGenerator.getOffersList();
        Offer offerNonExistentUniqueVinylId = new Offer(expectedUpdatedOffers.get(0));
        offerNonExistentUniqueVinylId.setId(7);
        offerNonExistentUniqueVinylId.setUniqueVinylId(5);
        offerNonExistentUniqueVinylId.setPrice(51.);
        offerNonExistentUniqueVinylId.setGenre("genre5");
        offerNonExistentUniqueVinylId.setOfferLink("shop1/release5");
        updatedOffersNonExistentUniqueVinylId.add(offerNonExistentUniqueVinylId);
        List<Offer> expectedUnaddedOffers = updatedOffersNonExistentUniqueVinylId.subList(6, 7);
        //when
        List<Offer> actualUnaddedOffers = offerDao.updateUniqueVinylsRewriteAll(expectedUpdatedUniqueVinyls, updatedOffersNonExistentUniqueVinylId);
        //then
        assertEquals(expectedUnaddedOffers, actualUnaddedOffers);
        List<UniqueVinyl> actualUpdatedUniqueVinyls = dataFinder.findAllUniqueVinyls();
        List<Offer> actualUpdatedOffers = dataFinder.findAllOffers();
        assertEquals(expectedUpdatedUniqueVinyls, actualUpdatedUniqueVinyls);
        assertEquals(expectedUpdatedOffers, actualUpdatedOffers);
    }

    @Test
    @DisplayName("Returns empty list of unadded offers on updating empty db with unique vinyls and valid offers")
    void updateUniqueVinylsRewriteAllEmptyTableTest() throws SQLException {
        //prepare
        databasePreparer.truncateCascadeUniqueVinyls();
        databasePreparer.truncateOffers();
        List<UniqueVinyl> expectedUpdatedUniqueVinyls = dataGenerator.getUniqueVinylsList();
        List<Offer> expectedUpdatedOffers = dataGenerator.getOffersList();
        //when
        List<Offer> actualUnaddedOffers = offerDao.updateUniqueVinylsRewriteAll(expectedUpdatedUniqueVinyls, expectedUpdatedOffers);
        //then
        assertTrue(actualUnaddedOffers.isEmpty());
        List<UniqueVinyl> actualUpdatedUniqueVinyls = dataFinder.findAllUniqueVinyls();
        List<Offer> actualUpdatedOffers = dataFinder.findAllOffers();
        assertEquals(expectedUpdatedUniqueVinyls, actualUpdatedUniqueVinyls);
        assertEquals(expectedUpdatedOffers, actualUpdatedOffers);
    }

}