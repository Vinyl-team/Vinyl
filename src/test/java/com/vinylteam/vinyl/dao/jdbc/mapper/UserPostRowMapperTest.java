package com.vinylteam.vinyl.dao.jdbc.mapper;

import com.vinylteam.vinyl.dao.RowMapper;
import com.vinylteam.vinyl.entity.UserPost;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserPostRowMapperTest {
    private final RowMapper<UserPost> rowMapper = new UserPostRowMapper();

    @Test
    @DisplayName("Map fields correctly for user post conversion")
    void mapFilledRowTest() throws SQLException {
        //prepare
        ResultSet mockedFilledResultSet = mock(ResultSet.class);
        when(mockedFilledResultSet.getLong("user_id")).thenReturn(99L);
        when(mockedFilledResultSet.getString("email")).thenReturn("testuser@vinyl.com");
        when(mockedFilledResultSet.getString("name")).thenReturn("Boris The Blade");
        when(mockedFilledResultSet.getString("theme")).thenReturn("Urgent Action");
        when(mockedFilledResultSet.getString("message")).thenReturn("I need new blades!");
        //when
        UserPost actualUserPost = rowMapper.mapRow(mockedFilledResultSet);
        //then
        assertEquals(99L, actualUserPost.getUserId());
        assertEquals("testuser@vinyl.com", actualUserPost.getEmail());
        assertEquals("Boris The Blade", actualUserPost.getName());
        assertEquals("Urgent Action", actualUserPost.getTheme());
        assertEquals("I need new blades!", actualUserPost.getMessage());
    }
}