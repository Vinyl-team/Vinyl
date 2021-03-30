package com.vinylteam.vinyl.web.servlets;

import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.security.impl.DefaultSecurityService;
import com.vinylteam.vinyl.service.impl.DefaultUserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SignInServlet {

    private final DefaultUserService defaultUserService;
    private final DefaultSecurityService defaultSecurityService;

    public SignInServlet(DefaultUserService defaultUserService, DefaultSecurityService defaultSecurityService) {
        this.defaultUserService = defaultUserService;
        this.defaultSecurityService = defaultSecurityService;
    }

}
