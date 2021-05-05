package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.DBDataSource;
import com.vinylteam.vinyl.dao.UserDao;
import com.vinylteam.vinyl.dao.jdbc.JdbcUserDao;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DefaultUserServiceITest {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final SecurityService securityService = new DefaultSecurityService();
    private final UserDao userDao = new JdbcUserDao();
    private final UserService userService = new
            DefaultUserService(userDao, securityService);
    private final UserDao mockedUserDao = mock(UserDao.class);
    private final User mockedUser = mock(User.class);
    private final SecurityService mockedSecurityService = mock(DefaultSecurityService.class);
    private final UserService mockedUserService = new
            DefaultUserService(mockedUserDao, mockedSecurityService);

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
    @DisplayName("Checks if .signInCheck(...) with null email as an argument returns Optional.empty().")
    void signInCheckNullEmailTest() {
        assertEquals(Optional.empty(),
                mockedUserService.signInCheck(null, "password"));
    }

    @Test
    @DisplayName("Checks if .signInCheck(...) with null password as an argument returns Optional.empty().")
    void signInCheckNullPasswordTest() {
        assertEquals(Optional.empty(),
                mockedUserService.signInCheck("verifieduser@vinyl.com", null));
    }

    @Test
    @DisplayName("Checks if .signInCheck(...) with non-existent user's email and password as arguments returns Optional.empty().")
    void signInCheckNonExistingUserTest() {
//        prepare
        when(mockedUserDao.getByEmail("testuser3@vinyl.com")).thenReturn(Optional.empty());
        when(mockedSecurityService.checkPasswordAgainstUserPassword(Optional.of(mockedUser).get(), "password3".toCharArray())).thenReturn(false);
//        when
        mockedUserService.signInCheck("testuser3@vinyl.com", "password3");
//        then
        verify(mockedUserDao).getByEmail("testuser3@vinyl.com");
        assertEquals(Optional.empty(),
                mockedUserService.signInCheck("testuser3@vinyl.com", "password3"));
    }

    @Test
    @DisplayName("Checks if .signInCheck(...) with existent user's email and wrong password as arguments" +
            " returns Optional.empty().")
    void signInCheckExistingVerifiedUserWrongPasswordTest() {
        //prepare
        when(mockedUserDao.getByEmail("verifieduser@vinyl.com")).thenReturn(Optional.of(mockedUser));
        when(mockedSecurityService.checkPasswordAgainstUserPassword(Optional.of(mockedUser).get(), "password3".toCharArray())).thenReturn(false);
        //when
        mockedUserService.signInCheck("verifieduser@vinyl.com", "password3");
        //then
        verify(mockedUserDao).getByEmail("verifieduser@vinyl.com");
        assertEquals(Optional.empty(),
                mockedUserService.signInCheck("verifieduser@vinyl.com", "wrong password"));
    }

    @Test
    @DisplayName("Checks if .signInCheck(...) with existent user's email and right password as arguments" +
            " returns verified user.")
    void signInCheckExistingVerifiedUserRightPasswordTest() {
        //prepare
        when(mockedUserDao.getByEmail("verifieduser@vinyl.com")).thenReturn(Optional.of(mockedUser));
        when(mockedSecurityService.checkPasswordAgainstUserPassword(Optional.of(mockedUser).get(), "password3".toCharArray())).thenReturn(true);
        //when
        mockedUserService.signInCheck("verifieduser@vinyl.com", "password3");
        //then
        verify(mockedUserDao, times(3)).getByEmail("verifieduser@vinyl.com");
        assertEquals(Optional.of(mockedUser),
                mockedUserService.signInCheck("verifieduser@vinyl.com", "password3"));
    }

    @Test
    @DisplayName("Checks if .add(...) with null email as an argument returns false.")
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
    void addWithExistingEmailTest() {
        assertFalse(userService.add("verifieduser@vinyl.com", "password1"));
    }

    @Test
    @DisplayName("Checks if .add(...) with not existing in database user's email and password as arguments returns true.")
    void addWithNewEmailTest() {
        assertTrue(userService.add("newuser@vinyl.com", "password3"));
    }

    @Test
    @DisplayName("Checks if edit(...) with null old email as an argument")
    void editWhenOldEmailIsNullTest() {
        assertFalse(userService.edit(null, "newVerifieduser@vinyl.com", "newPassword"));
    }

    @Test
    @DisplayName("Checks if edit(...) with null newEmail as an argument")
    void editWhenEmailIsNullTest() {
        assertFalse(userService.edit("verifieduser@vinyl.com", null, "newPassword"));
    }

    @Test
    @DisplayName("Checks if edit(...) with null newPassword as an argument")
    void editWhenNewPasswordIsNullTest() {
        assertFalse(userService.edit("verifieduser@vinyl.com", "newVerifieduser@vinyl.com", null));
    }

    @Test
    @DisplayName("Checks edit(...) with a non-existent user")
    void editWhenUserIsNotExistTest() {
        assertFalse(userService.edit("non-existent-user@vinyl.com", "newVerifieduser@vinyl.com", "newPassword"));
    }

    @Test
    @DisplayName("Checks edit(...) with an existing user")
    void editWhenUserIsExistTest() {
        assertTrue(userService.edit("verifieduser@vinyl.com", "newVerifieduser@vinyl.com", "newPassword"));
    }

    @Test
    @DisplayName("Checks edit(...) when only password was changed")
    void editWhenOnlyPasswordWasChangedTest() {
        assertTrue(userService.edit("verifieduser@vinyl.com", "verifieduser@vinyl.com", "newPassword"));
        assertTrue(userDao.getByEmail("verifieduser@vinyl.com").orElse(new User()).getStatus());
    }

}