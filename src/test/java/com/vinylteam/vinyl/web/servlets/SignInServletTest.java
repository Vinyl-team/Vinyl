/*
package com.vinylteam.vinyl.web.servlets;

import com.vinylteam.vinyl.entity.Role;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class SignInServletTest {

    private final UserService mockedUserService = mock(UserService.class);
    private final SignInServlet signInServlet = new SignInServlet(mockedUserService);

    private HttpServletRequest mockedHttpServletRequest;
    private HttpServletResponse mockedHttpServletResponse;
    private HttpSession mockedHttpSession;
    private User mockedUser;
    private PrintWriter printWriter;
    private InOrder inOrderResponse;
    private InOrder inOrderRequest;

    @BeforeEach
    void beforeEach() {
        mockedHttpServletRequest = mock(HttpServletRequest.class);
        mockedHttpServletResponse = mock(HttpServletResponse.class);
        mockedHttpSession = mock(HttpSession.class);
        mockedUser = mock(User.class);
        printWriter = new PrintWriter(new StringWriter());
        inOrderResponse = inOrder(mockedHttpServletResponse);
        inOrderRequest = inOrder(mockedHttpServletRequest);
    }

    @Test
    @DisplayName("Checks if all right methods are called & user is not authed")
    void doGetWithNotAuthedUserTest() throws IOException {
        //prepare
        when(mockedHttpServletRequest.getSession()).thenReturn(mockedHttpSession);
        when(mockedHttpSession.getAttribute("user")).thenReturn(null);
        when(mockedHttpServletResponse.getWriter()).thenReturn(printWriter);
        //when
        signInServlet.doGet(mockedHttpServletRequest, mockedHttpServletResponse);
        //then
        inOrderResponse.verify(mockedHttpServletResponse).setContentType("text/html;charset=utf-8");
        inOrderResponse.verify(mockedHttpServletResponse).setStatus(HttpServletResponse.SC_OK);
        inOrderRequest.verify(mockedHttpServletRequest).getSession();
        assertNull(mockedHttpSession.getAttribute("user"));
        verify(mockedUser, times(0)).getRole();
        inOrderResponse.verify(mockedHttpServletResponse).getWriter();
    }

    @Test
    @DisplayName("Checks if all right methods are called & user is authed")
    void doGetWithAuthedUserTest() throws IOException {
        //prepare
        when(mockedHttpServletRequest.getSession()).thenReturn(mockedHttpSession);
        when(mockedHttpSession.getAttribute("user")).thenReturn(mockedUser);
        when(mockedUser.getRole()).thenReturn(Role.USER);
        when(mockedHttpServletResponse.getWriter()).thenReturn(printWriter);
        //when
        signInServlet.doGet(mockedHttpServletRequest, mockedHttpServletResponse);
        //then
        inOrderResponse.verify(mockedHttpServletResponse).setContentType("text/html;charset=utf-8");
        inOrderResponse.verify(mockedHttpServletResponse).setStatus(HttpServletResponse.SC_OK);
        inOrderRequest.verify(mockedHttpServletRequest).getSession();
        verify(mockedHttpSession).getAttribute("user");
        assertEquals(mockedUser, mockedHttpSession.getAttribute("user"));
        verify(mockedUser, times(1)).getRole();
        assertEquals(Role.USER, mockedUser.getRole());
        verify(mockedHttpServletResponse).getWriter();
    }

    @Test
    @DisplayName("Checks if all right methods are called and response has code set to 200 and redirected to / " +
            "when email and password are right, and user's email is verified(user's status==true).")
    void doPostWithVerifiedUserRightPasswordTest() throws IOException {
        //prepare
        when(mockedHttpServletRequest.getParameter("email")).thenReturn("verifieduser@vinyl.com");
        when(mockedHttpServletRequest.getParameter("password")).thenReturn("right password");
        when(mockedUserService.signInCheck("verifieduser@vinyl.com", "right password"))
                .thenReturn(Optional.of(mockedUser));
        when(Optional.of(mockedUser).get().getStatus()).thenReturn(true);
        when(mockedHttpServletRequest.getSession()).thenReturn(mockedHttpSession);
        //when
        signInServlet.doPost(mockedHttpServletRequest, mockedHttpServletResponse);
        //then
        inOrderResponse.verify(mockedHttpServletResponse).setContentType("text/html;charset=utf-8");
        inOrderRequest.verify(mockedHttpServletRequest).getParameter("email");
        inOrderRequest.verify(mockedHttpServletRequest).getParameter("password");
        verify(mockedUserService).signInCheck("verifieduser@vinyl.com", "right password");
        inOrderResponse.verify(mockedHttpServletResponse).setStatus(HttpServletResponse.SC_OK);
        inOrderRequest.verify(mockedHttpServletRequest).getSession();
        verify(mockedHttpSession).setMaxInactiveInterval(60 * 60 * 5);
        verify(mockedHttpSession).setAttribute("user", mockedUser);
        inOrderResponse.verify(mockedHttpServletResponse).sendRedirect("/");
    }

    @Test
    @DisplayName("Checks if all right methods are called and response has code set to 303 and redirected to /signIn " +
            "when email and password are right, and user's email is not verified(user's status==false).")
    void doPostWithNotVerifiedUserRightPasswordTest() throws IOException {
        //prepare
        when(mockedHttpServletRequest.getParameter("email")).thenReturn("notverifieduser@vinyl.com");
        when(mockedHttpServletRequest.getParameter("password")).thenReturn("right password");
        when(mockedUserService.signInCheck("notverifieduser@vinyl.com", "right password")).thenReturn(Optional.of(mockedUser));
        when(Optional.of(mockedUser).get().getStatus()).thenReturn(false);
        when(mockedHttpServletResponse.getWriter()).thenReturn(printWriter);
        //when
        signInServlet.doPost(mockedHttpServletRequest, mockedHttpServletResponse);
        //then
        inOrderResponse.verify(mockedHttpServletResponse).setContentType("text/html;charset=utf-8");
        inOrderRequest.verify(mockedHttpServletRequest).getParameter("email");
        inOrderRequest.verify(mockedHttpServletRequest).getParameter("password");
        verify(mockedUserService).signInCheck("notverifieduser@vinyl.com", "right password");
        inOrderResponse.verify(mockedHttpServletResponse).setStatus(HttpServletResponse.SC_SEE_OTHER);
        verify(mockedHttpServletResponse).getWriter();
    }

    @Test
    @DisplayName("Checks if all right methods are called and response has code set to 400 and redirected to /signIn " +
            "when email is right but password is wrong, and user's email is verified(user's status==true).")
    void doPostWithVerifiedUserWrongPasswordTest() throws IOException {
        //prepare
        when(mockedHttpServletRequest.getParameter("email")).thenReturn("verifieduser@vinyl.com");
        when(mockedHttpServletRequest.getParameter("password")).thenReturn("wrong password");
        when(mockedUserService.signInCheck("verifieduser@vinyl.com", "wrong password")).thenReturn(Optional.empty());
        when(mockedHttpServletResponse.getWriter()).thenReturn(printWriter);
        //when
        signInServlet.doPost(mockedHttpServletRequest, mockedHttpServletResponse);
        //then
        inOrderResponse.verify(mockedHttpServletResponse).setContentType("text/html;charset=utf-8");
        inOrderRequest.verify(mockedHttpServletRequest).getParameter("email");
        inOrderRequest.verify(mockedHttpServletRequest).getParameter("password");
        verify(mockedUserService).signInCheck("verifieduser@vinyl.com", "wrong password");
        inOrderResponse.verify(mockedHttpServletResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(mockedHttpServletResponse).getWriter();
    }

}*/
