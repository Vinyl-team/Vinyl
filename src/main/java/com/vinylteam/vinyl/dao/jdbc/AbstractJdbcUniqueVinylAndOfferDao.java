package com.vinylteam.vinyl.dao.jdbc;

import com.vinylteam.vinyl.dao.DBDataSource;
import com.vinylteam.vinyl.dao.RowMapper;
import com.vinylteam.vinyl.dao.UniqueVinylAndOfferDao;
import org.postgresql.util.PSQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractJdbcUniqueVinylAndOfferDao<T> implements UniqueVinylAndOfferDao<T> {

    private RowMapper<T> rowMapper;
    private String INSERT;
    private String SELECT_ALL;
    private String SELECT_BY_ID;
    private String OBJECT_NAME;
    private String LIST_NAME;
    protected final String PREPARED_STATEMENT_MESSAGE = "Prepared statement {'preparedStatement':{}}";
    protected final String EXECUTED_STATEMENT_MESSAGE = "Executed statement {'statement':{}}";
    protected final String PASSED_ARGUMENT_NULL_ERROR_MESSAGE = "Passed {} is null";
    protected final String PASSED_LIST_IS_EMPTY_WARNING_MESSAGE = "Passed list {} is empty, not going in db";
    protected final String ADDING_DATABASE_ERROR_MESSAGE = "Database error while adding {} to db {'{}':{}}";
    protected final String ADDING_ERROR_MESSAGE = "Error while adding {} to db {'{}':{}}";
    protected final String ADDING_SUCCESS_MESSAGE = "Added {} to db {'{}':{}}";
    protected final String ADDING_FAIL_MESSAGE = "Failed to add {} to db {'{}':{}}";
    protected final String FINDING_ALL_ERROR_MESSAGE = "Error while finding {} in db {'{}':{}}";
    protected final String FINDING_BY_ERROR_MESSAGE = "Error while finding {} by {} from db {'{}':{}}";
    protected final String NO_ROW_BY_ERROR_MESSAGE = "No {} with that {} in table {'{}':{}}";
    protected final String RESULT_IS_MESSAGE = "Resulting {} is {{}:{}}";
    private final Logger logger = LoggerFactory.getLogger(getClass());


    @Override
    public boolean addAll(List<T> list) {
        boolean isAdded;
        if (list == null) {
            RuntimeException e = new NullPointerException();
            logger.error(PASSED_ARGUMENT_NULL_ERROR_MESSAGE, LIST_NAME, e);
            throw e;
        }
        if (!list.isEmpty()) {
            try (Connection connection = DBDataSource.getConnection();
                 PreparedStatement insertStatement = connection.prepareStatement(INSERT)) {
                connection.setAutoCommit(false);
                setAddAllStatementParameters(insertStatement, list);
                logger.debug(PREPARED_STATEMENT_MESSAGE, insertStatement);
                insertStatement.executeBatch();
                connection.commit();
                isAdded = true;
            } catch (PSQLException e) {
                logger.debug(ADDING_DATABASE_ERROR_MESSAGE, LIST_NAME, LIST_NAME, list, e);
                isAdded = false;
            } catch (SQLException e) {
                logger.error(ADDING_ERROR_MESSAGE, LIST_NAME, LIST_NAME, list, e);
                throw new RuntimeException(e);
            }
            if (isAdded) {
                logger.info(ADDING_SUCCESS_MESSAGE, LIST_NAME, LIST_NAME, list);
            } else {
                logger.info(ADDING_FAIL_MESSAGE, LIST_NAME, LIST_NAME, list);
            }
        } else {
            logger.error(PASSED_LIST_IS_EMPTY_WARNING_MESSAGE, LIST_NAME);
            isAdded = false;
        }
        return isAdded;
    }

    @Override
    public List<T> findAll() {
        List<T> resultList = new ArrayList<>();
        try (Connection connection = DBDataSource.getConnection();
             PreparedStatement findAllStatement = connection.prepareStatement(SELECT_ALL);
             ResultSet resultSet = findAllStatement.executeQuery()) {
            logger.debug(EXECUTED_STATEMENT_MESSAGE, findAllStatement);
            while (resultSet.next()) {
                T object = rowMapper.mapRow(resultSet);
                resultList.add(object);
            }
        } catch (SQLException e) {
            logger.error(FINDING_ALL_ERROR_MESSAGE, LIST_NAME, LIST_NAME, resultList, e);
            throw new RuntimeException(e);
        }
        logger.debug(RESULT_IS_MESSAGE, LIST_NAME, LIST_NAME, resultList);
        return resultList;
    }

    @Override
    public T findById(long id) {
        T object;
        try (Connection connection = DBDataSource.getConnection();
             PreparedStatement findByIdStatement = connection.prepareStatement(SELECT_BY_ID)) {
            findByIdStatement.setLong(1, id);
            logger.debug(PREPARED_STATEMENT_MESSAGE, findByIdStatement);
            try (ResultSet resultSet = findByIdStatement.executeQuery()) {
                if (resultSet.next()) {
                    object = rowMapper.mapRow(resultSet);
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
        logger.debug(RESULT_IS_MESSAGE, OBJECT_NAME, OBJECT_NAME, object);
        return object;
    }

    protected abstract void setAddAllStatementParameters(PreparedStatement preparedStatement, List<T> list) throws SQLException;
}
