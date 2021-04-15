package com.vinylteam.vinyl.util;

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
    private final String independentPropertiesFile = "application.properties";

    public PropertiesReader() {
        String beginningOfErrorMessage = "Error during loading properties from ";
        try (InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream("application.properties")) {
            logger.debug("Created input stream from file {'inputStream':{}, 'fileName':{}}",
                    inputStream, independentPropertiesFile);
            validateInputStream(inputStream);
            properties.load(inputStream);
            logger.debug("Loaded properties from input stream {'properties':{}, 'inputStream':{}}",
                    properties, inputStream);
        } catch (IOException e) {
            logger.error("{} {'fileName':{}}", beginningOfErrorMessage, independentPropertiesFile, e);
            throw new RuntimeException(e);
        }

        String dependentOnEnvPropertiesFile;
        String env = System.getenv("env");
        logger.debug("Created and initialised String env with the value " +
                "of environmental variable \"env\" {'env':{}}", env);
        if (System.getenv("env") == null) {
            dependentOnEnvPropertiesFile = "dev.application.properties";

            try (InputStream inputStream = getClass().getClassLoader()
                    .getResourceAsStream("dev.application.properties")) {
                logger.debug("Created input stream from file {'inputStream':{}, 'fileName':{}}",
                        inputStream, dependentOnEnvPropertiesFile);
                validateInputStream(inputStream);
                properties.load(inputStream);
                logger.debug("Loaded properties from input stream {'properties':{}, 'inputStream':{}}",
                        properties, inputStream);
            } catch (IOException e) {
                logger.error("{} {'fileName':{}}", beginningOfErrorMessage, dependentOnEnvPropertiesFile, e);
                throw new RuntimeException(e);
            }
        } else if (env.equals("PROD")) {
            URI databaseUri;
            String databaseUrlVariable = System.getenv("DATABASE_URL");
            String portVariable = System.getenv("PORT");
            logger.debug("Read \"DATABASE_URL\" and \"PORT\" variables from environment {'DATABASE_URL':{}, 'PORT':{}}",
                    databaseUrlVariable, portVariable);
            try {
                databaseUri = new URI(databaseUrlVariable);
                logger.debug("Initialized databaseUri with URI from value of environmental variable \"DATABASE_URL\" " +
                        "{'databaseUri':{}, 'DATABASE_URI':{}}", databaseUri, databaseUrlVariable);
            } catch (URISyntaxException e) {
                logger.error("Error during initializing databaseUri with URI from value " +
                        "of environmental variable \"DATABASE_URL\" {'DATABASE_URI':{}}", databaseUrlVariable, e);
                throw new RuntimeException(e);
            }

            properties.setProperty("jdbc.user", databaseUri.getUserInfo().split(":")[0]);
            properties.setProperty("jdbc.password", databaseUri.getUserInfo().split(":")[1]);
            properties.setProperty("jdbc.url", "jdbc:postgresql://" +
                    databaseUri.getHost() + ':' +
                    databaseUri.getPort() + databaseUri.getPath());
            properties.setProperty("appPort", portVariable);
            logger.debug("Set properties with data from databaseUri and value of environmental variable \"PORT\" " +
                    "{'properties':{}, 'databaseUri':{}, 'PORT':{}}", properties, databaseUri, portVariable);
        } else if (env.equals("DEV")) {
            dependentOnEnvPropertiesFile = "travis.application.properties";
            try (InputStream inputStream = getClass().getClassLoader()
                    .getResourceAsStream(dependentOnEnvPropertiesFile)) {
                logger.debug("Created input stream from file {'inputStream':{}, 'fileName':{}}",
                        inputStream, dependentOnEnvPropertiesFile);
                validateInputStream(inputStream);
                properties.load(inputStream);
                logger.debug("Loaded properties from input stream {'properties':{}, 'inputStream':{}}",
                        properties, inputStream);
            } catch (IOException e) {
                logger.error("{} {'fileName':{}}", beginningOfErrorMessage, dependentOnEnvPropertiesFile, e);
                throw new RuntimeException(e);
            }
        }
        logger.info("Loaded properties in {}", getClass());
    }

    public String getProperty(String propertyName) {
        return properties.getProperty(propertyName);
    }

    private void validateInputStream(InputStream inputStream) {
        if (inputStream == null) {
            RuntimeException e = new RuntimeException();
            logger.error(".properties file not found.", e);
            throw e;
        }
    }
}
