package com.vinylteam.vinyl.dao.jdbc.mapper;

import com.vinylteam.vinyl.entity.Vinyl;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class VinylRowMapperTest {
    private final VinylRowMapper vinylRowMapper = new VinylRowMapper();

    @Test
    void mapRow() throws SQLException {
        ResultSet vinylResult = mock(ResultSet.class);
        //prepare
        when(vinylResult.getInt("id")).thenReturn(1);
        when(vinylResult.getString("release")).thenReturn("some release");
        when(vinylResult.getString("artist")).thenReturn("new artist");
        when(vinylResult.getString("full_name")).thenReturn("some release - new artist");
        when(vinylResult.getString("genre")).thenReturn("rock");
        when(vinylResult.getString("price")).thenReturn("1000 euro");
        when(vinylResult.getString("link_to_vinyl")).thenReturn("https://vinylsite.com/there/coolreleasevinyl");
        when(vinylResult.getString("link_to_image")).thenReturn("https://imagestore.com/there/imagerelease.jpg");
        when(vinylResult.getInt("shop_id")).thenReturn(2);
        when(vinylResult.getLong("unique_vinyl_id")).thenReturn((long) 1);
        //when
        Vinyl vinyl = vinylRowMapper.mapRow(vinylResult);
        //then
        assertEquals(1, vinyl.getVinylId());
        assertEquals("some release", vinyl.getRelease());
        assertEquals("new artist", vinyl.getArtist());
        assertEquals("some release - new artist", vinyl.getFullNameVinyl());
        assertEquals("rock", vinyl.getGenre());
        assertEquals("1000 euro", vinyl.getPrice());
        assertEquals("https://vinylsite.com/there/coolreleasevinyl", vinyl.getVinylLink());
        assertEquals("https://imagestore.com/there/imagerelease.jpg", vinyl.getImageLink());
        assertEquals(2, vinyl.getShopId());
        assertEquals(1, vinyl.getUniqueVinylId());
    }

    @Test
    void mapRowWithNullParameterTest() {
        assertThrows(RuntimeException.class, () -> {
            vinylRowMapper.mapRow(null);
        });
    }
}