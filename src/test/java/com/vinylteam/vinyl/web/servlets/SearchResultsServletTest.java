/*
package com.vinylteam.vinyl.web.servlets;

import com.vinylteam.vinyl.entity.Role;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.service.VinylService;
import com.vinylteam.vinyl.service.impl.DefaultVinylService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class SearchResultsServletTest {

    private final VinylService mockedVinylService = mock(DefaultVinylService.class);
    private final SearchResultsServlet searchResultsServlet = new SearchResultsServlet(mockedVinylService);

    private HttpServletRequest mockedRequest;
    private HttpServletResponse mockedResponse;
    private HttpSession mockedHttpSession;
    private User mockedUser;
    private PrintWriter mockedPrintWriter;
    private InOrder inOrderResponse;
    private InOrder inOrderRequest;

    @BeforeEach
    void beforeEach() {
        mockedRequest = mock(HttpServletRequest.class);
        mockedResponse = mock(HttpServletResponse.class);
        mockedHttpSession = mock(HttpSession.class);
        mockedUser = mock(User.class);
        mockedPrintWriter = mock(PrintWriter.class);
        inOrderResponse = inOrder(mockedResponse);
        inOrderRequest = inOrder(mockedRequest);
    }

    @Test
    @DisplayName("Checks if all right methods are called & user is authed")
    void doGetWithAuthedUserTest() throws IOException {
        //prepare
        when(mockedRequest.getParameter("matcher")).thenReturn("release1");
        when(mockedVinylService.getManyFilteredUnique("release1")).thenReturn(new ArrayList<>());
        when(mockedRequest.getSession()).thenReturn(mockedHttpSession);
        when(mockedHttpSession.getAttribute("user")).thenReturn(mockedUser);
        when(mockedUser.getRole()).thenReturn(Role.USER);
        when(mockedResponse.getWriter()).thenReturn(mockedPrintWriter);
        //when
        searchResultsServlet.doGet(mockedRequest, mockedResponse);
        //then
        inOrderRequest.verify(mockedRequest).getParameter("matcher");
        verify(mockedVinylService).getManyFilteredUnique("release1");
        inOrderRequest.verify(mockedRequest).getSession();
        verify(mockedHttpSession).getAttribute("user");
        assertEquals(mockedUser, mockedHttpSession.getAttribute("user"));
        verify(mockedUser).getRole();
        assertEquals(Role.USER, mockedUser.getRole());
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_OK);
        inOrderResponse.verify(mockedResponse).getWriter();
    }

    @Test
    @DisplayName("Checks if all right methods are called & user is not authed")
    void doGetWithNotAuthedUserTest() throws IOException {
        //prepare
        when(mockedRequest.getParameter("matcher")).thenReturn("release1");
        when(mockedVinylService.getManyFilteredUnique("release1")).thenReturn(new ArrayList<>());
        when(mockedRequest.getSession()).thenReturn(mockedHttpSession);
        when(mockedHttpSession.getAttribute("user")).thenReturn(null);
        when(mockedResponse.getWriter()).thenReturn(mockedPrintWriter);
        //when
        searchResultsServlet.doGet(mockedRequest, mockedResponse);
        //then
        inOrderRequest.verify(mockedRequest).getParameter("matcher");
        verify(mockedVinylService).getManyFilteredUnique("release1");
        inOrderRequest.verify(mockedRequest).getSession();
        verify(mockedHttpSession).getAttribute("user");
        assertNull(mockedHttpSession.getAttribute("user"));
        verify(mockedUser, times(0)).getRole();
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_OK);
        inOrderResponse.verify(mockedResponse).getWriter();
    }

}*/
