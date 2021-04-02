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
    private static final PropertiesReader propertiesReader = new PropertiesReader();

    static {
        logger.info("Properties reader read properties");
        config.setJdbcUrl(propertiesReader.getJdbcUrl());
        config.setUsername(propertiesReader.getJdbcUser());
        config.setPassword(propertiesReader.getJdbcPassword());
        config.setDriverClassName(propertiesReader.getJdbcDriver());
        config.setMaximumPoolSize(Integer.parseInt(propertiesReader.getJdbcMaximumPoolSize()));
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

    private DBDataSource() {
    }

    public static DataSource getDataSource() {
        return dataSource;
    }
}
