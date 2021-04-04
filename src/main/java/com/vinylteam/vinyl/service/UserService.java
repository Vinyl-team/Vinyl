package com.vinylteam.vinyl.service;

import com.vinylteam.vinyl.entity.User;

import java.util.Optional;

public interface UserService {

    boolean add(User user);

    Optional<User> getByEmail(String email);

}
