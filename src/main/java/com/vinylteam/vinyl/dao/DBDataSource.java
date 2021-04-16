package com.vinylteam.vinyl.dao;

import com.vinylteam.vinyl.util.PropertiesReader;
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
    private static final Logger logger = LoggerFactory.getLogger(DBDataSource.class);

    static {
        logger.debug("Started static initializer in DBDataSource");
        PropertiesReader propertiesReader = new PropertiesReader();
        logger.info("Properties reader read properties");
        config.setJdbcUrl(propertiesReader.getProperty("jdbc.url"));
        config.setUsername(propertiesReader.getProperty("jdbc.user"));
        config.setPassword(propertiesReader.getProperty("jdbc.password"));
        config.setDriverClassName(propertiesReader.getProperty("jdbc.driver"));
        config.setMaximumPoolSize(Integer.parseInt(propertiesReader.getProperty("jdbc.maximum.pool.size")));
        dataSource = new HikariDataSource(config);
        logger.info("Configured and created HikariDataSource object {'dataSource':{}}", dataSource);
    }

    private DBDataSource() {
    }

    public static Connection getConnection() {
        try {
            Connection connection = dataSource.getConnection();
            logger.debug("Got connection from data source {'dataSource':{}, 'connection':{}}", dataSource, connection);
            return connection;
        } catch (SQLException e) {
            logger.error("Error during getting connection from data source {'dataSource':{}}",
                    dataSource, e);
            throw new RuntimeException(e);
        }
    }

    public static DataSource getDataSource() {
        logger.debug("Returning data source {'dataSource':{}}", dataSource);
        return dataSource;
    }

}
