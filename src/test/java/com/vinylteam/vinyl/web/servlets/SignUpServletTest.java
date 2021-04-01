package com.vinylteam.vinyl.web.servlets;

import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.security.impl.DefaultSecurityService;
import com.vinylteam.vinyl.service.impl.DefaultUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.Mockito.*;

class SignUpServletTest {

    private DefaultUserService mockedDefaultUserService;
    private DefaultSecurityService mockedDefaultSecurityService;
    private HttpServletRequest mockedHttpServletRequest;
    private HttpServletResponse mockedHttpServletResponse;
    private final User mockedUser = mock(User.class);

    @BeforeEach
    void beforeEach() {
        mockedDefaultSecurityService = null;
        mockedDefaultUserService = null;
        mockedHttpServletRequest = null;
        mockedHttpServletResponse = null;

        mockedDefaultUserService = mock(DefaultUserService.class);
        mockedDefaultSecurityService = mock(DefaultSecurityService.class);
        mockedHttpServletRequest = mock(HttpServletRequest.class);
        mockedHttpServletResponse = mock(HttpServletResponse.class);

        when(mockedHttpServletRequest.getParameter("email")).thenReturn("testuser1@vinyl.com");
        when(mockedHttpServletRequest.getParameter("password")).thenReturn("password");

        when(mockedDefaultSecurityService.createUserWithHashedPassword("testuser1@vinyl.com",
                "password".toCharArray())).thenReturn(mockedUser);
    }

    @Test
    void doPostWithExistingUserTest() throws IOException {
        SignUpServlet signUpServlet = new SignUpServlet(mockedDefaultUserService, mockedDefaultSecurityService);
        when(mockedDefaultUserService.add(mockedUser)).thenReturn(true);
        InOrder inOrderRequest = Mockito.inOrder(mockedHttpServletRequest);
        InOrder inOrderResponse = Mockito.inOrder(mockedHttpServletResponse);

        signUpServlet.doPost(mockedHttpServletRequest, mockedHttpServletResponse);

        inOrderRequest.verify(mockedHttpServletRequest).getParameter("email");
        inOrderRequest.verify(mockedHttpServletRequest).getParameter("password");
        verify(mockedDefaultSecurityService)
                .createUserWithHashedPassword("testuser1@vinyl.com", "password".toCharArray());
        verify(mockedDefaultUserService)
                .add(mockedUser);

        inOrderResponse.verify(mockedHttpServletResponse).setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
        inOrderResponse.verify(mockedHttpServletResponse).sendRedirect("/signIn");
    }

    @Test
    void doPostWithNewUserTest() throws IOException {
        SignUpServlet signUpServlet = new SignUpServlet(mockedDefaultUserService, mockedDefaultSecurityService);
        when(mockedDefaultUserService.add(mockedUser)).thenReturn(false);
        InOrder inOrderRequest = Mockito.inOrder(mockedHttpServletRequest);
        InOrder inOrderResponse = Mockito.inOrder(mockedHttpServletResponse);

        signUpServlet.doPost(mockedHttpServletRequest, mockedHttpServletResponse);

        inOrderRequest.verify(mockedHttpServletRequest).getParameter("email");
        inOrderRequest.verify(mockedHttpServletRequest).getParameter("password");

        verify(mockedDefaultSecurityService)
                .createUserWithHashedPassword("testuser1@vinyl.com", "password".toCharArray());
        verify(mockedDefaultUserService)
                .add(mockedUser);
        inOrderResponse.verify(mockedHttpServletResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        inOrderResponse.verify(mockedHttpServletResponse).sendRedirect("/signUp");
    }

    @Test
    void doPostWithNullEmailTest() throws IOException {
        SignUpServlet signUpServlet = new SignUpServlet(mockedDefaultUserService, mockedDefaultSecurityService);
        when(mockedHttpServletRequest.getParameter("email")).thenReturn(null);
        InOrder inOrderRequest = Mockito.inOrder(mockedHttpServletRequest);
        InOrder inOrderResponse = Mockito.inOrder(mockedHttpServletResponse);

        signUpServlet.doPost(mockedHttpServletRequest, mockedHttpServletResponse);

        inOrderRequest.verify(mockedHttpServletRequest).getParameter("email");
        inOrderRequest.verify(mockedHttpServletRequest).getParameter("password");
        verify(mockedDefaultSecurityService, never())
                .createUserWithHashedPassword(null, "password".toCharArray());
        verify(mockedDefaultUserService, never())
                .add(null);

        inOrderResponse.verify(mockedHttpServletResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        inOrderResponse.verify(mockedHttpServletResponse).sendRedirect("/signUp");
    }

    @Test
    void doPostWithNullPasswordTest() throws IOException {
        SignUpServlet signUpServlet = new SignUpServlet(mockedDefaultUserService, mockedDefaultSecurityService);
        when(mockedHttpServletRequest.getParameter("password")).thenReturn(null);
        InOrder inOrderRequest = Mockito.inOrder(mockedHttpServletRequest);
        InOrder inOrderResponse = Mockito.inOrder(mockedHttpServletResponse);

        signUpServlet.doPost(mockedHttpServletRequest, mockedHttpServletResponse);

        inOrderRequest.verify(mockedHttpServletRequest).getParameter("email");
        inOrderRequest.verify(mockedHttpServletRequest).getParameter("password");
        verify(mockedDefaultSecurityService, never())
                .createUserWithHashedPassword("testuser1@vinyl.com", null);
        verify(mockedDefaultUserService, never())
                .add(any());

        inOrderResponse.verify(mockedHttpServletResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        inOrderResponse.verify(mockedHttpServletResponse).sendRedirect("/signUp");
    }
}