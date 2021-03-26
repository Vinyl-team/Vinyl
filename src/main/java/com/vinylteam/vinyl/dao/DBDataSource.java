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

        this.dataSource.setPassword(propertiesReader.getPassword());
        this.dataSource.setUser(propertiesReader.getUser());

        this.dataSource.setDatabaseName(propertiesReader.getDatabaseName());
        this.dataSource.setServerNames(new String[]{propertiesReader.getServerName()});
        this.dataSource.setPortNumbers(new int[]{Integer.parseInt(propertiesReader.getPgPort())});
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
