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
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JdbcUserDaoITest {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DBDataSource dbDataSource = new DBDataSource();
    private final JdbcUserDao jdbcUserDao = dbDataSource.getJDBCUserDAO();

    private final String INSERT_INTO_TABLE = "insert into public.users " +
            "(email, password, salt, iterations, role) " +
            "values (?, ?, ?, ?, ?), (?, ?, ?, ?, ?)";
    private final String TRUNCATE_TABLE_RESTART_IDENTITY = "TRUNCATE public.users RESTART IDENTITY";
    private Connection connection;

    @BeforeAll
    void beforeAll() throws SQLException {
        logger.info("DB name: {}", dbDataSource.getDataSource().getDatabaseName());
        connection = dbDataSource.getDataSource().getConnection();
        try (Statement truncateStatement = connection.createStatement()) {
            truncateStatement.executeUpdate(TRUNCATE_TABLE_RESTART_IDENTITY);
        }
    }

    @AfterAll
    void afterAll() throws SQLException {
        connection.close();
    }

    @BeforeEach
    void beforeEach() throws SQLException {
/*        try (Statement truncateStatement = connection.createStatement()) {
            logger.info("DB name {}", dbDataSource.getDataSource().getDatabaseName());
            truncateStatement.executeUpdate(TRUNCATE_TABLE_RESTART_IDENTITY);*/
            try (PreparedStatement insertStatement = connection
                    .prepareStatement(INSERT_INTO_TABLE)) {
                logger.info("I am adding two rows to table users before the method!");
                insertStatement.setString(1, "testuser1@vinyl.com");
                insertStatement.setString(2, "HASH1");
                insertStatement.setString(3, "");
                insertStatement.setInt(4, 0);
                insertStatement.setString(5, Role.USER.toString());
                insertStatement.setString(6, "testuser2@vinyl.com");
                insertStatement.setString(7, "HASH2");
                insertStatement.setString(8, "");
                insertStatement.setInt(9, 0);
                insertStatement.setString(10, Role.USER.toString());
                insertStatement.executeUpdate();
            }
        //}
    }

    @AfterEach
    void afterEach() throws SQLException {
        logger.info("I am cleaning table users after the method!");
        try (Statement truncateStatement = connection.createStatement()) {
            truncateStatement.executeUpdate(TRUNCATE_TABLE_RESTART_IDENTITY);
        }
    }

    @Test
    @DisplayName("Checks the number of rows")
    void countAllFilledTest() {
        assertEquals(2, jdbcUserDao.countAll());
    }

    @Test
    @DisplayName("Checks the number of rows if table is empty")
    void countAllEmptyTest() throws SQLException {
        try (Statement truncateStatement = connection.createStatement()) {
            truncateStatement.executeUpdate(TRUNCATE_TABLE_RESTART_IDENTITY);
        }
        assertEquals(0, jdbcUserDao.countAll());
    }

    @Test
    @DisplayName("Gets an existing user from db")
    void getByExistingEmail() {
        User userGottenByExistingEmail;
        userGottenByExistingEmail = jdbcUserDao.getByEmail("testuser1@vinyl.com");

        assertEquals("testuser1@vinyl.com", userGottenByExistingEmail.getEmail());
        assertEquals("HASH1", userGottenByExistingEmail.getPassword());
        assertEquals("", userGottenByExistingEmail.getSalt());
        assertEquals(0, userGottenByExistingEmail.getIterations());
        assertEquals(Role.USER, userGottenByExistingEmail.getRole());
        assertEquals(2, jdbcUserDao.countAll());
    }

    @Test
    @DisplayName("Gets not existing user from db")
    void getByNotExistingEmail() {
        User userGottenByNonexistentEmail = jdbcUserDao.getByEmail("testuser3@vinyluserGottenByExistingEmail.com");
        assertNull(userGottenByNonexistentEmail);
        assertEquals(2, jdbcUserDao.countAll());
    }
    // FIXME
    @Test
    @DisplayName("Gets user by null email from db")
    void getByNullEmail() {
        User userGottenByNullEmail = jdbcUserDao.getByEmail(null);
        assertNull(userGottenByNullEmail);
        assertEquals(2, jdbcUserDao.countAll());
    }

    @Test
    @DisplayName("Adds user to db")
    void addUserTest() {
        //when
        assertTrue(jdbcUserDao.add(new User("newtestuser3@vinyl.com",
                "HASH3", Base64.getDecoder().decode(""), 0, Role.USER)));

        //then
        assertEquals(3, jdbcUserDao.countAll());
        User addedUser = jdbcUserDao.getByEmail("newtestuser3@vinyl.com");

        assertEquals("newtestuser3@vinyl.com", addedUser.getEmail());
        assertEquals("HASH3", addedUser.getPassword());
        assertEquals("", addedUser.getSalt());
        assertEquals(0, addedUser.getIterations());
        assertEquals(Role.USER, addedUser.getRole());
    }

    @Test
    @DisplayName("Adds existing user with the same password")
    void addExistingWithSamePasswordTest() {
        assertFalse(jdbcUserDao.add(new User("testuser2@vinyl.com",
                "HASH2", Base64.getDecoder().decode(""), 0, Role.USER)));
        assertEquals(2, jdbcUserDao.countAll());
    }

    @Test
    @DisplayName("Adds existing user with new password")
    void addExistingWithNewPasswordTest() {
        assertFalse(jdbcUserDao.add(new User("testuser2@vinyl.com",
                "HASH3", Base64.getDecoder().decode(""), 0, Role.USER)));
        assertEquals(2, jdbcUserDao.countAll());
    }

    // FIXME
    @Test
    @DisplayName("Adds user with the null email")
    void addWithNullEmailTest() {
        assertFalse(jdbcUserDao.add(new User(null,
                "HASH2", Base64.getDecoder().decode(""), 0, Role.USER)));
        assertEquals(2, jdbcUserDao.countAll());
    }

}