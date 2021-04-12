package com.vinylteam.vinyl.web.servlets;

import com.vinylteam.vinyl.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SignUpServlet {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final UserService userService;
    private final String successRedirect = "/signIn";
    private final String failRedirect = "/signUp";

    public SignUpServlet(UserService userService) {
        logger.debug("Starting constructor SignUpServlet(UserService userService) with " +
                "{'UserService':{}}", userService);
        this.userService = userService;
        logger.debug("Initialized this.userService " +
                "{'userService':{}}", this.userService);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.debug("Start of function SignUpServlet.doPost(HttpServletRequest request, " +
                "HttpServletResponse response) with " +
                "{'request':{},\n 'response':{}}", request, response);
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        boolean isAdded = userService.add(email, password);
        logger.debug("Got result of adding user with passed email and password to db " +
                "{'email':{}, 'isAdded':{}}", email, isAdded);
        if (isAdded) {
            response.setStatus(HttpServletResponse.SC_SEE_OTHER);
            logger.debug("Set response status to " +
                    "{'status':{}}", HttpServletResponse.SC_SEE_OTHER);
            response.sendRedirect(successRedirect);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            logger.debug("Set response status to " +
                    "{'status':{}}", HttpServletResponse.SC_BAD_REQUEST);
            response.sendRedirect(failRedirect);
        }
    }
}
