/*
package com.vinylteam.vinyl.web.servlets;

import com.vinylteam.vinyl.entity.Shop;
import com.vinylteam.vinyl.entity.Vinyl;
import com.vinylteam.vinyl.service.ShopService;
import com.vinylteam.vinyl.service.VinylService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

public class OneVinylOffersServlet extends HttpServlet {

    private final VinylService vinylService;
    private final ShopService shopService;

    public OneVinylOffersServlet(VinylService vinylService, ShopService shopService) {
        this.vinylService = vinylService;
        this.shopService = shopService;
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {

        long uniqueVinylId = Long.parseLong(request.getParameter("vinylId"));
        Vinyl uniqueVinyl = vinylService.getUniqueById(uniqueVinylId);
        List<Vinyl> vinylOffers = vinylService.getManyByUniqueVinylId(uniqueVinyl.getVinylId());
        List<Integer> shopIds = vinylService.getListOfShopIds(vinylOffers);
        List<Shop> shopsFromOffers = shopService.getManyByListOfIds(shopIds);

        String artist = uniqueVinyl.getArtist();
        List<Vinyl> uniqueVinylsByArtist = vinylService.getManyUniqueByArtist(artist);

    }

}
*/
