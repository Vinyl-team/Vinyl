package com.vinylteam.vinyl.web.servlets;

import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.web.templater.PageGenerator;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ProfileServlet extends HttpServlet {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        logger.debug("Set response status to {'status':{}}", HttpServletResponse.SC_OK);
        Map<String, String> attributes = new HashMap<>();
        User user = (User) request.getSession().getAttribute("user");
        if (user != null){
            attributes.put("userRole", String.valueOf(user.getRole()));
        }
        PageGenerator.getInstance().process("profile", attributes, response.getWriter());
    }
}
