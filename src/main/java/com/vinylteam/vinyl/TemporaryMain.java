package com.vinylteam.vinyl;

import com.vinylteam.vinyl.dao.OfferDao;
import com.vinylteam.vinyl.dao.UniqueVinylDao;
import com.vinylteam.vinyl.dao.jdbc.JdbcOfferDao;
import com.vinylteam.vinyl.dao.jdbc.JdbcUniqueVinylDao;
import com.vinylteam.vinyl.service.OfferService;
import com.vinylteam.vinyl.service.UniqueVinylService;
import com.vinylteam.vinyl.service.impl.DefaultOfferService;
import com.vinylteam.vinyl.service.impl.DefaultUniqueVinylService;
import com.vinylteam.vinyl.util.PropertiesReader;
import com.vinylteam.vinyl.util.RawOffersSorter;
import com.vinylteam.vinyl.util.ShopsParser;
import com.vinylteam.vinyl.util.Updater;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

public class TemporaryMain {

    private static final Logger logger = LoggerFactory.getLogger(TemporaryMain.class);

    public static void main(String[] args) {

        HikariDataSource dataSource;
        HikariConfig config = new HikariConfig();
        PropertiesReader propertiesReader = new PropertiesReader();
        config.setJdbcUrl(propertiesReader.getProperty("jdbc.url"));
        config.setUsername(propertiesReader.getProperty("jdbc.user"));
        config.setPassword(propertiesReader.getProperty("jdbc.password"));
        config.setDriverClassName(propertiesReader.getProperty("jdbc.driver"));
        config.setMaximumPoolSize(Integer.parseInt(propertiesReader.getProperty("jdbc.maximum.pool.size")));
        dataSource = new HikariDataSource(config);
        logger.info("Configed dataSourse");

        OfferDao offerDao = new JdbcOfferDao(dataSource);
        UniqueVinylDao uniqueVinylDao = new JdbcUniqueVinylDao(dataSource);
        OfferService offerService = new DefaultOfferService(offerDao);
        UniqueVinylService uniqueVinylService = new DefaultUniqueVinylService(uniqueVinylDao);
        RawOffersSorter rawOffersSorter = new RawOffersSorter();
        ShopsParser shopsParser = new ShopsParser();
        Updater updater = new Updater(uniqueVinylService, offerService, shopsParser, rawOffersSorter);
        TimerTask updateTask = new TimerTask() {
            @Override
            public void run() {
                logger.info("Started updater task");
                updater.updateUniqueVinylsRewriteOffers();
                logger.info("Finished updater task");
            }
        };

        Timer updateTimer = new Timer("Update Timer");
        long period = 1000L * 60L * 60L * 24L;
        updateTimer.scheduleAtFixedRate(updateTask, 0, period);
    }

}
