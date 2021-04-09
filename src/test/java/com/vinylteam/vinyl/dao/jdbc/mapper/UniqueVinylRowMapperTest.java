package com.vinylteam.vinyl.dao.jdbc.mapper;

import com.vinylteam.vinyl.entity.Vinyl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UniqueVinylRowMapperTest {
    private final UniqueVinylRowMapper uniqueVinylRowMapper = new UniqueVinylRowMapper();

    @Test
    @DisplayName("Checks if user created from resultSet has all fields right.")
    void mapRowWithFilledResultSetTest() throws SQLException {
        ResultSet mockedResultSet = mock(ResultSet.class);
        //prepared
        when(mockedResultSet.getInt("id")).thenReturn(1);
        when(mockedResultSet.getString("release")).thenReturn("release1");
        when(mockedResultSet.getString("artist")).thenReturn("artist1");
        when(mockedResultSet.getString("full_name")).thenReturn("release1 - artist1");
        when(mockedResultSet.getString("link_to_image")).thenReturn("https://imagestore.com/somewhere/image1.jpg");
        //when
        Vinyl vinyl = uniqueVinylRowMapper.mapRow(mockedResultSet);
        //then
        assertEquals(1, vinyl.getVinylId());
        assertEquals("release1", vinyl.getRelease());
        assertEquals("artist1", vinyl.getArtist());
        assertEquals("release1 - artist1", vinyl.getFullNameVinyl());
        assertEquals("https://imagestore.com/somewhere/image1.jpg", vinyl.getImageLink());
    }

    @Test
    @DisplayName("Checks if passing null ResultSet causes RuntimeException.")
    void mapRowWithNullResultSetTest() {
        assertThrows(RuntimeException.class, () -> {
            uniqueVinylRowMapper.mapRow(null);
        });
    }
}