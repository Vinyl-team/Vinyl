package com.vinylteam.vinyl.dao.jdbc.mapper;

import com.vinylteam.vinyl.dao.RowMapper;
import com.vinylteam.vinyl.entity.Currency;
import com.vinylteam.vinyl.entity.Offer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class OfferRowMapper implements RowMapper<Offer> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public Offer mapRow(ResultSet resultSet) {
        if (resultSet != null) {
            Offer offer = new Offer();
            try {
                offer.setId(resultSet.getInt("id"));
                offer.setUniqueVinylId(resultSet.getLong("vinyl_id"));
                offer.setShopId(resultSet.getInt("shop_id"));
                offer.setPrice(resultSet.getDouble("price"));
                offer.setCurrency(Optional.of(Currency.valueOf(resultSet.getString("currency"))));
                offer.setGenre(resultSet.getString("genre"));
                offer.setOfferLink(resultSet.getString("link_to_offer"));
                logger.debug("Resulting Offer object {'offer':{}}", offer);
                return offer;
            } catch (SQLException e) {
                logger.error("Error while getting data from result set into Offer object {'offer':{}}", offer, e);
                throw new RuntimeException(e);
            }
        } else {
            RuntimeException e = new RuntimeException();
            logger.error("ResultSet passed to OfferRowMapper is null", e);
            throw e;
        }
    }

}