package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.VinylDao;
import com.vinylteam.vinyl.dao.jdbc.JdbcVinylDao;
import com.vinylteam.vinyl.entity.Vinyl;
import com.vinylteam.vinyl.service.VinylService;
import org.postgresql.ds.PGSimpleDataSource;

import java.util.List;

public class DefaultVinylService implements VinylService {
    private final VinylDao vinylDao;

    public DefaultVinylService(VinylDao vinylDao) {
        this.vinylDao = vinylDao;
    }

    @Override
    public void addAllUnique(List<Vinyl> uniqueVinyls) {
        vinylDao.addAllUnique(uniqueVinyls);
    }

    @Override
    public void addAll(List<Vinyl> vinyls) {
        vinylDao.addAll(vinyls);
    }

    @Override
    public List<Vinyl> getAllUnique() {
        return vinylDao.getAllUnique();
    }

    @Override
    public List<Vinyl> getAll() {
        return vinylDao.getAll();
    }

    @Override
    public Vinyl getUniqueById(long id) {
        return vinylDao.getUniqueById(id);
    }

    @Override
    public Vinyl getById(long id) {
        return vinylDao.getById(id);
    }
}
