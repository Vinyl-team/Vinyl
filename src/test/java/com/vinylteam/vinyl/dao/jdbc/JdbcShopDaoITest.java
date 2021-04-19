package com.vinylteam.vinyl.dao.jdbc;

import com.vinylteam.vinyl.dao.DBDataSource;
import com.vinylteam.vinyl.entity.Shop;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JdbcShopDaoITest {

    public final JdbcShopDao jdbcShopDao = new JdbcShopDao();
    private final String TRUNCATE_SHOPS = "TRUNCATE shops RESTART IDENTITY CASCADE";
    private final String INSERT_SHOPS = "INSERT INTO shops(id, link_to_main_page, link_to_image, name)" +
            "VALUES(?, ?, ?, ?), (?, ?, ?, ?), (?, ?, ?, ?)";

    private List<Shop> shopList;
    private Connection connection;

    @BeforeAll
    void beforeAll() throws SQLException {
        connection = DBDataSource.getConnection();
        try (Statement truncateShops = connection.createStatement()) {
            truncateShops.executeUpdate(TRUNCATE_SHOPS);
        }
        shopList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Shop shop = new Shop();
            shop.setId(i + 1);
            shop.setName("shop" + (i + 1));
            shop.setMainPageLink(shop.getName() + "/main");
            shop.setImageLink(shop.getName() + "/image.png");
            shopList.add(shop);
        }
    }

    @AfterAll
    void afterAll() throws SQLException {
        try (Statement truncateShops = connection.createStatement()) {
            truncateShops.executeUpdate(TRUNCATE_SHOPS);
        }
        connection.close();
    }

    @BeforeEach
    void beforeEach() throws SQLException {
        try (PreparedStatement insertStatement = connection.prepareStatement(INSERT_SHOPS)) {
            for (int i = 0; i < 3; i++) {
                insertStatement.setInt(1 + i * 4, shopList.get(i).getId());
                insertStatement.setString(2 + i * 4, shopList.get(i).getMainPageLink());
                insertStatement.setString(3 + i * 4, shopList.get(i).getImageLink());
                insertStatement.setString(4 + i * 4, shopList.get(i).getName());
            }
            insertStatement.executeUpdate();
        }
    }

    @AfterEach
    void afterEach() throws SQLException {
        try (Statement truncateShops = connection.createStatement()) {
            truncateShops.executeUpdate(TRUNCATE_SHOPS);
        }
    }

    @Test
    @DisplayName("Gets list of shops with id-s with list of id-s from db")
    void getManyByListOfIds() {
        List<Integer> ids = List.of(1, 2);

        List<Shop> actualShops = jdbcShopDao.getManyByListOfIds(ids);

        assertEquals(2, actualShops.size());
        for (int i = 0; i < 2; i++) {
            assertEquals(shopList.get(i), actualShops.get(i));
        }
    }

    @Test
    @DisplayName("Gets list of shops with id-s with list of id-s where some ids do not exist in db")
    void getManyByListOfIdsWithSomeNonExistentIds() {
        List<Integer> ids = List.of(1, 2, 4);

        List<Shop> actualShops = jdbcShopDao.getManyByListOfIds(ids);

        assertEquals(2, actualShops.size());
        for (int i = 0; i < 2; i++) {
            assertEquals(shopList.get(i), actualShops.get(i));
        }
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
        try (Statement truncateShops = connection.createStatement()) {
            truncateShops.executeUpdate(TRUNCATE_SHOPS);
        }
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