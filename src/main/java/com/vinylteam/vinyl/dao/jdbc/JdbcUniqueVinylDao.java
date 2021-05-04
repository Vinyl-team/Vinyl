package com.vinylteam.vinyl.dao.jdbc;

import com.vinylteam.vinyl.dao.RowMapper;
import com.vinylteam.vinyl.dao.UniqueVinylDao;
import com.vinylteam.vinyl.dao.jdbc.mapper.UniqueVinylRowMapper;
import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcUniqueVinylDao extends AbstractJdbcUniqueVinylAndOfferDao<UniqueVinyl> implements UniqueVinylDao {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final RowMapper<UniqueVinyl> rowMapper = new UniqueVinylRowMapper();
    private final String OBJECT_NAME = "uniqueVinyl";
    private final String LIST_NAME = "uniqueVinyls";
    private final String SELECT_ALL = "SELECT id, release, artist, full_name, link_to_image FROM public.unique_vinyls";
    private final String SELECT_BY_ID = SELECT_ALL + " WHERE id=?";
    private final String SELECT_MANY_RANDOM = SELECT_ALL + " WHERE has_offers ORDER BY random() LIMIT ?";
    private final String SELECT_MANY_BY_FULL_NAME_MATCH = SELECT_ALL + " WHERE full_name ILIKE ? AND has_offers";
    private final String SELECT_BY_ARTIST = SELECT_ALL + " WHERE artist=? AND has_offers";
    private final String FINDING_AMOUNT_OF_RANDOM_ERROR_MESSAGE = "Error while finding that amount of random {} from db {'amount':{}, '{}':{}}";
    private final String FINDING_FILTERED_BY_FULL_NAME_SUBSTRING_ERROR_MESSAGE = "Error while finding {} by fullName matcher from db {'amount':{}, '{}':{}}";
    private final HikariDataSource dataSource;

    public JdbcUniqueVinylDao(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<UniqueVinyl> findAll() {
        List<UniqueVinyl> uniqueVinyls = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement findAllStatement = connection.prepareStatement(SELECT_ALL);
             ResultSet resultSet = findAllStatement.executeQuery()) {
            logger.debug(EXECUTED_STATEMENT_MESSAGE, findAllStatement);
            while (resultSet.next()) {
                UniqueVinyl uniqueVinyl = rowMapper.mapRow(resultSet);
                uniqueVinyls.add(uniqueVinyl);
            }
        } catch (SQLException e) {
            logger.error(FINDING_ALL_ERROR_MESSAGE, LIST_NAME, LIST_NAME, uniqueVinyls, e);
            throw new RuntimeException(e);
        }
        logger.debug(RESULT_IS_MESSAGE, LIST_NAME, LIST_NAME, uniqueVinyls);
        return uniqueVinyls;
    }

    @Override
    public UniqueVinyl findById(long id) {
        UniqueVinyl uniqueVinyl;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement findByIdStatement = connection.prepareStatement(SELECT_BY_ID)) {
            findByIdStatement.setLong(1, id);
            logger.debug(PREPARED_STATEMENT_MESSAGE, findByIdStatement);
            try (ResultSet resultSet = findByIdStatement.executeQuery()) {
                if (resultSet.next()) {
                    uniqueVinyl = rowMapper.mapRow(resultSet);
                } else {
                    RuntimeException e = new RuntimeException();
                    logger.error(NO_ROW_BY_ERROR_MESSAGE, OBJECT_NAME, "id", "id", id, e);
                    throw e;
                }
            }
        } catch (SQLException e) {
            logger.error(FINDING_BY_ERROR_MESSAGE, OBJECT_NAME, "id", "id", id);
            throw new RuntimeException(e);
        }
        logger.debug(RESULT_IS_MESSAGE, OBJECT_NAME, OBJECT_NAME, uniqueVinyl);
        return uniqueVinyl;
    }

    @Override
    public List<UniqueVinyl> findManyRandom(int amount) {
        List<UniqueVinyl> randomUniqueVinyls = new ArrayList<>();
        try(Connection connection = dataSource.getConnection();
            PreparedStatement findAmountOfRandom = connection.prepareStatement(SELECT_MANY_RANDOM)) {
            findAmountOfRandom.setInt(1, amount);
            logger.debug(PREPARED_STATEMENT_MESSAGE, findAmountOfRandom);
            try(ResultSet resultSet = findAmountOfRandom.executeQuery()) {
                while (resultSet.next()) {
                    UniqueVinyl uniqueVinyl = rowMapper.mapRow(resultSet);
                    randomUniqueVinyls.add(uniqueVinyl);
                }
            }
        } catch (SQLException e) {
            logger.error(FINDING_AMOUNT_OF_RANDOM_ERROR_MESSAGE, LIST_NAME, amount, LIST_NAME, randomUniqueVinyls, e);
            throw new RuntimeException("Error while finding " + amount + " of random unique vinyls from the db", e);
        }
        logger.debug(RESULT_IS_MESSAGE, LIST_NAME, LIST_NAME, randomUniqueVinyls);
        return randomUniqueVinyls;
    }

    @Override
    public List<UniqueVinyl> findManyFiltered(String matcher) {
        if (matcher == null) {
            RuntimeException e = new NullPointerException();
            logger.error(PASSED_ARGUMENT_NULL_ERROR_MESSAGE, "matcher", e);
            throw e;
        }
        List<UniqueVinyl> filteredUniqueVinyls = new ArrayList<>();
        try(Connection connection = dataSource.getConnection();
            PreparedStatement findFilteredByFullNameSubstring = connection.prepareStatement(SELECT_MANY_BY_FULL_NAME_MATCH)) {
            findFilteredByFullNameSubstring.setString(1, '%' + matcher + '%');
            logger.debug(PREPARED_STATEMENT_MESSAGE, findFilteredByFullNameSubstring);
            try (ResultSet resultSet = findFilteredByFullNameSubstring.executeQuery()) {
                while (resultSet.next()) {
                    UniqueVinyl uniqueVinyl = rowMapper.mapRow(resultSet);
                    filteredUniqueVinyls.add(uniqueVinyl);
                }
            }
        } catch (SQLException e) {
            logger.error(FINDING_FILTERED_BY_FULL_NAME_SUBSTRING_ERROR_MESSAGE, LIST_NAME, matcher, LIST_NAME, filteredUniqueVinyls);
        }
        logger.debug(RESULT_IS_MESSAGE, LIST_NAME, LIST_NAME, filteredUniqueVinyls);
        return filteredUniqueVinyls;
    }

    @Override
    public List<UniqueVinyl> findManyByArtist(String artist) {
        if (artist == null) {
            RuntimeException e = new NullPointerException();
            logger.error(PASSED_ARGUMENT_NULL_ERROR_MESSAGE, "artist", e);
            throw e;
        }
        List<UniqueVinyl> uniqueVinyls = new ArrayList<>();
        try(Connection connection = dataSource.getConnection();
            PreparedStatement findByArtist = connection.prepareStatement(SELECT_BY_ARTIST)) {
            findByArtist.setString(1, artist);
            logger.debug(PREPARED_STATEMENT_MESSAGE, findByArtist);
            try (ResultSet resultSet = findByArtist.executeQuery()) {
                while (resultSet.next()) {
                    UniqueVinyl uniqueVinyl = rowMapper.mapRow(resultSet);
                    uniqueVinyls.add(uniqueVinyl);
                }
            }
        } catch (SQLException e) {
            logger.error(FINDING_BY_ERROR_MESSAGE, LIST_NAME, artist, LIST_NAME, uniqueVinyls);
        }
        logger.debug(RESULT_IS_MESSAGE, LIST_NAME, LIST_NAME, uniqueVinyls);
        return uniqueVinyls;
    }

}
