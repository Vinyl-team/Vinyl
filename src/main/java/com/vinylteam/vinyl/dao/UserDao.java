package com.vinylteam.vinyl.dao;

import com.vinylteam.vinyl.entity.User;

import java.util.Optional;

public interface UserDao {

    boolean add(User user);

    boolean edit(String email, User user);

    Optional<User> getByEmail(String email);

}
