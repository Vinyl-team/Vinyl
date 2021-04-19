package com.vinylteam.vinyl.web.servlets;

import com.vinylteam.vinyl.service.VinylService;
import com.vinylteam.vinyl.service.impl.DefaultVinylService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import static org.mockito.Mockito.*;

class SearchResultsServletTest {

    @Test
    @DisplayName("Checks if all right methods are called")
    void doGetTest() throws IOException {
        //prepare
        HttpServletRequest mockedRequest = mock(HttpServletRequest.class);
        HttpServletResponse mockedResponse = mock(HttpServletResponse.class);
        VinylService mockedVinylService = mock(DefaultVinylService.class);
        PrintWriter mockedPrintWriter = mock(PrintWriter.class);

        when(mockedRequest.getParameter("matcher")).thenReturn("release1");
        when(mockedVinylService.getManyFilteredUnique("release1")).thenReturn(new ArrayList<>());
        when(mockedResponse.getWriter()).thenReturn(mockedPrintWriter);
        InOrder inOrderResponse = inOrder(mockedResponse);
        //when
        SearchResultsServlet searchResultsServlet = new SearchResultsServlet(mockedVinylService);
        searchResultsServlet.doGet(mockedRequest, mockedResponse);
        //then
        verify(mockedRequest).getParameter("matcher");
        verify(mockedVinylService).getManyFilteredUnique("release1");
        inOrderResponse.verify(mockedResponse).getWriter();
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_OK);
    }

}