package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.UserDao;
import com.vinylteam.vinyl.entity.SignInCheckResult;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.security.SecurityService;
import com.vinylteam.vinyl.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class DefaultUserService implements UserService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final UserDao userDao;
    private final SecurityService securityService;

    public DefaultUserService(UserDao userDao, SecurityService securityService) {
        logger.debug("Starting constructor DefaultUserService(UserDao userDao, SecurityService securityService) with " +
                "{'userDao':{}, 'securityService':{}}", userDao, securityService);
        this.userDao = userDao;
        this.securityService = securityService;
        logger.debug("Initialized this.userDao, this.securityService " +
                "{'userDao':{}, 'securityService':{}}", this.userDao, this.securityService);
    }

    @Override
    public boolean add(String email, String password) {
        logger.debug("Start of function DefaultUserService.add(String email, String password)" +
                " with {'email':{}}", email);
        boolean isAdded = false;
        if (email != null && password != null) {
            User userToAdd = securityService
                    .createUserWithHashedPassword(email, password.toCharArray());
            logger.debug("Created user to add {'user':{}}", userToAdd);
            isAdded = userDao.add(userToAdd);
            logger.debug("Attempted to add created user to db with boolean result " +
                    "{'user':{}, 'isAdded':{}}", userToAdd, isAdded);
        }
        logger.debug("Result of attempting to add user, created from passed email and password" +
                " if both are not null is" +
                "{'isAdded': {}, 'email':{}}", isAdded, email);
        return isAdded;
    }

    @Override
    public Optional<User> getByEmail(String email) {
        logger.debug("Start of function DefaultUserService.getByEmail(String email) " +
                "with {'email':{}}", email);
        Optional<User> resultingOptional = userDao.getByEmail(email);
        logger.debug("Resulting optional is {'optional':{}}", resultingOptional);
        return resultingOptional;
    }

    @Override
    public SignInCheckResult signInCheck(String email, String password) {
        logger.debug("Start of function DefaultUserService.signInCheck(String email, String password) " +
                "with {'email':{}}", email);
        SignInCheckResult checkResult = SignInCheckResult.FAIL;
        if (email != null && password != null) {
            Optional<User> optionalUser = userDao.getByEmail(email);
            logger.debug("Got optional with user from db by email " +
                    "{'email':{}, 'optionalUser':{}}", email, optionalUser);
            if (optionalUser.isPresent()) {
                if (securityService.checkPasswordAgainstUserPassword(
                        optionalUser.get(), password.toCharArray())) {
                    logger.debug("Hashed password passed as argument matches hashed password of user by passed email " +
                            "{'email':{}}", email);
                    if (optionalUser.get().getStatus()) {
                        logger.debug("User's status is {'status':{}}", optionalUser.get().getStatus());
                        checkResult = SignInCheckResult.OK_VERIFIED;
                    } else {
                        logger.debug("User's status is {'status':{}}", optionalUser.get().getStatus());
                        checkResult = SignInCheckResult.OK_NOT_VERIFIED;
                    }
                }
            }
        }
        logger.debug("Result of checking is {'checkResult':{}}", checkResult);
        return checkResult;
    }

}
