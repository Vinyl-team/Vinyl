package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.OfferDao;
import com.vinylteam.vinyl.entity.Offer;
import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.service.OfferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class DefaultOfferService implements OfferService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final OfferDao offerDao;

    public DefaultOfferService(OfferDao offerDao) {
        this.offerDao = offerDao;
    }

    @Override
    public List<Offer> findManyByUniqueVinylId(long uniqueVinylId) {
        List<Offer> offers;
        if (uniqueVinylId > 0) {
            offers = offerDao.findManyByUniqueVinylId(uniqueVinylId);
        } else {
            IllegalArgumentException e = new IllegalArgumentException();
            logger.error("uniqueVinylId is 0 or less {'uniqueVinylId':{}}", uniqueVinylId, e);
            throw new RuntimeException(e);
        }
        logger.debug("Resulting list of vinyls is {'vinyls':{}}", offers);
        return offers;
    }

    @Override
    public void updateUniqueVinylsRewriteAll(List<UniqueVinyl> uniqueVinyls, List<Offer> offers) {
        if (uniqueVinyls == null) {
            RuntimeException e = new NullPointerException("List of unique vinyls is null");
            logger.error("List of unique vinyls is null", e);
            throw e;
        }
        if (offers == null) {
            RuntimeException e = new NullPointerException("List of offers is null");
            logger.error("List of offers is null", e);
            throw e;
        }
        if (uniqueVinyls.isEmpty()) {
            RuntimeException e = new IllegalArgumentException("List of unique vinyls is empty");
            logger.error("List of unique vinyls is empty", e);
            throw e;
        }
        if (offers.isEmpty()) {
            RuntimeException e = new IllegalArgumentException("List of offers is empty");
            logger.error("List of offers is empty", e);
            throw e;
        }
        List<Offer> unaddedOffers = offerDao.updateUniqueVinylsRewriteAll(uniqueVinyls, offers);
        if (unaddedOffers.isEmpty()) {
            logger.info("Successfully updated database with {} unique vinyls and {} offers", uniqueVinyls.size(), offers.size());
        } else if (unaddedOffers.size() < offers.size()) {
            logger.error("Some offers weren't added to the database {'unaddedOffers':{}}", unaddedOffers);
        } else {
            RuntimeException e = new IllegalStateException("None of the offers were addad to the db");
            logger.error("None of the offers were added to the db {'unaddedOffers':{}}", unaddedOffers, e);
            throw e;
        }
    }

    @Override
    public List<Integer> getListOfShopIds(List<Offer> offers) {
        List<Integer> shopsIds = new ArrayList<>();
        if (offers != null) {
            for (Offer offer : offers) {
                if (!shopsIds.contains(Integer.valueOf(offer.getShopId()))) {
                    shopsIds.add(offer.getShopId());
                }
            }
        } else {
            logger.error("List of offers is null, returning empty list.");
        }
        logger.debug("Resulting list of shop id-s is {'shopIds':{}}", shopsIds);
        return shopsIds;
    }

}