package com.vinylteam.vinyl.web.servlets;

import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.entity.UserPost;
import com.vinylteam.vinyl.service.UserPostService;
import com.vinylteam.vinyl.service.impl.DefaultCaptchaService;
import com.vinylteam.vinyl.web.templater.PageGenerator;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ContactUsServlet extends HttpServlet {
    private final UserPostService service;
    DefaultCaptchaService defaultCaptchaService = new DefaultCaptchaService();

    public ContactUsServlet(UserPostService service) {
        this.service = service;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=utf-8");
        PageGenerator.getInstance().process("contactUs", response.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String subject = request.getParameter("subject");
        String messageContactUs = request.getParameter("messageContactUs");
        String captcha = request.getParameter("captcha");

        boolean isCaptchaValid = defaultCaptchaService.validateCaptcha(request.getSession().getId(), captcha);

        if (isCaptchaValid) {
            UserPost post = new UserPost(name, email, subject, messageContactUs, LocalDateTime.now());
            boolean isPostProcessed = service.processAdd(post);
            if (isPostProcessed) {
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
            response.sendRedirect("/");
        } else {
            Map<String, String> attributes = new HashMap<>();
            attributes.put("captchaError", "Captcha is invalid!!!");
            PageGenerator.getInstance().process("contactUs", attributes, response.getWriter());
        }
    }
}