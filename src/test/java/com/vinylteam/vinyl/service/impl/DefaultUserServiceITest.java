package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.DBDataSource;
import com.vinylteam.vinyl.dao.UserDao;
import com.vinylteam.vinyl.dao.jdbc.JdbcUserDao;
import com.vinylteam.vinyl.entity.SignInCheckResult;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.security.SecurityService;
import com.vinylteam.vinyl.security.impl.DefaultSecurityService;
import com.vinylteam.vinyl.service.UserService;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DefaultUserServiceITest {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final SecurityService securityService = new DefaultSecurityService();
    private final UserDao userDao = new JdbcUserDao();
    private final UserService userService = new
            DefaultUserService(userDao, securityService);

    private User verifiedUser;
    private User notVerifiedUser;

    private final String TRUNCATE_TABLE_RESTART_IDENTITY = "TRUNCATE public.users RESTART IDENTITY CASCADE";
    private Connection connection;

    @BeforeAll
    void beforeAll() throws SQLException {
        connection = DBDataSource.getConnection();
        try (Statement truncateStatement = connection.createStatement()) {
            truncateStatement.executeUpdate(TRUNCATE_TABLE_RESTART_IDENTITY);
            logger.info("Truncated table before all tests.");
        }
        verifiedUser = securityService
                .createUserWithHashedPassword("verifieduser@vinyl.com", "password1".toCharArray());
        verifiedUser.setStatus(true);
        notVerifiedUser = securityService
                .createUserWithHashedPassword("notverifieduser@vinyl.com", "password2".toCharArray());
    }

    @AfterAll
    void afterAll() throws SQLException {
        connection.close();
    }

    @BeforeEach
    void beforeEach() {
        logger.info("Adding one verified and one not verified user" +
                " to table users before the method.");
        userDao.add(verifiedUser);
        userDao.add(notVerifiedUser);
    }

    @AfterEach
    void afterEach() throws SQLException {
        try (Statement truncateStatement = connection.createStatement()) {
            truncateStatement.executeUpdate(TRUNCATE_TABLE_RESTART_IDENTITY);
            logger.info("Truncated table users after the method!");
        }
    }

    @Test
    @DisplayName("Checks if .signInCheck(...) with null email as an argument returns SignInCheckResult.FAIL.")
    void signInCheckNullEmailTest() {
        assertEquals(SignInCheckResult.FAIL,
                userService.signInCheck(null, "password"));
    }

    @Test
    @DisplayName("Checks if .signInCheck(...) with null password as an argument returns SignInCheckResult.FAIL.")
    void signInCheckNullPasswordTest() {
        assertEquals(SignInCheckResult.FAIL,
                userService.signInCheck("verifieduser@vinyl.com", null));
    }

    @Test
    @DisplayName("Checks if .signInCheck(...) with non-existent user's email and password as arguments returns SignInCheckResult.FAIL.")
    void signInCheckNonExistingUserTest() {
        assertEquals(SignInCheckResult.FAIL,
                userService.signInCheck("testuser3@vinyl.com", "password3"));
    }

    @Test
    @DisplayName("Checks if .signInCheck(...) with verified user's email and wrong password as arguments" +
            " returns SignInCheckResult.FAIL.")
    void signInCheckExistingVerifiedUserWrongPasswordTest() {
        assertEquals(SignInCheckResult.FAIL,
                userService.signInCheck("verifieduser@vinyl.com", "wrong password"));
    }

    @Test
    @DisplayName("Checks if .signInCheck(...) with not-verified user's email and wrong password as arguments" +
            " returns SignInCheckResult.FAIL.")
    void signInCheckExistingNotVerifiedUserWrongPasswordTest() {
        assertEquals(SignInCheckResult.FAIL,
                userService.signInCheck("notverifieduser@vinyl.com", "wrong password"));
    }

    @Test
    @DisplayName("Checks if .signInCheck(...) with verified user's email and right password as arguments" +
            " returns SignInCheckResult.OK_VERIFIED.")
    void signInCheckExistingVerifiedUserRightPasswordTest() {
        assertEquals(SignInCheckResult.OK_VERIFIED,
                userService.signInCheck("verifieduser@vinyl.com", "password1"));
    }

    @Test
    @DisplayName("Checks if .signInCheck(...) with not-verified user's email and right password as arguments" +
            " returns SignInCheckResult.OK_NOT_VERIFIED.")
    void signInCheckExistingNotVerifiedUserRightPasswordTest() {
        assertEquals(SignInCheckResult.OK_NOT_VERIFIED,
                userService.signInCheck("notverifieduser@vinyl.com", "password2"));
    }

    @Test
    @DisplayName("Checks if .add(...) with null email as an argument returns falseL.")
    void addWithNullEmailTest() {
        assertFalse(userService.add(null, "password"));
    }

    @Test
    @DisplayName("Checks if .add(...) with null password as an argument returns false.")
    void addWithNullPasswordTest() {
        assertFalse(userService.add("email@vinyl.com", null));
    }

    @Test
    @DisplayName("Checks if .add(...) with already existing in database user's email and password as arguments returns false.")
    void addWithExistingEmail() {
        assertFalse(userService.add("verifieduser@vinyl.com", "password1"));
    }

    @Test
    @DisplayName("Checks if .add(...) with not existing in database user's email and password as arguments returns true.")
    void addWithNewEmail() {
        assertTrue(userService.add("newuser@vinyl.com", "password3"));
    }

}