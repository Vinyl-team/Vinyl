package com.vinylteam.vinyl.dao.jdbc;

import com.vinylteam.vinyl.dao.DBDataSource;
import com.vinylteam.vinyl.entity.Role;
import com.vinylteam.vinyl.entity.User;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JdbcUserDaoITest {

    private final DBDataSource dbDataSource = new DBDataSource();
    private final JdbcUserDao jdbcUserDao = dbDataSource.getJDBCUserDAO();

    private final String INSERT_INTO_TABLE = "insert into public.users " +
            "(\"email\", \"password\", salt, \"iterations\", role) " +
            "values (?, ?, ?, ?, ?), (?, ?, ?, ?, ?)";
    private final String TRUNCATE_TABLE_RESTART_IDENTITY = "TRUNCATE public.users RESTART IDENTITY";
    private Connection connection;

    @BeforeAll
    void beforeAll() throws SQLException {
        connection = dbDataSource.getDataSource().getConnection();
    }

    @AfterAll
    void afterAll() throws SQLException {
        connection.close();
    }

    @BeforeEach
    void beforeEach() throws SQLException {
        try (Statement truncateStatement = connection.createStatement()) {

            truncateStatement.executeUpdate(TRUNCATE_TABLE_RESTART_IDENTITY);
            try (PreparedStatement insertStatement = connection
                    .prepareStatement(INSERT_INTO_TABLE)) {

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
        }
    }

    @Test
    void countAllFilledTest() {
        assertEquals(2, jdbcUserDao.countAll());
    }

    @Test
    void countAllEmptyTest() throws SQLException {
        try (Statement truncateStatement = connection.createStatement()) {
            truncateStatement.executeUpdate(TRUNCATE_TABLE_RESTART_IDENTITY);
        }
        assertEquals(0, jdbcUserDao.countAll());
    }

    @Test
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
    void getByNotExistingEmail() {

        User userGottenByNonexistentEmail = jdbcUserDao.getByEmail("testuser3@vinyluserGottenByExistingEmail.com");
        assertNull(userGottenByNonexistentEmail);
        assertEquals(2, jdbcUserDao.countAll());
    }

    @Test
    void getByNullEmail() {
        User userGottenByNullEmail = jdbcUserDao.getByEmail(null);
        assertNull(userGottenByNullEmail);
        assertEquals(2, jdbcUserDao.countAll());
    }

    @Test
    void addNewTest() {
        assertTrue(jdbcUserDao.add(new User("newtestuser3@vinyl.com",
                "HASH3", "", 0, Role.USER)));

        assertEquals(3, jdbcUserDao.countAll());
        User addedUser = jdbcUserDao.getByEmail("newtestuser3@vinyl.com");

        assertEquals("newtestuser3@vinyl.com", addedUser.getEmail());
        assertEquals("HASH3", addedUser.getPassword());
        assertEquals("", addedUser.getSalt());
        assertEquals(0, addedUser.getIterations());
        assertEquals(Role.USER, addedUser.getRole());
    }

    @Test
    void addExistingWithSamePasswordTest() {
        assertFalse(jdbcUserDao.add(new User("testuser2@vinyl.com",
                "HASH2", "", 0, Role.USER)));
        assertEquals(2, jdbcUserDao.countAll());
    }

    @Test
    void addExistingWithNewPasswordTest() {
        assertFalse(jdbcUserDao.add(new User("testuser2@vinyl.com",
                "HASH3", "", 0, Role.USER)));
        assertEquals(2, jdbcUserDao.countAll());
    }

    @Test
    void addWithNullEmailTest() {
        assertFalse(jdbcUserDao.add(new User(null,
                "HASH2", "", 0, Role.USER)));
        assertEquals(2, jdbcUserDao.countAll());
    }

}