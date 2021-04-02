package com.vinylteam.vinyl.dao.jdbc;

import com.vinylteam.vinyl.dao.DBDataSource;
import com.vinylteam.vinyl.entity.Role;
import com.vinylteam.vinyl.entity.User;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JdbcUserDaoITest {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final JdbcUserDao jdbcUserDao = new JdbcUserDao();

    private final String INSERT_INTO_TABLE = "INSERT INTO public.users " +
            "(email, password, salt, iterations, role, status) " +
            "VALUES (?, ?, ?, ?, ?, ?), (?, ?, ?, ?, ?, ?)";
    private final String TRUNCATE_TABLE_RESTART_IDENTITY = "TRUNCATE public.users RESTART IDENTITY";
    private Connection connection;

    @BeforeAll
    void beforeAll() throws SQLException {
        connection = DBDataSource.getConnection();
        try (Statement truncateStatement = connection.createStatement()) {
            truncateStatement.executeUpdate(TRUNCATE_TABLE_RESTART_IDENTITY);
            logger.info("Truncated table before all tests.");
        }
    }

    @AfterAll
    void afterAll() throws SQLException {
        connection.close();
    }

    @BeforeEach
    void beforeEach() throws SQLException {
        try (PreparedStatement insertStatement = connection
                .prepareStatement(INSERT_INTO_TABLE)) {
            logger.info("Adding two rows to table users before the method!");
            insertStatement.setString(1, "testuser1@vinyl.com");
            insertStatement.setString(2, "HASH1");
            insertStatement.setString(3, "");
            insertStatement.setInt(4, 0);
            insertStatement.setString(5, Role.USER.toString());
            insertStatement.setBoolean(6, true);
            insertStatement.setString(7, "testuser2@vinyl.com");
            insertStatement.setString(8, "HASH2");
            insertStatement.setString(9, "");
            insertStatement.setInt(10, 0);
            insertStatement.setString(11, Role.USER.toString());
            insertStatement.setBoolean(12, true);
            insertStatement.executeUpdate();
        }
    }

    @AfterEach
    void afterEach() throws SQLException {
        try (Statement truncateStatement = connection.createStatement()) {
            truncateStatement.executeUpdate(TRUNCATE_TABLE_RESTART_IDENTITY);
            logger.info("Truncated table users after the method!");
        }
    }

    @Test
    @DisplayName("Checks the number of rows")
    void countAllFilledTest() {
        logger.info("countAllFilledTest()");
        assertEquals(2, jdbcUserDao.countAll());
    }

    @Test
    @DisplayName("Checks the number of rows when table is empty")
    void countAllEmptyTest() throws SQLException {
        logger.info("countAllEmptyTest()");
        try (Statement truncateStatement = connection.createStatement()) {
            truncateStatement.executeUpdate(TRUNCATE_TABLE_RESTART_IDENTITY);
        }
        assertEquals(0, jdbcUserDao.countAll());
    }

    @Test
    @DisplayName("Gets an existing user from db")
    void getByExistingEmail() {
        logger.info("getByExistingEmail()");
        Optional<User> optionalUserGottenByExistingEmail;
        optionalUserGottenByExistingEmail = jdbcUserDao.getByEmail("testuser1@vinyl.com");

        assertFalse(optionalUserGottenByExistingEmail.isEmpty());
        assertEquals("testuser1@vinyl.com", optionalUserGottenByExistingEmail.get().getEmail());
        assertEquals("HASH1", optionalUserGottenByExistingEmail.get().getPassword());
        assertEquals("", optionalUserGottenByExistingEmail.get().getSalt());
        assertEquals(0, optionalUserGottenByExistingEmail.get().getIterations());
        assertEquals(Role.USER, optionalUserGottenByExistingEmail.get().getRole());
        assertEquals(2, jdbcUserDao.countAll());
    }

    @Test
    @DisplayName("Gets not existing user from db")
    void getByNotExistingEmail() {
        logger.info("getByNotExistingEmail()");
        Optional<User> optionalUserGottenByNonexistentEmail = jdbcUserDao.getByEmail("testuser3@vinyluserGottenByExistingEmail.com");
        assertFalse(optionalUserGottenByNonexistentEmail.isPresent());
        assertEquals(2, jdbcUserDao.countAll());
    }

    @Test
    @DisplayName("Adds user to db")
    void addNewTest() {
        logger.info("addNewTest()");
        User user = new User();
        user.setEmail("testuser3@vinyl.com");
        user.setPassword("HASH3");
        user.setSalt("");
        user.setIterations(0);
        user.setRole(Role.USER);
        user.setStatus(true);

        assertTrue(jdbcUserDao.add(user));

        assertEquals(3, jdbcUserDao.countAll());
        Optional<User> optionalAddedUser = jdbcUserDao.getByEmail("testuser3@vinyl.com");

        assertFalse(optionalAddedUser.isEmpty());
        assertEquals("testuser3@vinyl.com", optionalAddedUser.get().getEmail());
        assertEquals("HASH3", optionalAddedUser.get().getPassword());
        assertEquals("", optionalAddedUser.get().getSalt());
        assertEquals(0, optionalAddedUser.get().getIterations());
        assertEquals(Role.USER, optionalAddedUser.get().getRole());
        assertEquals(true, optionalAddedUser.get().getStatus());
    }

    @Test
    @DisplayName("Adds existing user with the same password")
    void addExistingWithSamePasswordTest() {
        logger.info("addExistingWithSamePasswordTest()");
        User user = new User();
        user.setEmail("testuser2@vinyl.com");
        user.setPassword("HASH2");
        user.setSalt("");
        user.setIterations(0);
        user.setRole(Role.USER);
        user.setStatus(true);
        assertFalse(jdbcUserDao.add(user));
        assertEquals(2, jdbcUserDao.countAll());
    }

    @Test
    @DisplayName("Adds existing user with new password")
    void addExistingWithNewPasswordTest() {
        logger.info("addExistingWithNewPasswordTest");
        User user = new User();
        user.setEmail("testuser2@vinyl.com");
        user.setPassword("HASH3");
        user.setSalt("");
        user.setIterations(0);
        user.setRole(Role.USER);
        user.setStatus(true);

        assertFalse(jdbcUserDao.add(user));
        assertEquals(2, jdbcUserDao.countAll());
    }

}