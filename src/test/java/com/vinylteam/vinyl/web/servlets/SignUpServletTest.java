package com.vinylteam.vinyl.web.servlets;

import com.vinylteam.vinyl.service.UserService;
import com.vinylteam.vinyl.service.impl.DefaultUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.io.IOException;

import static org.mockito.Mockito.*;

class SignUpServletTest {

    private final UserService mockedUserService = mock(DefaultUserService.class);
    private final HttpServletRequest mockedHttpServletRequest = mock(HttpServletRequest.class);
    private final HttpServletResponse mockedHttpServletResponse = mock(HttpServletResponse.class);
    private final SignUpServlet signUpServlet = new SignUpServlet(mockedUserService);

    @Test
    @DisplayName("Checks if all right methods are called and response has code set to 400 and redirected to /signUp " +
            "when email already existed in database before.")
    void doPostWithExistingUserTest() throws IOException {
        //prepare
        when(mockedHttpServletRequest.getParameter("email")).thenReturn("existinguser@vinyl.com");
        when(mockedHttpServletRequest.getParameter("password")).thenReturn("password");
        when(mockedUserService.add("existinguser@vinyl.com", "password")).thenReturn(false);

        InOrder inOrderRequest = Mockito.inOrder(mockedHttpServletRequest);
        InOrder inOrderResponse = Mockito.inOrder(mockedHttpServletResponse);
        //when
        signUpServlet.doPost(mockedHttpServletRequest, mockedHttpServletResponse);
        //then
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
        //prepare
        when(mockedHttpServletRequest.getParameter("email")).thenReturn("newuser@vinyl.com");
        when(mockedHttpServletRequest.getParameter("password")).thenReturn("password");
        when(mockedUserService.add("newuser@vinyl.com", "password")).thenReturn(true);

        InOrder inOrderRequest = Mockito.inOrder(mockedHttpServletRequest);
        InOrder inOrderResponse = Mockito.inOrder(mockedHttpServletResponse);
        //when
        signUpServlet.doPost(mockedHttpServletRequest, mockedHttpServletResponse);
        //then
        inOrderRequest.verify(mockedHttpServletRequest).getParameter("email");
        inOrderRequest.verify(mockedHttpServletRequest).getParameter("password");
        verify(mockedUserService)
                .add("newuser@vinyl.com", "password");
        inOrderResponse.verify(mockedHttpServletResponse).setStatus(HttpServletResponse.SC_SEE_OTHER);
        inOrderResponse.verify(mockedHttpServletResponse).sendRedirect("/signIn");
    }

}