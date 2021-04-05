package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.UserDao;
import com.vinylteam.vinyl.entity.SignInCheckResult;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.security.SecurityService;
import com.vinylteam.vinyl.service.UserService;

import java.util.Optional;

public class DefaultUserService implements UserService {

    private final UserDao userDao;
    private final SecurityService securityService;

    public DefaultUserService(UserDao userDao, SecurityService securityService) {
        this.userDao = userDao;
        this.securityService = securityService;
    }

    @Override
    public boolean add(String email, String password) {
        if (email != null && password != null) {
            User userToAdd = securityService
                    .createUserWithHashedPassword(email, password.toCharArray());
            return userDao.add(userToAdd);
        }
        return false;
    }

    @Override
    public Optional<User> getByEmail(String email) {
        return userDao.getByEmail(email);
    }

    @Override
    public SignInCheckResult signInCheck(String email, String password) {
        if (email != null && password != null) {
            Optional<User> optionalUser = userDao.getByEmail(email);
            if (optionalUser.isPresent()) {
                if (securityService.checkPasswordAgainstUserPassword(
                        optionalUser.get(), password.toCharArray())) {
                    if (optionalUser.get().getStatus()) {
                        return SignInCheckResult.OK_VERIFIED;
                    } else {
                        return SignInCheckResult.OK_NOT_VERIFIED;
                    }
                }
            }

        }
        return SignInCheckResult.FAIL;
    }


}
