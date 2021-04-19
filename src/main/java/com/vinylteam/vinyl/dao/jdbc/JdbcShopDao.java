package com.vinylteam.vinyl.dao.jdbc;

import com.vinylteam.vinyl.dao.DBDataSource;
import com.vinylteam.vinyl.dao.ShopDao;
import com.vinylteam.vinyl.dao.jdbc.mapper.ShopRowMapper;
import com.vinylteam.vinyl.entity.Shop;
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

    @Override
    public List<Shop> getManyByListOfIds(List<Integer> ids) {
        List<Shop> shops = new ArrayList<>();
        if (!ids.isEmpty()) {
            String queryToExecute = fillSelectManyByIdsStatement(ids);
            try (Connection connection = DBDataSource.getConnection();
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
        logger.error("Resulting list of shops with ids from list of ids is {'shops':{}}", shops);
        return shops;
    }

    String fillSelectManyByIdsStatement(List<Integer> ids) {
        StringBuffer stringBuffer = new StringBuffer(SELECT_MANY_SHOPS_BY_IDS);
        for (Integer id : ids) {
            if (stringBuffer.lastIndexOf(")") - stringBuffer.lastIndexOf("(") > 1) {
                stringBuffer.insert(stringBuffer.lastIndexOf(")"), ", ");
            }
            stringBuffer.insert(stringBuffer.lastIndexOf(")"), id);
        }
        return stringBuffer.toString();
    }

}
