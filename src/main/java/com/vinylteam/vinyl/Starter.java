package com.vinylteam.vinyl;

import com.vinylteam.vinyl.dao.OfferDao;
import com.vinylteam.vinyl.dao.ShopDao;
import com.vinylteam.vinyl.dao.UniqueVinylDao;
import com.vinylteam.vinyl.dao.UserDao;
import com.vinylteam.vinyl.dao.jdbc.JdbcOfferDao;
import com.vinylteam.vinyl.dao.jdbc.JdbcShopDao;
import com.vinylteam.vinyl.dao.jdbc.JdbcUniqueVinylDao;
import com.vinylteam.vinyl.dao.jdbc.JdbcUserDao;
import com.vinylteam.vinyl.security.SecurityService;
import com.vinylteam.vinyl.security.impl.DefaultSecurityService;
import com.vinylteam.vinyl.service.OfferService;
import com.vinylteam.vinyl.service.ShopService;
import com.vinylteam.vinyl.service.UniqueVinylService;
import com.vinylteam.vinyl.service.UserService;
import com.vinylteam.vinyl.service.impl.DefaultOfferService;
import com.vinylteam.vinyl.service.impl.DefaultShopService;
import com.vinylteam.vinyl.service.impl.DefaultUniqueVinylService;
import com.vinylteam.vinyl.service.impl.DefaultUserService;
import com.vinylteam.vinyl.util.*;
import com.vinylteam.vinyl.util.impl.VinylUaParser;
import com.vinylteam.vinyl.web.filter.SecurityFilter;
import com.vinylteam.vinyl.web.handler.DefaultErrorHandler;
import com.vinylteam.vinyl.web.servlets.*;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
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
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Starter {
    private static final Logger logger = LoggerFactory.getLogger(Starter.class);
    private static final PropertiesReader propertiesReader = new PropertiesReader();
    private static final String RESOURCE_PATH = propertiesReader.getProperty("resource.path");

    public static void main(String[] args) throws Exception {
//DAO
        HikariDataSource dataSource;
        HikariConfig config = new HikariConfig();
        PropertiesReader propertiesReader = new PropertiesReader();
        config.setJdbcUrl(propertiesReader.getProperty("jdbc.url"));
        config.setUsername(propertiesReader.getProperty("jdbc.user"));
        config.setPassword(propertiesReader.getProperty("jdbc.password"));
        config.setDriverClassName(propertiesReader.getProperty("jdbc.driver"));
        config.setMaximumPoolSize(Integer.parseInt(propertiesReader.getProperty("jdbc.maximum.pool.size")));
        dataSource = new HikariDataSource(config);
        logger.info("Configed dataSourse");

        UserDao userDao = new JdbcUserDao(dataSource);
        UniqueVinylDao uniqueVinylDao = new JdbcUniqueVinylDao(dataSource);
        OfferDao offerDao = new JdbcOfferDao(dataSource);
        ShopDao shopDao = new JdbcShopDao(dataSource);
//SERVICE

        SecurityService securityService = new DefaultSecurityService();
        UserService userService = new DefaultUserService(userDao, securityService);
        UniqueVinylService uniqueVinylService = new DefaultUniqueVinylService(uniqueVinylDao);
        OfferService offerService = new DefaultOfferService(offerDao);
        ShopService shopService = new DefaultShopService(shopDao);
//UTIL, FILL IN DATABASE

        ShopsParser shopsParser = new ShopsParser();
        RawOffersSorter rawOffersSorter = new RawOffersSorter();
        List<VinylParser> vinylParsers = List.of(new VinylUaParser());
        Updater updater = new Updater(uniqueVinylService, offerService, shopsParser, vinylParsers, rawOffersSorter);
        TimerTask updateTask = new TimerTask() {
            @Override
            public void run() {
                logger.info("Started updater task");
                updater.updateUniqueVinylsRewriteOffers();
                logger.info("Finished updater task");
            }
        };

        Timer updateTimer = new Timer("Update Timer");
        long updatePeriod = Long.parseLong(propertiesReader.getProperty("updatePeriod"));
        updateTimer.scheduleAtFixedRate(updateTask, 0, updatePeriod);
//WEB

        SecurityFilter securityFilter = new SecurityFilter();
        SignInServlet signInServlet = new SignInServlet(userService);
        SignUpServlet signUpServlet = new SignUpServlet(userService);
        CatalogueServlet catalogueServlet = new CatalogueServlet(uniqueVinylService);
        SearchResultsServlet searchResultsServlet = new SearchResultsServlet(uniqueVinylService);
        OneVinylOffersServlet oneVinylOffersServlet = new OneVinylOffersServlet(uniqueVinylService, offerService, shopService);
        SignOutServlet signOutServlet = new SignOutServlet();
        ProfileServlet profileServlet = new ProfileServlet();
        EditProfileServlet editProfileServlet = new EditProfileServlet(securityService, userService);
        HomeServlet homeServlet = new HomeServlet();

        Resource resource = JarFileResource.newClassPathResource(RESOURCE_PATH);
        ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servletContextHandler.setErrorHandler(new DefaultErrorHandler());
        servletContextHandler.setBaseResource(resource);

        servletContextHandler.addFilter(new FilterHolder(securityFilter), "/*",
                EnumSet.of(DispatcherType.REQUEST));
        servletContextHandler.addServlet(new ServletHolder(signInServlet), "/signIn");
        servletContextHandler.addServlet(new ServletHolder(signUpServlet), "/signUp");
        servletContextHandler.addServlet(new ServletHolder(catalogueServlet), "/catalog");
        servletContextHandler.addServlet(new ServletHolder(searchResultsServlet), "/search");
        servletContextHandler.addServlet(new ServletHolder(oneVinylOffersServlet), "/oneVinyl");
        servletContextHandler.addServlet(new ServletHolder(signOutServlet), "/signOut");
        servletContextHandler.addServlet(new ServletHolder(profileServlet), "/profile");
        servletContextHandler.addServlet(new ServletHolder(editProfileServlet), "/editProfile");
        servletContextHandler.addServlet(new ServletHolder(homeServlet), "");

        servletContextHandler.addServlet(DefaultServlet.class, "/*");

        Server server = new Server(Integer.parseInt(propertiesReader.getProperty("appPort")));
        server.setHandler(servletContextHandler);
        server.start();
        logger.info("Server started");
    }
}


















