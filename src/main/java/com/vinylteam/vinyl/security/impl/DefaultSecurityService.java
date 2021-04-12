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
    private final String algorithm = "PBKDF2WithHmacSHA512";

    {
        logger.debug("Started initializer in DefaultSecurityService");
        try {
            secretKeyFactory = SecretKeyFactory.getInstance(algorithm);
            logger.debug("Initialized secretKeyFactory with algorithm" +
                    "{'secretKeyFactory':{}, 'algorithm':{}", secretKeyFactory, algorithm);
        } catch (NoSuchAlgorithmException e) {
            logger.error("Error during initialisation of secretKeyFactory" +
                    " with algorithm {'algorithm':{}}", algorithm, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public String hashPassword(char[] password, byte[] salt, int iterations) {
        logger.debug("Start of function DefaultSecurityService" +
                ".hashPassword(char[] password, byte[] salt, int iterations) with " +
                "{'salt':{}, 'iterations':{}}", salt, iterations);
        try {
            PBEKeySpec pbeKeySpec = new PBEKeySpec(password, salt, iterations, 256);
            Arrays.fill(password, '\u0000');
            logger.debug("Generated pbeKeySpec and nulled elements of unprotected password array" +
                    "{'unprotectedPassword':{}}", password);
            SecretKey secretKey = secretKeyFactory.generateSecret(pbeKeySpec);
            logger.debug("Generated secretKey from pbeKeySpeck");
            byte[] result = secretKey.getEncoded();
            logger.debug("Encoded password into hash" +
                    "{'hash':{}}", Base64.getEncoder().encodeToString(result));
            return Base64.getEncoder().encodeToString(result);
        } catch (InvalidKeySpecException e) {
            logger.error("Error during hashing password", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] generateSalt() {
        logger.debug("Start of function DefaultSecurityService.generateSalt()");
        byte[] salt = new byte[20];
        random.nextBytes(salt);
        logger.debug("Resulting byte array with salt is {'salt':{}}", salt);
        return salt;
    }

    @Override
    public User createUserWithHashedPassword(String email, char[] password) {
        logger.debug("Start of function DefaultSecurityService" +
                ".createUserWithHashedPassword(String email, char[] password) with " +
                "{'email':{}}", email);
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
        logger.debug("Resulting user is {'user':{}}", user);
        return user;
    }

    @Override
    public boolean checkPasswordAgainstUserPassword(User user, char[] password) {
        logger.debug("Start of function DefaultSecurityService" +
                ".checkPasswordAgainstUserPassword(User user, char[] password) with " +
                "{'user':{}}", user);
        boolean isSameHash = false;
        if (user != null) {
            isSameHash = (user.getPassword().equals(hashPassword(password,
                    Base64.getDecoder().decode(user.getSalt()), user.getIterations())));
            logger.debug("Compared hash of passed password against user's hashed password");
        }
        logger.debug("Result of comparing hash of passed password against user's hashed password" +
                "if user isn't null is " +
                "{'isSameHash': {}, 'user':{}}", isSameHash, user);
        return isSameHash;
    }
}
