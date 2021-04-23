package com.vinylteam.vinyl.web.servlets;

import com.vinylteam.vinyl.entity.SignInCheckResult;
import com.vinylteam.vinyl.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.mockito.Mockito.*;

class SignInServletTest {

    private final UserService mockedUserService = mock(UserService.class);
    private final HttpServletRequest mockedHttpServletRequest = mock(HttpServletRequest.class);
    private final HttpServletResponse mockedHttpServletResponse = mock(HttpServletResponse.class);
    private final PrintWriter printWriter = new PrintWriter(new StringWriter());
    private final SignInServlet signInServlet = new SignInServlet(mockedUserService);

    @Test
    @DisplayName("Checks if the response code is 200 when loading the page /signIn")
    void doGetTest() throws IOException {
        //prepare
        when(mockedHttpServletResponse.getWriter()).thenReturn(printWriter);
        //when
        signInServlet.doGet(mockedHttpServletRequest, mockedHttpServletResponse);
        //then
        verify(mockedHttpServletResponse).getWriter();
        verify(mockedHttpServletResponse).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    @DisplayName("Checks if all right methods are called and response has code set to 200 and redirected to / " +
            "when email and password are right, and user's email is verified(user's status==true).")
    void doPostWithVerifiedUserRightPasswordTest() throws IOException {
        //prepare
        when(mockedHttpServletRequest.getParameter("email")).thenReturn("verifieduser@vinyl.com");
        when(mockedHttpServletRequest.getParameter("password")).thenReturn("right password");
        when(mockedHttpServletResponse.getWriter()).thenReturn(printWriter);
        when(mockedUserService.signInCheck("verifieduser@vinyl.com", "right password"))
                .thenReturn(SignInCheckResult.OK_VERIFIED);

        InOrder inOrderRequest = inOrder(mockedHttpServletRequest);
        InOrder inOrderResponse = inOrder(mockedHttpServletResponse);
        //when
        signInServlet.doPost(mockedHttpServletRequest, mockedHttpServletResponse);
        //then
        inOrderRequest.verify(mockedHttpServletRequest).getParameter("email");
        inOrderRequest.verify(mockedHttpServletRequest).getParameter("password");
        verify(mockedUserService)
                .signInCheck("verifieduser@vinyl.com", "right password");
        inOrderResponse.verify(mockedHttpServletResponse).setStatus(HttpServletResponse.SC_OK);
        inOrderResponse.verify(mockedHttpServletResponse).sendRedirect("/");
    }

    @Test
    @DisplayName("Checks if all right methods are called and response has code set to 400 and redirected to /signIn " +
            "when email is right but password is wrong, and user's email is verified(user's status==true).")
    void doPostWithVerifiedUserWrongPasswordTest() throws IOException {
        //prepare
        when(mockedHttpServletRequest.getParameter("email")).thenReturn("verifieduser@vinyl.com");
        when(mockedHttpServletRequest.getParameter("password")).thenReturn("wrong password");
        when(mockedHttpServletResponse.getWriter()).thenReturn(printWriter);
        when(mockedUserService.signInCheck("verifieduser@vinyl.com", "wrong password"))
                .thenReturn(SignInCheckResult.FAIL);

        InOrder inOrderRequest = inOrder(mockedHttpServletRequest);
        InOrder inOrderResponse = inOrder(mockedHttpServletResponse);
        //when
        signInServlet.doPost(mockedHttpServletRequest, mockedHttpServletResponse);
        //then
        inOrderRequest.verify(mockedHttpServletRequest).getParameter("email");
        inOrderRequest.verify(mockedHttpServletRequest).getParameter("password");
        verify(mockedUserService)
                .signInCheck("verifieduser@vinyl.com", "wrong password");
        inOrderResponse.verify(mockedHttpServletResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(mockedHttpServletResponse).getWriter();
    }

    @Test
    @DisplayName("Checks if all right methods are called and response has code set to 303 and redirected to /signIn " +
            "when email and password are right, and user's email is not verified(user's status==false).")
    void doPostWithNotVerifiedUserRightPasswordTest() throws IOException {
        //prepare
        when(mockedHttpServletRequest.getParameter("email")).thenReturn("notverifieduser@vinyl.com");
        when(mockedHttpServletRequest.getParameter("password")).thenReturn("right password");
        when(mockedHttpServletResponse.getWriter()).thenReturn(printWriter);
        when(mockedUserService.signInCheck("notverifieduser@vinyl.com", "right password"))
                .thenReturn(SignInCheckResult.OK_NOT_VERIFIED);

        InOrder inOrderRequest = inOrder(mockedHttpServletRequest);
        InOrder inOrderResponse = inOrder(mockedHttpServletResponse);
        //when
        signInServlet.doPost(mockedHttpServletRequest, mockedHttpServletResponse);
        //then
        inOrderRequest.verify(mockedHttpServletRequest).getParameter("email");
        inOrderRequest.verify(mockedHttpServletRequest).getParameter("password");
        verify(mockedUserService)
                .signInCheck("notverifieduser@vinyl.com", "right password");
        inOrderResponse.verify(mockedHttpServletResponse).setStatus(HttpServletResponse.SC_SEE_OTHER);
        verify(mockedHttpServletResponse).getWriter();
    }

}