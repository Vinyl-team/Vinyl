package com.vinylteam.vinyl.dao.jdbc.mapper;

import com.vinylteam.vinyl.entity.Vinyl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UniqueVinylRowMapper {

    private static Logger logger = LoggerFactory.getLogger(UniqueVinylRowMapper.class);

    public static Vinyl mapRow(ResultSet resultSet) {
        logger.debug("Start of function UniqueVinylRowMapper.mapRow(ResultSet resultSet)" +
                " with {'resultSet':{}}", resultSet);
        if (resultSet != null) {
            Vinyl vinyl = new Vinyl();
            logger.debug("Created Vinyl object {'vinyl':{}}", vinyl);
            try {
                vinyl.setVinylId(resultSet.getInt("id"));
                vinyl.setRelease(resultSet.getString("release"));
                vinyl.setArtist(resultSet.getString("artist"));
                vinyl.setFullNameVinyl(resultSet.getString("full_name"));
                vinyl.setImageLink(resultSet.getString("link_to_image"));
                logger.debug("Filled vinyl object {'vinyl':{}}", vinyl);
                return vinyl;
            } catch (SQLException e) {
                logger.error("Error while getting data from result set into Vinyl object {'vinyl':{}}",
                        vinyl, e);
                throw new RuntimeException(e);
            }
        } else {
            RuntimeException e = new RuntimeException();
            logger.error("ResultSet passed to UniqueVinylRowMapper is null", e);
            throw e;
        }
    }
}
