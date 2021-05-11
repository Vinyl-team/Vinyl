package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.UniqueVinylDao;
import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.service.UniqueVinylService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class DefaultUniqueVinylService implements UniqueVinylService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final UniqueVinylDao vinylDao;

    public DefaultUniqueVinylService(UniqueVinylDao vinylDao) {
        this.vinylDao = vinylDao;
    }

    @Override
    public List<UniqueVinyl> findAll() {
        List<UniqueVinyl> gottenUniqueVinyls = vinylDao.findAll();
        logger.debug("Resulting list with that amount of unique vinyls from db is {'uniqueVinyls':{}}", gottenUniqueVinyls);
        return gottenUniqueVinyls;
    }

    @Override
    public List<UniqueVinyl> findManyRandom(int amount) {
        List<UniqueVinyl> gottenRandomUniqueVinyls;
        if (amount > 0) {
            gottenRandomUniqueVinyls = vinylDao.findManyRandom(amount);
        } else {
            logger.error("Amount is 0 or less, returning empty list {'amount':{}}", amount);
            gottenRandomUniqueVinyls = new ArrayList<>();
        }
        logger.debug("Resulting list of random unique vinyls is {'randomUniqueVinyls':{}}", gottenRandomUniqueVinyls);
        return gottenRandomUniqueVinyls;
    }

    @Override
    public List<UniqueVinyl> findManyFiltered(String matcher) {
        List<UniqueVinyl> gottenFilteredUniqueVinyls;
        if (matcher != null) {
            gottenFilteredUniqueVinyls = vinylDao.findManyFiltered(matcher);
        } else {
            logger.error("Matcher is null, returning empty list.");
            gottenFilteredUniqueVinyls = new ArrayList<>();
        }
        logger.debug("Resulting list of random unique vinyls is {'filteredUniqueVinyls':{}}", gottenFilteredUniqueVinyls);
        return gottenFilteredUniqueVinyls;
    }

    @Override
    public List<UniqueVinyl> findManyByArtist(String artist) {
        List<UniqueVinyl> gottenUniqueVinyls;
        if (artist != null) {
            gottenUniqueVinyls = vinylDao.findManyByArtist(artist);
        } else {
            logger.error("Artist is null, returning empty list.");
            gottenUniqueVinyls = new ArrayList<>();
        }
        logger.debug("Resulting list of random unique vinyls is {'uniqueVinyls':{}}", gottenUniqueVinyls);
        return gottenUniqueVinyls;
    }

    @Override
    public UniqueVinyl findById(long id) {
        UniqueVinyl gottenUniqueVinyl;
        if (id > 0) {
            gottenUniqueVinyl = vinylDao.findById(id);
        } else {
            IllegalArgumentException e = new IllegalArgumentException();
            logger.error("Id is 0 or less {'id':{}}", id, e);
            throw new RuntimeException(e);
        }
        logger.debug("Resulting uniqueVinyl is {'uniqueVinyl':{}}", gottenUniqueVinyl);
        return gottenUniqueVinyl;
    }

}
