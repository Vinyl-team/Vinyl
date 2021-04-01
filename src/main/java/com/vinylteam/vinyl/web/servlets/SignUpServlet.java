package com.vinylteam.vinyl.web.servlets;

import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.security.impl.DefaultSecurityService;
import com.vinylteam.vinyl.service.impl.DefaultUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SignUpServlet {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DefaultUserService defaultUserService;
    private final DefaultSecurityService defaultSecurityService;

    public SignUpServlet(DefaultUserService defaultUserService,
                         DefaultSecurityService defaultSecurityService) {
        this.defaultUserService = defaultUserService;
        this.defaultSecurityService = defaultSecurityService;
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (email != null && password != null) {
            User userToAdd = defaultSecurityService
                    .createUserWithHashedPassword(email, password.toCharArray());

            if (defaultUserService.add(userToAdd)) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.sendRedirect("/signIn");
                return;
            }
        }
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.sendRedirect("/signUp");
    }
}
