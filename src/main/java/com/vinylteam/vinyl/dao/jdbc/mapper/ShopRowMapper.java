package com.vinylteam.vinyl.dao.jdbc.mapper;

import com.vinylteam.vinyl.dao.RowMapper;
import com.vinylteam.vinyl.entity.Shop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;

public class ShopRowMapper implements RowMapper<Shop> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public Shop mapRow(ResultSet resultSet) {
        if (resultSet != null) {
            Shop shop = new Shop();
            try {
                shop.setId(resultSet.getInt("id"));
                shop.setMainPageLink(resultSet.getString("link_to_main_page"));
                shop.setImageLink(resultSet.getString("link_to_image"));
                shop.setName(resultSet.getString("name"));
                logger.debug("Resulting Shop object {'shop':{}}", shop);
                return shop;
            } catch (Exception e) {
                logger.error("Error while getting data from result set into Shop object {'shop':{}}", shop, e);
                throw new RuntimeException(e);
            }
        } else {
            RuntimeException e = new RuntimeException();
            logger.error("ResultSet passed to ShopRowMapper is null", e);
            throw e;
        }
    }

}
