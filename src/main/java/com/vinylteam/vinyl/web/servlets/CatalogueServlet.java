package com.vinylteam.vinyl.web.servlets;

import com.vinylteam.vinyl.entity.Vinyl;
import com.vinylteam.vinyl.service.VinylService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

public class CatalogueServlet extends HttpServlet {

    private final VinylService vinylService;

    public CatalogueServlet(VinylService vinylService) {
        this.vinylService = vinylService;
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        List<Vinyl> randomUniqueVinyls = vinylService.getManyRandomUnique(50);
    }

}
