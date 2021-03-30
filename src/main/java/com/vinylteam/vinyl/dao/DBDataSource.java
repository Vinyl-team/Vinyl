package com.vinylteam.vinyl.dao;

import com.vinylteam.vinyl.PropertiesReader;
import com.vinylteam.vinyl.dao.jdbc.JdbcUserDao;
import com.vinylteam.vinyl.service.impl.DefaultUserService;
import org.postgresql.ds.PGSimpleDataSource;

public class DBDataSource {

    private final PGSimpleDataSource dataSource;

    public DBDataSource() {
        dataSource = new PGSimpleDataSource();
        PropertiesReader propertiesReader = new PropertiesReader();

        this.dataSource.setPassword(propertiesReader.getJdbcPassword());
        this.dataSource.setUser(propertiesReader.getJdbcUser());

        this.dataSource.setDatabaseName(propertiesReader.getJdbcDatabase());
        this.dataSource.setServerNames(new String[]{propertiesReader.getJdbcServer()});
        this.dataSource.setPortNumbers(new int[]{Integer.parseInt(propertiesReader.getJdbcPort())});
    }

    public DefaultUserService getServiceImplementation() {
        return new DefaultUserService(this.dataSource);
    }

    public JdbcUserDao getJDBCUserDAO() {
        return new JdbcUserDao(this.dataSource);
    }

    public PGSimpleDataSource getDataSource() {
        return dataSource;
    }

}
