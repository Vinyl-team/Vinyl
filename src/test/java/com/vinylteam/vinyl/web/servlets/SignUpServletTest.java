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
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.mockito.Mockito.*;

class SignUpServletTest {

    private final UserService mockedUserService = mock(DefaultUserService.class);
    private final HttpServletRequest mockedHttpServletRequest = mock(HttpServletRequest.class);
    private final HttpServletResponse mockedHttpServletResponse = mock(HttpServletResponse.class);
    private final SignUpServlet signUpServlet = new SignUpServlet(mockedUserService);
    private final PrintWriter printWriter = new PrintWriter(new StringWriter());
    private final InOrder inOrderRequest = Mockito.inOrder(mockedHttpServletRequest);
    private final InOrder inOrderResponse = Mockito.inOrder(mockedHttpServletResponse);

    @Test
    @DisplayName("Checks if all right methods are called")
    void doGetTest() throws IOException {
        //prepare
        PrintWriter printWriter = new PrintWriter(new StringWriter());
        when(mockedHttpServletResponse.getWriter()).thenReturn(printWriter);
        //when
        signUpServlet.doGet(mockedHttpServletRequest, mockedHttpServletResponse);
        //then
        inOrderResponse.verify(mockedHttpServletResponse).setContentType("text/html;charset=utf-8");
        inOrderResponse.verify(mockedHttpServletResponse).setStatus(HttpServletResponse.SC_OK);
        verify(mockedHttpServletResponse).getWriter();
    }

    @Test
    @DisplayName("Checks if all right methods are called and response has code set to 400 and redirected to /signUp " +
            "when email already exist in db.")
    void doPostWithExistingUserTest() throws IOException {
        //prepare
        when(mockedHttpServletRequest.getParameter("email")).thenReturn("existinguser@vinyl.com");
        when(mockedHttpServletRequest.getParameter("password")).thenReturn("password");
        when(mockedUserService.add("existinguser@vinyl.com", "password")).thenReturn(false);
        when(mockedHttpServletResponse.getWriter()).thenReturn(printWriter);
        //when
        signUpServlet.doPost(mockedHttpServletRequest, mockedHttpServletResponse);
        //then
        inOrderResponse.verify(mockedHttpServletResponse).setContentType("text/html;charset=utf-8");
        inOrderRequest.verify(mockedHttpServletRequest).getParameter("email");
        inOrderRequest.verify(mockedHttpServletRequest).getParameter("password");
        verify(mockedUserService, times(1))
                .add("existinguser@vinyl.com", "password");
        inOrderResponse.verify(mockedHttpServletResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(mockedHttpServletResponse).getWriter();
    }

    @Test
    @DisplayName("Checks if all right methods are called and response has code set to 303 and redirected to /signIn " +
            "when email did not exist in database before.")
    void doPostWithNewUserTest() throws IOException {
        //prepare
        when(mockedHttpServletRequest.getParameter("email")).thenReturn("newuser@vinyl.com");
        when(mockedHttpServletRequest.getParameter("password")).thenReturn("password");
        when(mockedUserService.add("newuser@vinyl.com", "password")).thenReturn(true);
        when(mockedHttpServletResponse.getWriter()).thenReturn(printWriter);
        //when
        signUpServlet.doPost(mockedHttpServletRequest, mockedHttpServletResponse);
        //then
        inOrderResponse.verify(mockedHttpServletResponse).setContentType("text/html;charset=utf-8");
        inOrderRequest.verify(mockedHttpServletRequest).getParameter("email");
        inOrderRequest.verify(mockedHttpServletRequest).getParameter("password");
        verify(mockedUserService, times(1))
                .add("newuser@vinyl.com", "password");
        inOrderResponse.verify(mockedHttpServletResponse).setStatus(HttpServletResponse.SC_SEE_OTHER);
        verify(mockedHttpServletResponse).getWriter();
    }

}