package com.vinylteam.vinyl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesReader {

    public PropertiesReader() {
        InputStream inputStream;

        inputStream = getClass().getClassLoader().getResourceAsStream("application.properties");

        setProperties(inputStream);
    }

    private final Properties properties = new Properties();

    private void setProperties(InputStream inputStream) {
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Was unable to load properties", e);
        }
    }

    public String getUser() {
        return properties.getProperty("user");
    }

    public String getPassword() {
        return properties.getProperty("password");
    }

    public String getDatabaseName() {
        return properties.getProperty("databaseName");
    }

    public String getServerName() {
        return properties.getProperty("serverName");
    }

    public String getPgPort() {
        return properties.getProperty("pgPort");
    }

    public String getAppPort() {
        if (System.getenv("env") == null) {
            return properties.getProperty("appPort");
        } else {
            return System.getenv("PORT");
        }
    }

}
