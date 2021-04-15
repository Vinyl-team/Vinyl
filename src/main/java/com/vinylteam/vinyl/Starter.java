package com.vinylteam.vinyl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinylteam.vinyl.dao.UserDao;
import com.vinylteam.vinyl.dao.VinylDao;
import com.vinylteam.vinyl.dao.jdbc.JdbcUserDao;
import com.vinylteam.vinyl.dao.jdbc.JdbcVinylDao;
import com.vinylteam.vinyl.entity.Vinyl;
import com.vinylteam.vinyl.security.SecurityService;
import com.vinylteam.vinyl.security.impl.DefaultSecurityService;
import com.vinylteam.vinyl.service.DiscogsService;
import com.vinylteam.vinyl.service.UserService;
import com.vinylteam.vinyl.service.VinylService;
import com.vinylteam.vinyl.service.impl.DefaultDiscogsService;
import com.vinylteam.vinyl.service.impl.DefaultUserService;
import com.vinylteam.vinyl.service.impl.DefaultVinylService;
import com.vinylteam.vinyl.util.PropertiesReader;
import com.vinylteam.vinyl.util.ShopsParser;
import com.vinylteam.vinyl.util.VinylParser;
import com.vinylteam.vinyl.util.VinylSorter;
import com.vinylteam.vinyl.util.impl.VinylUaParser;
import com.vinylteam.vinyl.web.servlets.SignInServlet;
import com.vinylteam.vinyl.web.servlets.SignUpServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.JarFileResource;
import org.eclipse.jetty.util.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class Starter {
    private static final Logger logger = LoggerFactory.getLogger(Starter.class);
    private static final String RESOURCE_PATH = "static";
    private static final PropertiesReader propertiesReader = new PropertiesReader();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) throws Exception {

        /*DAO*/
        UserDao userDao = new JdbcUserDao();
        VinylDao vinylDao = new JdbcVinylDao();

        /*SERVICE*/
        DiscogsService discogsService = new DefaultDiscogsService(propertiesReader.getProperty("consumer.key"),
                propertiesReader.getProperty("consumer.secret"), propertiesReader.getProperty("user.agent"),
                propertiesReader.getProperty("callback.url"), objectMapper);

        logger.info("Discogs service initialized");

        SecurityService securityService = new DefaultSecurityService();
        UserService userService = new DefaultUserService(userDao, securityService);
        VinylService vinylService = new DefaultVinylService(vinylDao, discogsService);

        /*UTIL*/
        ShopsParser shopsParser = new ShopsParser();
        VinylSorter vinylSorter = new VinylSorter();

        List<VinylParser> vinylParserList = List.of(new VinylUaParser());
        List<Vinyl> allVinyls = shopsParser.getAllVinyls(vinylParserList);
        Map<String, List<Vinyl>> mapWithAllAndUniqueLists = vinylSorter.getMapWithAllAndUniqueLists(allVinyls);

        /*FILL IN DATABASE*/
        vinylService.addAllUnique(mapWithAllAndUniqueLists.get("unique"));
        vinylService.addAll(mapWithAllAndUniqueLists.get("all"));

        logger.info("Vinyls added to DB");

        /*WEB*/
        SignInServlet signInServlet = new SignInServlet(userService);
        SignUpServlet signUpServlet = new SignUpServlet(userService);

        Resource resource = JarFileResource.newClassPathResource(RESOURCE_PATH);
        ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servletContextHandler.setBaseResource(resource);
        servletContextHandler.addServlet(new ServletHolder(signInServlet),"/login");
        servletContextHandler.addServlet(new ServletHolder(signUpServlet), "/registration");
        servletContextHandler.addServlet(DefaultServlet.class, "/");

        Server server = new Server(Integer.parseInt(propertiesReader.getProperty("appPort")));
        server.setHandler(servletContextHandler);
        server.start();

        logger.info("Server started");
    }

}
