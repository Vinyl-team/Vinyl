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
    private final ListPreparerForTests listPreparer = new ListPreparerForTests();
    private final List<Shop> shops = listPreparer.getShopsList();

    @BeforeAll
    void beforeAll() throws SQLException {
        databasePreparer.truncateCascadeShops();
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
        //prepare
        List<Integer> ids = List.of(1, 2);
        List<Shop> expectedShops = listPreparer.getShopsList();
        expectedShops.remove(2);
        //when
        List<Shop> actualShops = jdbcShopDao.getManyByListOfIds(ids);
        //then
        assertEquals(2, actualShops.size());
        assertEquals(expectedShops, actualShops);
    }

    @Test
    @DisplayName("Gets list of shops with id-s with list of id-s where some ids do not exist in db")
    void getManyByListOfIdsWithSomeNonExistentIds() {
        //prepare
        List<Integer> ids = List.of(1, 2, 4);
        List<Shop> expectedShops = listPreparer.getShopsList();
        expectedShops.remove(2);
        //when
        List<Shop> actualShops = jdbcShopDao.getManyByListOfIds(ids);
        //then
        assertEquals(2, actualShops.size());
        assertEquals(expectedShops, actualShops);
    }

    @Test
    @DisplayName("Gets empty list of shops with empty list of id-s from db")
    void getManyByListOfIdsEmptyList() {
        //prepare
        List<Integer> ids = new ArrayList<>();
        //when
        List<Shop> actualShops = jdbcShopDao.getManyByListOfIds(ids);
        //then
        assertTrue(actualShops.isEmpty());
    }

    @Test
    @DisplayName("Gets empty list of shops with list id-s from empty table")
    void getManyByListOfIdsFromEmptyTable() throws SQLException {
        //prepare
        databasePreparer.truncateCascadeShops();
        List<Integer> ids = List.of(1, 2);
        //when
        List<Shop> actualShops = jdbcShopDao.getManyByListOfIds(ids);
        //then
        assertTrue(actualShops.isEmpty());
    }

    @Test
    @DisplayName("Gets String filled with ids from filled list of ids")
    void fillSelectManyByIdsStatementWithFilledIdListTest() {
        //prepare
        List<Integer> ids = List.of(1, 2);
        String expectedStatement = "SELECT id, link_to_main_page, link_to_image, name " +
                "FROM public.shops WHERE id IN (1, 2)";
        //when
        String actualStatement = jdbcShopDao.fillSelectManyByIdsStatement(ids);
        //then
        assertEquals(expectedStatement, actualStatement);
    }

    @Test
    @DisplayName("Gets String not filled with ids from empty list of ids")
    void fillSelectManyByIdsStatementWithEmptyIdListTest() {
        //prepare
        List<Integer> ids = new ArrayList<>();
        String expectedStatement = "SELECT id, link_to_main_page, link_to_image, name " +
                "FROM public.shops WHERE id IN ()";
        //when
        String actualStatement = jdbcShopDao.fillSelectManyByIdsStatement(ids);
        //then
        assertEquals(expectedStatement, actualStatement);
    }

}