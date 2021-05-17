package com.vinylteam.vinyl.dao.jdbc;

import com.vinylteam.vinyl.dao.UserPostDao;
import com.vinylteam.vinyl.dao.jdbc.mapper.UserPostRowMapper;
import com.vinylteam.vinyl.entity.UserPost;
import com.zaxxer.hikari.HikariDataSource;
import org.postgresql.util.PSQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JdbcUserPostDao implements UserPostDao {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final String INSERT = "INSERT INTO public.user_posts" +
            " (user_id, name, email, theme, message)" +
            " VALUES (?, ?, ?, ?, ?)";
    private final HikariDataSource dataSource;

    public JdbcUserPostDao(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public boolean save(UserPost post) {
        boolean isAdded = false;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement insertStatement = connection.prepareStatement(INSERT)) {
            insertStatement.setLong(1, post.getUserId());
            insertStatement.setString(2, post.getName());
            insertStatement.setString(3, post.getEmail());
            insertStatement.setString(4, post.getTheme());
            insertStatement.setString(5, post.getMessage());
            logger.debug("Prepared statement {'preparedStatement':{}}.", insertStatement);
            int result = insertStatement.executeUpdate();
            if (result > 0) {
                isAdded = true;
            }
        } catch (PSQLException e) {
            logger.debug("Database error while adding user_post to public.user_posts", e);
            isAdded = false;
        } catch (SQLException e) {
            logger.error("Error while adding user_post to public.user_posts", e);
            throw new RuntimeException(e);
        }
        if (isAdded) {
            logger.info("User is added to the database {'user_post':{}}.", post);
        } else {
            logger.info("Failed to add user_post to the database {'user_post':{}}.", post);
        }
        return isAdded;
    }
}