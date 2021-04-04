package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.jdbc.JdbcUserDao;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.service.UserService;

import java.util.Optional;

public class DefaultUserService implements UserService {

    private final JdbcUserDao jdbcUserDao;

    public DefaultUserService() {
        this.jdbcUserDao = new JdbcUserDao();
    }

    @Override
    public boolean add(User user) {
        return jdbcUserDao.add(user);
    }

    @Override
    public Optional<User> getByEmail(String email) {
        return jdbcUserDao.getByEmail(email);
    }

}
