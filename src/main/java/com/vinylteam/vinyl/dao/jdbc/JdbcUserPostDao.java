package com.vinylteam.vinyl.dao.jdbc;

import com.vinylteam.vinyl.dao.UserPostDao;
import com.vinylteam.vinyl.entity.UserPost;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PSQLException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Slf4j
public class JdbcUserPostDao implements UserPostDao {
    private final String INSERT = "INSERT INTO public.user_posts" +
            " (user_id, name, email, theme, message)" +
            " VALUES (?, ?, ?, ?, ?)";
    private final HikariDataSource dataSource;

    public JdbcUserPostDao(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public boolean add(UserPost post) {
        boolean isAdded = false;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement insertStatement = connection.prepareStatement(INSERT)) {
            insertStatement.setLong(1, post.getUserId());
            insertStatement.setString(2, post.getName());
            insertStatement.setString(3, post.getEmail());
            insertStatement.setString(4, post.getTheme());
            insertStatement.setString(5, post.getMessage());
            log.debug("Prepared statement {'preparedStatement':{}}.", insertStatement);
            int result = insertStatement.executeUpdate();
            if (result > 0) {
                isAdded = true;
            }
        } catch (PSQLException e) {
            log.error("Database error while adding user post to public.user_posts", e);
            isAdded = false;
        } catch (SQLException e) {
            log.error("Error while adding user post to public.user_posts", e);
            throw new RuntimeException(e);
        }
        if (isAdded) {
            log.info("User post is added to the database {'userPost':{}}.", post);
        } else {
            log.info("Failed to add user post to the database {'userPost':{}}.", post);
        }
        return isAdded;
    }
}