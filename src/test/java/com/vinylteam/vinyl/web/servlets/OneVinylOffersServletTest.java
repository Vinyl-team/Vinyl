package com.vinylteam.vinyl.web.servlets;

import com.vinylteam.vinyl.entity.Shop;
import com.vinylteam.vinyl.entity.Vinyl;
import com.vinylteam.vinyl.service.ShopService;
import com.vinylteam.vinyl.service.VinylService;
import com.vinylteam.vinyl.service.impl.DefaultShopService;
import com.vinylteam.vinyl.service.impl.DefaultVinylService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

class OneVinylOffersServletTest {

    @Test
    void doGetTest() throws IOException {
        //prepare
        HttpServletRequest mockedRequest = mock(HttpServletRequest.class);
        HttpServletResponse mockedResponse = mock(HttpServletResponse.class);
        VinylService mockedVinylService = mock(DefaultVinylService.class);
        ShopService mockedShopService = mock(DefaultShopService.class);
        PrintWriter printWriter = new PrintWriter(new StringWriter());
        Vinyl mockedUniqueVinyl = mock(Vinyl.class);
        Vinyl mockedVinyl = mock(Vinyl.class);
        Shop mockedShop = mock(Shop.class);
        List<Vinyl> vinylOffers = List.of(mockedVinyl);
        List<Integer> shopsIds = new ArrayList<>();
        List<Shop> shopsByIds = List.of(mockedShop);

        when(mockedRequest.getParameter("vinylId")).thenReturn("1");
        when(mockedVinylService.getUniqueById(1)).thenReturn(mockedUniqueVinyl);

        when(mockedUniqueVinyl.getVinylId()).thenReturn(1L);
        when(mockedVinylService.getManyByUniqueVinylId(1)).thenReturn(List.of(mockedVinyl));
        when(mockedVinylService.getListOfShopIds(vinylOffers)).thenReturn(shopsIds);
        when(mockedShopService.getManyByListOfIds(shopsIds)).thenReturn(shopsByIds);

        when(mockedUniqueVinyl.getArtist()).thenReturn("artist1");
        when(mockedVinylService.getManyUniqueByArtist("artist1")).thenReturn(vinylOffers);
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        InOrder inOrderVinylService = inOrder(mockedVinylService);
        //when
        OneVinylOffersServlet oneVinylOffersServlet = new OneVinylOffersServlet(mockedVinylService, mockedShopService);
        oneVinylOffersServlet.doGet(mockedRequest, mockedResponse);
        //then
        verify(mockedRequest).getParameter("vinylId");
        inOrderVinylService.verify(mockedVinylService).getUniqueById(1);

        inOrderVinylService.verify(mockedVinylService).getManyByUniqueVinylId(1);
        inOrderVinylService.verify(mockedVinylService).getListOfShopIds(vinylOffers);
        verify(mockedShopService).getManyByListOfIds(shopsIds);

        verify(mockedUniqueVinyl, times(4)).getArtist();
        inOrderVinylService.verify(mockedVinylService).getManyUniqueByArtist("artist1");
        verify(mockedResponse).getWriter();
    }

}