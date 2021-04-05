package com.vinylteam.vinyl.dao.jdbc.mapper;

import com.vinylteam.vinyl.entity.Vinyl;
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
    void mapRowWithFilledResultSetTest() throws SQLException {
        ResultSet mockedResultSet = mock(ResultSet.class);
        //prepared
        when(mockedResultSet.getInt("id")).thenReturn(1);
        when(mockedResultSet.getString("release")).thenReturn("yellow banana");
        when(mockedResultSet.getString("artist")).thenReturn("monkey");
        when(mockedResultSet.getString("full_name")).thenReturn("yellow banana - monkey");
        when(mockedResultSet.getString("link_to_image")).thenReturn("https://imagestore.com/somewhere/image.jpg");
        //when
        Vinyl vinyl = uniqueVinylRowMapper.mapRow(mockedResultSet);
        //then
        assertEquals(1, vinyl.getVinylId());
        assertEquals("yellow banana", vinyl.getRelease());
        assertEquals("monkey", vinyl.getArtist());
        assertEquals("yellow banana - monkey", vinyl.getFullNameVinyl());
        assertEquals("https://imagestore.com/somewhere/image.jpg", vinyl.getImageLink());
    }

    @Test
    void mapRowWithNullResultSetTest() {
        assertThrows(RuntimeException.class, () -> {
            uniqueVinylRowMapper.mapRow(null);
        });
    }
}