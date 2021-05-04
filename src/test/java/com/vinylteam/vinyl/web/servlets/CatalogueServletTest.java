package com.vinylteam.vinyl.web.servlets;


import com.vinylteam.vinyl.entity.Role;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.entity.Vinyl;
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
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CatalogueServletTest {

    private final VinylService mockedVinylService = mock(DefaultVinylService.class);
    private final CatalogueServlet catalogueServlet = new CatalogueServlet(mockedVinylService);

    private HttpServletRequest mockedRequest;
    private HttpServletResponse mockedResponse;
    private InOrder inOrderResponse;
    private InOrder inOrderRequest;
    private HttpSession mockedHttpSession;
    private User mockedUser;
    private PrintWriter mockedPrintWriter;

    @BeforeEach
    void beforeEach() {
        mockedRequest = mock(HttpServletRequest.class);
        mockedResponse = mock(HttpServletResponse.class);
        inOrderResponse = inOrder(mockedResponse);
        inOrderRequest = inOrder(mockedRequest);
        mockedHttpSession = mock(HttpSession.class);
        mockedUser = mock(User.class);
        mockedPrintWriter = mock(PrintWriter.class);
    }

    @Test
    @DisplayName("Checks if all right methods are called & session doesn't exist")
    void doGetNoSessionTest() throws IOException {
        //prepare
        when(mockedVinylService.getManyRandomUnique(50)).thenReturn(
                new ArrayList<>(Collections.nCopies(50, new Vinyl())));
        when(mockedRequest.getSession(false)).thenReturn(null);
        when(mockedResponse.getWriter()).thenReturn(mockedPrintWriter);
        //when
        catalogueServlet.doGet(mockedRequest, mockedResponse);
        //then
        verify(mockedVinylService).getManyRandomUnique(50);
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_OK);
        inOrderRequest.verify(mockedRequest).getSession(false);
        verify(mockedHttpSession, times(0)).getAttribute("user");
        verify(mockedUser, times(0)).getRole();
        inOrderResponse.verify(mockedResponse).getWriter();
    }

    @Test
    @DisplayName("Checks if all right methods are called & user is authed")
    void doGetWithAuthedUserTest() throws IOException {
        //prepare
        when(mockedVinylService.getManyRandomUnique(50)).thenReturn(
                new ArrayList<>(Collections.nCopies(50, new Vinyl())));
        when(mockedRequest.getSession(false)).thenReturn(mockedHttpSession);
        when(mockedHttpSession.getAttribute("user")).thenReturn(mockedUser);
        when(mockedUser.getRole()).thenReturn(Role.USER);
        when(mockedResponse.getWriter()).thenReturn(mockedPrintWriter);
        //when
        catalogueServlet.doGet(mockedRequest, mockedResponse);
        //then
        verify(mockedVinylService).getManyRandomUnique(50);
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_OK);
        inOrderRequest.verify(mockedRequest).getSession(false);
        verify(mockedHttpSession).getAttribute("user");
        verify(mockedUser, times(1)).getRole();
        assertEquals(Role.USER, mockedUser.getRole());
        inOrderResponse.verify(mockedResponse).getWriter();
    }

    @Test
    @DisplayName("Checks if all right methods are called & user is not authed")
    void doGetWithNotAuthedUserTest() throws IOException {
        //prepare
        when(mockedVinylService.getManyRandomUnique(50)).thenReturn(
                new ArrayList<>(Collections.nCopies(50, new Vinyl())));
        when(mockedRequest.getSession(false)).thenReturn(mockedHttpSession);
        when(mockedHttpSession.getAttribute("user")).thenReturn(null);
        when(mockedResponse.getWriter()).thenReturn(mockedPrintWriter);
        //when
        catalogueServlet.doGet(mockedRequest, mockedResponse);
        //then
        verify(mockedVinylService).getManyRandomUnique(50);
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_OK);
        inOrderRequest.verify(mockedRequest).getSession(false);
        verify(mockedHttpSession).getAttribute("user");
        verify(mockedUser, times(0)).getRole();
        inOrderResponse.verify(mockedResponse).getWriter();
    }

}