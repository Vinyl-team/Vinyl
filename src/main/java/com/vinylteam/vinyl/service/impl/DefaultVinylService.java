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
        logger.debug("Starting constructor DefaultVinylService(VinylDao vinylDao) " +
                "with {'vinylDao':{}}", vinylDao);
        this.vinylDao = vinylDao;
        logger.debug("Initialized this.vinylDao " +
                "{'vinylDao':{}}", this.vinylDao);
    }

    @Override
    public void addAllUnique(List<Vinyl> uniqueVinyls) {
        logger.debug("Start of function DefaultVinylService.addAllUnique(List<Vinyl> uniqueVinyls)" +
                " with {'uniqueVinyls':{}}", uniqueVinyls);
        vinylDao.addAllUnique(uniqueVinyls);
        logger.debug("Attempted to add list of unique vinyls to db " +
                "{'uniqueVinyls':{}}", uniqueVinyls);
    }

    @Override
    public void addAll(List<Vinyl> vinyls) {
        logger.debug("Start of function DefaultVinylService.addAll(List<Vinyl> vinyls)" +
                " with {'vinyls':{}}", vinyls);
        vinylDao.addAll(vinyls);
        logger.debug("Attempted to add list of vinyls to db " +
                "{'vinyls':{}}", vinyls);
    }

    @Override
    public List<Vinyl> getAllUnique() {
        logger.debug("Start of function DefaultVinylService.getAllUnique()");
        List<Vinyl> gottenUniqueVinyls = vinylDao.getAllUnique();
        logger.debug("Resulting list of all unique vinyls from db is " +
                "{'uniqueVinyls':{}}", gottenUniqueVinyls);
        return gottenUniqueVinyls;
    }

    @Override
    public List<Vinyl> getAll() {
        logger.debug("Start of function DefaultVinylService.getAll()");
        List<Vinyl> gottenVinyls = vinylDao.getAll();
        logger.debug("Resulting list of all vinyls from db is " +
                "{'uniqueVinyls':{}}", gottenVinyls);
        return gottenVinyls;
    }

    @Override
    public Vinyl getUniqueById(long id) {
        logger.debug("Start of function DefaultVinylService.getUniqueById(long id) " +
                "with {'id':{}}", id);
        Vinyl gottenUniqueVinyl = vinylDao.getUniqueById(id);
        logger.debug("Resulting uniqueVinyl is {'uniqueVinyl':{}}", gottenUniqueVinyl);
        return gottenUniqueVinyl;
    }

    @Override
    public Vinyl getById(long id) {
        logger.debug("Start of function DefaultVinylService.getById(long id) " +
                "with {'id':{}}", id);
        Vinyl gottenVinyl = vinylDao.getById(id);
        logger.debug("Resulting vinyl is {'vinyl':{}}", gottenVinyl);
        return gottenVinyl;
    }
}
