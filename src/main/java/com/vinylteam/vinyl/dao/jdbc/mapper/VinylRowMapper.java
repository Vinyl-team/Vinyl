package com.vinylteam.vinyl.dao.jdbc.mapper;

import com.vinylteam.vinyl.entity.Currency;
import com.vinylteam.vinyl.entity.Vinyl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class VinylRowMapper {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public Vinyl mapRow(ResultSet resultSet) {
        if (resultSet != null) {
            Vinyl vinyl = new Vinyl();
            try {
                vinyl.setVinylId(resultSet.getInt("id"));
                vinyl.setRelease(resultSet.getString("release"));
                vinyl.setArtist(resultSet.getString("artist"));
                vinyl.setFullNameVinyl(resultSet.getString("full_name"));
                vinyl.setGenre(resultSet.getString("genre"));
                vinyl.setPrice(resultSet.getDouble("price"));
                vinyl.setCurrency(Optional.of(Currency.valueOf(resultSet.getString("currency"))));
                vinyl.setVinylLink(resultSet.getString("link_to_vinyl"));
                vinyl.setImageLink(resultSet.getString("link_to_image"));
                vinyl.setShopId(resultSet.getInt("shop_id"));
                vinyl.setUniqueVinylId(resultSet.getLong("unique_vinyl_id"));
                logger.debug("Resulting Vinyl object {'vinyl':{}}", vinyl);
                return vinyl;
            } catch (SQLException e) {
                logger.error("Error while getting data from result set into Vinyl object {'vinyl':{}}", vinyl, e);
                throw new RuntimeException(e);
            }
        } else {
            RuntimeException e = new RuntimeException();
            logger.error("ResultSet passed to VinylRowMapper is null", e);
            throw e;
        }
    }

}
