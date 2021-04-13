package com.vinylteam.vinyl.dao.jdbc.mapper;

import com.vinylteam.vinyl.entity.Vinyl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UniqueVinylRowMapper {

    private Logger logger = LoggerFactory.getLogger(getClass());

    public Vinyl mapRow(ResultSet resultSet) {
        if (resultSet != null) {
            Vinyl uniqueVinyl = new Vinyl();
            try {
                uniqueVinyl.setVinylId(resultSet.getInt("id"));
                uniqueVinyl.setRelease(resultSet.getString("release"));
                uniqueVinyl.setArtist(resultSet.getString("artist"));
                uniqueVinyl.setFullNameVinyl(resultSet.getString("full_name"));
                uniqueVinyl.setImageLink(resultSet.getString("link_to_image"));
                logger.debug("Resulting Vinyl object {'uniqueVinyl':{}}", uniqueVinyl);
                return uniqueVinyl;
            } catch (SQLException e) {
                logger.error("Error while getting data from result set into Vinyl object {'vinyl':{}}",
                        uniqueVinyl, e);
                throw new RuntimeException(e);
            }
        } else {
            RuntimeException e = new RuntimeException();
            logger.error("ResultSet passed to UniqueVinylRowMapper is null", e);
            throw e;
        }
    }
}
