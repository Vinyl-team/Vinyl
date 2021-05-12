package com.vinylteam.vinyl.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinylteam.vinyl.discogs4j.entity.DiscogsVinylInfo;
import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.util.PropertiesReader;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DefaultDiscogsServiceITest {

    private final List<UniqueVinyl> vinylListWithOneMatch;
    private final List<UniqueVinyl> vinylListWithNoMatch;
    private final PropertiesReader propertiesReader;
    private final DefaultDiscogsService defaultDiscogsService;
    private String discogsWantList = "{\"pagination\": {\"page\": 1, \"pages\": 1, \"per_page\": 50, \"items\": 3, \"urls\": {}}, \"wants\": [{\"id\": 12748000, \"resource_url\": \"https://api.discogs.com/users/Anthony_Hopkins/wants/12748000\", \"rating\": 0, \"date_added\": \"2021-04-07T12:49:32-07:00\", \"basic_information\": {\"id\": 12748000, \"master_id\": 29341, \"master_url\": \"https://api.discogs.com/masters/29341\", \"resource_url\": \"https://api.discogs.com/releases/12748000\", \"title\": \"Best Of Scorpions\", \"year\": 1991, \"formats\": [{\"name\": \"Cassette\", \"qty\": \"1\", \"descriptions\": [\"Compilation\", \"Reissue\"]}], \"labels\": [{\"name\": \"RCA\", \"catno\": \"NK 74006\", \"entity_type\": \"1\", \"entity_type_name\": \"Label\", \"id\": 895, \"resource_url\": \"https://api.discogs.com/labels/895\"}], \"artists\": [{\"name\": \"Scorpions\", \"anv\": \"\", \"join\": \"\", \"role\": \"\", \"tracks\": \"\", \"id\": 63552, \"resource_url\": \"https://api.discogs.com/artists/63552\"}], \"thumb\": \"\", \"cover_image\": \"\", \"genres\": [\"Rock\"], \"styles\": [\"Hard Rock\"]}}, {\"id\": 2288564, \"resource_url\": \"https://api.discogs.com/users/Anthony_Hopkins/wants/2288564\", \"rating\": 0, \"date_added\": \"2021-04-10T13:33:03-07:00\", \"basic_information\": {\"id\": 2288564, \"master_id\": null, \"master_url\": null, \"resource_url\": \"https://api.discogs.com/releases/2288564\", \"title\": \"No Freedom No Liberty\", \"year\": 2007, \"formats\": [{\"name\": \"Vinyl\", \"qty\": \"1\", \"text\": \"green\", \"descriptions\": [\"7\\\"\"]}], \"labels\": [{\"name\": \"True Rebel Records\", \"catno\": \"TRR012\", \"entity_type\": \"1\", \"entity_type_name\": \"Label\", \"id\": 139696, \"resource_url\": \"https://api.discogs.com/labels/139696\"}], \"artists\": [{\"name\": \"The Detectors\", \"anv\": \"\", \"join\": \"\", \"role\": \"\", \"tracks\": \"\", \"id\": 932626, \"resource_url\": \"https://api.discogs.com/artists/932626\"}], \"thumb\": \"\", \"cover_image\": \"\", \"genres\": [\"Rock\"], \"styles\": []}}, {\"id\": 1400099, \"resource_url\": \"https://api.discogs.com/users/Anthony_Hopkins/wants/1400099\", \"rating\": 0, \"date_added\": \"2021-04-10T13:39:02-07:00\", \"basic_information\": {\"id\": 1400099, \"master_id\": 16990, \"master_url\": \"https://api.discogs.com/masters/16990\", \"resource_url\": \"https://api.discogs.com/releases/1400099\", \"title\": \"\\u0416\\u0430\\u043b\\u044c, \\u041d\\u0435\\u0442 \\u0420\\u0443\\u0436\\u044c\\u044f\", \"year\": 2002, \"formats\": [{\"name\": \"CD\", \"qty\": \"1\", \"descriptions\": [\"Album\"]}], \"labels\": [{\"name\": \"\\u041c\\u0438\\u0441\\u0442\\u0435\\u0440\\u0438\\u044f \\u0417\\u0432\\u0443\\u043a\\u0430\", \"catno\": \"MZ-076-2\", \"entity_type\": \"1\", \"entity_type_name\": \"Label\", \"id\": 29191, \"resource_url\": \"https://api.discogs.com/labels/29191\"}], \"artists\": [{\"name\": \"\\u041a\\u043e\\u0440\\u043e\\u043b\\u044c \\u0418 \\u0428\\u0443\\u0442\", \"anv\": \"\", \"join\": \"\", \"role\": \"\", \"tracks\": \"\", \"id\": 386468, \"resource_url\": \"https://api.discogs.com/artists/386468\"}], \"thumb\": \"\", \"cover_image\": \"\", \"genres\": [\"Rock\"], \"styles\": [\"Punk\"]}}]}";

    public DefaultDiscogsServiceITest() {
        this.propertiesReader = new PropertiesReader();
        this.defaultDiscogsService = new DefaultDiscogsService(propertiesReader.getProperty("consumer.key"),
                propertiesReader.getProperty("consumer.secret"), propertiesReader.getProperty("user.agent"),
                propertiesReader.getProperty("callback.url"), new ObjectMapper());
        this.vinylListWithOneMatch = new ArrayList<>();
        this.vinylListWithNoMatch = new ArrayList<>();
    }

    @BeforeAll
    void beforeAll() {
        vinylListWithOneMatch.add(createVinyl("The Detectors", "No Freedom No Liberty"));
        vinylListWithOneMatch.add(createVinyl("Paul Jacobs", "Soul Grabber Part 2 (Remixes)"));
        vinylListWithOneMatch.add(createVinyl("Donnell & Douglas", "The Club Is Open"));

        vinylListWithNoMatch.add(createVinyl("Charis", "The Music, The Feelin'"));
        vinylListWithNoMatch.add(createVinyl("Paul Jacobs", "Soul Grabber Part 2 (Remixes)"));
        vinylListWithNoMatch.add(createVinyl("Donnell & Douglas", "The Club Is Open"));
    }


    @Test
    @DisplayName("Return empty list if discogs username is null")
    void getDiscogsMatchListWhenDiscogsUsernameIsNullTest() {
        //when
        List<UniqueVinyl> listAfterMatching = defaultDiscogsService.getDiscogsMatchList(null, vinylListWithOneMatch);

        //then
        assertTrue(listAfterMatching.isEmpty());
    }

    @Test
    @DisplayName("Return empty list if discogs username is empty String")
    void getDiscogsMatchListWhenDiscogsUsernameIsEmptyStringTest() {
        //when
        List<UniqueVinyl> listAfterMatching = defaultDiscogsService.getDiscogsMatchList("", vinylListWithOneMatch);

        //then
        assertTrue(listAfterMatching.isEmpty());
    }

    @Test
    @DisplayName("Return empty list if list of unique vinyls is null")
    void getDiscogsMatchListWhenListOfUniqueVinylsIsNullTest() {
        //when
        List<UniqueVinyl> listAfterMatching = defaultDiscogsService.getDiscogsMatchList("discogsUserName",
                null);

        //then
        assertTrue(listAfterMatching.isEmpty());
    }

    @Test
    @DisplayName("Return empty list if list of unique vinyls is empty")
    void getDiscogsMatchListWhenListOfUniqueVinylsIsEmptyTest() {
        //when
        List<UniqueVinyl> listAfterMatching = defaultDiscogsService.getDiscogsMatchList("discogsUserName",
                new ArrayList<>());

        //then
        assertTrue(listAfterMatching.isEmpty());
    }

    @Test
    @DisplayName("Return empty list if discogs username isn't exist")
    void getDiscogsMatchListWhenDiscogsUsernameIsNotExistTest() {
        //when
        List<UniqueVinyl> listAfterMatching = defaultDiscogsService.getDiscogsMatchList("not_exit_user_name",
                vinylListWithOneMatch);

        //then
        assertTrue(listAfterMatching.isEmpty());
    }

    @Test
    @DisplayName("Return empty list if there is no match after matching")
    void getDiscogsMatchListWhenNoMatchingTest() {
        //when
        List<UniqueVinyl> listAfterMatching = defaultDiscogsService.getDiscogsMatchList("Anthony_Hopkins",
                vinylListWithNoMatch);

        //then
        assertTrue(listAfterMatching.isEmpty());
    }

    @Test
    @DisplayName("Return list whit matched vinyls")
    void getDiscogsMatchListWhenThereIsMatchingTest() {
        //when
        List<UniqueVinyl> listAfterMatching = defaultDiscogsService.getDiscogsMatchList("Anthony_Hopkins",
                vinylListWithOneMatch);

        //then
        assertEquals(1, listAfterMatching.size());
    }

    @Test
    @DisplayName("Return empty String when artist is null")
    void getDiscogsLinkWhenArtistIsNullTest() throws ParseException {
        //when
        String discogsLink = defaultDiscogsService.getDiscogsLink(null, "No Freedom No Liberty",
                "null - No Freedom No Liberty");

        //then
        assertEquals("", discogsLink);
    }

    @Test
    @DisplayName("Return empty String when release is null")
    void getDiscogsLinkWhenReleaseIsNullTest() throws ParseException {
        //when
        String discogsLink = defaultDiscogsService.getDiscogsLink("The Detectors", null,
                "The Detectors - null");

        //then
        assertEquals("", discogsLink);
    }

    @Test
    @DisplayName("Return empty String when full name is null")
    void getDiscogsLinkWhenFullNameIsNullTest() throws ParseException {
        //when
        String discogsLink = defaultDiscogsService.getDiscogsLink("null", "null",
                null);

        //then
        assertEquals("", discogsLink);
    }

    @Test
    @DisplayName("Return empty String when artist is empty")
    void getDiscogsLinkWhenArtistIsEmptyTest() throws ParseException {
        //when
        String discogsLink = defaultDiscogsService.getDiscogsLink("", "No Freedom No Liberty",
                " - No Freedom No Liberty");

        //then
        assertEquals("", discogsLink);
    }

    @Test
    @DisplayName("Return empty String when release is empty")
    void getDiscogsLinkWhenReleaseIsEmptyTest() throws ParseException {
        //when
        String discogsLink = defaultDiscogsService.getDiscogsLink("The Detectors", "",
                "The Detectors - ");

        //then
        assertEquals("", discogsLink);
    }

    @Test
    @DisplayName("Return empty String when full name is empty")
    void getDiscogsLinkWhenFullNameIsEmptyTest() throws ParseException {
        //when
        String discogsLink = defaultDiscogsService.getDiscogsLink("empty", "empty",
                "");

        //then
        assertEquals("", discogsLink);
    }

    @Test
    @DisplayName("Return empty String when no link on Discogs")
    void getDiscogsLinkWhenNoSearchReleaseOnDiscogsTest() throws ParseException {
        //when
        String discogsLink = defaultDiscogsService.getDiscogsLink("no_existed_artist", "no_existed_release",
                "no_existed_release - no_existed_artist");

        //then
        assertEquals("", discogsLink);
    }

    @Test
    @DisplayName("Return empty String when link on Discogs exist but full name doesn't contains artist & release")
    void getDiscogsLinkWhenFullNameDoesNotContainsArtistAndReleaseTest() throws ParseException {
        //when
        String discogsLink = defaultDiscogsService.getDiscogsLink("artist", "release",
                "No Freedom No Liberty - The Detectors");

        //then
        assertEquals("", discogsLink);
    }

    @Test
    @DisplayName("Return empty String when link on Discogs exist but full name doesn't contains artist")
    void getDiscogsLinkWhenFullNameDoesNotContainsArtistTest() throws ParseException {
        //when
        String discogsLink = defaultDiscogsService.getDiscogsLink("artist", "No Freedom No Liberty",
                "No Freedom No Liberty - The Detectors");

        //then
        assertEquals("", discogsLink);
    }

    @Test
    @DisplayName("Return empty String when link on Discogs exist but full name doesn't contains release")
    void getDiscogsLinkWhenFullNameDoesNotContainsReleaseTest() throws ParseException {
        //when
        String discogsLink = defaultDiscogsService.getDiscogsLink("The Detectors", "release",
                "No Freedom No Liberty - The Detectors");

        //then
        assertEquals("", discogsLink);
    }

    @Test
    @DisplayName("Return Discogs link on release")
    void getDiscogsLinkWhenReleaseExistOnDiscogsTest() throws ParseException {
        //when
        String discogsLink = defaultDiscogsService.getDiscogsLink("The Detectors", "No Freedom No Liberty",
                "No Freedom No Liberty - The Detectors");

        //then
        assertEquals("https://www.discogs.com/ru/The-Detectors-No-Freedom-No-Liberty/release/2288564", discogsLink);
    }

    @Test
    @DisplayName("Return empty list of discogs vinyl info when discogs user name is null")
    void getDiscogsVinylInfoWhenDiscogsUserNameIsNullTest(){
        //when
        Optional<List<DiscogsVinylInfo>> discogsVinylInfoList = defaultDiscogsService.getDiscogsVinylInfo(null);

        //then
        assertEquals(Optional.empty(), discogsVinylInfoList);
    }

    @Test
    @DisplayName("Return empty list of discogs vinyl info when discogs user name is empty String")
    void getDiscogsVinylInfoWhenDiscogsUserNameIsEmptyStringTest(){
        //when
        Optional<List<DiscogsVinylInfo>> discogsVinylInfoList = defaultDiscogsService.getDiscogsVinylInfo("");

        //then
        assertEquals(Optional.empty(), discogsVinylInfoList);
    }

    @Test
    @DisplayName("Return empty list of discogs vinyl info when discogs want list is null")
    void getDiscogsVinylInfoWhenDiscogsWantListIsNullTest(){
        //when
        Optional<List<DiscogsVinylInfo>> discogsVinylInfoList = defaultDiscogsService
                .getDiscogsVinylInfo("not_exist_discogs_user_name");

        //then
        assertEquals(Optional.empty(), discogsVinylInfoList);
    }

    @Test
    @DisplayName("Return list of discogs vinyl info")
    void getDiscogsVinylInfoTest(){
        //when
        Optional<List<DiscogsVinylInfo>> discogsVinylInfoList = defaultDiscogsService
                .getDiscogsVinylInfo("Anthony_Hopkins");

        //then
        assertEquals(discogsWantList, defaultDiscogsService.getDiscogsClient().wantlist("Anthony_Hopkins"));
        assertEquals(3, discogsVinylInfoList.get().size());
    }

    @Test
    @DisplayName("Return empty String when parameter is null")
    void getParametersForComparisonWhenParameterIsNullTest(){
        //when
        String parameterForComparison = defaultDiscogsService.getParametersForComparison(null);

        //then
        assertEquals("", parameterForComparison);
    }

    @Test
    @DisplayName("Return first word 'the' when String is 'ThE'")
    void getParametersForComparisonWhenStringContainsOnlyOneWordAndItIsTheTest(){
        //when
        String parameterForComparison = defaultDiscogsService.getParametersForComparison("ThE");

        //then
        assertEquals("the", parameterForComparison);
    }

    @Test
    @DisplayName("Return first word 'a' when String is 'A'")
    void getParametersForComparisonWhenStringContainsOnlyOneWordAndItIsATest(){
        //when
        String parameterForComparison = defaultDiscogsService.getParametersForComparison("A");

        //then
        assertEquals("a", parameterForComparison);
    }

    @Test
    @DisplayName("Return second word 'artist' when String is 'The Artist'")
    void getParametersForComparisonWhenStringContainsTwoWordsWithArticleTheTest(){
        //when
        String parameterForComparison = defaultDiscogsService.getParametersForComparison("The Artist");

        //then
        assertEquals("artist", parameterForComparison);
    }

    @Test
    @DisplayName("Return second word 'release' when String is 'A Release'")
    void getParametersForComparisonWhenStringContainsTwoWordsWithArticleATest(){
        //when
        String parameterForComparison = defaultDiscogsService.getParametersForComparison("A Release");

        //then
        assertEquals("release", parameterForComparison);
    }

    @Test
    @DisplayName("Return first word 'best' when String is 'BEST RELEASE is here'")
    void getParametersForComparisonWhenStringContainsManyWordsWithoutArticleTest(){
        //when
        String parameterForComparison = defaultDiscogsService.getParametersForComparison("BEST RELEASE is here");

        //then
        assertEquals("best", parameterForComparison);
    }

    private UniqueVinyl createVinyl(String artist, String release) {
        UniqueVinyl vinyl = new UniqueVinyl();
        vinyl.setArtist(artist);
        vinyl.setRelease(release);
        return vinyl;
    }
}