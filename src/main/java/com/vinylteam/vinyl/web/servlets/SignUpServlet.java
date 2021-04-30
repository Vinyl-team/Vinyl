package com.vinylteam.vinyl.web.servlets;

import com.vinylteam.vinyl.service.UserService;
import com.vinylteam.vinyl.web.templater.PageGenerator;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SignUpServlet extends HttpServlet {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final UserService userService;

    public SignUpServlet(UserService userService) {
        this.userService = userService;
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        logger.debug("Set response status to {'status':{}}", HttpServletResponse.SC_OK);
        PageGenerator.getInstance().process("registration", response.getWriter());
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, String> attributes = new HashMap<>();
        response.setContentType("text/html;charset=utf-8");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        if (!password.equals(confirmPassword)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            logger.debug("Set response status to {'status':{}}", HttpServletResponse.SC_BAD_REQUEST);
            attributes.put("message", "Sorry, the passwords do not match!");
            PageGenerator.getInstance().process("registration", attributes, response.getWriter());
        } else {
            boolean isAdded = userService.add(email, password);
            logger.debug("Got result of adding user with " +
                    "passed email and password to db {'email':{}, 'isAdded':{}}", email, isAdded);
            if (isAdded) {
                response.setStatus(HttpServletResponse.SC_SEE_OTHER);
                logger.debug("Set response status to {'status':{}}", HttpServletResponse.SC_SEE_OTHER);
                attributes.put("message", "Please confirm your registration. To do this, follow the link that we sent you by email.");
                PageGenerator.getInstance().process("registration", attributes, response.getWriter());
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                logger.debug("Set response status to {'status':{}}", HttpServletResponse.SC_BAD_REQUEST);
                attributes.put("message", "Sorry, but a user with this email address already exists!");
                PageGenerator.getInstance().process("registration", attributes, response.getWriter());
            }
        }
    }

}
