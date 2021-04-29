package com.vinylteam.vinyl.web.servlets;

import com.vinylteam.vinyl.entity.Session;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.security.SecurityService;
import com.vinylteam.vinyl.service.UserService;
import com.vinylteam.vinyl.web.templater.PageGenerator;
import jakarta.servlet.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SignInServlet extends HttpServlet {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final UserService userService;
    private final String verifiedRedirect = "/";

    public SignInServlet(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        Map<String, String> attributes = new HashMap<>();
        User user = (User) request.getSession().getAttribute("user");
        if (user != null){
            attributes.put("userRole", String.valueOf(user.getRole()));
        }
        PageGenerator.getInstance().process("signIn", attributes, response.getWriter());
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        Map<String, String> attributes = new HashMap<>();

//        SignInCheckResult checkResult = userService.signInCheck(email, password);
        Optional<User> optionalUser = userService.signInCheck(email, password);
        logger.debug("Received a optional with User with password verification by the passed " +
                "email address and password {'email':{}, 'optionalUser':{}}", email, optionalUser);
        if (optionalUser.isPresent()) {
            if (optionalUser.get().getStatus()) {
                logger.debug("User's status is {'status':{}}", optionalUser.get().getStatus());
                response.setStatus(HttpServletResponse.SC_OK);
                logger.debug("Set response status to {'status':{}}", HttpServletResponse.SC_OK);
                User user = optionalUser.get();
                HttpSession session = request.getSession();
                session.setMaxInactiveInterval(60 * 60 * 5);
                session.setAttribute("user", user);
                response.sendRedirect("/");
            } else {
                logger.debug("User's status is {'status':{}}", optionalUser.get().getStatus());
                response.setStatus(HttpServletResponse.SC_SEE_OTHER);
                logger.debug("Set response status to {'status':{}}", HttpServletResponse.SC_SEE_OTHER);
                attributes.put("message", "Sorry, your email has not been verified. Please go to your mailbox and follow the link to confirm your registration.");
                PageGenerator.getInstance().process("signIn", attributes, response.getWriter());
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            logger.debug("Set response status to {'status':{}}", HttpServletResponse.SC_BAD_REQUEST);
            attributes.put("message", "Sorry, login or password is not correct, please check yours credentials and try again.");
            PageGenerator.getInstance().process("signIn", attributes, response.getWriter());
        }

//        if (checkResult == SignInCheckResult.OK_VERIFIED) {
//            response.setStatus(HttpServletResponse.SC_OK);
//            logger.debug("Set response status to {'status':{}}", HttpServletResponse.SC_OK);
//            Optional<User> userOptional = userService.getByEmail(email);
//            if (userOptional.isPresent()) {
//                User user = userOptional.get();
//                Session session = securityService.addSession(user);
//                if (session != null) {
//                    Cookie cookie = new Cookie("user-token", session.getToken());
//                    cookie.setMaxAge(60 * 60 * 3);
//                    cookie.setHttpOnly(true);
//                    response.addCookie(cookie);
//                    attributes.put("userRole", String.valueOf(session.getUser().getRole()));
//                    HttpSession session1 = request.getSession();
//                    session1.setAttribute("userRole", String.valueOf(session.getUser().getRole()));
//                    PageGenerator.getInstance().process("index", attributes, response.getWriter());
//                    //PageGenerator.getInstance().process("signIn", attributes, response.getWriter());
//                    //response.sendRedirect("/index");
//                    //response.sendRedirect("/index?userRole="+session.getUser().getRole());
//                }
//            }
//        } else if (checkResult == SignInCheckResult.OK_NOT_VERIFIED) {
//            response.setStatus(HttpServletResponse.SC_SEE_OTHER);
//            logger.debug("Set response status to {'status':{}}", HttpServletResponse.SC_SEE_OTHER);
//            attributes.put("message", "Sorry, your email has not been verified. Please go to your mailbox and follow the link to confirm your registration.");
//            PageGenerator.getInstance().process("signIn", attributes, response.getWriter());
//        } else if (checkResult == SignInCheckResult.FAIL) {
//            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            logger.debug("Set response status to {'status':{}}", HttpServletResponse.SC_BAD_REQUEST);
//            attributes.put("message", "Sorry, login or password is not correct, please check yours credentials and try again.");
//            PageGenerator.getInstance().process("signIn", attributes, response.getWriter());
//        }
    }

}
