package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.ShopDao;
import com.vinylteam.vinyl.entity.Shop;
import com.vinylteam.vinyl.service.ShopService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class DefaultShopService implements ShopService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ShopDao shopDao;

    public DefaultShopService(ShopDao shopDao) {
        this.shopDao = shopDao;
    }

    public List<Shop> getManyByListOfIds(List<Integer> ids) {
        List<Shop> shops;
        if (ids != null) {
            shops = shopDao.getManyByListOfIds(ids);
        } else {
            logger.error("List of ids is null, returning empty list.");
            shops = new ArrayList<>();
        }
        logger.debug("Resulting list of shops is {'shops':{}}", shops);
        return shops;
    }

    @Override
    public List<Shop> findAll() {
        List<Shop> shops = shopDao.findAll();
        logger.debug("Resulting list of shops is {'shops':{}}", shops);
        return shops;
    }

}
