package com.vinylteam.vinyl.service;

import com.vinylteam.vinyl.entity.User;

public interface UserService {

    boolean add(User user);

    void updatePassword(String email, String newPassword, int iterations, String newSalt);

    User getByEmail(String email);

    boolean removeUser(String email);

    boolean doesExistByEmail(String email);

}
