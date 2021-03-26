package com.vinylteam.vinyl.security;

import com.vinylteam.vinyl.entity.User;

public interface SecurityService {

    String hashPassword(char[] password, byte[] salt, int iterations);

    byte[] generateSalt();

    User createUserWithHashedPassword(String email, char[] password);


}
