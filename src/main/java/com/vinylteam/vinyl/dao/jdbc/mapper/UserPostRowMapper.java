package com.vinylteam.vinyl.dao.jdbc.mapper;

import com.vinylteam.vinyl.dao.RowMapper;
import com.vinylteam.vinyl.entity.UserPost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserPostRowMapper implements RowMapper<UserPost> {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public UserPost mapRow(ResultSet resultSet) {
        UserPost userPost = new UserPost();
        try {
            userPost.setId(resultSet.getLong("id"));
            userPost.setUserId(resultSet.getLong("user_id"));
            userPost.setName(resultSet.getString("name"));
            userPost.setEmail(resultSet.getString("email"));
            userPost.setTheme(resultSet.getString("theme"));
            userPost.setMessage(resultSet.getString("message"));
            logger.debug("Resulting UserPost object {'userPost':{}}", userPost);
            return userPost;
        } catch (SQLException e) {
            logger.error("Error while getting data from result set into UserPost object {'userPost':{}}", userPost, e);
            throw new RuntimeException(e);
        }
    }
}