package com.vinylteam.vinyl.web.servlets;

import com.vinylteam.vinyl.discogs4j.entity.DiscogsVinylInfo;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.entity.Vinyl;
import com.vinylteam.vinyl.service.DiscogsService;
import com.vinylteam.vinyl.service.VinylService;
import com.vinylteam.vinyl.web.templater.PageGenerator;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.text.html.ListView;
import java.io.IOException;
import java.util.*;

public class CatalogueServlet extends HttpServlet {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final VinylService vinylService;
    private final DiscogsService discogsService;

    public CatalogueServlet(VinylService vinylService, DiscogsService discogsService) {
        this.vinylService = vinylService;
        this.discogsService = discogsService;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String discogsUserName;
        List<Vinyl> randomUniqueVinyls = vinylService.getManyRandomUnique(50);
        List<Vinyl> forShowing = new ArrayList<>();
        List<Vinyl> allUniqueVinyl = vinylService.getAllUnique();
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        Map<String, String> attributes = new HashMap<>();
        HttpSession session = request.getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute("user");
            if (user != null) {
                attributes.put("userRole", user.getRole().toString());
                discogsUserName = user.getDiscogsUserName();
                forShowing = discogsService.getDiscogsMatchList(discogsUserName, allUniqueVinyl);
            }
        }
        if (!forShowing.isEmpty()) {
            PageGenerator.getInstance().process("catalog", forShowing, attributes, response.getWriter());
        } else {
            PageGenerator.getInstance().process("catalog", randomUniqueVinyls, attributes, response.getWriter());
        }
    }
}
