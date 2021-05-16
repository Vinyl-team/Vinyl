package com.vinylteam.vinyl.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

public class PropertiesReader {

    private static final Logger logger = LoggerFactory.getLogger(PropertiesReader.class);
    private static final Properties properties = new Properties();
    private static final String INDEPENDENT_PROPERTIES_FILE = "application.properties";

    public PropertiesReader() {
        String beginningOfErrorMessage = "Error during loading properties from";

        try (InputStream inputStream = PropertiesReader.class.getClassLoader()
                .getResourceAsStream(INDEPENDENT_PROPERTIES_FILE)) {
            logger.debug("Created input stream from file {'inputStream':{}, 'fileName':{}}",
                    inputStream, INDEPENDENT_PROPERTIES_FILE);
            validateInputStream(inputStream);
            properties.load(inputStream);
            logger.debug("Loaded properties from input stream {'properties':{}, 'inputStream':{}}",
                    properties, inputStream);
        } catch (IOException e) {
            logger.error("{} {'fileName':{}}", beginningOfErrorMessage, INDEPENDENT_PROPERTIES_FILE, e);
            throw new RuntimeException(e);
        }

        String dependentOnEnvPropertiesFile;
        String env = System.getenv("env");
        logger.debug("Created and initialised String env with the value " +
                "of environmental variable \"env\" {'env':{}}", env);
        if (env == null) {
            dependentOnEnvPropertiesFile = "dev.application.properties";

            try (InputStream inputStream = PropertiesReader.class.getClassLoader()
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
        } else if (env.equals("PROD")) {
            URI databaseUri;
            String databaseUrlVariable = System.getenv("RDS_DATABASE_URL");
            String portVariable = System.getenv("PORT");
            logger.debug("Read \"RDS_DATABASE_URL\" and \"PORT\" variables from environment {'RDS_DATABASE_URL':{}, 'PORT':{}}",
                    databaseUrlVariable, portVariable);
            try {
                databaseUri = new URI(databaseUrlVariable);
                logger.debug("Initialized databaseUri with URI from value of environmental variable \"RDS_DATABASE_URL\" " +
                        "{'databaseUri':{}, 'RDS_DATABASE_URI':{}}", databaseUri, databaseUrlVariable);
            } catch (URISyntaxException e) {
                logger.error("Error during initializing databaseUri with URI from value " +
                        "of environmental variable \"RDS_DATABASE_URL\" {'RDS_DATABASE_URL':{}}", databaseUrlVariable, e);
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
            try (InputStream inputStream = PropertiesReader.class.getClassLoader()
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
        logger.info("Loaded properties in {}", PropertiesReader.class);
    }

    public String getProperty(String propertyName) {
        return properties.getProperty(propertyName);
    }

    private void validateInputStream(InputStream inputStream) {
        if (inputStream == null) {
            RuntimeException e = new RuntimeException(".properties file not found, input stream is null");
            logger.error(".properties file not found, input stream is null", e);
            throw e;
        }
        logger.debug("Input stream is valid {'inputStream':{}}", inputStream);
    }

}
