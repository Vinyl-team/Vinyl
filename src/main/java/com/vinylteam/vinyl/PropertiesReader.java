package com.vinylteam.vinyl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesReader {

    Logger logger = LoggerFactory.getLogger(getClass());

    public PropertiesReader() {

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            properties.load(inputStream);
        } catch (IOException e) {
            logger.error("Error during loading properties", e);
            throw new RuntimeException(e);
        }
    }

    private final Properties properties = new Properties();

    public String getJdbcUser() {
        return properties.getProperty("jdbc.user");
    }

    public String getJdbcPassword() {
        return properties.getProperty("jdbc.password");
    }

    public String getJdbcDatabase() {
        return properties.getProperty("jdbc.database");
    }

    public String getJdbcServer() {
        return properties.getProperty("jdbc.server");
    }

    public String getJdbcPort() {
        return properties.getProperty("jdbc.port");
    }

    public String getAppPort() {
        if (System.getenv("env") == null) {
            return properties.getProperty("appPort");
        } else {
            return System.getenv("PORT");
        }
    }

}