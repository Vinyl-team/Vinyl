package com.vinylteam.vinyl.web.filter;

import com.vinylteam.vinyl.entity.Role;
import com.vinylteam.vinyl.entity.User;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.EnumSet;

public class SecurityFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        String signUp = "/signUp";
        String profile = "/profile";
        String signIn = "/signIn";

        String uri = httpServletRequest.getRequestURI();
        boolean isAuth = false;

        HttpSession httpSession = httpServletRequest.getSession(false);
        if (httpSession != null){
            User user = (User) httpServletRequest.getSession().getAttribute("user");
            if (user != null){
                Role userRole = user.getRole();
                if (EnumSet.of(Role.USER, Role.ADMIN).contains(userRole)) {
                    isAuth = true;
                }
            }
        }
        if (isAuth && uri.equals(signUp)) {
            httpServletResponse.sendRedirect(profile);
        } else if (!isAuth && uri.equals(profile)){
            httpServletResponse.sendRedirect(signIn);
        } else {
            filterChain.doFilter(request, response);
        }

    }
}
