package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.DBDataSource;
import com.vinylteam.vinyl.dao.jdbc.JdbcUserDao;
import com.vinylteam.vinyl.entity.SignInCheckResult;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.security.impl.DefaultSecurityService;
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
    private final DefaultSecurityService defaultSecurityService = new DefaultSecurityService();
    private final JdbcUserDao jdbcUserDao = new JdbcUserDao();
    private final DefaultUserService defaultUserService = new
            DefaultUserService(jdbcUserDao, defaultSecurityService);

    private User verifiedUser;
    private User notVerifiedUser;

    private final String TRUNCATE_TABLE_RESTART_IDENTITY = "TRUNCATE public.users RESTART IDENTITY";
    private Connection connection;

    @BeforeAll
    void beforeAll() throws SQLException {
        connection = DBDataSource.getConnection();
        try (Statement truncateStatement = connection.createStatement()) {
            truncateStatement.executeUpdate(TRUNCATE_TABLE_RESTART_IDENTITY);
            logger.info("Truncated table before all tests.");
        }
        verifiedUser = defaultSecurityService
                .createUserWithHashedPassword("verifieduser@vinyl.com", "password1".toCharArray());
        verifiedUser.setStatus(true);
        notVerifiedUser = defaultSecurityService
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
        jdbcUserDao.add(verifiedUser);
        jdbcUserDao.add(notVerifiedUser);
    }

    @AfterEach
    void afterEach() throws SQLException {
        try (Statement truncateStatement = connection.createStatement()) {
            truncateStatement.executeUpdate(TRUNCATE_TABLE_RESTART_IDENTITY);
            logger.info("Truncated table users after the method!");
        }
    }

    @Test
    void signInCheckNullEmailTest() {
        assertEquals(SignInCheckResult.FAIL,
                defaultUserService.signInCheck(null, "password"));
    }

    @Test
    void signInCheckNullPasswordTest() {
        assertEquals(SignInCheckResult.FAIL,
                defaultUserService.signInCheck("verifieduser@vinyl.com", null));
    }

    @Test
    void signInCheckNonExistingUserTest() {
        assertEquals(SignInCheckResult.FAIL,
                defaultUserService.signInCheck("testuser3@vinyl.com", "password3"));
    }

    @Test
    void signInCheckExistingVerifiedUserWrongPasswordTest() {
        assertEquals(SignInCheckResult.FAIL,
                defaultUserService.signInCheck("verifieduser@vinyl.com", "wrong password"));
    }

    @Test
    void signInCheckExistingNotVerifiedUserWrongPasswordTest() {
        assertEquals(SignInCheckResult.FAIL,
                defaultUserService.signInCheck("notverifieduser@vinyl.com", "wrong password"));
    }

    @Test
    void signInCheckExistingVerifiedUserRightPasswordTest() {
        assertEquals(SignInCheckResult.OK_VERIFIED,
                defaultUserService.signInCheck("verifieduser@vinyl.com", "password1"));
    }

    @Test
    void signInCheckExistingNotVerifiedUserRightPasswordTest() {
        assertEquals(SignInCheckResult.OK_NOT_VERIFIED,
                defaultUserService.signInCheck("notverifieduser@vinyl.com", "password2"));
    }

    @Test
    void addWithNullEmailTest() {
        assertFalse(defaultUserService.add(null, "password"));
    }

    @Test
    void addWithNullPasswordTest() {
        assertFalse(defaultUserService.add("email@vinyl.com", null));
    }

    @Test
    void addWithExistingEmail() {
        assertFalse(defaultUserService.add("verifieduser@vinyl.com", "password1"));
    }

    @Test
    void addWithNewEmail() {
        assertTrue(defaultUserService.add("newuser@vinyl.com", "password3"));
    }
}