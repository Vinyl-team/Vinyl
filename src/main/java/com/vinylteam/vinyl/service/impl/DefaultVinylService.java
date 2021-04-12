package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.VinylDao;
import com.vinylteam.vinyl.entity.Vinyl;
import com.vinylteam.vinyl.service.VinylService;

import java.util.List;
import java.util.Optional;

public class DefaultVinylService implements VinylService {
    private final VinylDao vinylDao;

    public DefaultVinylService(VinylDao vinylDao) {
        this.vinylDao = vinylDao;
    }

    @Override
    public void saveAllUnique(List<Vinyl> uniqueVinyls) {
        vinylDao.saveAllUnique(uniqueVinyls);
    }

    @Override
    public void saveAll(List<Vinyl> vinyls) {
        vinylDao.saveAll(vinyls);
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

    @Override
    public Optional<Vinyl> getByRelease(String vinylsRelease) {
        return vinylDao.getByRelease(vinylsRelease);
    }
}
