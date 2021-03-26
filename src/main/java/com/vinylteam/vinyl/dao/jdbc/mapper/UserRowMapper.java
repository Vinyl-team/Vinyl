package com.vinylteam.vinyl.dao.jdbc.mapper;

import com.vinylteam.vinyl.entity.Role;
import com.vinylteam.vinyl.entity.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRowMapper {

    public static User mapRow(ResultSet resultSet) {
//        int id = 0;
        String email;
        String password;
        String salt;
        int iterations;
        Role role;
        try {
//            id = resultSet.getInt("id");
            email = resultSet.getString("email");
            password = resultSet.getString("password");
            salt = resultSet.getString("salt");
            iterations = resultSet.getInt("iterations");
            role = Role.valueOf(resultSet.getString("role"));
            return new User(email, password, salt, iterations, role);
        } catch (NullPointerException | SQLException e){
            throw new RuntimeException("Exception while getting data from result set", e);
        }
    }
}
