package com.vinylteam.vinyl;

import com.vinylteam.vinyl.dao.ShopDao;
import com.vinylteam.vinyl.dao.UserDao;
import com.vinylteam.vinyl.dao.VinylDao;
import com.vinylteam.vinyl.dao.jdbc.JdbcShopDao;
import com.vinylteam.vinyl.dao.jdbc.JdbcUserDao;
import com.vinylteam.vinyl.dao.jdbc.JdbcVinylDao;
import com.vinylteam.vinyl.security.SecurityService;
import com.vinylteam.vinyl.security.impl.DefaultSecurityService;
import com.vinylteam.vinyl.service.ShopService;
import com.vinylteam.vinyl.service.UserService;
import com.vinylteam.vinyl.service.VinylService;
import com.vinylteam.vinyl.service.impl.DefaultShopService;
import com.vinylteam.vinyl.service.impl.DefaultUserService;
import com.vinylteam.vinyl.service.impl.DefaultVinylService;
import com.vinylteam.vinyl.util.PropertiesReader;
import com.vinylteam.vinyl.web.filter.SecurityFilter;
import com.vinylteam.vinyl.web.handler.DefaultErrorHandler;
import com.vinylteam.vinyl.web.servlets.*;
import jakarta.servlet.DispatcherType;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.JarFileResource;
import org.eclipse.jetty.util.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;

public class Starter {
    private static final Logger logger = LoggerFactory.getLogger(Starter.class);
    private static final PropertiesReader propertiesReader = new PropertiesReader();
    private static final String RESOURCE_PATH = propertiesReader.getProperty("resource.path");

    public static void main(String[] args) throws Exception {
        /*DAO*/
        UserDao userDao = new JdbcUserDao();
        VinylDao vinylDao = new JdbcVinylDao();
        ShopDao shopDao = new JdbcShopDao();
        /*SERVICE*/
        SecurityService securityService = new DefaultSecurityService();
        UserService userService = new DefaultUserService(userDao, securityService);
        VinylService vinylService = new DefaultVinylService(vinylDao);
        ShopService shopService = new DefaultShopService(shopDao);
        /*UTIL, FILL IN DATABASE*/
/*        ShopsParser shopsParser = new ShopsParser();
        VinylSorter vinylSorter = new VinylSorter();
        List<VinylParser> vinylParserList = List.of(new VinylUaParser());
        List<Vinyl> allVinyls = shopsParser.getAllVinyls(vinylParserList);

        Map<String, List<Vinyl>> mapWithAllAndUniqueLists = vinylSorter.getMapWithAllAndUniqueLists(allVinyls);
        vinylService.addAllUnique(mapWithAllAndUniqueLists.get("unique"));
        vinylService.addAll(mapWithAllAndUniqueLists.get("all"));
        logger.info("Vinyls added to DB");*/

        /*WEB*/
        SignInServlet signInServlet = new SignInServlet(userService, securityService);
        SignUpServlet signUpServlet = new SignUpServlet(userService);
        CatalogueServlet catalogueServlet = new CatalogueServlet(vinylService);
        SearchResultsServlet searchResultsServlet = new SearchResultsServlet(vinylService);
        OneVinylOffersServlet oneVinylOffersServlet = new OneVinylOffersServlet(vinylService, shopService);
        ProfileServlet profileServlet = new ProfileServlet();
        HomeServlet homeServlet = new HomeServlet(securityService);
        Resource resource = JarFileResource.newClassPathResource(RESOURCE_PATH);
        ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servletContextHandler.setErrorHandler(new DefaultErrorHandler());
        servletContextHandler.setBaseResource(resource);
        servletContextHandler.addServlet(new ServletHolder(signInServlet), "/signIn");
        servletContextHandler.addServlet(new ServletHolder(signUpServlet), "/signUp");
        servletContextHandler.addServlet(new ServletHolder(catalogueServlet), "/catalog");
        servletContextHandler.addServlet(new ServletHolder(searchResultsServlet), "/search");
        servletContextHandler.addServlet(new ServletHolder(oneVinylOffersServlet), "/oneVinyl");
        servletContextHandler.addServlet(new ServletHolder(profileServlet), "/profile");
        servletContextHandler.addServlet(new ServletHolder(homeServlet), "/index");
        servletContextHandler.addFilter(new FilterHolder(new SecurityFilter(securityService)), "/signUp", EnumSet.of(DispatcherType.REQUEST));
        servletContextHandler.addServlet(DefaultServlet.class, "/*");
        Server server = new Server(Integer.parseInt(propertiesReader.getProperty("appPort")));
        server.setHandler(servletContextHandler);
        server.start();
        logger.info("Server started");
    }
}


















