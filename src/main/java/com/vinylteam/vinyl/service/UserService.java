package com.vinylteam.vinyl.service;

import com.vinylteam.vinyl.entity.User;

import java.util.Optional;

public interface UserService {

    boolean add(String email, String password);

    boolean edit(String oldEmail, String newEmail, String newPassword);

    Optional<User> getByEmail(String email);

    Optional<User> signInCheck(String email, String password);

}
