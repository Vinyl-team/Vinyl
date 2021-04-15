package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.VinylDao;
import com.vinylteam.vinyl.entity.Vinyl;
import com.vinylteam.vinyl.service.VinylService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DefaultVinylService implements VinylService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final VinylDao vinylDao;

    public DefaultVinylService(VinylDao vinylDao) {
        this.vinylDao = vinylDao;
    }

    @Override
    public void addAllUnique(List<Vinyl> uniqueVinyls) {
        vinylDao.addAllUnique(uniqueVinyls);
        logger.debug("Attempted to add list of unique vinyls to db {'uniqueVinyls':{}}", uniqueVinyls);
    }

    @Override
    public void addAll(List<Vinyl> vinyls) {
        vinylDao.addAll(vinyls);
        logger.debug("Attempted to add list of vinyls to db {'vinyls':{}}", vinyls);
    }

    @Override
    public List<Vinyl> getAllUnique() {
        List<Vinyl> gottenRandomUniqueVinyls = vinylDao.getAllUnique();
        logger.debug("Resulting list of random unique vinyls is {'randomUniqueVinyls':{}}", gottenRandomUniqueVinyls);
        return gottenRandomUniqueVinyls;
    }

    @Override
    public List<Vinyl> getManyRandomUnique(int amount) {
        List<Vinyl> gottenUniqueVinyls = vinylDao.getAllUnique();
        logger.debug("Resulting list of amount unique vinyls from db is {'uniqueVinyls':{}}", gottenUniqueVinyls);
        return gottenUniqueVinyls;
    }

    @Override
    public List<Vinyl> getAll() {
        List<Vinyl> gottenVinyls = vinylDao.getAll();
        logger.debug("Resulting list of all vinyls from db is {'uniqueVinyls':{}}", gottenVinyls);
        return gottenVinyls;
    }

    @Override
    public Vinyl getUniqueById(long id) {
        Vinyl gottenUniqueVinyl = vinylDao.getUniqueById(id);
        logger.debug("Resulting uniqueVinyl is {'uniqueVinyl':{}}", gottenUniqueVinyl);
        return gottenUniqueVinyl;
    }

    @Override
    public Vinyl getById(long id) {
        Vinyl gottenVinyl = vinylDao.getById(id);
        logger.debug("Resulting vinyl is {'vinyl':{}}", gottenVinyl);
        return gottenVinyl;
    }

}
