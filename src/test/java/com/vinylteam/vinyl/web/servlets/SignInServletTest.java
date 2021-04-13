package com.vinylteam.vinyl.web.servlets;

import com.vinylteam.vinyl.entity.SignInCheckResult;
import com.vinylteam.vinyl.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.Mockito.*;

class SignInServletTest {

    private final UserService mockedUserService = mock(UserService.class);
    private final HttpServletRequest mockedHttpServletRequest = mock(HttpServletRequest.class);
    private final HttpServletResponse mockedHttpServletResponse = mock(HttpServletResponse.class);

    @Test
    @DisplayName("Checks if all right methods are called and response has code set to 200 and redirected to /home " +
            "when email and password are right, and user's email is verified(user's status==true).")
    void doPostWithVerifiedUserRightPasswordTest() throws IOException {
        when(mockedHttpServletRequest.getParameter("email")).thenReturn("verifieduser@vinyl.com");
        when(mockedHttpServletRequest.getParameter("password")).thenReturn("right password");
        when(mockedUserService.signInCheck("verifieduser@vinyl.com", "right password"))
                .thenReturn(SignInCheckResult.OK_VERIFIED);
        SignInServlet signInServlet = new SignInServlet(mockedUserService);
        InOrder inOrderRequest = inOrder(mockedHttpServletRequest);
        InOrder inOrderResponse = inOrder(mockedHttpServletResponse);

        signInServlet.doPost(mockedHttpServletRequest, mockedHttpServletResponse);

        inOrderRequest.verify(mockedHttpServletRequest).getParameter("email");
        inOrderRequest.verify(mockedHttpServletRequest).getParameter("password");
        verify(mockedUserService)
                .signInCheck("verifieduser@vinyl.com", "right password");
        inOrderResponse.verify(mockedHttpServletResponse).setStatus(HttpServletResponse.SC_OK);
        inOrderResponse.verify(mockedHttpServletResponse).sendRedirect("/home");
    }

    @Test
    @DisplayName("Checks if all right methods are called and response has code set to 400 and redirected to /signIn " +
            "when email is right but password is wrong, and user's email is verified(user's status==true).")
    void doPostWithVerifiedUserWrongPasswordTest() throws IOException {
        when(mockedHttpServletRequest.getParameter("email")).thenReturn("verifieduser@vinyl.com");
        when(mockedHttpServletRequest.getParameter("password")).thenReturn("wrong password");
        when(mockedUserService.signInCheck("verifieduser@vinyl.com", "wrong password"))
                .thenReturn(SignInCheckResult.FAIL);
        SignInServlet signInServlet = new SignInServlet(mockedUserService);
        InOrder inOrderRequest = inOrder(mockedHttpServletRequest);
        InOrder inOrderResponse = inOrder(mockedHttpServletResponse);

        signInServlet.doPost(mockedHttpServletRequest, mockedHttpServletResponse);

        inOrderRequest.verify(mockedHttpServletRequest).getParameter("email");
        inOrderRequest.verify(mockedHttpServletRequest).getParameter("password");
        verify(mockedUserService)
                .signInCheck("verifieduser@vinyl.com", "wrong password");
        inOrderResponse.verify(mockedHttpServletResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        inOrderResponse.verify(mockedHttpServletResponse).sendRedirect("/signIn");
    }

    @Test
    @DisplayName("Checks if all right methods are called and response has code set to 303 and redirected to /signIn " +
            "when email and password are right, and user's email is not verified(user's status==false).")
    void doPostWithNotVerifiedUserRightPasswordTest() throws IOException {
        when(mockedHttpServletRequest.getParameter("email")).thenReturn("notverifieduser@vinyl.com");
        when(mockedHttpServletRequest.getParameter("password")).thenReturn("right password");
        when(mockedUserService.signInCheck("notverifieduser@vinyl.com", "right password"))
                .thenReturn(SignInCheckResult.OK_NOT_VERIFIED);
        SignInServlet signInServlet = new SignInServlet(mockedUserService);
        InOrder inOrderRequest = inOrder(mockedHttpServletRequest);
        InOrder inOrderResponse = inOrder(mockedHttpServletResponse);

        signInServlet.doPost(mockedHttpServletRequest, mockedHttpServletResponse);

        inOrderRequest.verify(mockedHttpServletRequest).getParameter("email");
        inOrderRequest.verify(mockedHttpServletRequest).getParameter("password");
        verify(mockedUserService)
                .signInCheck("notverifieduser@vinyl.com", "right password");
        inOrderResponse.verify(mockedHttpServletResponse).setStatus(HttpServletResponse.SC_SEE_OTHER);
        inOrderResponse.verify(mockedHttpServletResponse).sendRedirect("/signIn");
    }

    @Test
    @DisplayName("Checks if all right methods are called and response has code set to 400 and redirected to /signIn " +
            "when email is right but password is wrong, and user's email is verified(user's status==true).")
    void doPostWithNotVerifiedUserWrongPasswordTest() throws IOException {
        when(mockedHttpServletRequest.getParameter("email")).thenReturn("notverifieduser@vinyl.com");
        when(mockedHttpServletRequest.getParameter("password")).thenReturn("wrong password");
        when(mockedUserService.signInCheck("notverifieduser@vinyl.com", "wrong password"))
                .thenReturn(SignInCheckResult.FAIL);
        SignInServlet signInServlet = new SignInServlet(mockedUserService);
        InOrder inOrderRequest = inOrder(mockedHttpServletRequest);
        InOrder inOrderResponse = inOrder(mockedHttpServletResponse);

        signInServlet.doPost(mockedHttpServletRequest, mockedHttpServletResponse);

        inOrderRequest.verify(mockedHttpServletRequest).getParameter("email");
        inOrderRequest.verify(mockedHttpServletRequest).getParameter("password");
        verify(mockedUserService)
                .signInCheck("notverifieduser@vinyl.com", "wrong password");
        inOrderResponse.verify(mockedHttpServletResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        inOrderResponse.verify(mockedHttpServletResponse).sendRedirect("/signIn");
    }

}