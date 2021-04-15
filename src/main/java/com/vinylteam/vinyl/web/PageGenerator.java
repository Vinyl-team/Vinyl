package com.vinylteam.vinyl.web;

import com.vinylteam.vinyl.entity.Vinyl;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.Writer;
import java.util.List;

public class PageGenerator {
    private static PageGenerator pageGenerator;
    private TemplateEngine templateEngine;

    public static PageGenerator getInstance(){
        if (pageGenerator == null){
            pageGenerator = new PageGenerator();
        }
        return pageGenerator;
    }

    private PageGenerator(){
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setCharacterEncoding("UTF-8");
        templateEngine.setTemplateResolver(templateResolver);
    }

    public void process(String fileName, List<Vinyl> list, Writer writer){
        Context context = new Context();
        context.setVariable("vinylList", list);
        templateEngine.process(fileName, context, writer);
    }
}