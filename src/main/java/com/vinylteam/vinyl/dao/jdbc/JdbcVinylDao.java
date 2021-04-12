package com.vinylteam.vinyl.dao.jdbc;

import com.vinylteam.vinyl.dao.DBDataSource;
import com.vinylteam.vinyl.dao.VinylDao;
import com.vinylteam.vinyl.dao.jdbc.mapper.UniqueVinylRowMapper;
import com.vinylteam.vinyl.dao.jdbc.mapper.VinylRowMapper;
import com.vinylteam.vinyl.entity.Vinyl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcVinylDao implements VinylDao {


    private final String INSERT_UNIQUE_VINYLS = "INSERT INTO unique_vinyls(id, release, artist, full_name," +
            " link_to_image)" +
            " VALUES(?, ?, ?, ?, ?)";
    private final String SELECT_UNIQUE_VINYLS = "SELECT id, release, artist, full_name, link_to_image" +
            " FROM unique_vinyls ORDER BY id";
    private final String SELECT_UNIQUE_VINYL_BY_ID = "SELECT id, release, artist, full_name, link_to_image" +
            " FROM unique_vinyls WHERE id=?";
    private final String INSERT_VINYLS = "INSERT INTO vinyls(release, artist, full_name, genre," +
            " price, currency, link_to_vinyl, link_to_image, shop_id, unique_vinyl_id)" +
            " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private final String SELECT_VINYLS = "SELECT id, release, artist, full_name, genre," +
            " price, currency, link_to_vinyl, link_to_image, shop_id, unique_vinyl_id" +
            " FROM vinyls";
    private final String SELECT_VINYL_BY_ID = "SELECT id, release, artist, full_name, genre," +
            " price, currency, link_to_vinyl, link_to_image, shop_id, unique_vinyl_id" +
            " FROM vinyls WHERE id=?";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void addAllUnique(List<Vinyl> uniqueVinyls) {
        logger.debug("Start of function JdbcVinylDao.add(List<Vinyl> uniqueVinyls)" +
                " with {'uniqueVinyls':{}}", uniqueVinyls);
        try (Connection connection = DBDataSource.getConnection();
             PreparedStatement insertUniqueVinylsStatement = connection.prepareStatement(INSERT_UNIQUE_VINYLS)) {
            logger.debug("Got connection to db from DBDataSource, created prepared statement" +
                    "{'preparedStatement':{}}. Starting to fill it.", insertUniqueVinylsStatement);
            connection.setAutoCommit(false);
            for (Vinyl uniqueVinyl : uniqueVinyls) {
                insertUniqueVinylsStatement.setLong(1, uniqueVinyl.getUniqueVinylId());
                insertUniqueVinylsStatement.setString(2, uniqueVinyl.getRelease());
                insertUniqueVinylsStatement.setString(3, uniqueVinyl.getArtist());
                insertUniqueVinylsStatement.setString(4, uniqueVinyl.getFullNameVinyl());
                insertUniqueVinylsStatement.setString(5, uniqueVinyl.getImageLink());
                insertUniqueVinylsStatement.addBatch();
            }
            logger.debug("Prepared statement filled" +
                    "{'preparedStatement':{}}.", insertUniqueVinylsStatement);
            insertUniqueVinylsStatement.executeBatch();
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Exception while saving unique vinyls into unique_vinyls table of DataBase!", e);
        }
    }

    @Override
    public void addAll(List<Vinyl> vinyls) {
        logger.debug("Start of function JdbcVinylDao.add(List<Vinyl> vinyls)" +
                " with {'vinyls':{}}", vinyls);
        try (Connection connection = DBDataSource.getConnection();
             PreparedStatement insertVinylsStatement = connection.prepareStatement(INSERT_VINYLS)) {
            logger.debug("Got connection to db from DBDataSource, created prepared statement" +
                    "{'preparedStatement':{}}. Starting to fill it.", insertVinylsStatement);
            connection.setAutoCommit(false);
            for (Vinyl vinyl : vinyls) {
                insertVinylsStatement.setString(1, vinyl.getRelease());
                insertVinylsStatement.setString(2, vinyl.getArtist());
                insertVinylsStatement.setString(3, vinyl.getFullNameVinyl());
                insertVinylsStatement.setString(4, vinyl.getGenre());
                insertVinylsStatement.setDouble(5, vinyl.getPrice());
                if (vinyl.getCurrency().isPresent()) {
                    insertVinylsStatement.setString(6, vinyl.getCurrency().get().toString());
                } else {
                    throw new RuntimeException("No defined currency.");
                }
                insertVinylsStatement.setString(7, vinyl.getVinylLink());
                insertVinylsStatement.setString(8, vinyl.getImageLink());
                insertVinylsStatement.setInt(9, vinyl.getShopId());
                insertVinylsStatement.setLong(10, vinyl.getUniqueVinylId());
                insertVinylsStatement.addBatch();
            }
            logger.debug("Prepared statement filled" +
                    "{'preparedStatement':{}}.", insertVinylsStatement);
            insertVinylsStatement.executeBatch();
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Exception while saving vinyls into vinyls table of DataBase!", e);
        }
    }

    @Override
    public List<Vinyl> getAllUnique() {

        logger.debug("Start of function JdbcVinylDao.getAllUnique()");
        List<Vinyl> uniqueVinyls = new ArrayList<>();
        try (Connection connection = DBDataSource.getConnection();
             PreparedStatement getUniqueVinylsStatement = connection.prepareStatement(SELECT_UNIQUE_VINYLS);
             ResultSet resultSet = getUniqueVinylsStatement.executeQuery()) {
            logger.debug("Got connection to db from DBDataSource, created statement, executed statement with result set" +
                    "{'statement':{}, 'resultSet':{}}", getUniqueVinylsStatement, resultSet);
            while (resultSet.next()) {
                Vinyl vinyl = UniqueVinylRowMapper.mapRow(resultSet);
                uniqueVinyls.add(vinyl);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Exception while getting unique vinyls from unique_vinyls table of DataBase!", e);
        }
        logger.debug("Resulting list of unique vinyls is {'uniqueVinyls':{}}", uniqueVinyls);
        return uniqueVinyls;
    }

    @Override
    public List<Vinyl> getAll() {
        logger.debug("Start of function JdbcVinylDao.getAllUnique()");
        List<Vinyl> allVinyls = new ArrayList<>();
        try (Connection connection = DBDataSource.getConnection();
             PreparedStatement getAllVinylsStatement = connection.prepareStatement(SELECT_VINYLS);
             ResultSet resultSet = getAllVinylsStatement.executeQuery()) {
            logger.debug("Got connection to db from DBDataSource, created statement, executed statement with result set" +
                    "{'statement':{}, 'resultSet':{}}", getAllVinylsStatement, resultSet);
            while (resultSet.next()) {
                Vinyl vinyl = VinylRowMapper.mapRow(resultSet);
                allVinyls.add(vinyl);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Exception while getting vinyls from vinyls table of DataBase!", e);
        }
        logger.debug("Resulting list of vinyls is {'vinyls':{}}", allVinyls);
        return allVinyls;
    }

    @Override
    public Vinyl getUniqueById(long id) {
        logger.debug("Start of function JdbcVinylDao.getUniqueById(long id)" +
                " with {'id':{}}", id);
        Vinyl vinyl;
        try (Connection connection = DBDataSource.getConnection();
             PreparedStatement getUniqueVinylByIdStatement = connection.prepareStatement(SELECT_UNIQUE_VINYL_BY_ID)) {
            logger.debug("Got connection to db from DBDataSource, created prepared statement" +
                    "{'preparedStatement':{}}. Starting to fill it.", getUniqueVinylByIdStatement);
            getUniqueVinylByIdStatement.setLong(1, id);
            logger.debug("Prepared statement filled" +
                    "{'preparedStatement':{}}.", getUniqueVinylByIdStatement);
            try (ResultSet resultSet = getUniqueVinylByIdStatement.executeQuery()) {
                logger.debug("Executed statement, result set after executing" +
                        "{'preparedStatement':{}, 'resultSet':{}}", getUniqueVinylByIdStatement, resultSet);
                if (resultSet.next()) {
                    vinyl = UniqueVinylRowMapper.mapRow(resultSet);
                } else {
                    throw new SQLException("Unique vinyl with id = " + id + " is not exist in unique_vinyls table of DataBase!");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Exception while getting unique vinyl by id from unique_vinyls table of DataBase!", e);
        }
        logger.debug("Resulting unique vinyl is {'uniqueVinyl':{}}", vinyl);
        return vinyl;
    }

    @Override
    public Vinyl getById(long id) {
        logger.debug("Start of function JdbcVinylDao.getById(long id)" +
                " with {'id':{}}", id);
        Vinyl vinyl;
        try (Connection connection = DBDataSource.getConnection();
             PreparedStatement getVinylByIdStatement = connection.prepareStatement(SELECT_VINYL_BY_ID)) {
            logger.debug("Got connection to db from DBDataSource, created prepared statement" +
                    "{'preparedStatement':{}}. Starting to fill it.", getVinylByIdStatement);
            getVinylByIdStatement.setLong(1, id);
            logger.debug("Prepared statement filled" +
                    "{'preparedStatement':{}}.", getVinylByIdStatement);
            try (ResultSet resultSet = getVinylByIdStatement.executeQuery()) {
                logger.debug("Executed statement, result set after executing" +
                        "{'preparedStatement':{}, 'resultSet':{}}", getVinylByIdStatement, resultSet);
                if (resultSet.next()) {
                    vinyl = VinylRowMapper.mapRow(resultSet);
                } else {
                    throw new SQLException("Vinyl with id = " + id + " is not exist in vinyls table of DataBase!");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Exception while getting vinyl by id from vinyls table of DataBase!", e);
        }
        logger.debug("Resulting vinyl is {'vinyl':{}}", vinyl);
        return vinyl;
    }
}
