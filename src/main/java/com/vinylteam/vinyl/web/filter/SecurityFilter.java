package com.vinylteam.vinyl.web.filter;

import com.vinylteam.vinyl.entity.Role;
import com.vinylteam.vinyl.entity.Session;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.security.SecurityService;
import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.EnumSet;

public class SecurityFilter implements Filter {
    private SecurityService securityService;

    public SecurityFilter(SecurityService securityService) {
        this.securityService = securityService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        boolean isAuth = false;
        String url = httpServletRequest.getRequestURL().toString();

        Role userRole = (Role) httpServletRequest.getSession().getAttribute("userRole");
        if (EnumSet.of(Role.USER, Role.ADMIN).contains(userRole)){
            isAuth=true;
        }

        if (isAuth) {
            filterChain.doFilter(request, response);
        } else {
            httpServletResponse.sendRedirect("/signIn");
        }

    }
}
