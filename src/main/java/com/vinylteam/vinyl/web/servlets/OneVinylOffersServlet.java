package com.vinylteam.vinyl.web.servlets;

import com.vinylteam.vinyl.entity.Shop;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.entity.Vinyl;
import com.vinylteam.vinyl.service.DiscogsService;
import com.vinylteam.vinyl.service.ShopService;
import com.vinylteam.vinyl.service.VinylService;
import com.vinylteam.vinyl.service.impl.DefaultDiscogsService;
import com.vinylteam.vinyl.web.dto.OneVinylOffersServletResponse;
import com.vinylteam.vinyl.web.templater.PageGenerator;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OneVinylOffersServlet extends HttpServlet {

    private final VinylService vinylService;
    private final ShopService shopService;
    private final DiscogsService discogsService;

    public OneVinylOffersServlet(VinylService vinylService, ShopService shopService, DiscogsService discogsService) {
        this.vinylService = vinylService;
        this.shopService = shopService;
        this.discogsService = discogsService;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        Map<String, String> attributes = new HashMap<>();
        HttpSession session = request.getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute("user");
            if (user != null) {
                attributes.put("userRole", user.getRole().toString());
            }
        }

        long uniqueVinylId = Long.parseLong(request.getParameter("vinylId"));
        Vinyl uniqueVinyl = vinylService.getUniqueById(uniqueVinylId);

        List<Vinyl> vinylOffers = vinylService.getManyByUniqueVinylId(uniqueVinyl.getVinylId());
        List<Integer> shopIds = vinylService.getListOfShopIds(vinylOffers);
        List<Shop> shopsFromOffers = shopService.getManyByListOfIds(shopIds);

        try {
            String discogsLink = discogsService.getDiscogsLink(uniqueVinyl.getArtist(),
                    uniqueVinyl.getRelease(), uniqueVinyl.getFullNameVinyl());
            attributes.put("discogsLink", discogsLink);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        List<OneVinylOffersServletResponse> vinylOffersResponseList = new ArrayList<>();
        for (Vinyl vinyl : vinylOffers) {
            for (Shop shop : shopsFromOffers) {
                if (vinyl.getShopId() == shop.getId()) {
                    OneVinylOffersServletResponse vinylOffersResponse = new OneVinylOffersServletResponse();
                    vinylOffersResponse.setPrice(vinyl.getPrice());
                    vinylOffersResponse.setVinylLink(vinyl.getVinylLink());
                    vinylOffersResponse.setImageLink(shop.getImageLink());
                    vinylOffersResponseList.add(vinylOffersResponse);
                }
            }
        }

        vinylOffersResponseList.sort((vinyl1, vinyl2) -> (int) (vinyl1.getPrice() - vinyl2.getPrice()));

        String artist = uniqueVinyl.getArtist();
        List<Vinyl> uniqueVinylsByArtist = vinylService.getManyUniqueByArtist(artist);

        List<Vinyl> preparedListById = new ArrayList<>();
        preparedListById.add(0, uniqueVinyl);

        if (!uniqueVinylsByArtist.isEmpty()) {
            for (Vinyl vinyl : uniqueVinylsByArtist) {
                if (uniqueVinyl.getVinylId() != vinyl.getVinylId()) {
                    preparedListById.add(vinyl);
                }
            }
        }
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        PageGenerator.getInstance().process("vinyl", preparedListById, vinylOffersResponseList, attributes, response.getWriter());
    }

}
