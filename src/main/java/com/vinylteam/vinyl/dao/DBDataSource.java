package com.vinylteam.vinyl.dao;

import com.vinylteam.vinyl.PropertiesReader;
import com.vinylteam.vinyl.dao.jdbc.JdbcUserDao;
import com.vinylteam.vinyl.service.impl.DefaultUserService;
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
        config.setJdbcUrl(propertiesReader.getJdbcUrl());
        config.setUsername(propertiesReader.getJdbcUser());
        config.setPassword(propertiesReader.getJdbcPassword());
        config.setDriverClassName(propertiesReader.getJdbcDriver());
        config.setMaximumPoolSize(Integer.parseInt(propertiesReader.getJdbcMaximumPoolSize()));
        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            logger.error("Error during getting connection from data source", e);
            throw new RuntimeException(e);
        }
    }

    private DBDataSource() {
    }

    public DefaultUserService getServiceImplementation() {
        return new DefaultUserService();
    }

    public static JdbcUserDao getJDBCUserDAO() {
        return new JdbcUserDao();
    }

    public DataSource getDataSource() {
        return dataSource;
    }

}
