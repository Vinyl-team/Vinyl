package com.vinylteam.vinyl.web.servlets;

import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.service.VinylService;
import com.vinylteam.vinyl.service.impl.DefaultVinylService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import static org.mockito.Mockito.*;

class SearchResultsServletTest {

    @Test
    @DisplayName("Checks if all right methods are called & user is authed")
    void doGetWithAuthedUserTest() throws IOException {
        //prepare
        VinylService mockedVinylService = mock(DefaultVinylService.class);
        SearchResultsServlet searchResultsServlet = new SearchResultsServlet(mockedVinylService);

        HttpServletRequest mockedRequest = mock(HttpServletRequest.class);
        HttpServletResponse mockedResponse = mock(HttpServletResponse.class);
        HttpSession mockedHttpSession = mock(HttpSession.class);
        User mockedUser = mock(User.class);
        PrintWriter mockedPrintWriter = mock(PrintWriter.class);

        when(mockedRequest.getParameter("matcher")).thenReturn("release1");
        when(mockedVinylService.getManyFilteredUnique("release1")).thenReturn(new ArrayList<>());
        when(mockedRequest.getSession()).thenReturn(mockedHttpSession);
        when(mockedHttpSession.getAttribute("user")).thenReturn(mockedUser);
        when(mockedResponse.getWriter()).thenReturn(mockedPrintWriter);
        InOrder inOrderResponse = inOrder(mockedResponse);
        //when
        searchResultsServlet.doGet(mockedRequest, mockedResponse);
        //then
        verify(mockedRequest).getParameter("matcher");
        verify(mockedVinylService).getManyFilteredUnique("release1");
        verify(mockedRequest).getSession();
        verify(mockedHttpSession).getAttribute("user");
        verify(mockedUser).getRole();
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_OK);
        inOrderResponse.verify(mockedResponse).getWriter();
    }

    @Test
    @DisplayName("Checks if all right methods are called & user is authed")
    void doGetWithNonAuthedUserTest() throws IOException {
        //prepare
        VinylService mockedVinylService = mock(DefaultVinylService.class);
        SearchResultsServlet searchResultsServlet = new SearchResultsServlet(mockedVinylService);

        HttpServletRequest mockedRequest = mock(HttpServletRequest.class);
        HttpServletResponse mockedResponse = mock(HttpServletResponse.class);
        HttpSession mockedHttpSession = mock(HttpSession.class);
        User mockedUser = mock(User.class);
        PrintWriter mockedPrintWriter = mock(PrintWriter.class);

        when(mockedRequest.getParameter("matcher")).thenReturn("release1");
        when(mockedVinylService.getManyFilteredUnique("release1")).thenReturn(new ArrayList<>());
        when(mockedRequest.getSession()).thenReturn(mockedHttpSession);
        when(mockedHttpSession.getAttribute("user")).thenReturn(null);
        when(mockedResponse.getWriter()).thenReturn(mockedPrintWriter);
        InOrder inOrderResponse = inOrder(mockedResponse);
        //when
        searchResultsServlet.doGet(mockedRequest, mockedResponse);
        //then
        verify(mockedRequest).getParameter("matcher");
        verify(mockedVinylService).getManyFilteredUnique("release1");
        verify(mockedRequest).getSession();
        verify(mockedHttpSession).getAttribute("user");
        verify(mockedUser, times(0)).getRole();
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_OK);
        inOrderResponse.verify(mockedResponse).getWriter();
    }

}