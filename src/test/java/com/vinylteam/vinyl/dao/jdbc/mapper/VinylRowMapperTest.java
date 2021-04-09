package com.vinylteam.vinyl.dao.jdbc.mapper;

import com.vinylteam.vinyl.entity.Currency;
import com.vinylteam.vinyl.entity.Vinyl;
import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("Checks if user created from resultSet has all fields right.")
    void mapRowFilledResultSetTest() throws SQLException {
        ResultSet vinylResult = mock(ResultSet.class);
        //prepare
        when(vinylResult.getInt("id")).thenReturn(1);
        when(vinylResult.getString("release")).thenReturn("release1");
        when(vinylResult.getString("artist")).thenReturn("artist1");
        when(vinylResult.getString("full_name")).thenReturn("release1 - artist1");
        when(vinylResult.getString("genre")).thenReturn("rock");
        when(vinylResult.getDouble("price")).thenReturn(1000.0);
        when(vinylResult.getString("currency")).thenReturn("EUR");
        when(vinylResult.getString("link_to_vinyl")).thenReturn("https://vinylsite.com/there/release1");
        when(vinylResult.getString("link_to_image")).thenReturn("https://imagestore.com/there/image1.jpg");
        when(vinylResult.getInt("shop_id")).thenReturn(2);
        when(vinylResult.getLong("unique_vinyl_id")).thenReturn((long) 1);
        //when
        Vinyl vinyl = vinylRowMapper.mapRow(vinylResult);
        //then
        assertEquals(1, vinyl.getVinylId());
        assertEquals("release1", vinyl.getRelease());
        assertEquals("artist1", vinyl.getArtist());
        assertEquals("release1 - artist1", vinyl.getFullNameVinyl());
        assertEquals("rock", vinyl.getGenre());
        assertEquals(1000.0, vinyl.getPrice());
        assertEquals(Currency.EUR, vinyl.getCurrency().get());
        assertEquals("https://vinylsite.com/there/release1", vinyl.getVinylLink());
        assertEquals("https://imagestore.com/there/image1.jpg", vinyl.getImageLink());
        assertEquals(2, vinyl.getShopId());
        assertEquals(1, vinyl.getUniqueVinylId());
    }

    @Test
    @DisplayName("Checks if passing null ResultSet causes RuntimeException.")
    void mapRowWithNullResultSetTest() {
        assertThrows(RuntimeException.class, () -> {
            vinylRowMapper.mapRow(null);
        });
    }
}