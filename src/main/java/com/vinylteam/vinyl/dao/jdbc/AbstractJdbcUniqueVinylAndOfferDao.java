package com.vinylteam.vinyl.dao.jdbc;

import com.vinylteam.vinyl.dao.RowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractJdbcUniqueVinylAndOfferDao<T>{

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

}
