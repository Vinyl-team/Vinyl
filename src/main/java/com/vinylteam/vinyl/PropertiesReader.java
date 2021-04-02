package com.vinylteam.vinyl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

public class PropertiesReader {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Properties properties = new Properties();

    public PropertiesReader() {
        String beginningOfErrorMessage = "Error during loading properties from ";
        try (InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream("application.properties")) {
            validateInputStream(inputStream);
            properties.load(inputStream);
        } catch (IOException e) {
            logger.error(beginningOfErrorMessage + "application.properties", e);
            throw new RuntimeException(e);
        }

        if (System.getenv("env") == null) {
            logger.info("Environment NULL");
            try (InputStream inputStream = getClass().getClassLoader()
                    .getResourceAsStream("dev.application.properties")) {
                validateInputStream(inputStream);
                properties.load(inputStream);
            } catch (IOException e) {
                logger.error(beginningOfErrorMessage + "dev.application.properties", e);
                throw new RuntimeException(e);
            }
        } else if (System.getenv("env").equals("PROD")) {
            logger.info("Environment PROD");
            URI databaseUri;
            try {
                databaseUri = new URI(System.getenv("DATABASE_URL"));
            } catch (URISyntaxException e) {
                logger.error("Error during getting databaseUri", e);
                throw new RuntimeException(e);
            }

            properties.setProperty("jdbc.user", databaseUri.getUserInfo().split(":")[0]);
            properties.setProperty("jdbc.password", databaseUri.getUserInfo().split(":")[1]);
            properties.setProperty("jdbc.url", "jdbc:postgresql://" +
                    databaseUri.getHost() + ':' +
                    databaseUri.getPort() + databaseUri.getPath());
            properties.setProperty("appPort", System.getenv("PORT"));

        } else if (System.getenv("env").equals("DEV")) {
            logger.info("Environment DEV");
            try (InputStream inputStream = getClass().getClassLoader()
                    .getResourceAsStream("travis.application.properties")) {
                logger.info("Got input stream: {}", inputStream);
                validateInputStream(inputStream);
                logger.info("Input stream validated: {}", inputStream);
                properties.load(inputStream);
                logger.info("Properties loaded: {}", properties);
            } catch (IOException e) {
                logger.error(beginningOfErrorMessage + "travis.application.properties", e);
                throw new RuntimeException(e);
            }
        }

        logger.info("DB properties: {}", properties);
    }

    public String getJdbcUser() {
        return properties.getProperty("jdbc.user");
    }

    public String getJdbcPassword() {
        return properties.getProperty("jdbc.password");
    }

    public String getJdbcUrl() {
        return properties.getProperty("jdbc.url");
    }

    public String getJdbcDriver() {
        return properties.getProperty("jdbc.driver");
    }

    public String getJdbcMaximumPoolSize() {
        return properties.getProperty("jdbc.maximum.pool.size");
    }

    public String getAppPort() {
        return properties.getProperty("appPort");
    }

    private void validateInputStream(InputStream inputStream) {
        logger.info("Input stream validation: {}", inputStream);
        if (inputStream == null) {
            RuntimeException e = new RuntimeException();
            logger.error(".properties file not found.", e);
            throw e;
        }
    }
}
