package com.vinylteam.vinyl.web.servlets;

import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.security.SecurityService;
import com.vinylteam.vinyl.service.UserService;
import com.vinylteam.vinyl.web.templater.PageGenerator;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EditProfileServlet extends HttpServlet {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private SecurityService securityService;
    private UserService userService;

    public EditProfileServlet(SecurityService securityService, UserService userService) {
        this.securityService = securityService;
        this.userService = userService;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        logger.debug("Set response status to {'status':{}}", HttpServletResponse.SC_OK);
        Map<String, String> attributes = new HashMap<>();
        HttpSession session = request.getSession(false);
        if (session != null){
            User user = (User) session.getAttribute("user");
            if (user != null) {
                attributes.put("userRole", user.getRole().toString());
                attributes.put("email", user.getEmail());
            }
        }
        PageGenerator.getInstance().process("editProfile", attributes, response.getWriter());
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, String> attributes = new HashMap<>();
        response.setContentType("text/html;charset=utf-8");
        String newEmail = request.getParameter("email");
        String oldPassword = request.getParameter("oldPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmNewPassword = request.getParameter("confirmNewPassword");
        HttpSession httpSession = request.getSession(false);
        if (httpSession != null) {
            User user = (User) httpSession.getAttribute("user");
            if (user != null) {
                attributes.put("userRole", user.getRole().toString());
                String email = user.getEmail();
                if (!newPassword.equals(confirmNewPassword)) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    logger.debug("Set response status to {'status':{}}", HttpServletResponse.SC_BAD_REQUEST);
                    attributes.put("message", "Sorry, passwords don't match!");
                } else {
                    boolean checkOldPassword = securityService.checkPasswordAgainstUserPassword(user, oldPassword.toCharArray());
                    if (checkOldPassword) {
                        boolean isEdit = userService.edit(email, newEmail, newPassword);
                        logger.debug("Got result of edit user by email with " +
                                "passed email and password to db {'newEmail':{}, 'isEdit':{}}", newEmail, isEdit);
                        if (isEdit) {
                            response.setStatus(HttpServletResponse.SC_SEE_OTHER);
                            logger.debug("Set response status to {'status':{}}", HttpServletResponse.SC_SEE_OTHER);
                            attributes.put("message", "Your profile is edit successful.");
                            httpSession.invalidate();
                            email = newEmail;
                            HttpSession newSession = request.getSession(true);
                            newSession.setMaxInactiveInterval(60 * 60 * 5);
                            newSession.setAttribute("user", userService.getByEmail(email).orElse(user));
                        } else {
                            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                            logger.debug("Set response status to {'status':{}}", HttpServletResponse.SC_BAD_REQUEST);
                            attributes.put("message", "Edit is fail! Try again!");
                        }
                    } else {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        logger.debug("Set response status to {'status':{}}", HttpServletResponse.SC_BAD_REQUEST);
                        attributes.put("message", "Sorry, old password isn't correct!");
                    }
                }
                attributes.put("email", email);
                PageGenerator.getInstance().process("editProfile", attributes, response.getWriter());
            } else {
                response.sendRedirect("/signIn");
            }
        } else {
            response.sendRedirect("/signIn");
        }
    }
}
