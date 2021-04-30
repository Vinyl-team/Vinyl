package com.vinylteam.vinyl.dao.jdbc;

import com.vinylteam.vinyl.dao.OfferDao;
import com.vinylteam.vinyl.dao.RowMapper;
import com.vinylteam.vinyl.dao.jdbc.mapper.OfferRowMapper;
import com.vinylteam.vinyl.entity.Offer;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcOfferDao extends AbstractJdbcUniqueVinylAndOfferDao<Offer> implements OfferDao {

    private final String OBJECT_NAME = "offer";
    private final String LIST_NAME = "offers";
    private final String INSERT = "INSERT INTO public.offers(unique_vinyl_id, shop_id, price, currency, genre, link_to_offer) " +
            "VALUES(?, ?, ?, ?, ?, ?)";
    private final String SELECT_ALL = "SELECT id, unique_vinyl_id, shop_id, price, currency, genre, link_to_offer FROM public.offers";
    private final String SELECT_MANY_BY_UNIQUE_VINYL_ID = SELECT_ALL + " WHERE unique_vinyl_id=?";
    private final String SELECT_BY_ID = SELECT_ALL + " WHERE id=?";
    private final String NO_DEFINED_CURRENCY_ERROR_MESSAGE = "No defined currency in offer {'currency':{}, 'offer':{}}";
    private final RowMapper<Offer> rowMapper = new OfferRowMapper();
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final HikariDataSource dataSource;

    public JdbcOfferDao(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Offer> findManyByUniqueVinylId(long uniqueVinylId) {
        List<Offer> offers = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement findVinylByIdStatement = connection.prepareStatement(SELECT_MANY_BY_UNIQUE_VINYL_ID)) {
            findVinylByIdStatement.setLong(1, uniqueVinylId);
            logger.debug(PREPARED_STATEMENT_MESSAGE, findVinylByIdStatement);
            try (ResultSet resultSet = findVinylByIdStatement.executeQuery()) {
                boolean isResultSetEmpty = true;
                while (resultSet.next()) {
                    isResultSetEmpty = false;
                    Offer offer = rowMapper.mapRow(resultSet);
                    offers.add(offer);
                }
                if (isResultSetEmpty) {
                    RuntimeException e = new RuntimeException();
                    logger.error(NO_ROW_BY_ERROR_MESSAGE, OBJECT_NAME, "uniqueVinylId", "uniqueVinylId", uniqueVinylId, e);
                    throw e;
                }
            }
        } catch (SQLException e) {
            logger.error(FINDING_BY_ERROR_MESSAGE, OBJECT_NAME, "uniqueVinylId", "uniqueVinylId", uniqueVinylId, e);
            throw new RuntimeException(e);
        }
        logger.debug(RESULT_IS_MESSAGE, LIST_NAME, LIST_NAME, offers);
        return offers;
    }

}
