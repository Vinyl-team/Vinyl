package com.vinylteam.vinyl.dao.jdbc;

import com.vinylteam.vinyl.dao.UserDao;
import com.vinylteam.vinyl.dao.jdbc.mapper.UserRowMapper;
import com.vinylteam.vinyl.entity.User;
import org.postgresql.ds.PGSimpleDataSource;
import org.postgresql.util.PSQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class JdbcUserDao implements UserDao {

    private final PGSimpleDataSource dataSource;
    private final String COUNT_ALL = "SELECT COUNT(*) FROM public.users";
    private final String FIND_BY_EMAIL = "SELECT * FROM public.users" +
            " WHERE email=?";
    private final String INSERT = "INSERT INTO public.users" +
            " (email, password, salt, iterations, role)" +
            " VALUES (?, ?, ?, ?, ?)";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public JdbcUserDao(PGSimpleDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public boolean add(User user) {
        boolean isAdded;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement insertStatement = connection.prepareStatement(INSERT)) {
            insertStatement.setString(1, user.getEmail());
            insertStatement.setString(2, user.getPassword());
            insertStatement.setString(3, user.getSalt());
            insertStatement.setInt(4, user.getIterations());
            insertStatement.setString(5, user.getRole().toString());

            insertStatement.executeUpdate();
            isAdded = true;
        } catch (PSQLException e) {
            logger.info("Database error while adding user to public.users", e);
            isAdded = false;
        } catch (SQLException e) {
            logger.error("Error while adding user to public.users", e);
            throw new RuntimeException(e);
        }
        return isAdded;
    }

    public void updatePassword(String email, String newPassword, int iterations, String newSalt) {
    }

    public User getByEmail(String email) {
        User user = null;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement findByEmailStatement = connection.prepareStatement(FIND_BY_EMAIL)) {
            findByEmailStatement.setString(1, email);
            try (ResultSet resultSet = findByEmailStatement.executeQuery()) {
                if (resultSet.next()) {
                    user = UserRowMapper.mapRow(resultSet);
                    if (resultSet.next()) {
                        logger.error("More than one user was found for email: {}", email);
                        throw new SQLException("More than one user was found for email: ".concat(email));
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("SQLException retrieving user by email from public.users", e);
            throw new RuntimeException(e);
        }
        return user;
    }

    public boolean removeUser(String email) {
        return false;
    }

    int countAll() {

        int count = -1;
        try (Connection connection = dataSource.getConnection();
             Statement countStatement = connection.createStatement();
             ResultSet resultSet = countStatement.executeQuery(COUNT_ALL)) {
            if (resultSet.next()) {
                count = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("SQLException while counting amount of rows in public.users", e);
            throw new RuntimeException(e);
        }

        return count;
    }

}