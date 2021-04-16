package com.vinylteam.vinyl.web.servlets;

import com.vinylteam.vinyl.entity.Vinyl;
import com.vinylteam.vinyl.service.VinylService;
import com.vinylteam.vinyl.web.PageGenerator;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public class SearchResultsServlet extends HttpServlet {

    private final VinylService vinylService;

    public SearchResultsServlet(VinylService vinylService) {
        this.vinylService = vinylService;
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String matcher = request.getParameter("matcher");
        List<Vinyl> filteredUniqueVinyls = vinylService.getManyFilteredUnique(matcher);
        PageGenerator.getInstance().process("search", filteredUniqueVinyls, response.getWriter());
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);

    }

}
