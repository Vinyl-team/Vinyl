package com.vinylteam.vinyl.dao.jdbc.mapper;

import com.vinylteam.vinyl.entity.Role;
import com.vinylteam.vinyl.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;

public class UserRowMapper {

    private static final Logger logger = LoggerFactory.getLogger(UserRowMapper.class);

    public static User mapRow(ResultSet resultSet) {
        String email;
        String password;
        String salt;
        int iterations;
        Role role;
        try {
            email = resultSet.getString("email");
            password = resultSet.getString("password");
            salt = resultSet.getString("salt");
            iterations = resultSet.getInt("iterations");
            role = Role.valueOf(resultSet.getString("role"));
            return new User(email, password, Base64.getDecoder().decode(salt), iterations, role);
        } catch (SQLException e) {
            logger.error("Error while getting data from result set", e);
            throw new RuntimeException(e);
        }
    }
}
