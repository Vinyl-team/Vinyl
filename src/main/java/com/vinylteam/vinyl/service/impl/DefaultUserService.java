package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.jdbc.JdbcUserDao;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.service.UserService;
import org.postgresql.ds.PGSimpleDataSource;

public class DefaultUserService implements UserService {

    private final JdbcUserDao jdbcUserDao;

    public DefaultUserService(PGSimpleDataSource dataSource) {
        this.jdbcUserDao = new JdbcUserDao(dataSource);
    }

    @Override
    public boolean add(User user) {
        return jdbcUserDao.add(user);
    }

    @Override
    public void updatePassword(String email, String newPassword, int iterations, String newSalt) {

    }

    @Override
    public User getByEmail(String email) {
        return jdbcUserDao.getByEmail(email);
    }

    @Override
    public boolean removeUser(String email) {
        return false;
    }

    @Override
    public boolean doesExistByEmail(String email) {
        return false;
    }
}
