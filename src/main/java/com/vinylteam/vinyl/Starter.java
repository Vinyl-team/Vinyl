package com.vinylteam.vinyl;

import com.vinylteam.vinyl.dao.jdbc.JdbcUserDao;
import com.vinylteam.vinyl.security.impl.DefaultSecurityService;
import com.vinylteam.vinyl.service.impl.DefaultUserService;
import com.vinylteam.vinyl.util.PropertiesReader;
import com.vinylteam.vinyl.web.handler.DefaultErrorHandler;
import com.vinylteam.vinyl.web.servlets.SignInServlet;
import com.vinylteam.vinyl.web.servlets.SignUpServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.PathResource;

import static java.lang.Integer.parseInt;

public class Starter {

    public static void main(String[] args) throws Exception {

        DefaultUserService defaultUserService = new DefaultUserService(new JdbcUserDao(), new DefaultSecurityService());

        PropertiesReader propertiesReader = new PropertiesReader();

        SignUpServlet signUpServlet = new SignUpServlet(defaultUserService);
        SignInServlet signInServlet = new SignInServlet(defaultUserService);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setBaseResource(new PathResource(Starter.class.getClassLoader().getResource("static")));

        context.setErrorHandler(new DefaultErrorHandler());
        context.addServlet(new ServletHolder(signUpServlet), "/signUp");
        context.addServlet(new ServletHolder(signInServlet), "/signIn");
        context.addServlet(DefaultServlet.class, "/");

        Server server = new Server(parseInt(propertiesReader.getAppPort()));
        server.setHandler(context);
        server.start();
    }

}
