package com.vinylteam.vinyl.web.servlets;

import com.vinylteam.vinyl.entity.Vinyl;
import com.vinylteam.vinyl.service.VinylService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

public class SearchResultsServlet extends HttpServlet {

    private final VinylService vinylService;

    public SearchResultsServlet(VinylService vinylService) {
        this.vinylService = vinylService;
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        String matcher = request.getParameter("matcher");
        List<Vinyl> filteredUniqueVinyls = vinylService.getManyFilteredUnique(matcher);

}

}
