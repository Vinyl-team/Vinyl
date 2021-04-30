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

public class HomeServlet extends HttpServlet {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=utf-8");
        Map<String, String> attributes = new HashMap<>();
        User user = (User) request.getSession().getAttribute("user");
        if (user != null) {
            attributes.put("userRole", String.valueOf(user.getRole()));
        }
        response.setStatus(HttpServletResponse.SC_OK);
        logger.debug("Set response status to {'status':{}}", HttpServletResponse.SC_OK);
        PageGenerator.getInstance().process("index", attributes, response.getWriter());
    }
}
