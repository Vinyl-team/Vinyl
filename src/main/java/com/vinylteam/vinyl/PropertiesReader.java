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
    private String user;
    private String password;
    private String jdbcUrl;

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
            try (InputStream inputStream = getClass().getClassLoader()
                    .getResourceAsStream("dev.application.properties")) {
                validateInputStream(inputStream);
                properties.load(inputStream);
            } catch (IOException e) {
                logger.error(beginningOfErrorMessage + "dev.application.properties", e);
                throw new RuntimeException(e);
            }
        } else if (System.getenv("env").equals("PROD")) {
            URI databaseUri;
            try {
                databaseUri = new URI(System.getenv("DATABASE_URL"));
            } catch (URISyntaxException e) {
                logger.error("Error during getting databaseUri", e);
                throw new RuntimeException(e);
            }
            user = databaseUri.getUserInfo().split(":")[0];
            password = databaseUri.getUserInfo().split(":")[1];
            jdbcUrl = "jdbc:postgresql://" +
                    databaseUri.getHost() + ':' +
                    databaseUri.getPort() + databaseUri.getPath();
        } else if (System.getenv("env").equals("DEV")) {
            try (InputStream inputStream = getClass().getClassLoader()
                    .getResourceAsStream("travis.application.properties")) {
                validateInputStream(inputStream);
                properties.load(inputStream);
            } catch (IOException e) {
                logger.error(beginningOfErrorMessage + "travis.application.properties", e);
                throw new RuntimeException(e);
            }
        }
    }

    public String getJdbcUser() {
        return user == null ? properties.getProperty("jdbc.user") : user;
    }

    public String getJdbcPassword() {
        return password == null ? properties.getProperty("jdbc.password") : password;
    }

    public String getJdbcUrl() {
        return jdbcUrl == null ? properties.getProperty("jdbc.url") : jdbcUrl;
    }

    public String getJdbcDriver() {
        return properties.getProperty("jdbc.driver");
    }

    public String getJdbcMaximumPoolSize() {
        return properties.getProperty("jdbc.maximum.pool.size");
    }

    public String getAppPort() {
        if (System.getenv("env") == null) {
            return properties.getProperty("appPort");
        } else {
            return System.getenv("PORT");
        }
    }

    private void validateInputStream(InputStream inputStream) {
        if (inputStream == null) {
            RuntimeException e = new RuntimeException();
            logger.error(".properties file not found.", e);
            throw e;
        }
    }
}
