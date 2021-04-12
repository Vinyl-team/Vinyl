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
    private static final Logger logger = LoggerFactory.getLogger("com.vinylteam.vinyl.dao.DBDataSource");

    static {
        PropertiesReader propertiesReader = new PropertiesReader();
        logger.info("Properties reader read properties");
        config.setJdbcUrl(propertiesReader.getProperties().getProperty("jdbc.url"));
        config.setUsername(propertiesReader.getProperties().getProperty("jdbc.user"));
        config.setPassword(propertiesReader.getProperties().getProperty("jdbc.password"));
        config.setDriverClassName(propertiesReader.getProperties().getProperty("jdbc.driver"));
        config.setMaximumPoolSize(Integer.parseInt(propertiesReader.getProperties().getProperty("jdbc.maximum.pool.size")));
        dataSource = new HikariDataSource(config);
        logger.info("Data source: {}", dataSource);
    }

    public static Connection getConnection() {
        try {
            logger.info("Trying to get connection from data source: {}", dataSource);
            return dataSource.getConnection();
        } catch (SQLException e) {
            logger.error("Error during getting connection from data source", e);
            throw new RuntimeException(e);
        }
    }

    public DBDataSource() {
    }

    public static DataSource getDataSource() {
        return dataSource;
    }
}
