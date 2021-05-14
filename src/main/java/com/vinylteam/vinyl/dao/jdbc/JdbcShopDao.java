package com.vinylteam.vinyl.dao.jdbc;

import com.vinylteam.vinyl.dao.ShopDao;
import com.vinylteam.vinyl.dao.jdbc.mapper.ShopRowMapper;
import com.vinylteam.vinyl.entity.Shop;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class JdbcShopDao implements ShopDao {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ShopRowMapper shopRowMapper = new ShopRowMapper();
    final String SELECT_MANY_SHOPS_BY_IDS = "SELECT id, link_to_main_page, link_to_image, name " +
            "FROM public.shops WHERE id IN ()";
    private final HikariDataSource dataSource;

    public JdbcShopDao(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Shop> getManyByListOfIds(List<Integer> ids) {
        List<Shop> shops = new ArrayList<>();
        if (!ids.isEmpty()) {
            String queryToExecute = fillSelectManyByIdsStatement(ids);
            try (Connection connection = dataSource.getConnection();
                 Statement getManyByListOfIdsStatement = connection.createStatement();
                 ResultSet resultSet = getManyByListOfIdsStatement.executeQuery(queryToExecute)) {
                logger.debug("Executed statement {'statement':{}}", getManyByListOfIdsStatement);
                while (resultSet.next()) {
                    Shop shop = shopRowMapper.mapRow(resultSet);
                    shops.add(shop);
                }
            } catch (SQLException e) {
                logger.error("Error while getting list of shops with ids from list of ids from db {'ids':{}, 'shops':{}}",
                        ids, shops, e);
                throw new RuntimeException(e);
            }
        }
        logger.debug("Resulting list of shops with ids from list of ids is {'shops':{}}", shops);
        return shops;
    }

    @Override
    public List<Shop> getAll() {
        return List.of();
    }

    String fillSelectManyByIdsStatement(List<Integer> ids) {
        StringBuffer stringBuffer = new StringBuffer(SELECT_MANY_SHOPS_BY_IDS);
        for (Integer id : ids) {
            if (stringBuffer.lastIndexOf(")") - stringBuffer.lastIndexOf("(") > 1) {
                stringBuffer.insert(stringBuffer.lastIndexOf(")"), ", ");
            }
            stringBuffer.insert(stringBuffer.lastIndexOf(")"), id);
        }
        logger.debug("Resulting string from string buffer is {'string':{}}", stringBuffer);
        return stringBuffer.toString();
    }

}
