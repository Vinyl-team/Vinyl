package com.vinylteam.vinyl.dao.jdbc;

import com.vinylteam.vinyl.dao.DBDataSource;
import com.vinylteam.vinyl.dao.RowMapper;
import com.vinylteam.vinyl.dao.UniqueVinylDao;
import com.vinylteam.vinyl.dao.jdbc.mapper.UniqueVinylRowMapper;
import com.vinylteam.vinyl.entity.UniqueVinyl;
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
    private final String INSERT = "INSERT INTO public.unique_vinyls(id, release, artist, full_name, link_to_image) VALUES(?, ?, ?, ?, ?)";
    private final String SELECT_ALL = "SELECT id, release, artist, full_name, link_to_image FROM public.unique_vinyls";
    private final String SELECT_BY_ID = SELECT_ALL + " WHERE id=?";
    private final String SELECT_MANY_RANDOM = SELECT_ALL + " TABLESAMPLE SYSTEM_ROWS(?) LIMIT ?";
    private final String SELECT_MANY_BY_FULL_NAME_MATCH = SELECT_ALL + " WHERE full_name ILIKE ?";
    private final String SELECT_BY_ARTIST = SELECT_ALL + " WHERE artist=?";
    private final String FINDING_AMOUNT_OF_RANDOM_ERROR_MESSAGE = "Error while finding that amount of random {} from db {'amount':{}, '{}':{}}";
    private final String FINDING_FILTERED_BY_FULL_NAME_SUBSTRING_ERROR_MESSAGE = "Error while finding {} by fullName matcher from db {'amount':{}, '{}':{}}";


    @Override
    public List<UniqueVinyl> findManyRandom(int amount) {
        List<UniqueVinyl> randomUniqueVinyls = new ArrayList<>();
        try(Connection connection = DBDataSource.getConnection();
            PreparedStatement findAmountOfRandom = connection.prepareStatement(SELECT_MANY_RANDOM)) {
            findAmountOfRandom.setInt(1, amount * 2);
            findAmountOfRandom.setInt(2, amount);
            logger.debug(PREPARED_STATEMENT_MESSAGE, findAmountOfRandom);
            try(ResultSet resultSet = findAmountOfRandom.executeQuery()) {
                while (resultSet.next()) {
                    UniqueVinyl uniqueVinyl = rowMapper.mapRow(resultSet);
                    randomUniqueVinyls.add(uniqueVinyl);
                }
            }
        } catch (SQLException e) {
            logger.error(FINDING_AMOUNT_OF_RANDOM_ERROR_MESSAGE, LIST_NAME, amount, LIST_NAME, randomUniqueVinyls);
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
        try(Connection connection = DBDataSource.getConnection();
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
        try(Connection connection = DBDataSource.getConnection();
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

    @Override
    protected void setAddAllStatementParameters(PreparedStatement preparedStatement, List<UniqueVinyl> uniqueVinyls) throws SQLException {
        for (UniqueVinyl uniqueVinyl : uniqueVinyls) {
            preparedStatement.setLong(1, uniqueVinyl.getId());
            preparedStatement.setString(2, uniqueVinyl.getRelease());
            preparedStatement.setString(3, uniqueVinyl.getArtist());
            preparedStatement.setString(4, uniqueVinyl.getFullName());
            preparedStatement.setString(5, uniqueVinyl.getImageLink());
            preparedStatement.addBatch();
        }
    }
}
