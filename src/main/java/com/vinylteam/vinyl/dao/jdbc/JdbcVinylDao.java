package com.vinylteam.vinyl.dao.jdbc;

import com.vinylteam.vinyl.dao.VinylDao;
import com.vinylteam.vinyl.dao.jdbc.mapper.UniqueVinylRowMapper;
import com.vinylteam.vinyl.dao.jdbc.mapper.VinylRowMapper;
import com.vinylteam.vinyl.entity.Vinyl;
import org.postgresql.ds.PGSimpleDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcVinylDao implements VinylDao {
    private static final String INSERT_UNIQUE_VINYLS = "INSERT INTO unique_vinyls(id, release, artist, full_name, link_to_image) VALUES(?, ?, ?, ?, ?)";
    private static final String SELECT_UNIQUE_VINYLS = "SELECT id, release, artist, full_name, link_to_image FROM unique_vinyls ORDER BY id";
    private static final String SELECT_UNIQUE_VINYL_BY_ID = "SELECT id, release, artist, full_name, link_to_image FROM unique_vinyls WHERE id=?";

    private static final String INSERT_VINYLS = "INSERT INTO vinyls(release, artist, full_name, genre, price, link_to_vinyl, link_to_image, shop_id, unique_vinyl_id) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_VINYLS = "SELECT id, release, artist, full_name, genre, price, link_to_vinyl, link_to_image, shop_id, unique_vinyl_id FROM vinyls";
    private static final String SELECT_VINYL_BY_ID = "SELECT id, release, artist, full_name, genre, price, link_to_vinyl, link_to_image, shop_id, unique_vinyl_id FROM vinyls WHERE id=?";

    private final PGSimpleDataSource dataSource;
    private final VinylRowMapper vinylRowMapper = new VinylRowMapper();
    private final UniqueVinylRowMapper uniqueVinylRowMapper = new UniqueVinylRowMapper();

    public JdbcVinylDao(PGSimpleDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void saveUniqueVinyls(List<Vinyl> uniqueVinyls) {
        try (Connection connection = dataSource.getConnection();
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
    public void saveVinyls(List<Vinyl> vinyls) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement insertVinylsStatement = connection.prepareStatement(INSERT_VINYLS)) {
            connection.setAutoCommit(false);
            for (Vinyl vinyl : vinyls) {
                insertVinylsStatement.setString(1, vinyl.getRelease());
                insertVinylsStatement.setString(2, vinyl.getArtist());
                insertVinylsStatement.setString(3, vinyl.getFullNameVinyl());
                insertVinylsStatement.setString(4, vinyl.getGenre());
                insertVinylsStatement.setString(5, vinyl.getPrice());
                insertVinylsStatement.setString(6, vinyl.getVinylLink());
                insertVinylsStatement.setString(7, vinyl.getImageLink());
                insertVinylsStatement.setInt(8, vinyl.getShopId());
                insertVinylsStatement.setLong(9, vinyl.getUniqueVinylId());
                insertVinylsStatement.addBatch();
            }
            insertVinylsStatement.executeBatch();
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Exception while saving vinyls into vinyls table of DataBase!", e);
        }
    }

    @Override
    public List<Vinyl> getUniqueVinyls() {
        List<Vinyl> uniqueVinyls = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
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
    public List<Vinyl> getVinyls() {
        List<Vinyl> allVinyls = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
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
    public Vinyl getUniqueVinylById(long id) {
        Vinyl vinyl;
        try (Connection connection = dataSource.getConnection();
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
    public Vinyl getVinylById(long id) {
        Vinyl vinyl;
        try (Connection connection = dataSource.getConnection();
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