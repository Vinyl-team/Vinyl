package com.vinylteam.vinyl.dao;

import com.vinylteam.vinyl.PropertiesReader;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DBDataSource {

    private static final HikariDataSource dataSource;
    private static final HikariConfig config = new HikariConfig();
    private static final Logger logger = LoggerFactory.getLogger("com.vinylteam.vinyl.dao.DBDataSource");

    static {
        logger.debug("Started static initializer in DBDataSource");
        PropertiesReader propertiesReader = new PropertiesReader();
        logger.debug("Created properties reader, starts reading properties" +
                "{'propertiesReader':{}}", propertiesReader);
        config.setJdbcUrl(propertiesReader.getJdbcUrl());
        config.setUsername(propertiesReader.getJdbcUser());
        config.setPassword(propertiesReader.getJdbcPassword());
        config.setDriverClassName(propertiesReader.getJdbcDriver());
        config.setMaximumPoolSize(Integer.parseInt(propertiesReader.getJdbcMaximumPoolSize()));
        dataSource = new HikariDataSource(config);
        logger.info("Configured and created HikariDataSource object {'dataSource':{}}", dataSource);
    }

    public static Connection getConnection() {
        logger.debug("Start of function DBDataSource.getConnection()");
        try {
            Connection connection = dataSource.getConnection();
            logger.debug("Got connection from data source" +
                    " {'dataSource':{}, 'connection':{}}", dataSource, connection);
            return connection;
        } catch (SQLException e) {
            logger.error("Error during getting connection from data source {'dataSource':{}}", dataSource, e);
            throw new RuntimeException(e);
        }
    }

    public static DataSource getDataSource() {
        logger.debug("Start of function DBDataSource.getDataSource()");
        logger.debug("Returning data source {'dataSource':{}}", dataSource);
        return dataSource;
    }
}
