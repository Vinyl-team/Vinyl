package com.vinylteam.vinyl.service;

import com.vinylteam.vinyl.entity.User;

import java.util.Optional;

public interface UserService {

    boolean add(String email, String password, String discogsUserName);

    boolean update(String oldEmail, String newEmail, String newPassword, String discogsUserName);

    boolean delete(User user);

    Optional<User> getByEmail(String email);

    Optional<User> signInCheck(String email, String password);

}
