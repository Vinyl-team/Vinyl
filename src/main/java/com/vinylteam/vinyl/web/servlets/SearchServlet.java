package com.vinylteam.vinyl.web.servlets;

import com.vinylteam.vinyl.entity.Vinyl;
import com.vinylteam.vinyl.service.VinylService;
import com.vinylteam.vinyl.web.PageGenerator;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public class SearchServlet extends HttpServlet {

    private final VinylService vinylService;

    public SearchServlet(VinylService vinylService) {
        this.vinylService = vinylService;
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<Vinyl> randomUniqueVinyls = vinylService.getManyRandomUnique(50);
        PageGenerator.getInstance().process("search", randomUniqueVinyls, response.getWriter());
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
    }

}
