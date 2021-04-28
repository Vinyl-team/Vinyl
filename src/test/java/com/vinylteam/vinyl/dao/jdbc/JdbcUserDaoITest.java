package com.vinylteam.vinyl.dao.jdbc;

import com.vinylteam.vinyl.dao.DBDataSource;
import com.vinylteam.vinyl.entity.Role;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.util.DatabasePreparerForITests;
import com.vinylteam.vinyl.util.ListPreparerForTests;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JdbcUserDaoITest {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private Connection connection;
    private final JdbcUserDao jdbcUserDao = new JdbcUserDao();
    private DatabasePreparerForITests databasePreparer;
    private final List<User> users = new ArrayList<>();
    private final ListPreparerForTests listPreparer = new ListPreparerForTests();

    @BeforeAll
    void beforeAll() throws SQLException {
        logger.info("BeforeAll");
        connection = DBDataSource.getConnection();
        databasePreparer = new DatabasePreparerForITests(connection);
        databasePreparer.truncateCascadeUsers();
        listPreparer.fillUsersList(users);
    }

    @AfterAll
    void afterAll() throws SQLException {
        connection.close();
    }

    @BeforeEach
    void beforeEach() throws SQLException {
        databasePreparer.insertUsers(users);
    }

    @AfterEach
    void afterEach() throws SQLException {
        databasePreparer.truncateCascadeUsers();
    }

    @Test
    @DisplayName("Checks the number of rows")
    void countAllFilledTest() {
        assertEquals(2, jdbcUserDao.countAll());
    }

    @Test
    @DisplayName("Checks the number of rows when table is empty")
    void countAllEmptyTest() throws SQLException {
        databasePreparer.truncateCascadeUsers();

        assertEquals(0, jdbcUserDao.countAll());
    }

    @Test
    @DisplayName("Gets an existing user from db")
    void getByExistingEmailTest() {
        Optional<User> optionalUserGottenByExistingEmail;

        optionalUserGottenByExistingEmail = jdbcUserDao.getByEmail("user1@waxdeals.com");

        assertFalse(optionalUserGottenByExistingEmail.isEmpty());
        assertEquals(users.get(0), optionalUserGottenByExistingEmail.get());
    }

    @Test
    @DisplayName("Gets not existing user from db")
    void getByNotExistingEmailTest() {
        Optional<User> optionalUserGottenByNonexistentEmail = jdbcUserDao.getByEmail("user3@waxdeals.com");

        assertFalse(optionalUserGottenByNonexistentEmail.isPresent());
        assertEquals(2, jdbcUserDao.countAll());
    }

    @Test
    @DisplayName("Adds user to db")
    void addNewTest() {
        User expectedUser = new User();
        expectedUser.setEmail("user3@waxdeals.com");
        expectedUser.setPassword("hash3");
        expectedUser.setSalt("salt3");
        expectedUser.setIterations(3);
        expectedUser.setRole(Role.USER);
        expectedUser.setStatus(true);

        assertTrue(jdbcUserDao.add(expectedUser));

        assertEquals(3, jdbcUserDao.countAll());
        Optional<User> optionalAddedUser = jdbcUserDao.getByEmail("user3@waxdeals.com");
        assertFalse(optionalAddedUser.isEmpty());
        assertEquals(expectedUser, optionalAddedUser.get());
    }

    @Test
    @DisplayName("Adds existing user with the same password")
    void addExistingWithSamePasswordTest() {
        assertFalse(jdbcUserDao.add(users.get(0)));

        assertEquals(2, jdbcUserDao.countAll());
    }

    @Test
    @DisplayName("Adds existing user with new password")
    void addExistingWithNewPasswordTest() {
        User user = List.copyOf(users).get(0);
        user.setPassword("hash3");

        assertFalse(jdbcUserDao.add(user));

        assertEquals(2, jdbcUserDao.countAll());
    }

}