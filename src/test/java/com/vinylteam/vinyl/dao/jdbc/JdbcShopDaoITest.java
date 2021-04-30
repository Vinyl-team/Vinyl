package com.vinylteam.vinyl.dao.jdbc;

import com.vinylteam.vinyl.entity.Shop;
import com.vinylteam.vinyl.util.DatabasePreparerForITests;
import com.vinylteam.vinyl.util.ListPreparerForTests;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JdbcShopDaoITest {

    private final DatabasePreparerForITests databasePreparer = new DatabasePreparerForITests();
    private final JdbcShopDao jdbcShopDao = new JdbcShopDao(databasePreparer.getDataSource());
    private final List<Shop> shops = new ArrayList<>();
    private final ListPreparerForTests listPreparer = new ListPreparerForTests();

    @BeforeAll
    void beforeAll() throws SQLException {
        databasePreparer.truncateCascadeShops();
        listPreparer.fillShopsList(shops);
    }

    @AfterAll
    void afterAll() throws SQLException {
        databasePreparer.truncateCascadeShops();
    }

    @BeforeEach
    void beforeEach() throws SQLException {
        databasePreparer.insertShops(shops);
    }

    @AfterEach
    void afterEach() throws SQLException {
        databasePreparer.truncateCascadeShops();
    }

    @Test
    @DisplayName("Gets list of shops with id-s with list of id-s from db")
    void getManyByListOfIds() {
        List<Integer> ids = List.of(1, 2);
        List<Shop>  expectedShops = new ArrayList(List.of(shops.get(0), shops.get(1)));

        List<Shop> actualShops = jdbcShopDao.getManyByListOfIds(ids);

        assertEquals(2, actualShops.size());
        assertEquals(expectedShops, actualShops);
    }

    @Test
    @DisplayName("Gets list of shops with id-s with list of id-s where some ids do not exist in db")
    void getManyByListOfIdsWithSomeNonExistentIds() {
        List<Integer> ids = List.of(1, 2, 4);
        List<Shop>  expectedShops = new ArrayList(List.of(shops.get(0), shops.get(1)));

        List<Shop> actualShops = jdbcShopDao.getManyByListOfIds(ids);

        assertEquals(2, actualShops.size());
        assertEquals(expectedShops, actualShops);
    }

    @Test
    @DisplayName("Gets empty list of shops with empty list of id-s from db")
    void getManyByListOfIdsEmptyList() {
        List<Integer> ids = new ArrayList<>();

        List<Shop> actualShops = jdbcShopDao.getManyByListOfIds(ids);

        assertTrue(actualShops.isEmpty());
    }

    @Test
    @DisplayName("Gets empty list of shops with list id-s from empty table")
    void getManyByListOfIdsFromEmptyTable() throws SQLException {
        databasePreparer.truncateCascadeShops();
        List<Integer> ids = List.of(1, 2);

        List<Shop> actualShops = jdbcShopDao.getManyByListOfIds(ids);

        assertTrue(actualShops.isEmpty());
    }

    @Test
    @DisplayName("Gets String filled with ids from filled list of ids")
    void fillSelectManyByIdsStatementWithFilledIdListTest() {
        List<Integer> ids = List.of(1, 2);
        String expectedStatement = "SELECT id, link_to_main_page, link_to_image, name " +
                "FROM public.shops WHERE id IN (1, 2)";

        String actualStatement = jdbcShopDao.fillSelectManyByIdsStatement(ids);

        assertEquals(expectedStatement, actualStatement);
    }

    @Test
    @DisplayName("Gets String not filled with ids from empty list of ids")
    void fillSelectManyByIdsStatementWithEmptyIdListTest() {
        List<Integer> ids = new ArrayList<>();
        String expectedStatement = "SELECT id, link_to_main_page, link_to_image, name " +
                "FROM public.shops WHERE id IN ()";

        String actualStatement = jdbcShopDao.fillSelectManyByIdsStatement(ids);

        assertEquals(expectedStatement, actualStatement);
    }

}