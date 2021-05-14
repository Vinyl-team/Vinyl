package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.UserDao;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.security.SecurityService;
import com.vinylteam.vinyl.security.impl.DefaultSecurityService;
import com.vinylteam.vinyl.service.UserService;
import com.vinylteam.vinyl.util.DataGeneratorForTests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DefaultUserServiceTest {

    private final DataGeneratorForTests dataGenerator = new DataGeneratorForTests();
    private final UserDao mockedUserDao = mock(UserDao.class);
    private final SecurityService mockedSecurityService = mock(DefaultSecurityService.class);
    private final UserService userService = new DefaultUserService(mockedUserDao, mockedSecurityService);
    private final List<User> users = dataGenerator.getUsersList();

    @BeforeEach
    void beforeEach() {
        reset(mockedUserDao);
        reset(mockedSecurityService);
    }

//    @Test
//    @DisplayName("Checks if .add(...) with null email returns false, securityService.createUserWithHashedPassword(...), userDao.add(...) aren't called")
//    void addWithNullEmailTest() {
//        //prepare
//        String password = "password2";
//        //when
//        boolean actualIsAdded = userService.add(null, password);
//        //then
//        assertFalse(actualIsAdded);
//        verify(mockedSecurityService, never()).createUserWithHashedPassword(eq(null), eq(password.toCharArray()));
//        verify(mockedUserDao, never()).add(any());
//    }

//    @Test
//    @DisplayName("Checks if .add(...) with null password returns false, securityService.createUserWithHashedPassword(...), userDao.add(...) aren't called")
//    void addWithNullPasswordTest() {
//        //prepare
//        String email = "user2@waxdeals.com";
//        //when
//        boolean actualIsAdded = userService.add(email, null);
//        //then
//        assertFalse(actualIsAdded);
//        verify(mockedSecurityService, never()).createUserWithHashedPassword(eq(email), any());
//        verify(mockedUserDao, never()).add(any());
//    }

//    @Test
//    @DisplayName("Checks if .add(...) with already existing in database user's email and password returns false," +
//            " securityService.createUserWithHashedPassword(...), userDao.add(...) are called")
//    void addWithExistingEmailTest() {
//        //prepare
//        String existingEmail = "user1@waxdeals.com";
//        String password = "password1";
//        when(mockedSecurityService.createUserWithHashedPassword(eq(existingEmail), eq(password.toCharArray()))).thenReturn(users.get(0));
//        when(mockedUserDao.add(users.get(0))).thenReturn(false);
//        //when
//        boolean actualIsAdded = userService.add(existingEmail, password);
//        //then
//        assertFalse(actualIsAdded);
//        verify(mockedSecurityService).createUserWithHashedPassword(eq(existingEmail), eq(password.toCharArray()));
//        verify(mockedUserDao).add(users.get(0));
//    }

//    @Test
//    @DisplayName("Checks if .add(...) with not existing in database user's email and password returns true," +
//            " securityService.createUserWithHashedPassword(...), userDao.add(...) are called")
//    void addWithNewEmail() {
//        //prepare
//        String newEmail = "user2@waxdeals.com";
//        String password = "password2";
//        when(mockedSecurityService.createUserWithHashedPassword(eq(newEmail), eq(password.toCharArray()))).thenReturn(users.get(1));
//        when(mockedUserDao.add(users.get(1))).thenReturn(true);
//        //when
//        boolean actualIsAdded = userService.add(newEmail, password);
//        //then
//        assertTrue(actualIsAdded);
//        verify(mockedSecurityService).createUserWithHashedPassword(eq(newEmail), eq(password.toCharArray()));
//        verify(mockedUserDao).add(users.get(1));
//    }

    @Test
    @DisplayName("Checks if .getByEmail(...) calls userDao.getByEmail() when email is not null and returns it's result")
    void getByEmailTest() {
        //prepare
        String email = "user1@waxdeals.com";
        Optional optionalUserFromDB = Optional.of(users.get(0));
        when(mockedUserDao.getByEmail(email)).thenReturn(optionalUserFromDB);
        //when
        Optional actualOptional = userService.getByEmail(email);
        //then
        assertEquals(optionalUserFromDB, actualOptional);
        verify(mockedUserDao).getByEmail(email);
    }

    @Test
    @DisplayName("Checks if .getByEmail(...) doesn't call userDao.getByEmail() when email is null and returns empty optional")
    void getByEmailNullEmailTest() {
        //when
        Optional actualOptional = userService.getByEmail(null);
        //then
        assertTrue(actualOptional.isEmpty());
        verify(mockedUserDao, never()).getByEmail(null);
    }

    @Test
    @DisplayName("Checks if .signInCheck(...) with null email returns Optional.empty()," +
            " userDao.getByEmail(...) and securityService.checkPasswordAgainstUserPassword(...) aren't called")
    void signInCheckNullEmailTest() {
        //prepare
        String password = "password1";
        //when
        Optional<User> actualOptional = userService.signInCheck(null, password);
        //then
        assertTrue(actualOptional.isEmpty());
        verify(mockedUserDao, never()).getByEmail(null);
        verify(mockedSecurityService, never()).checkPasswordAgainstUserPassword(any(), eq(password.toCharArray()));
    }

    @Test
    @DisplayName("Checks if .signInCheck(...) with null password returns Optional.empty()," +
            " userDao.getByEmail(...) and securityService.checkPasswordAgainstUserPassword(...) aren't called")
    void signInCheckNullPasswordTest() {
        //prepare
        String email = "user1@waxdeals.com";
        //when
        Optional<User> actualOptional = userService.signInCheck(email, null);
        //then
        assertTrue(actualOptional.isEmpty());
        verify(mockedUserDao, never()).getByEmail(email);
        verify(mockedSecurityService, never()).checkPasswordAgainstUserPassword(any(), eq(null));

    }

    @Test
    @DisplayName("Checks if .signInCheck(...) with non-existent user's email and password returns Optional.empty()," +
            " userDao.getByEmail(...) is called and securityService.checkPasswordAgainstUserPassword(...) isn't called")
    void signInCheckNonExistingUserTest() {
        //prepare
        String newEmail = "user2@waxdeals.com";
        String newPassword = "password2";
        when(mockedUserDao.getByEmail(newEmail)).thenReturn(Optional.empty());
        //when
        Optional<User> actualOptional = userService.signInCheck(newEmail, newPassword);
        //then
        assertTrue(actualOptional.isEmpty());
        verify(mockedUserDao).getByEmail(newEmail);
        verify(mockedSecurityService, never()).checkPasswordAgainstUserPassword(eq(null), eq(newPassword.toCharArray()));
    }

    @Test
    @DisplayName("Checks if .signInCheck(...) with existent user's email and wrong password as arguments returns Optional.empty()," +
            " userDao.getByEmail(...) and securityService.checkPasswordAgainstUserPassword(...) are called")
    void signInCheckExistingVerifiedUserWrongPasswordTest() {
        //prepare
        String existingEmail = "user1@waxdeals.com";
        String wrongPassword = "password3";
        Optional<User> optionalUserFromDB = Optional.of(users.get(0));
        when(mockedUserDao.getByEmail(existingEmail)).thenReturn(optionalUserFromDB);
        when(mockedSecurityService.checkPasswordAgainstUserPassword(eq(optionalUserFromDB.get()), eq(wrongPassword.toCharArray()))).thenReturn(false);
        //when
        Optional<User> actualOptional = userService.signInCheck(existingEmail, wrongPassword);
        //then
        assertTrue(actualOptional.isEmpty());
        verify(mockedUserDao).getByEmail(existingEmail);
        verify(mockedSecurityService).checkPasswordAgainstUserPassword(eq(optionalUserFromDB.get()), eq(wrongPassword.toCharArray()));
    }

    @Test
    @DisplayName("Checks if .signInCheck(...) with existent user's email and right password returns optional with user," +
            " userDao.getByEmail(...) and securityService.checkPasswordAgainstUserPassword(...) are called")
    void signInCheckExistingVerifiedUserRightPasswordTest() {
        //prepare
        String existingEmail = "user1@waxdeals.com";
        String rightPassword = "password1";
        Optional<User> optionalUserFromDB = Optional.of(users.get(0));
        when(mockedUserDao.getByEmail(existingEmail)).thenReturn(optionalUserFromDB);
        when(mockedSecurityService.checkPasswordAgainstUserPassword(eq(optionalUserFromDB.get()), eq(rightPassword.toCharArray()))).thenReturn(true);
        //when
        Optional<User> actualOptional = userService.signInCheck("user1@waxdeals.com", "password1");
        //then
        assertEquals(optionalUserFromDB, actualOptional);
        verify(mockedUserDao).getByEmail(existingEmail);
        verify(mockedSecurityService).checkPasswordAgainstUserPassword(eq(optionalUserFromDB.get()), eq(rightPassword.toCharArray()));
    }

    @Test
    @DisplayName("Checks if edit(...) with null old email as an argument")
    void editWhenOldEmailIsNullTest() {
        //prepare
        String newVerifiedUser = "newVerifieduser@vinyl.com";
        String password = "newPassword";
        String discogsUserName = "discogsUserName";
        //when
        boolean isEdit = userService.edit(null, newVerifiedUser, password, discogsUserName);
        //then
        assertFalse(isEdit);
    }

    @Test
    @DisplayName("Checks if edit(...) with null newEmail as an argument")
    void editWhenEmailIsNullTest() {
        //prepare
        String oldEmail = "oldEmail";
        String password = "newPassword";
        String discogsUserName = "discogsUserName";
        //when
        boolean isEdit = userService.edit(oldEmail, null, password, discogsUserName);
        //then
        assertFalse(isEdit);
    }

    @Test
    @DisplayName("Checks if edit(...) with null newPassword as an argument")
    void editWhenNewPasswordIsNullTest() {
        //prepare
        String oldEmail = "oldEmail";
        String newVerifiedUser = "newVerifiedUser";
        String discogsUserName = "discogsUserName";
        //when
        boolean isEdit = userService.edit(oldEmail, newVerifiedUser, null, discogsUserName);
        //then
        assertFalse(isEdit);
    }

//    @Test
//    @DisplayName("Checks edit(...) with a non-existent user")
//    void editWhenUserIsNotExistTest() {
//        //prepare
//        String notExistUser = "notExistUser";
//        String newUser = "newUser";
//        String newPassword = "newPassword";
//        String discogsUserName = "discogsUserName";
//        User userFromDB = new User();
//        when(mockedSecurityService.createUserWithHashedPassword(newUser, newPassword.toCharArray(), discogsUserName)).thenReturn(userFromDB);
//        //when
//        assertFalse(userService.edit("non-existent-user@vinyl.com", "newVerifieduser@vinyl.com", "newPassword", "discogsUserName"));
//
//        //then
//
//    }
//
//    @Test
//    @DisplayName("Checks edit(...) with an existing user")
//    void editWhenUserIsExistTest() {
//        assertTrue(userService.edit("verifieduser@vinyl.com", "newVerifieduser@vinyl.com", "newPassword", "discogsUserName"));
//    }
//
//    @Test
//    @DisplayName("Checks edit(...) when only password was changed")
//    void editWhenOnlyPasswordWasChangedTest() {
//        assertTrue(userService.edit("verifieduser@vinyl.com", "verifieduser@vinyl.com", "newPassword", "discogsUserName"));
//        assertTrue(userDao.getByEmail("verifieduser@vinyl.com").orElse(new User()).getStatus());
//    }

}
