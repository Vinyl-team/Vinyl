package com.vinylteam.vinyl.security.impl;

import com.vinylteam.vinyl.entity.Role;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.security.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

public class DefaultSecurityService implements SecurityService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Random random = new SecureRandom();
    private final SecretKeyFactory secretKeyFactory;

    {
        try {
            secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        } catch (NoSuchAlgorithmException e) {
            logger.error("Error during initialisation of SecretKeyFactory" +
                    " with algorithm", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public String hashPassword(char[] password, byte[] salt, int iterations) {
        try {
            PBEKeySpec pbeKeySpec = new PBEKeySpec(password, salt, iterations, 256);
            Arrays.fill(password, '\u0000');
            SecretKey secretKey = secretKeyFactory.generateSecret(pbeKeySpec);
            byte[] result = secretKey.getEncoded();
            return Base64.getEncoder().encodeToString(result);
        } catch (InvalidKeySpecException e) {
            logger.error("Error during hashing password", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] generateSalt() {
        byte[] salt = new byte[20];
        random.nextBytes(salt);
        return salt;
    }

    @Override
    public User createUserWithHashedPassword(String email, char[] password) {
        byte[] salt = generateSalt();
        int iterations = 10000;
        String hashedPassword = hashPassword(password, salt, iterations);
        User user = new User();
        user.setEmail(email);
        user.setPassword(hashedPassword);
        user.setSalt(Base64.getEncoder().encodeToString(salt));
        user.setIterations(iterations);
        user.setRole(Role.USER);
        user.setStatus(false);
        return user;
    }

    @Override
    public boolean checkPasswordAgainstUserPassword(User user, char[] password) {
        if (user == null) {
            return false;
        } else {
            boolean isSameHash;
            isSameHash = (user.getPassword().equals(
                    hashPassword(password, Base64.getDecoder().decode(user.getSalt()), user.getIterations())));
            return isSameHash;
        }
    }
}
