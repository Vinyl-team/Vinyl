package com.vinylteam.vinyl.web.servlets;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.io.IOException;

import static org.mockito.Mockito.*;

class SignOutServletTest {
    @Test
    @DisplayName("Checks if all right methods are called")
    void doGetTest() throws IOException {
        //prepare
        HttpServletRequest mockedRequest = mock(HttpServletRequest.class);
        HttpServletResponse mockedResponse = mock(HttpServletResponse.class);
        HttpSession mockedHttpSession = mock(HttpSession.class);
        SignOutServlet signOutServlet = new SignOutServlet();
        InOrder inOrderRequest = inOrder(mockedRequest);
        InOrder inOrderResponse = inOrder(mockedResponse);

        when(mockedRequest.getSession()).thenReturn(mockedHttpSession);
        //when
        signOutServlet.doGet(mockedRequest, mockedResponse);
        //then
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderRequest.verify(mockedRequest).getSession();
        verify(mockedHttpSession).invalidate();
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_OK);
        inOrderResponse.verify(mockedResponse).sendRedirect("/");
    }
}