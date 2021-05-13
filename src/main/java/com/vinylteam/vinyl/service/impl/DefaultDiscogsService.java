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
        if (discogsUserName == null || allUniqueVinyl == null || discogsUserName.isEmpty() || allUniqueVinyl.isEmpty()) {
            return forShowing;
        }
        Optional<List<DiscogsVinylInfo>> discogsVinylInfo = getDiscogsVinylInfo(discogsUserName);
        logger.debug("Get list with DiscogsVinylInfo {'discogsVinylInfo':{}}", discogsVinylInfo);
        if (discogsVinylInfo.isPresent()) {
            for (DiscogsVinylInfo vinylInfo : discogsVinylInfo.get()) {
                String release = getParametersForComparison(vinylInfo.getRelease());
                logger.debug("Prepared release from DiscogsVinylInfo for comparison with release from UniqueVinyl {'release':{}}",
                        release);
                String artist = getParametersForComparison(vinylInfo.getArtist());
                logger.debug("Prepared artist from DiscogsVinylInfo for comparison with artist from UniqueVinyl {'artist':{}}",
                        artist);
                for (UniqueVinyl uniqueVinyl : allUniqueVinyl) {
                    String uniqueRelease = getParametersForComparison(uniqueVinyl.getRelease());
                    logger.debug("Prepared uniqueRelease from UniqueVinyl for comparison with release from DiscogsVinylInfo {'uniqueRelease':{}}",
                            uniqueRelease);
                    String uniqueArtist = getParametersForComparison(uniqueVinyl.getArtist());
                    logger.debug("Prepared uniqueArtist from UniqueVinyl for comparison with artist from DiscogsVinylInfo {'uniqueArtist':{}}",
                            uniqueArtist);
                    if (release.equals(uniqueRelease) && artist.equals(uniqueArtist)) {
                        forShowing.add(uniqueVinyl);
                        logger.debug("Comparison with artist & release from DiscogsVinylInfo & UniqueVinyl is successful. UniqueVinyl " +
                                        "was added into list of UniqueVinyl that call 'forShowing' {'forShowing':{}}",
                                forShowing);
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
                || artist.isEmpty() || release.isEmpty() || fullName.isEmpty()) {
            return discogsLink;
        }
        String artistComparing = getParametersForComparison(artist);
        logger.debug("Prepared artistComparing for comparison with release from Discogs {'artistComparing':{}}", artistComparing);
        String releaseComparing = getParametersForComparison(release);
        logger.debug("Prepared releaseComparing for comparison with artist from Discogs {'releaseComparing':{}}", releaseComparing);
        query = fullName.replace(" ", "+");
        logger.debug("Prepared query for search vinyl on Discogs {'query':{}}", query);
        HttpRequest request = HttpRequest.get("https://api.discogs.com/database/search?q=" + query +
                "&token=DzUqaiWPuQDWExZlqrAUuIZBYAHuBjNnapETonep");
        requestBody = request.body();
        logger.debug("Get requestBody after search vinyl on Discogs {'requestBody':{}}", requestBody);
        JSONObject jsonRequest = (JSONObject) new JSONParser().parse(requestBody);
        logger.debug("Get JSONObject from requestBody after search vinyl on Discogs {'jsonRequest':{}}", jsonRequest);
        JSONArray resultSearch = (JSONArray) jsonRequest.get("results");
        logger.debug("Get JSONArray of necessary data from JSONObject after search vinyl on Discogs {'resultSearch':{}}", resultSearch);
        if (resultSearch != null) {
            for (Object searchItem : resultSearch) {
                JSONObject jsonItem = (JSONObject) searchItem;
                String discogsFullName = jsonItem.get("title").toString().toLowerCase();
                logger.debug("Prepared discogsFullName from Discogs for comparison with artist & release {'discogsFullName':{}}",
                        discogsFullName);
                if (discogsFullName.contains(artistComparing) && discogsFullName.contains(releaseComparing)) {
                    discogsLink = jsonItem.get("uri").toString();
                    discogsLink = "https://www.discogs.com/ru" + discogsLink;
                    logger.debug("Created link of vinyl to Discogs after successful comparison with data from db {'discogsLink':{}}", discogsLink);
                    break;
                }
            }
        }
        return discogsLink;
    }

    Optional<List<DiscogsVinylInfo>> getDiscogsVinylInfo(String discogsUserName) {
        if (discogsUserName == null || discogsUserName.isEmpty()) {
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
        if (param == null) {
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
