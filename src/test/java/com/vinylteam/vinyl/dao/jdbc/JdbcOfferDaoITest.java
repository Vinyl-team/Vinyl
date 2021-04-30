package com.vinylteam.vinyl.dao.jdbc;

import com.vinylteam.vinyl.dao.OfferDao;
import com.vinylteam.vinyl.entity.Offer;
import com.vinylteam.vinyl.entity.Shop;
import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.util.DatabasePreparerForITests;
import com.vinylteam.vinyl.util.ListPreparerForTests;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JdbcOfferDaoITest {

    private final DatabasePreparerForITests databasePreparer = new DatabasePreparerForITests();
    private final OfferDao offerDao = new JdbcOfferDao(databasePreparer.getDataSource());
    private final List<Shop> shops = new ArrayList<>();
    private final List<UniqueVinyl> uniqueVinyls = new ArrayList<>();
    private final List<Offer> offers = new ArrayList<>();
    private final ListPreparerForTests listPreparer = new ListPreparerForTests();

    @BeforeAll
    void beforeAll() throws SQLException {
        listPreparer.fillShopsList(shops);
        listPreparer.fillUniqueVinylsList(uniqueVinyls);
        listPreparer.fillOffersList(offers);
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