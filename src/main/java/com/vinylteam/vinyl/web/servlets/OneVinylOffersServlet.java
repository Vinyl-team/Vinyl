package com.vinylteam.vinyl.web.servlets;

import com.vinylteam.vinyl.entity.Offer;
import com.vinylteam.vinyl.entity.Shop;
import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.service.OfferService;
import com.vinylteam.vinyl.service.ShopService;
import com.vinylteam.vinyl.service.UniqueVinylService;
import com.vinylteam.vinyl.web.dto.OneVinylOffersServletResponse;
import com.vinylteam.vinyl.web.templater.PageGenerator;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OneVinylOffersServlet extends HttpServlet {

    private final UniqueVinylService uniqueVinylService;
    private final OfferService offerService;
    private final ShopService shopService;

    public OneVinylOffersServlet(UniqueVinylService uniqueVinylService, OfferService offerService, ShopService shopService) {
        this.uniqueVinylService = uniqueVinylService;
        this.offerService = offerService;
        this.shopService = shopService;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, String> attributes = new HashMap<>();
        User user = (User) request.getSession().getAttribute("user");
        if (user != null) {
            attributes.put("userRole", String.valueOf(user.getRole()));
        }

        long uniqueVinylId = Long.parseLong(request.getParameter("id"));
        UniqueVinyl uniqueVinyl = uniqueVinylService.findById(uniqueVinylId);

        List<Offer> offers = offerService.findManyByUniqueVinylId(uniqueVinyl.getId());
        List<Integer> shopIds = offerService.getListOfShopIds(offers);
        List<Shop> shopsFromOffers = shopService.getManyByListOfIds(shopIds);

        List<OneVinylOffersServletResponse> offersResponseList = new ArrayList<>();
        for (Offer offer : offers) {
            for (Shop shop : shopsFromOffers) {
                if (offer.getShopId() == shop.getId()) {
                    OneVinylOffersServletResponse offersResponse = new OneVinylOffersServletResponse();
                    offersResponse.setPrice(offer.getPrice());
                    offersResponse.setVinylLink(offer.getOfferLink());
                    offersResponse.setImageLink(shop.getImageLink());
                    offersResponseList.add(offersResponse);
                }
            }
        }

        offersResponseList.sort((offer1, offer2) -> (int) (offer1.getPrice() - offer2.getPrice()));

        String artist = uniqueVinyl.getArtist();
        List<UniqueVinyl> uniqueVinylsByArtist = uniqueVinylService.findManyByArtist(artist);

        List<UniqueVinyl> preparedListById = new ArrayList<>();
        preparedListById.add(0, uniqueVinyl);

        if (!uniqueVinylsByArtist.isEmpty()) {
            for (UniqueVinyl uniqueVinylByArtist : uniqueVinylsByArtist) {
                if (uniqueVinyl.getId() != uniqueVinylByArtist.getId()) {
                    preparedListById.add(uniqueVinylByArtist);
                }
            }
        }
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        PageGenerator.getInstance().process("vinyl", preparedListById, offersResponseList, attributes, response.getWriter());
    }

}
