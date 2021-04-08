package com.vinylteam.vinyl.dao.jdbc;

import com.vinylteam.vinyl.dao.DBDataSource;
import com.vinylteam.vinyl.dao.VinylDao;
import com.vinylteam.vinyl.dao.jdbc.mapper.UniqueVinylRowMapper;
import com.vinylteam.vinyl.dao.jdbc.mapper.VinylRowMapper;
import com.vinylteam.vinyl.entity.Vinyl;

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

    private final VinylRowMapper vinylRowMapper = new VinylRowMapper();
    private final UniqueVinylRowMapper uniqueVinylRowMapper = new UniqueVinylRowMapper();

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
            insertUniqueVinylsStatement.executeBatch();
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Exception while saving unique vinyls into unique_vinyls table of DataBase!", e);
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
                    throw new RuntimeException("No defined currency.");
                }
                insertVinylsStatement.setString(7, vinyl.getVinylLink());
                insertVinylsStatement.setString(8, vinyl.getImageLink());
                insertVinylsStatement.setInt(9, vinyl.getShopId());
                insertVinylsStatement.setLong(10, vinyl.getUniqueVinylId());
                insertVinylsStatement.addBatch();
            }
            insertVinylsStatement.executeBatch();
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Exception while saving vinyls into vinyls table of DataBase!", e);
        }
    }

    @Override
    public List<Vinyl> getAllUnique() {
        List<Vinyl> uniqueVinyls = new ArrayList<>();
        try (Connection connection = DBDataSource.getConnection();
             PreparedStatement getUniqueVinylsStatement = connection.prepareStatement(SELECT_UNIQUE_VINYLS);
             ResultSet resultSet = getUniqueVinylsStatement.executeQuery()) {
            while (resultSet.next()) {
                Vinyl vinyl = uniqueVinylRowMapper.mapRow(resultSet);
                uniqueVinyls.add(vinyl);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Exception while getting unique vinyls from unique_vinyls table of DataBase!", e);
        }
        return uniqueVinyls;
    }

    @Override
    public List<Vinyl> getAll() {
        List<Vinyl> allVinyls = new ArrayList<>();
        try (Connection connection = DBDataSource.getConnection();
             PreparedStatement getAllVinylsStatement = connection.prepareStatement(SELECT_VINYLS);
             ResultSet resultSet = getAllVinylsStatement.executeQuery()) {
            while (resultSet.next()) {
                Vinyl vinyl = vinylRowMapper.mapRow(resultSet);
                allVinyls.add(vinyl);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Exception while getting vinyls from vinyls table of DataBase!", e);
        }
        return allVinyls;
    }

    @Override
    public Vinyl getUniqueById(long id) {
        Vinyl vinyl;
        try (Connection connection = DBDataSource.getConnection();
             PreparedStatement getUniqueVinylByIdStatement = connection.prepareStatement(SELECT_UNIQUE_VINYL_BY_ID)) {
            getUniqueVinylByIdStatement.setLong(1, id);
            try (ResultSet resultSet = getUniqueVinylByIdStatement.executeQuery()) {
                if (resultSet.next()) {
                    vinyl = uniqueVinylRowMapper.mapRow(resultSet);
                } else {
                    throw new SQLException("Unique vinyl with id = " + id + " is not exist in unique_vinyls table of DataBase!");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Exception while getting unique vinyl by id from unique_vinyls table of DataBase!", e);
        }
        return vinyl;
    }

    @Override
    public Vinyl getById(long id) {
        Vinyl vinyl;
        try (Connection connection = DBDataSource.getConnection();
             PreparedStatement getVinylByIdStatement = connection.prepareStatement(SELECT_VINYL_BY_ID)) {
            getVinylByIdStatement.setLong(1, id);
            try (ResultSet resultSet = getVinylByIdStatement.executeQuery()) {
                if (resultSet.next()) {
                    vinyl = vinylRowMapper.mapRow(resultSet);
                } else {
                    throw new SQLException("Vinyl with id = " + id + " is not exist in vinyls table of DataBase!");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Exception while getting vinyl by id from vinyls table of DataBase!", e);
        }
        return vinyl;
    }
}
