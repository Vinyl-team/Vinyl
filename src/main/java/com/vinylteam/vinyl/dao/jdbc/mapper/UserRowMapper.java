package com.vinylteam.vinyl.dao.jdbc.mapper;

import com.vinylteam.vinyl.dao.RowMapper;
import com.vinylteam.vinyl.entity.Role;
import com.vinylteam.vinyl.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;

public class UserRowMapper implements RowMapper<User> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public User mapRow(ResultSet resultSet) {
        User user = new User();
        try {
            user.setEmail(resultSet.getString("email"));
            user.setPassword(resultSet.getString("password"));
            user.setDiscogsUserName(resultSet.getString("discogs_user_name"));
            user.setSalt(resultSet.getString("salt"));
            user.setIterations(resultSet.getInt("iterations"));
            user.setRole(Role.valueOf(resultSet.getString("role")));
            user.setStatus(resultSet.getBoolean("status"));
            logger.debug("Resulting User object {'user':{}}", user);
            return user;
        } catch (Exception e) {
            logger.error("Error while getting data from result set into User object {'user':{}}", user, e);
            throw new RuntimeException(e);
        }
    }

}
