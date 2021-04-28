package com.vinylteam.vinyl.security;

import com.vinylteam.vinyl.entity.Session;
import com.vinylteam.vinyl.entity.User;
import jakarta.servlet.http.Cookie;

public interface SecurityService {

    String hashPassword(char[] password, byte[] salt, int iterations);

    byte[] generateSalt();

    User createUserWithHashedPassword(String email, char[] password);

    boolean checkPasswordAgainstUserPassword(User user, char[] password);

    Session addSession(User user);

    void delSession(String token);

    Session getSession(String token);

    String getRole(Cookie[] cookies);
}
