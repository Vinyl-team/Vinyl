package com.vinylteam.vinyl.dao.jdbc.mapper;

import com.vinylteam.vinyl.entity.Role;
import com.vinylteam.vinyl.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;

public class UserRowMapper {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public User mapRow(ResultSet resultSet) {
        logger.debug("Start of function UserRowMapper.mapRow(ResultSet resultSet)" +
                " with {'resultSet':{}}", resultSet);
        if (resultSet != null) {
            User user = new User();
            logger.debug("Created User object {'user':{}}", user);
            try {
                user.setEmail(resultSet.getString("email"));
                user.setPassword(resultSet.getString("password"));
                user.setSalt(resultSet.getString("salt"));
                user.setIterations(resultSet.getInt("iterations"));
                user.setRole(Role.valueOf(resultSet.getString("role")));
                user.setStatus(resultSet.getBoolean("status"));
                logger.debug("Filled User object {'user':{}}", user);
                return user;
            } catch (Exception e) {
                logger.error("Error while getting data from result set into User object {'user':{}}",
                        user, e);
                throw new RuntimeException(e);
            }
        } else {
            RuntimeException e = new RuntimeException();
            logger.error("ResultSet passed to UserRowMapper is null", e);
            throw e;
        }
    }
}
