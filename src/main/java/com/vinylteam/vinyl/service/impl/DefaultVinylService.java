/*
package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.entity.Vinyl;
import com.vinylteam.vinyl.service.VinylService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
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
        List<Vinyl> gottenUniqueVinyls = vinylDao.getAllUnique();
        logger.debug("Resulting list with that amount of unique vinyls from db is {'uniqueVinyls':{}}", gottenUniqueVinyls);
        return gottenUniqueVinyls;
    }

    @Override
    public List<Vinyl> getManyRandomUnique(int amount) {
        List<Vinyl> gottenRandomUniqueVinyls;
        if (amount > 0) {
            gottenRandomUniqueVinyls = vinylDao.getManyRandomUnique(amount);
        } else {
            logger.error("Amount is 0 or less, returning empty list {'amount':{}}", amount);
            gottenRandomUniqueVinyls = new ArrayList<>();
        }
        logger.debug("Resulting list of random unique vinyls is {'randomUniqueVinyls':{}}", gottenRandomUniqueVinyls);
        return gottenRandomUniqueVinyls;
    }

    @Override
    public List<Vinyl> getManyFilteredUnique(String matcher) {
        List<Vinyl> gottenFilteredUniqueVinyls;
        if (matcher != null) {
            gottenFilteredUniqueVinyls = vinylDao.getManyFilteredUnique(matcher);
        } else {
            logger.error("Matcher is null, returning empty list.");
            gottenFilteredUniqueVinyls = new ArrayList<>();
        }
        logger.debug("Resulting list of random unique vinyls is {'filteredUniqueVinyls':{}}", gottenFilteredUniqueVinyls);
        return gottenFilteredUniqueVinyls;
    }

    @Override
    public List<Vinyl> getManyUniqueByArtist(String artist) {
        List<Vinyl> gottenUniqueVinyls;
        if (artist != null) {
            gottenUniqueVinyls = vinylDao.getManyUniqueByArtist(artist);
        } else {
            logger.error("Artist is null, returning empty list.");
            gottenUniqueVinyls = new ArrayList<>();
        }
        logger.debug("Resulting list of random unique vinyls is {'uniqueVinyls':{}}", gottenUniqueVinyls);
        return gottenUniqueVinyls;
    }

    @Override
    public List<Vinyl> getAll() {
        List<Vinyl> gottenVinyls = vinylDao.getAll();
        logger.debug("Resulting list of all vinyls from db is {'vinyls':{}}", gottenVinyls);
        return gottenVinyls;
    }

    @Override
    public List<Vinyl> getManyByUniqueVinylId(long id) {
        List<Vinyl> gottenVinyls;
        if (id > 0) {
            gottenVinyls = vinylDao.getManyByUniqueVinylId(id);
        } else {
            IllegalArgumentException e = new IllegalArgumentException();
            logger.error("Id is 0 or less {'id':{}}", id, e);
            throw new RuntimeException(e);
        }
        logger.debug("Resulting list of vinyls is {'vinyls':{}}", gottenVinyls);
        return gottenVinyls;
    }

    @Override
    public Vinyl getUniqueById(long id) {
        Vinyl gottenUniqueVinyl;
        if (id > 0) {
            gottenUniqueVinyl = vinylDao.getUniqueById(id);
        } else {
            IllegalArgumentException e = new IllegalArgumentException();
            logger.error("Id is 0 or less {'id':{}}", id, e);
            throw new RuntimeException(e);
        }
        logger.debug("Resulting uniqueVinyl is {'uniqueVinyl':{}}", gottenUniqueVinyl);
        return gottenUniqueVinyl;
    }

    @Override
    public Vinyl getById(long id) {
        Vinyl gottenVinyl;
        if (id > 0) {
            gottenVinyl = vinylDao.getById(id);
        } else {
            IllegalArgumentException e = new IllegalArgumentException();
            logger.error("Id is 0 or less {'id':{}}", id, e);
            throw new RuntimeException(e);
        }
        logger.debug("Resulting vinyl is {'vinyl':{}}", gottenVinyl);
        return gottenVinyl;
    }

    @Override
    public List<Integer> getListOfShopIds(List<Vinyl> vinyls) {
        List<Integer> shopsIds = new ArrayList<>();
        if (vinyls != null) {
            for (Vinyl vinyl : vinyls) {
                if (!shopsIds.contains(Integer.valueOf(vinyl.getShopId()))) {
                    shopsIds.add(vinyl.getShopId());
                }
            }
        } else {
            logger.error("List of vinyls is null, returning empty list.");
        }
        logger.debug("Resulting list of shop id-s is {'shopIds':{}}", shopsIds);
        return shopsIds;
    }

}
*/
