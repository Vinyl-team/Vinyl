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

public class DeleteUserServlet extends HttpServlet {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private final UserService userService;

    public DeleteUserServlet(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=utf-8");
        Map<String, String> attributes = new HashMap<>();
        HttpSession httpSession = request.getSession(false);
        if (httpSession != null) {
            User user = (User) httpSession.getAttribute("user");
            if (user != null) {
                boolean isDeleted = userService.remove(user);
                if (isDeleted) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    logger.debug("Set response status to {'status':{}}", HttpServletResponse.SC_OK);
                    httpSession.invalidate();
                    response.sendRedirect("/signUp");
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    logger.debug("Set response status to {'status':{}}", HttpServletResponse.SC_BAD_REQUEST);
                    attributes.put("message", "Delete is fail! Try again!");
                    PageGenerator.getInstance().process("editProfile", attributes, response.getWriter());
                }
            } else {
                response.sendRedirect("/signIn");
            }
        } else {
            response.sendRedirect("/signIn");
        }
    }
}
