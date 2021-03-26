package com.vinylteam.vinyl.web.servlets;

import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.security.impl.DefaultSecurityService;
import com.vinylteam.vinyl.service.impl.DefaultUserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SignUpServlet {

    private final DefaultUserService defaultUserService;
    private final DefaultSecurityService defaultSecurityService;

    public SignUpServlet(DefaultUserService defaultUserService, DefaultSecurityService defaultSecurityService) {
        this.defaultUserService = defaultUserService;
        this.defaultSecurityService = defaultSecurityService;
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        String email = request.getParameter("email");
        char[] password = request.getParameter("password").toCharArray();

        User userToAdd = defaultSecurityService
                .createUserWithHashedPassword(email, password);
        if (defaultUserService.add(userToAdd)) {
            response.setStatus(HttpServletResponse.SC_OK);
            try {
                response.sendRedirect("/signIn");
            } catch (IOException e) {
                throw new RuntimeException("Failed redirect to /signIn.", e);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            try {
                response.sendRedirect("/signUp");
            } catch (IOException e) {
                throw new RuntimeException("Failed redirect to /signUp.", e);
            }
        }
    }
}
