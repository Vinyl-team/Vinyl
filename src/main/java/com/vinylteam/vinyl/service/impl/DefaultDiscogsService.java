package com.vinylteam.vinyl.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinylteam.vinyl.discogs4j.client.DiscogsClient;
import com.vinylteam.vinyl.discogs4j.entity.DiscogsVinylInfo;
import com.vinylteam.vinyl.discogs4j.entity.RawResponse;
import com.vinylteam.vinyl.discogs4j.util.HttpRequest;
import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.service.DiscogsService;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
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
            logger.error("Failed to connect to discogs user with {'consumeKey': {}, {'consumerSecret': {}, " +
                    "{'userAgent': {}, {'callbackUrl': {}} ", CONSUMER_KEY, CONSUMER_SECRET, USER_AGENT, CALLBACK_URL, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<UniqueVinyl> getDiscogsMatchList(String discogsUserName, List<UniqueVinyl> allUniqueVinyl) {
        List<UniqueVinyl> forShowing = new ArrayList<>();
        if (discogsUserName == null || allUniqueVinyl == null || discogsUserName.equals("") || allUniqueVinyl.isEmpty()) {
            return forShowing;
        }
        Optional<List<DiscogsVinylInfo>> discogsVinylInfo = getDiscogsVinylInfo(discogsUserName);
        if (discogsVinylInfo.isPresent()) {
            for (DiscogsVinylInfo vinylInfo : discogsVinylInfo.get()) {
                String release = getParametersForComparison(vinylInfo.getRelease());
                String artist = getParametersForComparison(vinylInfo.getArtist());
                for (UniqueVinyl uniqueVinyl : allUniqueVinyl) {
                    String uniqueRelease = getParametersForComparison(uniqueVinyl.getRelease());
                    String uniqueArtist = getParametersForComparison(uniqueVinyl.getArtist());
                    if (release.equals(uniqueRelease) && artist.equals(uniqueArtist)) {
                        forShowing.add(uniqueVinyl);
                    }
                }
            }
        }
        return forShowing;
    }

    @Override
    public String getDiscogsLink(String artist, String release, String fullName) throws ParseException {
        String query;
        String requestBody;
        String discogsLink = "";
        if (artist == null || release == null || fullName == null
                || artist.equals("") || release.equals("") || fullName.equals("")) {
            return discogsLink;
        }
        String artistComparing = getParametersForComparison(artist);
        String releaseComparing = getParametersForComparison(release);
        query = fullName.replace(" ", "+");
        HttpRequest request = HttpRequest.get("https://api.discogs.com/database/search?q=" + query +
                "&token=DzUqaiWPuQDWExZlqrAUuIZBYAHuBjNnapETonep");
        requestBody = request.body();
        JSONObject jsonRequest = (JSONObject) new JSONParser().parse(requestBody);
        JSONArray resultSearch = (JSONArray) jsonRequest.get("results");
        if (resultSearch != null) {
            for (Object searchItem : resultSearch) {
                JSONObject jsonItem = (JSONObject) searchItem;
                String discogsFullName = jsonItem.get("title").toString().toLowerCase();
                if (discogsFullName.contains(artistComparing) && discogsFullName.contains(releaseComparing)) {
                    discogsLink = jsonItem.get("uri").toString();
                    discogsLink = "https://www.discogs.com/ru" + discogsLink;
                    break;
                }
            }
        }
        return discogsLink;
    }

    public DiscogsClient getDiscogsClient() {
        return discogsClient;
    }

    Optional<List<DiscogsVinylInfo>> getDiscogsVinylInfo(String discogsUserName) {
        if (discogsUserName == null || discogsUserName.equals("")){
            return Optional.empty();
        }
        String discogsWantList = discogsClient.wantlist(discogsUserName);
        try {
            if (discogsWantList != null) {
                RawResponse rawResponse = objectMapper.readValue(discogsWantList, RawResponse.class);
                return rawResponse.getVinylsInfo();
            }
            return Optional.empty();
        } catch (JsonProcessingException e) {
            logger.error("{'wantList':{}}", discogsWantList, e);
            throw new RuntimeException("Exception while want list json processing", e);
        }
    }

    String getParametersForComparison(String param) {
        if (param == null){
            return "";
        }
        String[] paramArray = param.split(" ");
        logger.debug("Split param into param array {'param':{}, 'paramArray':{}}", param, paramArray);
        if (paramArray.length > 1 && (paramArray[0].equalsIgnoreCase("the") || paramArray[0].equalsIgnoreCase("a"))) {
            paramArray[0] = paramArray[1];
        }
        logger.debug("Resulting string is {'resultParam':{}}", paramArray[0]);
        return paramArray[0].toLowerCase();
    }

}
