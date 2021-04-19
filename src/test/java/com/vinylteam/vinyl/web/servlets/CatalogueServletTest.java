package com.vinylteam.vinyl.web.servlets;


import com.vinylteam.vinyl.entity.Vinyl;
import com.vinylteam.vinyl.service.VinylService;
import com.vinylteam.vinyl.service.impl.DefaultVinylService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;

import static org.mockito.Mockito.*;

class CatalogueServletTest {

    @Test
    @DisplayName("Checks if all right methods are called")
    void doGetTest() throws IOException {
        HttpServletRequest mockedRequest = mock(HttpServletRequest.class);
        HttpServletResponse mockedResponse = mock(HttpServletResponse.class);
        VinylService mockedVinylService = mock(DefaultVinylService.class);
        PrintWriter mockedPrintWriter = mock(PrintWriter.class);

        when(mockedVinylService.getManyRandomUnique(50)).thenReturn(
                new ArrayList<>(Collections.nCopies(50, new Vinyl())));
        when(mockedResponse.getWriter()).thenReturn(mockedPrintWriter);

        CatalogueServlet catalogueServlet = new CatalogueServlet(mockedVinylService);
        catalogueServlet.doGet(mockedRequest, mockedResponse);

        verify(mockedVinylService).getManyRandomUnique(50);
        verify(mockedResponse).getWriter();
    }

}