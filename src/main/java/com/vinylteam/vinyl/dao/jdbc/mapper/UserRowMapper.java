package com.vinylteam.vinyl.dao.jdbc.mapper;

import com.vinylteam.vinyl.entity.Role;
import com.vinylteam.vinyl.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;

public class UserRowMapper {

    private final Logger logger = LoggerFactory.getLogger(UserRowMapper.class);

    public User mapRow(ResultSet resultSet) {
        if (resultSet != null) {
            User user = newgi User();
            try {
                user.setEmail(resultSet.getString("email"));
                user.setPassword(resultSet.getString("password"));
                user.setSalt(resultSet.getString("salt"));
                user.setIterations(resultSet.getInt("iterations"));
                user.setRole(Role.valueOf(resultSet.getString("role")));
                return user;
            } catch (Exception e) {
                logger.error("Error while getting data from result set", e);
                throw new RuntimeException(e);
            }
        } else {
            RuntimeException e = new RuntimeException();
            logger.error("ResultSet is null.", e);
            throw e;
        }
    }
}
