package com.vinylteam.vinyl.dao.jdbc.mapper;

import com.vinylteam.vinyl.entity.Role;
import com.vinylteam.vinyl.entity.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserRowMapperTest {
    ResultSet mockedFilledResultSet;
    ResultSet mockedEmptyResultSet;

    @BeforeAll
    void beforeAll() throws SQLException {
        mockedFilledResultSet = mock(ResultSet.class);
        mockedEmptyResultSet = mock(ResultSet.class);
        when(mockedFilledResultSet.getString("email")).thenReturn("testuser@vinyl.com");
        when(mockedFilledResultSet.getString("password")).thenReturn("HASH");
        when(mockedFilledResultSet.getString("salt")).thenReturn("salt");
        when(mockedFilledResultSet.getInt("iterations")).thenReturn(1);
        when(mockedFilledResultSet.getString("role")).thenReturn("USER");
    }

    @Test
    void mapFilledRowTest() {
        User actualUser = UserRowMapper.mapRow(mockedFilledResultSet);

        assertEquals("testuser@vinyl.com", actualUser.getEmail());
        assertEquals("HASH", actualUser.getPassword());
        assertEquals("salt", actualUser.getSalt());
        assertEquals(1, actualUser.getIterations());
        assertEquals(Role.USER, actualUser.getRole());
    }

    @Test
    void mapEmptyRowTest() {
        assertThrows(RuntimeException.class, () -> {
            UserRowMapper.mapRow(mockedEmptyResultSet);
        });
    }

}