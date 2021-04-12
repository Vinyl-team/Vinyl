package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.util.discogs4j.entity.DiscogsVinylInfo;
import com.vinylteam.vinyl.dao.DBDataSource;
import com.vinylteam.vinyl.dao.jdbc.JdbcVinylDao;
import com.vinylteam.vinyl.entity.Vinyl;
import com.vinylteam.vinyl.service.VinylService;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DefaultDiscogsServiceITest {
    private final String TRUNCATE_UNIQUE_VINYLS = "TRUNCATE unique_vinyls RESTART IDENTITY CASCADE";
    private final String INSERT_UNIQUE_VINYLS = "INSERT INTO unique_vinyls(id, release, artist, full_name, link_to_image)" +
            "VALUES(?, ?, ?, ?, ?)";

    private JdbcVinylDao jdbcVinylDao = new JdbcVinylDao();
    private VinylService vinylService = new DefaultVinylService(jdbcVinylDao);
    private DefaultDiscogsService discogsService = new DefaultDiscogsService(vinylService);
    private String discogsWantList = "{\"pagination\": {\"page\": 1, \"pages\": 1, \"per_page\": 50, \"items\": 3, \"urls\": {}}, \"wants\": [{\"id\": 12748000, \"resource_url\": \"https://api.discogs.com/users/Anthony_Hopkins/wants/12748000\", \"rating\": 0, \"date_added\": \"2021-04-07T12:49:32-07:00\", \"basic_information\": {\"id\": 12748000, \"master_id\": 29341, \"master_url\": \"https://api.discogs.com/masters/29341\", \"resource_url\": \"https://api.discogs.com/releases/12748000\", \"title\": \"Best Of Scorpions\", \"year\": 1991, \"formats\": [{\"name\": \"Cassette\", \"qty\": \"1\", \"descriptions\": [\"Compilation\", \"Reissue\"]}], \"labels\": [{\"name\": \"RCA\", \"catno\": \"NK 74006\", \"entity_type\": \"1\", \"entity_type_name\": \"Label\", \"id\": 895, \"resource_url\": \"https://api.discogs.com/labels/895\"}], \"artists\": [{\"name\": \"Scorpions\", \"anv\": \"\", \"join\": \"\", \"role\": \"\", \"tracks\": \"\", \"id\": 63552, \"resource_url\": \"https://api.discogs.com/artists/63552\"}], \"thumb\": \"\", \"cover_image\": \"\", \"genres\": [\"Rock\"], \"styles\": [\"Hard Rock\"]}}, {\"id\": 2288564, \"resource_url\": \"https://api.discogs.com/users/Anthony_Hopkins/wants/2288564\", \"rating\": 0, \"date_added\": \"2021-04-10T13:33:03-07:00\", \"basic_information\": {\"id\": 2288564, \"master_id\": null, \"master_url\": null, \"resource_url\": \"https://api.discogs.com/releases/2288564\", \"title\": \"No Freedom No Liberty\", \"year\": 2007, \"formats\": [{\"name\": \"Vinyl\", \"qty\": \"1\", \"text\": \"green\", \"descriptions\": [\"7\\\"\"]}], \"labels\": [{\"name\": \"True Rebel Records\", \"catno\": \"TRR012\", \"entity_type\": \"1\", \"entity_type_name\": \"Label\", \"id\": 139696, \"resource_url\": \"https://api.discogs.com/labels/139696\"}], \"artists\": [{\"name\": \"The Detectors\", \"anv\": \"\", \"join\": \"\", \"role\": \"\", \"tracks\": \"\", \"id\": 932626, \"resource_url\": \"https://api.discogs.com/artists/932626\"}], \"thumb\": \"\", \"cover_image\": \"\", \"genres\": [\"Rock\"], \"styles\": []}}, {\"id\": 1400099, \"resource_url\": \"https://api.discogs.com/users/Anthony_Hopkins/wants/1400099\", \"rating\": 0, \"date_added\": \"2021-04-10T13:39:02-07:00\", \"basic_information\": {\"id\": 1400099, \"master_id\": 16990, \"master_url\": \"https://api.discogs.com/masters/16990\", \"resource_url\": \"https://api.discogs.com/releases/1400099\", \"title\": \"\\u0416\\u0430\\u043b\\u044c, \\u041d\\u0435\\u0442 \\u0420\\u0443\\u0436\\u044c\\u044f\", \"year\": 2002, \"formats\": [{\"name\": \"CD\", \"qty\": \"1\", \"descriptions\": [\"Album\"]}], \"labels\": [{\"name\": \"\\u041c\\u0438\\u0441\\u0442\\u0435\\u0440\\u0438\\u044f \\u0417\\u0432\\u0443\\u043a\\u0430\", \"catno\": \"MZ-076-2\", \"entity_type\": \"1\", \"entity_type_name\": \"Label\", \"id\": 29191, \"resource_url\": \"https://api.discogs.com/labels/29191\"}], \"artists\": [{\"name\": \"\\u041a\\u043e\\u0440\\u043e\\u043b\\u044c \\u0418 \\u0428\\u0443\\u0442\", \"anv\": \"\", \"join\": \"\", \"role\": \"\", \"tracks\": \"\", \"id\": 386468, \"resource_url\": \"https://api.discogs.com/artists/386468\"}], \"thumb\": \"\", \"cover_image\": \"\", \"genres\": [\"Rock\"], \"styles\": [\"Punk\"]}}]}";
    private Connection connection;

    @BeforeAll
    void beforeAll() throws SQLException {
        connection = DBDataSource.getDataSource().getConnection();
        try (Statement truncateUniqueVinyls = connection.createStatement()) {
            truncateUniqueVinyls.executeUpdate(TRUNCATE_UNIQUE_VINYLS);
        }
    }

    @AfterAll
    void afterAll() throws SQLException {
        try (Statement truncateUniqueVinyls = connection.createStatement()) {
            truncateUniqueVinyls.executeUpdate(TRUNCATE_UNIQUE_VINYLS);
        }
        connection.close();
    }

    @BeforeEach
    void beforeEach() throws SQLException {
        try (PreparedStatement insertUniqueVinyls = connection.prepareStatement(INSERT_UNIQUE_VINYLS)){
            insertUniqueVinyls.setLong(1, 1);
            insertUniqueVinyls.setString(2, "No Freedom No Liberty");
            insertUniqueVinyls.setString(3, "The Detectors");
            insertUniqueVinyls.setString(4, "No Freedom No Liberty - The Detectors");
            insertUniqueVinyls.setString(5, "https://s3.eu-central-1.amazonaws.com/media.vinyl.ua/ReleaseCover/r-825-ErzesC5D-m.jpg");
            insertUniqueVinyls.executeUpdate();
        }
    }

    @AfterEach
    void afterEach() throws SQLException {
        try (Statement truncateUniqueVinyls = connection.createStatement()) {
            truncateUniqueVinyls.executeUpdate(TRUNCATE_UNIQUE_VINYLS);
        }
    }


    @Test
    @DisplayName("Returns vinyls list matches releases from want list")
    void getVinylsFromDiscogsWantList() {
        //when
        List<Vinyl> actualVinylsList = discogsService.getVinylsFromDiscogsWantList("Anthony_Hopkins");

        //then
        assertNotNull(actualVinylsList);
        assertFalse(actualVinylsList.isEmpty());
        assertNotNull(actualVinylsList);
        assertFalse(actualVinylsList.isEmpty());
        assertEquals(1, actualVinylsList.get(0).getVinylId());
        assertEquals("The Detectors", actualVinylsList.get(0).getArtist());
        assertEquals("No Freedom No Liberty", actualVinylsList.get(0).getRelease());
        assertEquals("No Freedom No Liberty - The Detectors", actualVinylsList.get(0).getFullNameVinyl());
        assertEquals("https://s3.eu-central-1.amazonaws.com/media.vinyl.ua/ReleaseCover/r-825-ErzesC5D-m.jpg", actualVinylsList.get(0).getImageLink());
    }

    @Test
    @DisplayName("Parses discogs want list onto the Discogs vinyl info list")
    void parseDiscogsWantList() {
        //when
        Optional<List<DiscogsVinylInfo>> actualOptionalWantList = discogsService.parseDiscogsWantList(discogsWantList);

        //then
        assertTrue(actualOptionalWantList.isPresent());
        assertEquals("Best Of Scorpions", actualOptionalWantList.get().get(0).getRelease());
        assertEquals("Scorpions", actualOptionalWantList.get().get(0).getArtist());
        assertEquals("No Freedom No Liberty", actualOptionalWantList.get().get(1).getRelease());
        assertEquals("The Detectors", actualOptionalWantList.get().get(1).getArtist());
        assertEquals("Жаль, Нет Ружья", actualOptionalWantList.get().get(2).getRelease());
        assertEquals("Король И Шут", actualOptionalWantList.get().get(2).getArtist());
    }

    @Test
    @DisplayName("Returns list of vinyls from DB with vinyls matches releases names from want list")
    void getVinylsByReleasesFromDB() {
        //prepare
        DiscogsVinylInfo discogsVinylInfo = new DiscogsVinylInfo();
        discogsVinylInfo.setArtist("The Detectors");
        discogsVinylInfo.setRelease("No Freedom No Liberty");
        List<DiscogsVinylInfo> discogsVinylInfoList = List.of(discogsVinylInfo);

        //when
        List<Vinyl> actualVinylsList = discogsService.getVinylsByReleasesFromDB(discogsVinylInfoList);

        //then
        assertNotNull(actualVinylsList);
        assertFalse(actualVinylsList.isEmpty());
        assertEquals(1, actualVinylsList.get(0).getVinylId());
        assertEquals("The Detectors", actualVinylsList.get(0).getArtist());
        assertEquals("No Freedom No Liberty", actualVinylsList.get(0).getRelease());
        assertEquals("No Freedom No Liberty - The Detectors", actualVinylsList.get(0).getFullNameVinyl());
        assertEquals("https://s3.eu-central-1.amazonaws.com/media.vinyl.ua/ReleaseCover/r-825-ErzesC5D-m.jpg", actualVinylsList.get(0).getImageLink());
    }

}