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
    private final String SELECT_ALL_UNIQUE_VINYLS = "SELECT id, release, artist, full_name, link_to_image" +
            " FROM unique_vinyls ORDER BY id";
    private final String SELECT_MANY_RANDOM_UNIQUE_VINYLS = "SELECT id, release, artist, full_name, link_to_image " +
            "FROM unique_vinyls TABLESAMPLE SYSTEM_ROWS(?) LIMIT ?;";
    private final String SELECT_UNIQUE_VINYL_BY_ID = "SELECT id, release, artist, full_name, link_to_image" +
            " FROM unique_vinyls WHERE id=?";
    private final String INSERT_VINYLS = "INSERT INTO vinyls(release, artist, full_name, genre," +
            " price, currency, link_to_vinyl, link_to_image, shop_id, unique_vinyl_id)" +
            " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private final String SELECT_ALL_VINYLS = "SELECT id, release, artist, full_name, genre," +
            " price, currency, link_to_vinyl, link_to_image, shop_id, unique_vinyl_id" +
            " FROM vinyls";
    private final String SELECT_VINYL_BY_ID = "SELECT id, release, artist, full_name, genre," +
            " price, currency, link_to_vinyl, link_to_image, shop_id, unique_vinyl_id" +
            " FROM vinyls WHERE id=?";

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final UniqueVinylRowMapper uniqueVinylRowMapper = new UniqueVinylRowMapper();
    private final VinylRowMapper vinylRowMapper = new VinylRowMapper();

    @Override
    public void addAllUnique(List<Vinyl> uniqueVinyls) {
        try (Connection connection = DBDataSource.getConnection();
             PreparedStatement insertUniqueVinylsStatement = connection.prepareStatement(INSERT_UNIQUE_VINYLS)) {
            connection.setAutoCommit(false);
            for (Vinyl uniqueVinyl : uniqueVinyls) {
                insertUniqueVinylsStatement.setLong(1, uniqueVinyl.getUniqueVinylId());
                insertUniqueVinylsStatement.setString(2, uniqueVinyl.getRelease());
                insertUniqueVinylsStatement.setString(3, uniqueVinyl.getArtist());
                insertUniqueVinylsStatement.setString(4, uniqueVinyl.getFullNameVinyl());
                insertUniqueVinylsStatement.setString(5, uniqueVinyl.getImageLink());
                insertUniqueVinylsStatement.addBatch();
            }
            logger.debug("Prepared statement {'preparedStatement':{}}.", insertUniqueVinylsStatement);
            insertUniqueVinylsStatement.executeBatch();
            connection.commit();
        } catch (SQLException e) {
            logger.error("Error while adding unique vinyls to db {'uniqueVinyls':{}}", uniqueVinyls, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addAll(List<Vinyl> vinyls) {
        try (Connection connection = DBDataSource.getConnection();
             PreparedStatement insertVinylsStatement = connection.prepareStatement(INSERT_VINYLS)) {
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
                    RuntimeException e = new RuntimeException();
                    logger.error("No defined currency in vinyl {'currency':{}}", vinyl.getCurrency(), e);
                    throw e;
                }
                insertVinylsStatement.setString(7, vinyl.getVinylLink());
                insertVinylsStatement.setString(8, vinyl.getImageLink());
                insertVinylsStatement.setInt(9, vinyl.getShopId());
                insertVinylsStatement.setLong(10, vinyl.getUniqueVinylId());
                insertVinylsStatement.addBatch();
            }
            logger.debug("Prepared statement {'preparedStatement':{}}.", insertVinylsStatement);
            insertVinylsStatement.executeBatch();
            connection.commit();
        } catch (SQLException e) {
            logger.error("Error while adding vinyls to db {'uniqueVinyls':{}}", vinyls, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Vinyl> getAllUnique() {
        List<Vinyl> uniqueVinyls = new ArrayList<>();
        try (Connection connection = DBDataSource.getConnection();
             PreparedStatement getUniqueVinylsStatement = connection.prepareStatement(SELECT_ALL_UNIQUE_VINYLS);
             ResultSet resultSet = getUniqueVinylsStatement.executeQuery()) {
            logger.debug("Executed statement {'statement':{}}", getUniqueVinylsStatement);
            while (resultSet.next()) {
                Vinyl uniqueVinyl = uniqueVinylRowMapper.mapRow(resultSet);
                uniqueVinyls.add(uniqueVinyl);
            }
        } catch (SQLException e) {
            logger.error("Error while getting unique vinyls from db {'uniqueVinyls':{}}", uniqueVinyls, e);
            throw new RuntimeException(e);
        }
        logger.debug("Resulting list of unique vinyls is {'uniqueVinyls':{}}", uniqueVinyls);
        return uniqueVinyls;
    }

    @Override
    public List<Vinyl> getManyRandomUnique(int amount) {
        List<Vinyl> randomUniqueVinyls = new ArrayList<>();
        try (Connection connection = DBDataSource.getConnection();
             PreparedStatement getManyRandomUniqueVinylsStatement = connection.prepareStatement(SELECT_MANY_RANDOM_UNIQUE_VINYLS)) {
            getManyRandomUniqueVinylsStatement.setInt(1, amount * 2);
            getManyRandomUniqueVinylsStatement.setInt(2, amount);
            logger.info("Prepared statement {'preparedStatement':{}}.", getManyRandomUniqueVinylsStatement);
            try (ResultSet resultSet = getManyRandomUniqueVinylsStatement.executeQuery()) {
                while (resultSet.next()) {
                    Vinyl randomUniqueVinyl = uniqueVinylRowMapper.mapRow(resultSet);
                    randomUniqueVinyls.add(randomUniqueVinyl);
                }
            }
        } catch (SQLException e) {
            logger.error("Error while getting list of random unique vinyls with amount from db {'randomUniqueVinyls':{}, 'amount':{}}",
                    randomUniqueVinyls, amount);
            throw new RuntimeException(e);
        }
        logger.debug("Resulting list of random unique vinyls is {'randomUniqueVinyls':{}}", randomUniqueVinyls);
        return randomUniqueVinyls;
    }

    @Override
    public List<Vinyl> getAll() {
        List<Vinyl> vinyls = new ArrayList<>();
        try (Connection connection = DBDataSource.getConnection();
             PreparedStatement getAllVinylsStatement = connection.prepareStatement(SELECT_ALL_VINYLS);
             ResultSet resultSet = getAllVinylsStatement.executeQuery()) {
            logger.debug("Executed statement {'statement':{}}", getAllVinylsStatement);
            while (resultSet.next()) {
                Vinyl vinyl = vinylRowMapper.mapRow(resultSet);
                vinyls.add(vinyl);
            }
        } catch (SQLException e) {
            logger.error("Error while getting vinyls from db {'vinyls':{}}", vinyls, e);
            throw new RuntimeException(e);
        }
        logger.debug("Resulting list of vinyls is {'vinyls':{}}", vinyls);
        return vinyls;
    }

    @Override
    public Vinyl getUniqueById(long id) {
        Vinyl uniqueVinyl;
        try (Connection connection = DBDataSource.getConnection();
             PreparedStatement getUniqueVinylByIdStatement = connection.prepareStatement(SELECT_UNIQUE_VINYL_BY_ID)) {
            getUniqueVinylByIdStatement.setLong(1, id);
            logger.debug("Prepared statement {'preparedStatement':{}}.", getUniqueVinylByIdStatement);
            try (ResultSet resultSet = getUniqueVinylByIdStatement.executeQuery()) {
                if (resultSet.next()) {
                    uniqueVinyl = uniqueVinylRowMapper.mapRow(resultSet);
                } else {
                    RuntimeException e = new RuntimeException();
                    logger.error("No unique vinyl with that id in db {'id':{}}", id, e);
                    throw e;
                }
            }
        } catch (SQLException e) {
            logger.error("Error while getting unique vinyl by id from db {'id':{}}", id);
            throw new RuntimeException(e);
        }
        logger.debug("Resulting unique vinyl is {'uniqueVinyl':{}}", uniqueVinyl);
        return uniqueVinyl;
    }

    @Override
    public Vinyl getById(long id) {
        Vinyl vinyl;
        try (Connection connection = DBDataSource.getConnection();
             PreparedStatement getVinylByIdStatement = connection.prepareStatement(SELECT_VINYL_BY_ID)) {
            getVinylByIdStatement.setLong(1, id);
            logger.debug("Prepared statement {'preparedStatement':{}}.", getVinylByIdStatement);
            try (ResultSet resultSet = getVinylByIdStatement.executeQuery()) {
                if (resultSet.next()) {
                    vinyl = vinylRowMapper.mapRow(resultSet);
                } else {
                    RuntimeException e = new RuntimeException();
                    logger.error("No vinyl with that id in db {'id':{}}", id, e);
                    throw e;
                }
            }
        } catch (SQLException e) {
            logger.error("Error while getting unique vinyl by id from db {'id':{}}", id);
            throw new RuntimeException(e);
        }
        logger.debug("Resulting vinyl is {'vinyl':{}}", vinyl);
        return vinyl;
    }

}
