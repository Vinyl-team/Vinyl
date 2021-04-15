package com.vinylteam.vinyl;

import com.vinylteam.vinyl.dao.jdbc.JdbcUserDao;
import com.vinylteam.vinyl.dao.jdbc.JdbcVinylDao;
import com.vinylteam.vinyl.entity.Vinyl;
import com.vinylteam.vinyl.security.impl.DefaultSecurityService;
import com.vinylteam.vinyl.service.impl.DefaultUserService;
import com.vinylteam.vinyl.service.impl.DefaultVinylService;
import com.vinylteam.vinyl.util.PropertiesReader;
import com.vinylteam.vinyl.util.VinylSorter;
import com.vinylteam.vinyl.web.handler.DefaultErrorHandler;
import com.vinylteam.vinyl.web.servlets.SignInServlet;
import com.vinylteam.vinyl.web.servlets.SignUpServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.PathResource;

import java.util.List;
import java.util.Map;

import static java.lang.Integer.parseInt;

public class Starter {

    public static void main(String[] args) throws Exception {

        PropertiesReader propertiesReader = new PropertiesReader();
        JdbcUserDao jdbcUserDao = new JdbcUserDao();
        JdbcVinylDao jdbcVinylDao = new JdbcVinylDao();
        DefaultSecurityService defaultSecurityService = new DefaultSecurityService();
        VinylSorter vinylSorter = new VinylSorter();
        DefaultUserService defaultUserService = new DefaultUserService(jdbcUserDao, defaultSecurityService);
        DefaultVinylService defaultVinylService = new DefaultVinylService(jdbcVinylDao);

        SignUpServlet signUpServlet = new SignUpServlet(defaultUserService);
        SignInServlet signInServlet = new SignInServlet(defaultUserService);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setBaseResource(new PathResource(Starter.class.getClassLoader().getResource("static")));

        context.setErrorHandler(new DefaultErrorHandler());
        context.addServlet(new ServletHolder(signUpServlet), "/signUp");
        context.addServlet(new ServletHolder(signInServlet), "/signIn");
        context.addServlet(DefaultServlet.class, "/");

        Map<String, List<Vinyl>> allAndUnique = vinylSorter.getMapWithAllAndUniqueLists();
        defaultVinylService.addAllUnique(allAndUnique.get("unique"));
        defaultVinylService.addAll(allAndUnique.get("all"));

        Server server = new Server(parseInt(propertiesReader.getAppPort()));
        server.setHandler(context);
        server.start();
    }

}
