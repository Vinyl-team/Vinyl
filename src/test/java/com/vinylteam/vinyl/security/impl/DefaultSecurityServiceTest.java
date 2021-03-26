package com.vinylteam.vinyl.security.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DefaultSecurityServiceTest {

    private final DefaultSecurityService defaultSecurityService = new DefaultSecurityService();
    private final char[] passwordToHash = "password".toCharArray();
    private final byte[] salt = defaultSecurityService.generateSalt();

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
}