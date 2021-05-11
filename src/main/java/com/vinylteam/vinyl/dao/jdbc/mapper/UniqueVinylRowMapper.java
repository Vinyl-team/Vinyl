package com.vinylteam.vinyl.dao.jdbc.mapper;

import com.vinylteam.vinyl.dao.RowMapper;
import com.vinylteam.vinyl.entity.UniqueVinyl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UniqueVinylRowMapper implements RowMapper<UniqueVinyl> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public UniqueVinyl mapRow(ResultSet resultSet) {
        UniqueVinyl uniqueVinyl = new UniqueVinyl();
        try {
            uniqueVinyl.setId(resultSet.getInt("id"));
            uniqueVinyl.setRelease(resultSet.getString("release"));
            uniqueVinyl.setArtist(resultSet.getString("artist"));
            uniqueVinyl.setFullName(resultSet.getString("full_name"));
            uniqueVinyl.setImageLink(resultSet.getString("link_to_image"));
            logger.debug("Resulting UniqueVinyl object {'uniqueVinyl':{}}", uniqueVinyl);
            return uniqueVinyl;
        } catch (SQLException e) {
            logger.error("Error while getting data from result set into UniqueVinyl object {'uniqueVinyl':{}}", uniqueVinyl, e);
            throw new RuntimeException(e);
        }
    }

}
