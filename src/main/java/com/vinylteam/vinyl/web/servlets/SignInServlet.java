package com.vinylteam.vinyl.web.servlets;

import com.vinylteam.vinyl.entity.SignInCheckResult;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.security.impl.DefaultSecurityService;
import com.vinylteam.vinyl.service.impl.DefaultUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class SignInServlet {

    private final DefaultUserService defaultUserService;

    public SignInServlet(DefaultUserService defaultUserService) {
        this.defaultUserService = defaultUserService;
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        SignInCheckResult checkResult = defaultUserService.signInCheck(email, password);

        if (checkResult == SignInCheckResult.OK_VERIFIED) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.sendRedirect("/home");
        } else if (checkResult == SignInCheckResult.OK_NOT_VERIFIED) {
            response.setStatus(HttpServletResponse.SC_SEE_OTHER);
            response.sendRedirect("/verify");
        } else if (checkResult == SignInCheckResult.FAIL) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.sendRedirect("/signIn");
        }
    }
}
