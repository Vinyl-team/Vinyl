package com.vinylteam.vinyl.web.servlets;

import com.vinylteam.vinyl.entity.SignInCheckResult;
import com.vinylteam.vinyl.service.impl.DefaultUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.Mockito.*;

class SignInServletTest {

    private DefaultUserService mockedDefaultUserService;
    private HttpServletRequest mockedHttpServletRequest;
    private HttpServletResponse mockedHttpServletResponse;

    @BeforeEach
    void beforeEach() {
        mockedDefaultUserService = null;
        mockedHttpServletRequest = null;
        mockedHttpServletResponse = null;

        mockedDefaultUserService = mock(DefaultUserService.class);
        mockedHttpServletRequest = mock(HttpServletRequest.class);
        mockedHttpServletResponse = mock(HttpServletResponse.class);
    }

    @Test
    void doPostWithVerifiedUserRightPasswordTest() throws IOException {
        when(mockedHttpServletRequest.getParameter("email")).thenReturn("verifieduser@vinyl.com");
        when(mockedHttpServletRequest.getParameter("password")).thenReturn("right password");
        when(mockedDefaultUserService.signInCheck("verifieduser@vinyl.com", "right password"))
                .thenReturn(SignInCheckResult.OK_VERIFIED);
        SignInServlet signInServlet = new SignInServlet(mockedDefaultUserService);
        InOrder inOrderRequest = inOrder(mockedHttpServletRequest);
        InOrder inOrderResponse = inOrder(mockedHttpServletResponse);

        signInServlet.doPost(mockedHttpServletRequest, mockedHttpServletResponse);

        inOrderRequest.verify(mockedHttpServletRequest).getParameter("email");
        inOrderRequest.verify(mockedHttpServletRequest).getParameter("password");
        verify(mockedDefaultUserService)
                .signInCheck("verifieduser@vinyl.com", "right password");
        inOrderResponse.verify(mockedHttpServletResponse).setStatus(HttpServletResponse.SC_OK);
        inOrderResponse.verify(mockedHttpServletResponse).sendRedirect("/home");
    }

    @Test
    void doPostWithVerifiedUserWrongPasswordTest() throws IOException {
        when(mockedHttpServletRequest.getParameter("email")).thenReturn("verifieduser@vinyl.com");
        when(mockedHttpServletRequest.getParameter("password")).thenReturn("wrong password");
        when(mockedDefaultUserService.signInCheck("verifieduser@vinyl.com", "wrong password"))
                .thenReturn(SignInCheckResult.FAIL);
        SignInServlet signInServlet = new SignInServlet(mockedDefaultUserService);
        InOrder inOrderRequest = inOrder(mockedHttpServletRequest);
        InOrder inOrderResponse = inOrder(mockedHttpServletResponse);

        signInServlet.doPost(mockedHttpServletRequest, mockedHttpServletResponse);

        inOrderRequest.verify(mockedHttpServletRequest).getParameter("email");
        inOrderRequest.verify(mockedHttpServletRequest).getParameter("password");
        verify(mockedDefaultUserService)
                .signInCheck("verifieduser@vinyl.com", "wrong password");
        inOrderResponse.verify(mockedHttpServletResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        inOrderResponse.verify(mockedHttpServletResponse).sendRedirect("/signIn");
    }

    @Test
    void doPostWithNotVerifiedUserRightPasswordTest() throws IOException {
        when(mockedHttpServletRequest.getParameter("email")).thenReturn("notverifieduser@vinyl.com");
        when(mockedHttpServletRequest.getParameter("password")).thenReturn("right password");
        when(mockedDefaultUserService.signInCheck("notverifieduser@vinyl.com", "right password"))
                .thenReturn(SignInCheckResult.OK_NOT_VERIFIED);
        SignInServlet signInServlet = new SignInServlet(mockedDefaultUserService);
        InOrder inOrderRequest = inOrder(mockedHttpServletRequest);
        InOrder inOrderResponse = inOrder(mockedHttpServletResponse);

        signInServlet.doPost(mockedHttpServletRequest, mockedHttpServletResponse);

        inOrderRequest.verify(mockedHttpServletRequest).getParameter("email");
        inOrderRequest.verify(mockedHttpServletRequest).getParameter("password");
        verify(mockedDefaultUserService)
                .signInCheck("notverifieduser@vinyl.com", "right password");
        inOrderResponse.verify(mockedHttpServletResponse).setStatus(HttpServletResponse.SC_SEE_OTHER);
        inOrderResponse.verify(mockedHttpServletResponse).sendRedirect("/verify");
    }

    @Test
    void doPostWithNotVerifiedUserWrongPasswordTest() throws IOException {
        when(mockedHttpServletRequest.getParameter("email")).thenReturn("notverifieduser@vinyl.com");
        when(mockedHttpServletRequest.getParameter("password")).thenReturn("wrong password");
        when(mockedDefaultUserService.signInCheck("notverifieduser@vinyl.com", "wrong password"))
                .thenReturn(SignInCheckResult.FAIL);
        SignInServlet signInServlet = new SignInServlet(mockedDefaultUserService);
        InOrder inOrderRequest = inOrder(mockedHttpServletRequest);
        InOrder inOrderResponse = inOrder(mockedHttpServletResponse);

        signInServlet.doPost(mockedHttpServletRequest, mockedHttpServletResponse);

        inOrderRequest.verify(mockedHttpServletRequest).getParameter("email");
        inOrderRequest.verify(mockedHttpServletRequest).getParameter("password");
        verify(mockedDefaultUserService)
                .signInCheck("notverifieduser@vinyl.com", "wrong password");
        inOrderResponse.verify(mockedHttpServletResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        inOrderResponse.verify(mockedHttpServletResponse).sendRedirect("/signIn");
    }
}