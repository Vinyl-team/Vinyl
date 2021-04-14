package com.vinylteam.vinyl.dao.jdbc.mapper;

import com.vinylteam.vinyl.entity.Role;
import com.vinylteam.vinyl.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserRowMapperTest {

    private final UserRowMapper userRowMapper = new UserRowMapper();

    @Test
    @DisplayName("Checks if user created from resultSet has all fields right.")
    void mapFilledRowTest() throws SQLException {
        ResultSet mockedFilledResultSet = mock(ResultSet.class);
        when(mockedFilledResultSet.getString("email")).thenReturn("testuser@vinyl.com");
        when(mockedFilledResultSet.getString("password")).thenReturn("HASH");
        when(mockedFilledResultSet.getString("salt")).thenReturn("salt");
        when(mockedFilledResultSet.getInt("iterations")).thenReturn(1);
        when(mockedFilledResultSet.getString("role")).thenReturn("USER");
        when(mockedFilledResultSet.getBoolean("status")).thenReturn(true);

        User actualUser = userRowMapper.mapRow(mockedFilledResultSet);

        assertEquals("testuser@vinyl.com", actualUser.getEmail());
        assertEquals("HASH", actualUser.getPassword());
        assertEquals("salt", actualUser.getSalt());
        assertEquals(1, actualUser.getIterations());
        assertEquals(Role.USER, actualUser.getRole());
        assertTrue(actualUser.getStatus());
    }

    @Test
    @DisplayName("Checks if passing null ResultSet causes RuntimeException.")
    void mapNullRowTest() {
        assertThrows(RuntimeException.class, () -> {
            userRowMapper.mapRow(null);
        });
    }

}