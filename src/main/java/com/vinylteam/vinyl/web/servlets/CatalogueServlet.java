package com.vinylteam.vinyl.web.servlets;

import com.vinylteam.vinyl.entity.User;
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
import java.util.Optional;

public class CatalogueServlet extends HttpServlet {

    private final VinylService vinylService;

    public CatalogueServlet(VinylService vinylService) {
        this.vinylService = vinylService;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<Vinyl> randomUniqueVinyls = vinylService.getManyRandomUnique(50);
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        Map<String, String> attributes = new HashMap<>();
        User user = (User) request.getSession().getAttribute("user");
        if (user != null){
            attributes.put("userRole", String.valueOf(user.getRole()));
        }
        PageGenerator.getInstance().process("catalog", randomUniqueVinyls, attributes, response.getWriter());
    }

}
