package com.vinylteam.vinyl.web.servlets;

import com.vinylteam.vinyl.service.UserService;
import com.vinylteam.vinyl.service.impl.DefaultUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.Mockito.*;

class SignUpServletTest {

    private UserService mockedUserService;
    private HttpServletRequest mockedHttpServletRequest;
    private HttpServletResponse mockedHttpServletResponse;

    @BeforeEach
    void beforeEach() {
        mockedHttpServletRequest = null;
        mockedHttpServletResponse = null;

        mockedUserService = mock(DefaultUserService.class);
        mockedHttpServletRequest = mock(HttpServletRequest.class);
        mockedHttpServletResponse = mock(HttpServletResponse.class);
    }

    @Test
    @DisplayName("Checks if all right methods are called and response has code set to 400 and redirected to /signUp " +
            "when email already existed in database before.")
    void doPostWithExistingUserTest() throws IOException {
        when(mockedHttpServletRequest.getParameter("email")).thenReturn("existinguser@vinyl.com");
        when(mockedHttpServletRequest.getParameter("password")).thenReturn("password");
        SignUpServlet signUpServlet = new SignUpServlet(mockedUserService);
        when(mockedUserService.add("existinguser@vinyl.com", "password")).thenReturn(false);

        signUpServlet.doPost(mockedHttpServletRequest, mockedHttpServletResponse);

        InOrder inOrderRequest = Mockito.inOrder(mockedHttpServletRequest);
        InOrder inOrderResponse = Mockito.inOrder(mockedHttpServletResponse);
        inOrderRequest.verify(mockedHttpServletRequest).getParameter("email");
        inOrderRequest.verify(mockedHttpServletRequest).getParameter("password");
        verify(mockedUserService)
                .add("existinguser@vinyl.com", "password");
        inOrderResponse.verify(mockedHttpServletResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        inOrderResponse.verify(mockedHttpServletResponse).sendRedirect("/signUp");
    }

    @Test
    @DisplayName("Checks if all right methods are called and response has code set to 303 and redirected to /signIn " +
            "when email did not exist in database before.")
    void doPostWithNewUserTest() throws IOException {
        when(mockedHttpServletRequest.getParameter("email")).thenReturn("newuser@vinyl.com");
        when(mockedHttpServletRequest.getParameter("password")).thenReturn("password");
        SignUpServlet signUpServlet = new SignUpServlet(mockedUserService);
        when(mockedUserService.add("newuser@vinyl.com", "password")).thenReturn(true);

        signUpServlet.doPost(mockedHttpServletRequest, mockedHttpServletResponse);

        InOrder inOrderRequest = Mockito.inOrder(mockedHttpServletRequest);
        InOrder inOrderResponse = Mockito.inOrder(mockedHttpServletResponse);
        inOrderRequest.verify(mockedHttpServletRequest).getParameter("email");
        inOrderRequest.verify(mockedHttpServletRequest).getParameter("password");
        verify(mockedUserService)
                .add("newuser@vinyl.com", "password");
        inOrderResponse.verify(mockedHttpServletResponse).setStatus(HttpServletResponse.SC_SEE_OTHER);
        inOrderResponse.verify(mockedHttpServletResponse).sendRedirect("/signIn");
    }

}