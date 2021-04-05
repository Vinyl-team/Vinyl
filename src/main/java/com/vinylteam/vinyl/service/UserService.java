package com.vinylteam.vinyl.service;

import com.vinylteam.vinyl.entity.SignInCheckResult;
import com.vinylteam.vinyl.entity.User;

import java.util.Optional;

public interface UserService {

    boolean add(String email, String password);

    Optional<User> getByEmail(String email);

    SignInCheckResult signInCheck(String email, String password);

}
