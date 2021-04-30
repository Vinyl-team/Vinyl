package com.vinylteam.vinyl.dao.jdbc;

import com.vinylteam.vinyl.dao.UniqueVinylDao;
import com.vinylteam.vinyl.entity.Offer;
import com.vinylteam.vinyl.entity.Shop;
import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.util.DatabasePreparerForITests;
import com.vinylteam.vinyl.util.ListPreparerForTests;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JdbcUniqueVinylDaoITest {

    private final DatabasePreparerForITests databasePreparer = new DatabasePreparerForITests();
    private final UniqueVinylDao uniqueVinylDao = new JdbcUniqueVinylDao(databasePreparer.getDataSource());
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
    @DisplayName("Returns filled list with all unique vinyls from table that isn't empty")
    void findAllTest() {
        //when
        List<UniqueVinyl> actualUniqueVinyls = uniqueVinylDao.findAll();
        List<UniqueVinyl> expectedUniqueVinyls = new ArrayList<>(uniqueVinyls);
        for (UniqueVinyl expectedUniqueVinyl : expectedUniqueVinyls) {
            expectedUniqueVinyl.setHasOffers(false);
        }
        //then
        assertEquals(uniqueVinyls, actualUniqueVinyls);
    }

    @Test
    @DisplayName("Returns empty list from empty table")
    void findAllEmptyTableTest() throws SQLException {
        //prepare
        databasePreparer.truncateCascadeUniqueVinyls();
        //when
        List<UniqueVinyl> actualUniqueVinyls = uniqueVinylDao.findAll();
        //then
        assertTrue(actualUniqueVinyls.isEmpty());
    }

    @Test
    @DisplayName("Returns uniqueVinyl by id match from table")
    void findByIdTest() {
        //when
        UniqueVinyl actualUniqueVinyl = uniqueVinylDao.findById(1);
        UniqueVinyl expectedUniqueVinyl = List.copyOf(uniqueVinyls).get(0);
        expectedUniqueVinyl.setHasOffers(false);
        //then
        assertEquals(expectedUniqueVinyl, actualUniqueVinyl);
    }

    @Test
    @DisplayName("Throws RuntimeException when id has no matches")
    void findByNoMatchIdTest() {
        //when
        assertThrows(RuntimeException.class, () -> uniqueVinylDao.findById(5));
    }

    @Test
    @DisplayName("Throws RuntimeException when finding by id in empty table")
    void findByIdEmptyTableTest() throws SQLException {
        //prepare
        databasePreparer.truncateCascadeUniqueVinyls();
        //when
        assertThrows(RuntimeException.class, () -> uniqueVinylDao.findById(3));
    }

    @Test
    @DisplayName("Returns filled list with exact amount of different unique vinyls selected randomly when requested amount is less than amount of rows in table")
    void findManyRandomTest() {
        //when
        List<UniqueVinyl> actualUniqueVinyls = uniqueVinylDao.findManyRandom(2);
        //then
        assertEquals(2, actualUniqueVinyls.size());
        assertNotEquals(actualUniqueVinyls.get(0), actualUniqueVinyls.get(1));
    }

    @Test
    @DisplayName("Returns filled list with all unique vinyls from table selected randomly when requested amount is equal or bigger than amount of rows in table")
    void findManyRandomAmountBiggerThanTableSizeTest() {
        //when
        List<UniqueVinyl> actualUniqueVinyls = uniqueVinylDao.findManyRandom(4);
        //then
        assertEquals(3, actualUniqueVinyls.size());
        assertTrue(uniqueVinyls.containsAll(actualUniqueVinyls));
        assertNotEquals(actualUniqueVinyls.get(0), actualUniqueVinyls.get(1));
        assertNotEquals(actualUniqueVinyls.get(1), actualUniqueVinyls.get(2));
        assertNotEquals(actualUniqueVinyls.get(2), actualUniqueVinyls.get(0));
    }

    @Test
    @DisplayName("Returns empty list when requested valid amount from empty table")
    void findManyRandomEmptyTableTest() throws SQLException {
        //prepare
        databasePreparer.truncateCascadeUniqueVinyls();
        //when
        List<UniqueVinyl> actualUniqueVinyls = uniqueVinylDao.findManyRandom(3);
        //then
        assertTrue(actualUniqueVinyls.isEmpty());
    }

    @Test
    @DisplayName("Returns empty list when requested amount 0 from filled table")
    void findManyRandomAmountZeroTest() {
        //when
        List<UniqueVinyl> actualUniqueVinyls = uniqueVinylDao.findManyRandom(0);
        //then
        assertTrue(actualUniqueVinyls.isEmpty());
    }

    @Test
    @DisplayName("Throws RuntimeException when amount is less then 0")
    void findManyRandomNegativeAmountTest() {
        //when
        assertThrows(RuntimeException.class, () -> uniqueVinylDao.findManyRandom(-3));
    }

    @Test
    @DisplayName("Returns filled list by full name substring matcher that has matches in table")
    void findManyFilteredTest() {
        //prepare
        List<UniqueVinyl> expectedUniqueVinyls = new ArrayList<>(List.of(uniqueVinyls.get(0)));
        expectedUniqueVinyls.get(0).setHasOffers(false);
        //when
        List<UniqueVinyl> actualUniqueVinyls = uniqueVinylDao.findManyFiltered("1");
        //then
        assertEquals(expectedUniqueVinyls, actualUniqueVinyls);
    }

    @Test
    @DisplayName("Returns empty list by full name substring matcher that has no matches in table")
    void findManyFilteredZeroMatchesTest() {
        //when
        List<UniqueVinyl> actualUniqueVinyls = uniqueVinylDao.findManyFiltered("4");
        //then
        assertTrue(actualUniqueVinyls.isEmpty());
    }

    @Test
    @DisplayName("Returns filled list of all rows with has_offers=true when finding by full name substring matcher that is empty string")
    void findManyFilteredEmptyMatcherTest() {
        //prepare
        List<UniqueVinyl> expectedUniqueVinyls = new ArrayList<>(uniqueVinyls);
        expectedUniqueVinyls.remove(3);
        for (UniqueVinyl expectedUniqueVinyl : expectedUniqueVinyls) {
            expectedUniqueVinyl.setHasOffers(false);
        }
        //when
        List<UniqueVinyl> actualUniqueVinyls = uniqueVinylDao.findManyFiltered("");
        //then
        assertEquals(expectedUniqueVinyls, actualUniqueVinyls);
    }

    @Test
    @DisplayName("Throws RuntimeException when matcher is null")
    void findManyFilteredNullMatcherTest() {
        //when
        assertThrows(RuntimeException.class, () -> uniqueVinylDao.findManyFiltered(null));
    }

    @Test
    @DisplayName("Throws RuntimeException when matcher is null and table is empty")
    void findManyFilteredNullMatcherEmptyTableTest() throws SQLException {
        //prepare
        databasePreparer.truncateCascadeUniqueVinyls();
        //when
        assertThrows(RuntimeException.class, () -> uniqueVinylDao.findManyFiltered(null));
    }

    @Test
    @DisplayName("Returns empty list when finding by matcher in empty table")
    void findManyFilteredEmptyTableTest() throws SQLException {
        //prepare
        databasePreparer.truncateCascadeUniqueVinyls();
        //when
        List<UniqueVinyl> actualUniqueVinyls = uniqueVinylDao.findManyFiltered("1");
        //then
        assertTrue(actualUniqueVinyls.isEmpty());
    }

    @Test
    @DisplayName("Returns filled list by artist that has matches in the table")
    void findManyByArtistTest() {
        //prepare
        List<UniqueVinyl> expectedUniqueVinyls = new ArrayList<>(List.of(uniqueVinyls.get(0)));
        expectedUniqueVinyls.get(0).setHasOffers(false);
        //when
        List<UniqueVinyl> actualUniqueVinyls = uniqueVinylDao.findManyByArtist("artist1");
        //then
        assertEquals(expectedUniqueVinyls, actualUniqueVinyls);
    }

    @Test
    @DisplayName("Returns empty list by artist that has no matches in the table")
    void findManyByArtistNoMatchesTest() {
        //when
        List<UniqueVinyl> actualUniqueVinyls = uniqueVinylDao.findManyByArtist("artist4");
        //then
        assertTrue(actualUniqueVinyls.isEmpty());
    }

    @Test
    @DisplayName("Throws RuntimeException when passed artist is null")
    void findManyByNullArtistTest() {
        //when
        assertThrows(RuntimeException.class, () -> uniqueVinylDao.findManyByArtist(null));
    }

    @Test
    @DisplayName("Throws RuntimeException when passed artist is null and table is empty")
    void findManyByNullArtistEmptyTableTest() throws SQLException {
        //prepare
        databasePreparer.truncateCascadeUniqueVinyls();
        //when
        assertThrows(RuntimeException.class, () -> uniqueVinylDao.findManyByArtist(null));
    }

    @Test
    @DisplayName("Returns empty list when table is empty")
    void findManyByArtistEmptyTableTest() throws SQLException {
        //prepare
        databasePreparer.truncateCascadeUniqueVinyls();
        //when
        List<UniqueVinyl> actualUniqueVinyls = uniqueVinylDao.findManyByArtist("artist1");
        //then
        assertTrue(actualUniqueVinyls.isEmpty());
    }

}