package com.vinylteam.vinyl.security.impl;

import com.vinylteam.vinyl.entity.Role;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.security.SecurityService;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Random;

public class DefaultSecurityService implements SecurityService {


    @Override
    public String hashPassword(char[] password, byte[] salt, int iterations) {

        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, 256);
            SecretKey key = skf.generateSecret(spec);
            byte[] res = key.getEncoded();
            return Arrays.toString(res);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Exception while hashing password", e);
        }
    }

    @Override
    public byte[] generateSalt() {
        Random r = new SecureRandom();
        byte[] salt = new byte[20];
        r.nextBytes(salt);
        return salt;
    }

    @Override
    public User createUserWithHashedPassword(String email, char[] password) {

        byte[] salt = generateSalt();
        int iterations = 1;
        String hashedPassword = hashPassword(password, salt, iterations);
        Arrays.fill(password, '\u0000');
        return new User(email, hashedPassword, Arrays.toString(salt),
                iterations, Role.USER);
    }
}
