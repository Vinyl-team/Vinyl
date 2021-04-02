package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.VinylDao;
import com.vinylteam.vinyl.dao.jdbc.JdbcVinylDao;
import com.vinylteam.vinyl.entity.Vinyl;
import com.vinylteam.vinyl.service.VinylService;
import org.postgresql.ds.PGSimpleDataSource;

import java.util.List;

public class DefaultVinylService implements VinylService {
    private final VinylDao vinylDao;

    public DefaultVinylService(PGSimpleDataSource dataSource) {
        this.vinylDao = new JdbcVinylDao(dataSource);
    }

    @Override
    public void saveUniqueVinyls(List<Vinyl> uniqueVinyls) {
        vinylDao.saveUniqueVinyls(uniqueVinyls);
    }

    @Override
    public void saveVinyls(List<Vinyl> vinyls) {
        vinylDao.saveVinyls(vinyls);
    }

    @Override
    public List<Vinyl> getUniqueVinyls() {
        return vinylDao.getUniqueVinyls();
    }

    @Override
    public List<Vinyl> getVinyls() {
        return vinylDao.getVinyls();
    }

    @Override
    public Vinyl getUniqueVinylById(long id) {
        return vinylDao.getUniqueVinylById(id);
    }

    @Override
    public Vinyl getVinylById(long id) {
        return vinylDao.getVinylById(id);
    }
}
