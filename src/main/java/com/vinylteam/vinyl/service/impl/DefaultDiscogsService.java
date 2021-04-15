package com.vinylteam.vinyl.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinylteam.vinyl.service.DiscogsService;
import com.vinylteam.vinyl.discogs4j.client.DiscogsClient;
import com.vinylteam.vinyl.discogs4j.entity.DiscogsVinylInfo;
import com.vinylteam.vinyl.discogs4j.entity.RawResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DefaultDiscogsService implements DiscogsService {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ObjectMapper objectMapper;
    private final DiscogsClient discogsClient;

    private final String CONSUMER_KEY;
    private final String CONSUMER_SECRET;
    private final String USER_AGENT;
    private final String CALLBACK_URL;

    public DefaultDiscogsService(String consumerKey, String consumerSecret, String userAgent, String callbackUrl,
                                 ObjectMapper objectMapper) {
        this.CONSUMER_KEY = consumerKey;
        this.CONSUMER_SECRET = consumerSecret;
        this.USER_AGENT = userAgent;
        this.CALLBACK_URL = callbackUrl;
        this.discogsClient = new DiscogsClient(CONSUMER_KEY, CONSUMER_SECRET, USER_AGENT, CALLBACK_URL);
        this.objectMapper = objectMapper;

        try {
            discogsClient.getRequestToken();
        } catch (ArrayIndexOutOfBoundsException e) {
            logger.error("Failed to connect to discogs user with consumer key: {}, consumer secret: {}, user agent: {}," +
                    " callback url: {} ", CONSUMER_KEY, CONSUMER_SECRET, USER_AGENT, CALLBACK_URL, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> getVinylsReleasesFromDiscogsWantList(String discogsUserName) {
        String discogsWantList = discogsClient.wantlist(discogsUserName);
        Optional<List<DiscogsVinylInfo>> optionalDiscogsVinylInfoList = parseDiscogsWantList(discogsWantList);
        logger.info("Want list: {}", discogsWantList);
        if (optionalDiscogsVinylInfoList.isPresent()) {
            return getVinylsReleases(optionalDiscogsVinylInfoList.get());
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

    List<String> getVinylsReleases(List<DiscogsVinylInfo> discogsVinylInfoList) {
        List<String> vinylsReleasesList = new ArrayList<>();

        for (DiscogsVinylInfo discogsVinylInfo : discogsVinylInfoList) {
            vinylsReleasesList.add(discogsVinylInfo.getRelease());
        }
        return vinylsReleasesList;
    }

}
