package com.vinylteam.vinyl.dao;

import com.vinylteam.vinyl.entity.User;

public interface UserDao {

    boolean add(User user);

    void updatePassword(String email, String newPassword, int iterations, String newSalt);

    User getByEmail(String email);

    boolean removeUser(String email);

    int countAll();

    boolean doesExistByEmail(String email);

}
