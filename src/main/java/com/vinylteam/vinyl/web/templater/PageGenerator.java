package com.vinylteam.vinyl.web.templater;

import com.vinylteam.vinyl.entity.Vinyl;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

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

    public void process(String fileName, List<Vinyl> list, Writer writer) {
        Context context = getContext(list);
        templateEngine.process(fileName, context, writer);
    }

    private Context getContext(List<Vinyl> list) {
        Context context = new Context();
        List<Vinyl> firstVinylRow = new ArrayList<>();
        List<Vinyl> otherVinylRow = new ArrayList<>();
        context.setVariable("vinylList", list);
        if (!list.isEmpty()) {
            context.setVariable("firstVinyl", list.get(0));
        }
        if (list.size() > 1) {
            if (list.size() >= 5) {
                for (int i = 1; i < 5; i++) {
                    firstVinylRow.add(list.get(i));
                }
            } else {
                for (int i = 1; i < list.size(); i++) {
                    firstVinylRow.add(list.get(i));
                }
            }
            context.setVariable("firstVinylRow", firstVinylRow);
        }
        if (list.size() > 5) {
            for (int i = 5; i < list.size(); i++) {
                otherVinylRow.add(list.get(i));
            }
            context.setVariable("otherVinylRow", otherVinylRow);
        }
        return context;
    }
}