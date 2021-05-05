package com.vinylteam.vinyl.web.templater;

import com.vinylteam.vinyl.entity.Vinyl;
import com.vinylteam.vinyl.web.dto.OneVinylOffersServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PageGenerator {
    private static PageGenerator pageGenerator;
    private TemplateEngine templateEngine;

    public static PageGenerator getInstance() {
        if (pageGenerator == null) {
            pageGenerator = new PageGenerator();
        }
        return pageGenerator;
    }

    private PageGenerator() {
        templateEngine = new TemplateEngine();
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setCharacterEncoding("UTF-8");
        templateEngine.setTemplateResolver(templateResolver);
    }

    public void process(String fileName, Writer writer) {
        process(fileName, new ArrayList<>(), new ArrayList<>(), new HashMap<>(), writer);
    }

    public void process(String fileName, Map<String, String> attributes, Writer writer) {
        process(fileName, new ArrayList<>(), new ArrayList<>(), attributes, writer);
    }

    public void process(String fileName, List<Vinyl> list, Writer writer) {
        process(fileName, list, new ArrayList<>(), new HashMap<>(), writer);
    }

    public void process(String fileName, List<Vinyl> list, List<OneVinylOffersServletResponse> vinylOffersList, Writer writer) {
        process(fileName, list, vinylOffersList, new HashMap<>(), writer);
    }

    public void process(String fileName, List<Vinyl> list, Map<String, String> attributes, Writer writer) {
        process(fileName, list, new ArrayList<>(), attributes, writer);
    }

    public void process(String fileName, List<Vinyl> vinylList, List<OneVinylOffersServletResponse> vinylOffersList, Map<String, String> attributes, Writer writer) {
        Context context = getContext(vinylList, vinylOffersList, attributes);
        templateEngine.process(fileName, context, writer);
    }

    private Context getContext(List<Vinyl> vinylList, List<OneVinylOffersServletResponse> vinylOffersList, Map<String, String> attributes) {
        Context context = new Context();
        List<Vinyl> firstVinylRow = new ArrayList<>();
        List<Vinyl> otherVinylRow = new ArrayList<>();
        List<Vinyl> vinylsByArtist = new ArrayList<>();

        String searchWord = attributes.get("searchWord");
        if (searchWord != null) {
            context.setVariable("matcher", searchWord);
        }

        String userRole = attributes.get("userRole");
        if (userRole != null) {
            context.setVariable("userRole", userRole);
        }

        String email = attributes.get("email");
        if (email != null) {
            context.setVariable("email", email);
        }

        String discogsUserName = attributes.get("discogsUserName");
        if (discogsUserName != null) {
            context.setVariable("discogsUserName", discogsUserName);
        }

        String message = attributes.get("message");
        if (message != null) {
            context.setVariable("message", message);
        }

        /** for catalog page*/
        context.setVariable("vinylList", vinylList);

        /** for search & vinyl pages */
        if (!vinylList.isEmpty()) {
            context.setVariable("firstVinyl", vinylList.get(0));
        }

        /** for search page */
        if (vinylList.size() > 1) {
            if (vinylList.size() >= 7) {
                for (int i = 1; i < 7; i++) {
                    firstVinylRow.add(vinylList.get(i));
                }
            } else {
                for (int i = 1; i < vinylList.size(); i++) {
                    firstVinylRow.add(vinylList.get(i));
                }
            }
            context.setVariable("firstVinylRow", firstVinylRow);
        }
        if (vinylList.size() > 7) {
            for (int i = 7; i < vinylList.size(); i++) {
                otherVinylRow.add(vinylList.get(i));
            }
            context.setVariable("otherVinylRow", otherVinylRow);
        }

        /** for vinyl page */
        if (vinylList.size() > 1) {
            for (int i = 1; i < vinylList.size(); i++) {
                vinylsByArtist.add(vinylList.get(i));
            }
            context.setVariable("vinylsByArtist", vinylsByArtist);
        }

        context.setVariable("vinylOffersList", vinylOffersList);

        return context;
    }
}