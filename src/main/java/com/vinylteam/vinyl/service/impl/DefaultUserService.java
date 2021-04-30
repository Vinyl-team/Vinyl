package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.UserDao;
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
        this.userDao = userDao;
        this.securityService = securityService;
    }

    @Override
    public boolean add(String email, String password) {
        boolean isAdded = false;
        if (email != null && password != null) {
            User userToAdd = securityService
                    .createUserWithHashedPassword(email, password.toCharArray());
            isAdded = userDao.add(userToAdd);
            logger.debug("Attempted to add created user to db with boolean result {'isAdded':{}}",
                    isAdded);
        }
        logger.debug("Result of attempting to add user, created from passed email and password" +
                " if both are not null is {'isAdded': {}, 'email':{}}", isAdded, email);
        return isAdded;
    }

    @Override
    public Optional<User> getByEmail(String email) {
        Optional<User> resultingOptional = userDao.getByEmail(email);
        logger.debug("Resulting optional is {'optional':{}}", resultingOptional);
        return resultingOptional;
    }

    @Override
    public Optional<User> signInCheck(String email, String password) {
        Optional<User> optionalUser = Optional.empty();
        if (email != null && password != null) {
            Optional<User> optionalUserFromDataBase = userDao.getByEmail(email);
            logger.debug("Got optional with user from db by email {'email':{}, 'optionalUser':{}}",
                    email, optionalUserFromDataBase);
            if (optionalUserFromDataBase.isPresent()) {
                if (securityService.checkPasswordAgainstUserPassword(
                        optionalUserFromDataBase.get(), password.toCharArray())) {
                    logger.debug("Hashed password passed as argument matches hashed password " +
                            "of user by passed email {'email':{}}", email);
                    optionalUser = optionalUserFromDataBase;
                }
            }
        }
        return optionalUser;
    }

}
