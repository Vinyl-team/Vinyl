package com.vinylteam.vinyl.dao.jdbc;

import com.vinylteam.vinyl.dao.DBDataSource;
import com.vinylteam.vinyl.dao.OfferDao;
import com.vinylteam.vinyl.dao.RowMapper;
import com.vinylteam.vinyl.dao.jdbc.mapper.OfferRowMapper;
import com.vinylteam.vinyl.entity.Offer;
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

    @Override
    public List<Offer> findManyByUniqueVinylId(long uniqueVinylId) {
        List<Offer> offers = new ArrayList<>();
        try (Connection connection = DBDataSource.getConnection();
             PreparedStatement findVinylByIdStatement = connection.prepareStatement(SELECT_MANY_BY_UNIQUE_VINYL_ID)) {
            findVinylByIdStatement.setLong(1, uniqueVinylId);
            logger.debug(PREPARED_STATEMENT_MESSAGE, findVinylByIdStatement);
            try (ResultSet resultSet = findVinylByIdStatement.executeQuery()) {
                if (resultSet.next()) {
                    resultSet.previous();
                    while (resultSet.next()) {
                        Offer offer = rowMapper.mapRow(resultSet);
                        offers.add(offer);
                    }
                } else {
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

    @Override
    protected void setAddAllStatementParameters(PreparedStatement preparedStatement, List<Offer> offers) throws SQLException {
        for (Offer offer : offers) {
            preparedStatement.setLong(1, offer.getUniqueVinylId());
            preparedStatement.setInt(2, offer.getShopId());
            preparedStatement.setDouble(3, offer.getPrice());
            if (offer.getCurrency().isPresent()) {
                preparedStatement.setString(4, offer.getCurrency().get().toString());
            } else {
                RuntimeException e = new RuntimeException();
                logger.error(NO_DEFINED_CURRENCY_ERROR_MESSAGE, offer.getCurrency(), offer, e);
                throw e;
            }
            preparedStatement.setString(5, offer.getGenre());
            preparedStatement.setString(6, offer.getOfferLink());
            preparedStatement.addBatch();
        }
    }
}
