package com.vinylteam.vinyl.security.impl;

import com.vinylteam.vinyl.entity.Role;
import com.vinylteam.vinyl.entity.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class DefaultSecurityServiceTest {

    private final DefaultSecurityService defaultSecurityService = new DefaultSecurityService();
    private final char[] passwordToHash = "password".toCharArray();
    private final char[] rightPassword = "existingUserRightPassword".toCharArray();
    private final byte[] salt = defaultSecurityService.generateSalt();
    private final User existingUser = new User("testuser1@vinyl.com",
            defaultSecurityService.hashPassword(rightPassword, salt, 10),
            salt, 10, Role.USER);

@Test
    void hashPasswordWithTenThousandIterationsTest() {
        String resultHash = defaultSecurityService.hashPassword(passwordToHash, salt, 10000);
        assertEquals(resultHash,
                defaultSecurityService.hashPassword(passwordToHash, salt, 10000));
    }

    @Test
    void hashPasswordWithZeroIterationsTest() {

        assertThrows(IllegalArgumentException.class, () ->
        {
            defaultSecurityService.hashPassword(passwordToHash, salt, 0);
        });
    }

    @Test
    void checkPasswordAgainstExistingUserPasswordWithRightPasswordTest() {
        System.out.println(existingUser.getSalt().getBytes().equals(salt));
        assertTrue(defaultSecurityService.checkPasswordAgainstUserPassword(
                existingUser, rightPassword));
    }

    @Test
    void checkPasswordAgainstUserPasswordWithWrongPasswordTest() {
        assertFalse(defaultSecurityService.checkPasswordAgainstUserPassword(
                existingUser, "existingUserWrongPassword".toCharArray()));
    }

    @Test
    void checkPasswordAgainstUserPasswordNullUserTest() {
        assertFalse(defaultSecurityService.checkPasswordAgainstUserPassword(
                null, "existingUserRightPassword".toCharArray()));
    }
}