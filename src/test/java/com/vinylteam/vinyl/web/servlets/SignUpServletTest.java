package com.vinylteam.vinyl.web.servlets;

import com.vinylteam.vinyl.entity.Role;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.service.UserService;
import com.vinylteam.vinyl.service.impl.DefaultUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class SignUpServletTest {

    private final UserService mockedUserService = mock(DefaultUserService.class);
    private final SignUpServlet signUpServlet = new SignUpServlet(mockedUserService);

    private HttpServletRequest mockedRequest;
    private HttpServletResponse mockedResponse;
    private HttpSession mockedHttpSession;
    private User mockedUser;
    private PrintWriter printWriter;
    private InOrder inOrderRequest;
    private InOrder inOrderResponse;

    @BeforeEach
    void beforeEach() {
        mockedRequest = mock(HttpServletRequest.class);
        mockedResponse = mock(HttpServletResponse.class);
        mockedHttpSession = mock(HttpSession.class);
        mockedUser = mock(User.class);
        printWriter = new PrintWriter(new StringWriter());
        inOrderRequest = Mockito.inOrder(mockedRequest);
        inOrderResponse = Mockito.inOrder(mockedResponse);
    }

    @Test
    @DisplayName("Checks if all right methods are called & session isn't exist")
    void doGetWithNoSessionTest() throws IOException {
        //prepare
        when(mockedRequest.getSession(false)).thenReturn(null);
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        //when
        signUpServlet.doGet(mockedRequest, mockedResponse);
        //then
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_OK);
        inOrderRequest.verify(mockedRequest).getSession(false);
        verify(mockedHttpSession, times(0)).getAttribute("user");
        verify(mockedUser, times(0)).getRole();
        inOrderResponse.verify(mockedResponse).getWriter();
    }

    @Test
    @DisplayName("Checks if all right methods are called & user is authed")
    void doGetWithAuthedTestTest() throws IOException {
        //prepare
        when(mockedRequest.getSession(false)).thenReturn(mockedHttpSession);
        when(mockedHttpSession.getAttribute("user")).thenReturn(mockedUser);
        when(mockedUser.getRole()).thenReturn(Role.USER);
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        //when
        signUpServlet.doGet(mockedRequest, mockedResponse);
        //then
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_OK);
        inOrderRequest.verify(mockedRequest).getSession(false);
        verify(mockedHttpSession).getAttribute("user");
        assertEquals(mockedUser, mockedHttpSession.getAttribute("user"));
        verify(mockedUser, times(1)).getRole();
        assertEquals(Role.USER, mockedUser.getRole());
        verify(mockedResponse).getWriter();
    }

    @Test
    @DisplayName("Checks if all right methods are called & user is not authed")
    void doGetWithNotAuthedTestTest() throws IOException {
        //prepare
        when(mockedRequest.getSession(false)).thenReturn(mockedHttpSession);
        when(mockedHttpSession.getAttribute("user")).thenReturn(null);
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        //when
        signUpServlet.doGet(mockedRequest, mockedResponse);
        //then
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_OK);
        inOrderRequest.verify(mockedRequest).getSession(false);
        verify(mockedHttpSession).getAttribute("user");
        assertNull(mockedHttpSession.getAttribute("user"));
        verify(mockedUser, times(0)).getRole();
        verify(mockedResponse).getWriter();
    }

    @Test
    @DisplayName("Checks if all right methods are called and response has code set to 400 and redirected to /signUp " +
            "when password is not correct.")
    void doPostWithNotCorrectPasswordTest() throws IOException {
        //prepare
        when(mockedRequest.getParameter("email")).thenReturn("existinguser@vinyl.com");
        when(mockedRequest.getParameter("password")).thenReturn("password");
        when(mockedRequest.getParameter("confirmPassword")).thenReturn("confirmPassword");
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        //when
        signUpServlet.doPost(mockedRequest, mockedResponse);
        //then
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderRequest.verify(mockedRequest).getParameter("email");
        inOrderRequest.verify(mockedRequest).getParameter("password");
        inOrderRequest.verify(mockedRequest).getParameter("confirmPassword");
        verify(mockedUserService, times(0))
                .add("existinguser@vinyl.com", "password");
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(mockedResponse).getWriter();
    }

    @Test
    @DisplayName("Checks if all right methods are called and response has code set to 400 and redirected to /signUp " +
            "when email already exist in db.")
    void doPostWithExistingUserTest() throws IOException {
        //prepare
        when(mockedRequest.getParameter("email")).thenReturn("existinguser@vinyl.com");
        when(mockedRequest.getParameter("password")).thenReturn("password");
        when(mockedRequest.getParameter("confirmPassword")).thenReturn("password");
        when(mockedUserService.add("existinguser@vinyl.com", "password")).thenReturn(false);
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        //when
        signUpServlet.doPost(mockedRequest, mockedResponse);
        //then
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderRequest.verify(mockedRequest).getParameter("email");
        inOrderRequest.verify(mockedRequest).getParameter("password");
        inOrderRequest.verify(mockedRequest).getParameter("confirmPassword");
        verify(mockedUserService, times(1))
                .add("existinguser@vinyl.com", "password");
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(mockedResponse).getWriter();
    }

    @Test
    @DisplayName("Checks if all right methods are called and response has code set to 303 and redirected to /signIn " +
            "when email did not exist in database before.")
    void doPostWithNewUserTest() throws IOException {
        //prepare
        when(mockedRequest.getParameter("email")).thenReturn("newuser@vinyl.com");
        when(mockedRequest.getParameter("password")).thenReturn("password");
        when(mockedRequest.getParameter("confirmPassword")).thenReturn("password");
        when(mockedUserService.add("newuser@vinyl.com", "password")).thenReturn(true);
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        //when
        signUpServlet.doPost(mockedRequest, mockedResponse);
        //then
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderRequest.verify(mockedRequest).getParameter("email");
        inOrderRequest.verify(mockedRequest).getParameter("password");
        inOrderRequest.verify(mockedRequest).getParameter("confirmPassword");
        verify(mockedUserService, times(1))
                .add("newuser@vinyl.com", "password");
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_SEE_OTHER);
        verify(mockedResponse).getWriter();
    }

}