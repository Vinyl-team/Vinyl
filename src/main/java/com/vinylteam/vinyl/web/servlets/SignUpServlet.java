package com.vinylteam.vinyl.web.servlets;

import com.vinylteam.vinyl.security.impl.DefaultSecurityService;
import com.vinylteam.vinyl.service.impl.DefaultUserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SignUpServlet {

    private final DefaultUserService defaultUserService;

    public SignUpServlet(DefaultUserService defaultUserService) {
        this.defaultUserService = defaultUserService;
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (defaultUserService.add(email, password)) {
            response.setStatus(HttpServletResponse.SC_SEE_OTHER);
            response.sendRedirect("/verify");
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.sendRedirect("/signUp");
        }
    }
}
