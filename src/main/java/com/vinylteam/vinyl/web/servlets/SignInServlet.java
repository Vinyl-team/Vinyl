package com.vinylteam.vinyl.web.servlets;

import com.vinylteam.vinyl.entity.SignInCheckResult;
import com.vinylteam.vinyl.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SignInServlet {

    private final UserService userService;

    public SignInServlet(UserService userService) {
        this.userService = userService;
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        SignInCheckResult checkResult = userService.signInCheck(email, password);

        if (checkResult == SignInCheckResult.OK_VERIFIED) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.sendRedirect("/home");
        } else if (checkResult == SignInCheckResult.OK_NOT_VERIFIED) {
            response.setStatus(HttpServletResponse.SC_SEE_OTHER);
            response.sendRedirect("/signIn");
        } else if (checkResult == SignInCheckResult.FAIL) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.sendRedirect("/signIn");
        }
    }
}
