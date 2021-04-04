package com.vinylteam.vinyl.security.impl;

import com.vinylteam.vinyl.entity.Role;
import com.vinylteam.vinyl.entity.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DefaultSecurityServiceTest {

    private final DefaultSecurityService defaultSecurityService = new DefaultSecurityService();
    private final String rightPassword = "existingUserRightPassword";
    private final String wrongPassword = "existingUserWrongPassword";
    private final String passwordToHash = "password";
    private final byte[] salt = defaultSecurityService.generateSalt();
    private final User existingUser = new User();

    @BeforeAll
    void beforeAll() {
        existingUser.setEmail("testuser1@vinyl.com");
        existingUser.setPassword(defaultSecurityService
                .hashPassword(rightPassword.toCharArray(), salt, 10000));
        existingUser.setSalt(Base64.getEncoder().encodeToString(salt));
        existingUser.setIterations(10000);
        existingUser.setRole(Role.USER);
    }

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