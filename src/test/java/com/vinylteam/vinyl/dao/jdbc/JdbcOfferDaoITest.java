package com.vinylteam.vinyl.dao.jdbc;

import com.vinylteam.vinyl.dao.DBDataSource;
import com.vinylteam.vinyl.dao.OfferDao;
import com.vinylteam.vinyl.entity.Offer;
import com.vinylteam.vinyl.entity.Shop;
import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.util.DatabasePreparerForITests;
import com.vinylteam.vinyl.util.ListPreparerForTests;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JdbcOfferDaoITest {

    private Connection connection;
    private final OfferDao offerDao = new JdbcOfferDao();
    private final DatabasePreparerForITests databasePreparer = new DatabasePreparerForITests(connection);
    private final List<Shop> shops = new ArrayList<>();
    private final List<UniqueVinyl> uniqueVinyls = new ArrayList<>();
    private final List<Offer> offers = new ArrayList<>();
    private final ListPreparerForTests listPreparer = new ListPreparerForTests();

    @BeforeAll
    void beforeAll() throws SQLException {
        connection = DBDataSource.getConnection();
        listPreparer.fillShopsList(shops);
        listPreparer.fillUniqueVinylsList(uniqueVinyls);
        listPreparer.fillOffersList(offers);
        databasePreparer.truncateAllVinylTables();
    }

    @AfterAll
    void afterAll() throws SQLException {
        databasePreparer.truncateAllVinylTables();
        connection.close();
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
    @DisplayName("Returns true after adding elements of valid filled list")
    void addAllTest() {
        //when
        boolean actualIsAdded = offerDao.addAll(offers);
        //then
        assertTrue(actualIsAdded);
    }

    @Test
    @DisplayName("Returns false when empty list is passed")
    void addAllEmptyListTest() {
        //prepare
        List<Offer> emptyOffers = new ArrayList<>();
        //when
        boolean actualIsAdded = offerDao.addAll(emptyOffers);
        //then
        assertFalse(actualIsAdded);
    }

    @Test
    @DisplayName("Throws RuntimeException when null is passed")
    void addAllNullListTest() {
        //when
        assertThrows(RuntimeException.class, () -> offerDao.addAll(null));
    }

    @Test
    @DisplayName("Throws RuntimeException when one of elements has empty currency")
    void addAllEmptyCurrencyTest() {
        //prepare
        List<Offer> oneEmptyCurrencyOffers = new ArrayList<>(offers);
        oneEmptyCurrencyOffers.get(0).setCurrency(Optional.empty());
        //when
        assertThrows(RuntimeException.class, () -> offerDao.addAll(oneEmptyCurrencyOffers));
    }

    @Test
    @DisplayName("Returns false when one of elements has null offerLink")
    void addAllNullOfferLinkTest() {
        //prepare
        List<Offer> oneNullOfferLinkOffers = new ArrayList<>(offers);
        oneNullOfferLinkOffers.get(0).setOfferLink(null);
        //when
        boolean actualIsAdded = offerDao.addAll(oneNullOfferLinkOffers);
        //then
        assertFalse(actualIsAdded);
    }

    @Test
    @DisplayName("Returns true after adding valid offers where in one of elements genre is null")
    void addAllNullGenreTest() {
        //prepare
        List<Offer> oneNullGenreOffers = new ArrayList<>(offers);
        oneNullGenreOffers.get(0).setGenre(null);
        //when
        boolean actualIsAdded = offerDao.addAll(oneNullGenreOffers);
        //then
        assertTrue(actualIsAdded);
    }

    @Test
    @DisplayName("Returns filled list with all offers from table that isn't empty")
    void findAllTest() {
        //when
        List<Offer> actualOffers = offerDao.findAll();
        //then
        assertEquals(offers, actualOffers);
    }

    @Test
    @DisplayName("Returns empty list from empty table")
    void findAllEmptyTableTest() throws SQLException {
        //prepare
        databasePreparer.truncateOffers();
        //when
        List<Offer> actualOffers = offerDao.findAll();
        //then
        assertTrue(actualOffers.isEmpty());
    }

    @Test
    @DisplayName("Returns offer by id with match from table")
    void findByIdTest() {
        //prepare
        Offer expectedOffer = offers.get(0);
        //when
        Offer actualOffer = offerDao.findById(1);
        //then
        assertEquals(expectedOffer, actualOffer);
    }

    @Test
    @DisplayName("Throws RuntimeException when id has no matches")
    void findByNoMatchIdTest() {
        //when
        assertThrows(RuntimeException.class, () -> offerDao.findById(7));
    }

    @Test
    @DisplayName("Throws RuntimeException when finding by id in empty table")
    void findByIdEmptyTableTest() throws SQLException {
        //prepare
        databasePreparer.truncateOffers();
        //when
        assertThrows(RuntimeException.class, () -> offerDao.findById(1));
    }

    @Test
    @DisplayName("Returns list of offers with uniqueVinylId-s matching passed uniqueVinylId")
    void findManyByUniqueVinylIdTest() {
        //prepare
        List<Offer> expectedOffers = new ArrayList<>(List.of(offers.get(0), offers.get(1)));
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

}