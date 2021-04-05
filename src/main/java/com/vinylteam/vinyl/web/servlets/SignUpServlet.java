package com.vinylteam.vinyl.web.servlets;

import com.vinylteam.vinyl.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SignUpServlet {

    private final UserService userService;

    public SignUpServlet(UserService userService) {
        this.userService = userService;
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (userService.add(email, password)) {
            response.setStatus(HttpServletResponse.SC_SEE_OTHER);
            response.sendRedirect("/signIn");
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.sendRedirect("/signUp");
        }
    }
}
