package com.vinylteam.vinyl.web.servlets;

import com.vinylteam.vinyl.entity.Vinyl;
import com.vinylteam.vinyl.service.VinylService;
import com.vinylteam.vinyl.service.impl.DefaultVinylService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;

import static org.mockito.Mockito.*;

class CatalogueServletTest {

    @Test
    @DisplayName("Checks if all right methods are called")
    void doGetTest() {
        HttpServletRequest mockedRequest = mock(HttpServletRequest.class);
        HttpServletResponse mockedResponse = mock(HttpServletResponse.class);
        VinylService mockedVinylService = mock(DefaultVinylService.class);
        when(mockedVinylService.getManyRandomUnique(50)).thenReturn(
                new ArrayList<Vinyl>(Collections.nCopies(50, new Vinyl())));

        CatalogueServlet catalogueServlet = new CatalogueServlet(mockedVinylService);
        catalogueServlet.doGet(mockedRequest, mockedResponse);

        verify(mockedVinylService).getManyRandomUnique(50);
    }

}