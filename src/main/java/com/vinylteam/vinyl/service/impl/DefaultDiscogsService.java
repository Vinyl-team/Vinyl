package com.vinylteam.vinyl.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinylteam.vinyl.util.discogs4j.client.DiscogsClient;
import com.vinylteam.vinyl.util.discogs4j.entity.DiscogsVinylInfo;
import com.vinylteam.vinyl.util.discogs4j.entity.RawResponse;
import com.vinylteam.vinyl.util.PropertiesReader;
import com.vinylteam.vinyl.entity.Vinyl;
import com.vinylteam.vinyl.service.DiscogsService;
import com.vinylteam.vinyl.service.VinylService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DefaultDiscogsService implements DiscogsService {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final PropertiesReader propertiesReader = new PropertiesReader();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final VinylService vinylService;
    private final DiscogsClient discogsClient;

    private final String CONSUMER_KEY;
    private final String CONSUMER_SECRET;
    private final String USER_AGENT;
    private final String CALLBACK_URL;

    public DefaultDiscogsService(VinylService vinylService) {
        this.vinylService = vinylService;
        this.CONSUMER_KEY = propertiesReader.getProperties().getProperty("consumer.key");
        this.CONSUMER_SECRET = propertiesReader.getProperties().getProperty("consumer.secret");
        this.USER_AGENT = propertiesReader.getProperties().getProperty("user.agent");
        this.CALLBACK_URL = propertiesReader.getProperties().getProperty("callback.url");
        this.discogsClient = new DiscogsClient(CONSUMER_KEY, CONSUMER_SECRET, USER_AGENT, CALLBACK_URL);

        try {
            discogsClient.getRequestToken();
        } catch (ArrayIndexOutOfBoundsException e) {
            logger.error("Failed to connect to discogs user with consumer key: {}, consumer secret: {}, user agent: {}," +
                    " callback url: {} ", CONSUMER_KEY, CONSUMER_SECRET, USER_AGENT, CALLBACK_URL);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Vinyl> getVinylsFromDiscogsWantList(String discogsUserName) {
        String discogsWantList = discogsClient.wantlist(discogsUserName);
        Optional<List<DiscogsVinylInfo>> optionalDiscogsVinylInfoList = parseDiscogsWantList(discogsWantList);
        logger.info("Want list: {}", discogsWantList);
        if (optionalDiscogsVinylInfoList.isPresent()) {
            return getVinylsByReleasesFromDB(optionalDiscogsVinylInfoList.get());
        }
        return List.of();
    }

    Optional<List<DiscogsVinylInfo>> parseDiscogsWantList(String discogsWantList) {
        try {
            RawResponse rawResponse = objectMapper.readValue(discogsWantList, RawResponse.class);
            return rawResponse.getVinylsInfo();
        } catch (JsonProcessingException e) {
            logger.error("Want list: {}", discogsWantList);
            throw new RuntimeException("Exception while want list json processing", e);
        }
    }

    List<Vinyl> getVinylsByReleasesFromDB(List<DiscogsVinylInfo> discogsVinylInfoList) {
        List<Vinyl> vinylsList = new ArrayList<>();

        for (DiscogsVinylInfo discogsVinylInfo : discogsVinylInfoList) {
            Optional<Vinyl> optionalVinyl = vinylService.getByRelease(discogsVinylInfo.getRelease());
            optionalVinyl.ifPresent(vinylsList::add);
        }
        return vinylsList;
    }

}
