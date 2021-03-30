package com.vinylteam.vinyl.security.impl;

import com.vinylteam.vinyl.entity.Role;
import com.vinylteam.vinyl.entity.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DefaultSecurityServiceTest {

    private final DefaultSecurityService defaultSecurityService = new DefaultSecurityService();
    private final String rightPassword = "existingUserRightPassword";
    private final String wrongPassword = "existingUserWrongPassword";
    private final String passwordToHash = "password";
    private final byte[] salt = defaultSecurityService.generateSalt();
    private final User existingUser = new User("testuser1@vinyl.com",
            defaultSecurityService.hashPassword(rightPassword.toCharArray(), salt, 10000),
            salt, 10000, Role.USER);

    @Test
    void hashPasswordWithTenThousandIterationsTest() {
        String resultHash = defaultSecurityService.hashPassword(passwordToHash.toCharArray(), salt, 10000);
        assertEquals(resultHash,
                defaultSecurityService.hashPassword(passwordToHash.toCharArray(), salt, 10000));
    }

    @Test
    void hashPasswordWithZeroIterationsTest() {

        assertThrows(IllegalArgumentException.class, () ->
                defaultSecurityService.hashPassword(passwordToHash.toCharArray(), salt, 0));
    }

    @Test
    void checkPasswordAgainstExistingUserPasswordWithRightPasswordTest() {
        assertTrue(defaultSecurityService.checkPasswordAgainstUserPassword(
                existingUser, rightPassword.toCharArray()));
    }

    @Test
    void checkPasswordAgainstUserPasswordWithWrongPasswordTest() {
        assertFalse(defaultSecurityService.checkPasswordAgainstUserPassword(
                existingUser, wrongPassword.toCharArray()));
    }

    @Test
    void checkPasswordAgainstUserPasswordNullUserTest() {
        assertFalse(defaultSecurityService.checkPasswordAgainstUserPassword(
                null, rightPassword.toCharArray()));
    }
}