package com.vinylteam.vinyl.dao.jdbc;

import com.vinylteam.vinyl.dao.UserDao;
import com.vinylteam.vinyl.dao.jdbc.mapper.UserRowMapper;
import com.vinylteam.vinyl.entity.User;
import com.zaxxer.hikari.HikariDataSource;
import org.postgresql.util.PSQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class JdbcUserDao implements UserDao {

    private static final Logger logger = LoggerFactory.getLogger(JdbcUserDao.class);
    private static final UserRowMapper userRowMapper = new UserRowMapper();
    private static final String FIND_BY_EMAIL = "SELECT email, password, salt, iterations, role, status, discogs_user_name" +
            " FROM public.users" +
            " WHERE email=?";
    private static final String INSERT = "INSERT INTO public.users" +
            " (email, password, salt, iterations, role, status, discogs_user_name)" +
            " VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE public.users" +
            " SET email = ?, password = ?, salt = ?, iterations = ?, role = ?, status = ?, discogs_user_name = ?" +
            " WHERE email = ?";
    private final HikariDataSource dataSource;

    public JdbcUserDao(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public boolean add(User user) {
        boolean isAdded = false;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement insertStatement = connection.prepareStatement(INSERT)) {
            insertStatement.setString(1, user.getEmail());
            insertStatement.setString(2, user.getPassword());
            insertStatement.setString(3, user.getSalt());
            insertStatement.setInt(4, user.getIterations());
            insertStatement.setString(5, user.getRole().toString());
            insertStatement.setBoolean(6, user.getStatus());
            insertStatement.setString(7, user.getDiscogsUserName());
            logger.debug("Prepared statement {'preparedStatement':{}}.", insertStatement);
            int result = insertStatement.executeUpdate();
            if (result > 0) {
                isAdded = true;
            }
        } catch (PSQLException e) {
            logger.debug("Database error while adding user to public.users", e);
            isAdded = false;
        } catch (SQLException e) {
            logger.error("Error while adding user to public.users", e);
            throw new RuntimeException(e);
        }
        if (isAdded) {
            logger.info("User is added to the database {'user':{}}.", user);
        } else {
            logger.info("Failed to add user to the database {'user':{}}.", user);
        }
        return isAdded;
    }

    @Override
    public boolean update(String email, User user) {
        boolean isUpdated = false;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement updateStatement = connection.prepareStatement(UPDATE)) {
            updateStatement.setString(1, user.getEmail());
            updateStatement.setString(2, user.getPassword());
            updateStatement.setString(3, user.getSalt());
            updateStatement.setInt(4, user.getIterations());
            updateStatement.setString(5, user.getRole().toString());
            updateStatement.setBoolean(6, user.getStatus());
            updateStatement.setString(7, user.getDiscogsUserName());
            updateStatement.setString(8, email);
            logger.debug("Prepared statement {'preparedStatement':{}}.", updateStatement);
            int result = updateStatement.executeUpdate();
            if (result > 0) {
                isUpdated = true;
            }
        } catch (PSQLException e) {
            logger.debug("Database error while edit user to public.users", e);
            isUpdated = false;
        } catch (SQLException e) {
            logger.error("Error while updating user in public.users", e);
            throw new RuntimeException(e);
        }
        if (isUpdated) {
            logger.info("User was updated in the database {'user':{}}.", user);
        } else {
            logger.info("Failed to update user in the database {'user':{}}.", user);
        }
        return isUpdated;
    }

    @Override
    public Optional<User> getByEmail(String email) {
        User user = null;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement findByEmailStatement = connection.prepareStatement(FIND_BY_EMAIL)) {
            findByEmailStatement.setString(1, email);
            logger.debug("Prepared statement {'preparedStatement':{}}.", findByEmailStatement);
            try (ResultSet resultSet = findByEmailStatement.executeQuery()) {
                if (resultSet.next()) {
                    user = userRowMapper.mapRow(resultSet);
                    if (resultSet.next()) {
                        RuntimeException e = new RuntimeException();
                        logger.error("More than one user was found for email: {}", email, e);
                        throw e;
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("SQLException retrieving user by email from public.users", e);
            throw new RuntimeException(e);
        }
        logger.debug("Resulting optional with user is {'Optional.ofNullable(user)':{}}", Optional.ofNullable(user));
        return Optional.ofNullable(user);
    }

}
