package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.jdbc.JdbcUserDao;
import com.vinylteam.vinyl.entity.SignInCheckResult;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.security.impl.DefaultSecurityService;
import com.vinylteam.vinyl.service.UserService;

import java.util.Optional;

public class DefaultUserService implements UserService {

    private final JdbcUserDao jdbcUserDao;
    private final DefaultSecurityService defaultSecurityService;

    public DefaultUserService(JdbcUserDao jdbcUserDao, DefaultSecurityService defaultSecurityService) {
        this.jdbcUserDao = jdbcUserDao;
        this.defaultSecurityService = defaultSecurityService;
    }

    @Override
    public boolean add(String email, String password) {
        if (email != null && password != null) {
            User userToAdd = defaultSecurityService
                    .createUserWithHashedPassword(email, password.toCharArray());
            return jdbcUserDao.add(userToAdd);
        }
        return false;
    }

    @Override
    public Optional<User> getByEmail(String email) {
        return jdbcUserDao.getByEmail(email);
    }

    @Override
    public SignInCheckResult signInCheck(String email, String password) {
        if (email != null && password != null) {
            Optional<User> optionalUser = jdbcUserDao.getByEmail(email);
            if (optionalUser.isPresent()) {
                if (defaultSecurityService.checkPasswordAgainstUserPassword(
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
