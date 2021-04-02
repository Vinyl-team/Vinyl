package com.vinylteam.vinyl.dao.jdbc.mapper;

import com.vinylteam.vinyl.entity.Vinyl;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UniqueVinylRowMapper {
    public Vinyl mapRow(ResultSet resultSet) {
        if (resultSet != null) {
            Vinyl vinyl = new Vinyl();
            try {
                vinyl.setVinylId(resultSet.getInt("id"));
                vinyl.setRelease(resultSet.getString("release"));
                vinyl.setArtist(resultSet.getString("artist"));
                vinyl.setFullNameVinyl(resultSet.getString("full_name"));
                vinyl.setImageLink(resultSet.getString("link_to_image"));
                return vinyl;
            } catch (SQLException e) {
                throw new RuntimeException("Exception while getting data from ResultSet in UniqueVinylRowMapper!", e);
            }
        } else {
            throw new RuntimeException("ResultSet in UniqueVinylRowMapper is null!");
        }
    }
}
