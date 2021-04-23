package com.vinylteam.vinyl.web.servlets;

import com.vinylteam.vinyl.entity.Vinyl;
import com.vinylteam.vinyl.service.VinylService;
import com.vinylteam.vinyl.web.templater.PageGenerator;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchResultsServlet extends HttpServlet {

    private final VinylService vinylService;

    public SearchResultsServlet(VinylService vinylService) {
        this.vinylService = vinylService;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, String> attributes = new HashMap<>();
        String matcher = request.getParameter("matcher");
        List<Vinyl> filteredUniqueVinyls = vinylService.getManyFilteredUnique(matcher);
        attributes.put("searchWord", matcher);
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        PageGenerator.getInstance().process("search", filteredUniqueVinyls, attributes, response.getWriter());
    }

}
