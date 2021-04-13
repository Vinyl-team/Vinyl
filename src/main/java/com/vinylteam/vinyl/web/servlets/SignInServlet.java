package com.vinylteam.vinyl.web.servlets;

import com.vinylteam.vinyl.entity.SignInCheckResult;
import com.vinylteam.vinyl.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SignInServlet {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final UserService userService;
    private final String verifiedRedirect = "/home";
    private final String notVerifiedRedirect = "/signIn";
    private final String failRedirect = "/signIn";

    public SignInServlet(UserService userService) {
        this.userService = userService;
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        SignInCheckResult checkResult = userService.signInCheck(email, password);
        logger.debug("Got result of checking user with " +
                "passed email and password {'email':{}, 'checkResult':{}}", email, checkResult);
        if (checkResult == SignInCheckResult.OK_VERIFIED) {
            response.setStatus(HttpServletResponse.SC_OK);
            logger.debug("Set response status to {'status':{}}", HttpServletResponse.SC_OK);
            response.sendRedirect(verifiedRedirect);
        } else if (checkResult == SignInCheckResult.OK_NOT_VERIFIED) {
            response.setStatus(HttpServletResponse.SC_SEE_OTHER);
            logger.debug("Set response status to {'status':{}}", HttpServletResponse.SC_SEE_OTHER);
            response.sendRedirect(notVerifiedRedirect);
        } else if (checkResult == SignInCheckResult.FAIL) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            logger.debug("Set response status to {'status':{}}", HttpServletResponse.SC_BAD_REQUEST);
            response.sendRedirect(failRedirect);
        }
    }
}
