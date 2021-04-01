package com.vinylteam.vinyl.dao;

import com.vinylteam.vinyl.entity.User;

import java.util.Optional;

public interface UserDao {

    boolean add(User user);

    void updatePassword(String email, String newPassword, int iterations, String newSalt);

    Optional<User> getByEmail(String email);

    boolean removeUser(String email);

}
