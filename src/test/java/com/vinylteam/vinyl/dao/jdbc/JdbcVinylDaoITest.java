package com.vinylteam.vinyl.dao.jdbc;

import com.vinylteam.vinyl.dao.DBDataSource;
import com.vinylteam.vinyl.dao.VinylDao;
import com.vinylteam.vinyl.entity.Vinyl;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JdbcVinylDaoITest {
    private static final String TRUNCATE_UNIQUE_VINYLS = "TRUNCATE unique_vinyls RESTART IDENTITY CASCADE";
    private static final String TRUNCATE_VINYLS = "TRUNCATE vinyls RESTART IDENTITY";
    private static final String INSERT_UNIQUE_VINYLS = "INSERT INTO unique_vinyls(id, release, artist, full_name, link_to_image)" +
            "VALUES(?, ?, ?, ?, ?), (?, ?, ?, ?, ?)";
    private static final String INSERT_VINYLS = "INSERT INTO vinyls(release, artist, full_name, genre, price, link_to_vinyl, link_to_image, shop_id, unique_vinyl_id)" +
            "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?), (?, ?, ?, ?, ?, ?, ?, ?, ?), (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private final DBDataSource dbDataSource = new DBDataSource();
    private final VinylDao vinylDao = dbDataSource.getJDBCVinylDAO();

    private List<Vinyl> vinylList;
    private Connection connection;

    @BeforeAll
    void beforeAll() throws SQLException {
        connection = dbDataSource.getDataSource().getConnection();
    }

    @BeforeEach
    void beforeEach() throws SQLException {
        try (PreparedStatement insertUniqueVinyls = connection.prepareStatement(INSERT_UNIQUE_VINYLS);
             PreparedStatement insertVinyls = connection.prepareStatement(INSERT_VINYLS)) {

            insertUniqueVinyls.setLong(1, 1);
            insertUniqueVinyls.setString(2, "cool release");
            insertUniqueVinyls.setString(3, "best artist");
            insertUniqueVinyls.setString(4, "cool release - best artist");
            insertUniqueVinyls.setString(5, "https://imagestore.com/somewhere/image.jpg");
            insertUniqueVinyls.setLong(6, 2);
            insertUniqueVinyls.setString(7, "wow effect");
            insertUniqueVinyls.setString(8, "best of the best");
            insertUniqueVinyls.setString(9, "wow effect - best of the best");
            insertUniqueVinyls.setString(10, "https://imagestore.com/somewhere/wow.jpg");
            insertUniqueVinyls.executeUpdate();

            insertVinyls.setString(1, "cool release");
            insertVinyls.setString(2, "best artist");
            insertVinyls.setString(3, "cool release - best artist");
            insertVinyls.setString(4, "pop");
            insertVinyls.setString(5, "55 uah");
            insertVinyls.setString(6, "https://vinylstore.com/somewhere/coolrelease");
            insertVinyls.setString(7, "https://imagestore.com/somewhere/image.jpg");
            insertVinyls.setInt(8, 1);
            insertVinyls.setLong(9, 1);
            insertVinyls.setString(10, "cool release (Vinyl)");
            insertVinyls.setString(11, "best artist");
            insertVinyls.setString(12, "cool release (Vinyl) - best artist");
            insertVinyls.setString(13, "pop");
            insertVinyls.setString(14, "200 uah");
            insertVinyls.setString(15, "https://vinylsite.com/there/coolreleasevinyl");
            insertVinyls.setString(16, "https://imagestore.com/there/imagerelease.jpg");
            insertVinyls.setInt(17, 2);
            insertVinyls.setLong(18, 1);
            insertVinyls.setString(19, "wow effect");
            insertVinyls.setString(20, "best of the best");
            insertVinyls.setString(21, "wow effect - best of the best");
            insertVinyls.setString(22, "rock");
            insertVinyls.setString(23, "1500 euro");
            insertVinyls.setString(24, "https://bestvinyl.com/here/woweffect");
            insertVinyls.setString(25, "https://imagestore.com/somewhere/wow.jpg");
            insertVinyls.setInt(26, 1);
            insertVinyls.setLong(27, 2);
            insertVinyls.executeUpdate();
        }
        vinylList = new ArrayList<>();

        Vinyl firstVinyl = new Vinyl();
        firstVinyl.setVinylId(1);
        firstVinyl.setRelease("cool release");
        firstVinyl.setArtist("best artist");
        firstVinyl.setFullNameVinyl("cool release - best artist");
        firstVinyl.setGenre("pop");
        firstVinyl.setPrice("55 uah");
        firstVinyl.setVinylLink("https://vinylstore.com/somewhere/coolrelease");
        firstVinyl.setImageLink("https://imagestore.com/somewhere/image.jpg");
        firstVinyl.setShopId(1);
        firstVinyl.setUniqueVinylId(1);
        vinylList.add(firstVinyl);

        Vinyl secondVinyl = new Vinyl();
        secondVinyl.setVinylId(2);
        secondVinyl.setRelease("wow effect");
        secondVinyl.setArtist("best of the best");
        secondVinyl.setFullNameVinyl("wow effect - best of the best");
        secondVinyl.setGenre("rock");
        secondVinyl.setPrice("1500 euro");
        secondVinyl.setVinylLink("https://bestvinyl.com/here/woweffect");
        secondVinyl.setImageLink("https://imagestore.com/somewhere/wow.jpg");
        secondVinyl.setShopId(1);
        secondVinyl.setUniqueVinylId(2);
        vinylList.add(secondVinyl);

    }

    @AfterEach
    void afterEach() throws SQLException {
        try (Statement truncateUniqueVinyls = connection.createStatement();
             Statement truncateVinyls = connection.createStatement()) {
            truncateUniqueVinyls.executeUpdate(TRUNCATE_UNIQUE_VINYLS);
            truncateVinyls.executeUpdate(TRUNCATE_VINYLS);
        }
    }

    @Test
    void saveUniqueVinylsITest() throws SQLException {
        try (Statement truncateUniqueVinyls = connection.createStatement()) {
            truncateUniqueVinyls.executeUpdate(TRUNCATE_UNIQUE_VINYLS);
            vinylDao.saveUniqueVinyls(vinylList);
            List<Vinyl> vinyls = vinylDao.getUniqueVinyls();
            assertEquals(1, vinyls.get(0).getVinylId());
            assertEquals("cool release", vinyls.get(0).getRelease());
            assertEquals("best artist", vinyls.get(0).getArtist());
            assertEquals("cool release - best artist", vinyls.get(0).getFullNameVinyl());
            assertEquals("https://imagestore.com/somewhere/image.jpg", vinyls.get(0).getImageLink());

            assertEquals(2, vinyls.get(1).getVinylId());
            assertEquals("wow effect", vinyls.get(1).getRelease());
            assertEquals("best of the best", vinyls.get(1).getArtist());
            assertEquals("wow effect - best of the best", vinyls.get(1).getFullNameVinyl());
            assertEquals("https://imagestore.com/somewhere/wow.jpg", vinyls.get(1).getImageLink());
        }
    }

    @Test
    void saveUniqueVinylsWithDuplicatedUniqueIdITest() throws SQLException {
        try (Statement truncateUniqueVinyls = connection.createStatement()) {
            truncateUniqueVinyls.executeUpdate(TRUNCATE_UNIQUE_VINYLS);
            vinylList.get(1).setUniqueVinylId(1);
            assertThrows(RuntimeException.class, () -> {
                vinylDao.saveUniqueVinyls(vinylList);
            });
        }
    }

    @Test
    void saveUniqueVinylsWithNullReleaseITest() throws SQLException {
        try (Statement truncateUniqueVinyls = connection.createStatement()) {
            truncateUniqueVinyls.executeUpdate(TRUNCATE_UNIQUE_VINYLS);
            vinylList.get(0).setRelease(null);
            assertThrows(RuntimeException.class, () -> {
                vinylDao.saveUniqueVinyls(vinylList);
            });
        }
    }

    @Test
    void saveUniqueVinylsWithNullArtistITest() throws SQLException {
        try (Statement truncateUniqueVinyls = connection.createStatement()) {
            truncateUniqueVinyls.executeUpdate(TRUNCATE_UNIQUE_VINYLS);
            vinylList.get(0).setArtist(null);
            assertThrows(RuntimeException.class, () -> {
                vinylDao.saveUniqueVinyls(vinylList);
            });
        }
    }

    @Test
    void saveUniqueVinylsWithNullLinkToImageITest() throws SQLException {
        try (Statement truncateUniqueVinyls = connection.createStatement()) {
            truncateUniqueVinyls.executeUpdate(TRUNCATE_UNIQUE_VINYLS);
            vinylList.get(0).setImageLink(null);
            assertThrows(RuntimeException.class, () -> {
                vinylDao.saveUniqueVinyls(vinylList);
            });
        }
    }

    @Test
    void saveVinylsITest() throws SQLException {
        try (Statement truncateVinyls = connection.createStatement()) {
            truncateVinyls.executeUpdate(TRUNCATE_VINYLS);
            vinylDao.saveVinyls(vinylList);
            List<Vinyl> vinyls = vinylDao.getVinyls();
            assertEquals(1, vinyls.get(0).getVinylId());
            assertEquals("cool release", vinyls.get(0).getRelease());
            assertEquals("best artist", vinyls.get(0).getArtist());
            assertEquals("cool release - best artist", vinyls.get(0).getFullNameVinyl());
            assertEquals("pop", vinyls.get(0).getGenre());
            assertEquals("55 uah", vinyls.get(0).getPrice());
            assertEquals("https://vinylstore.com/somewhere/coolrelease", vinyls.get(0).getVinylLink());
            assertEquals("https://imagestore.com/somewhere/image.jpg", vinyls.get(0).getImageLink());
            assertEquals(1, vinyls.get(0).getShopId());
            assertEquals(1, vinyls.get(0).getUniqueVinylId());

            assertEquals(2, vinyls.get(1).getVinylId());
            assertEquals("wow effect", vinyls.get(1).getRelease());
            assertEquals("best of the best", vinyls.get(1).getArtist());
            assertEquals("wow effect - best of the best", vinyls.get(1).getFullNameVinyl());
            assertEquals("rock", vinyls.get(1).getGenre());
            assertEquals("1500 euro", vinyls.get(1).getPrice());
            assertEquals("https://bestvinyl.com/here/woweffect", vinyls.get(1).getVinylLink());
            assertEquals("https://imagestore.com/somewhere/wow.jpg", vinyls.get(1).getImageLink());
            assertEquals(1, vinyls.get(1).getShopId());
            assertEquals(2, vinyls.get(1).getUniqueVinylId());
        }
    }

    @Test
    void saveVinylsWithNullReleaseITest() throws SQLException {
        try (Statement truncateVinyls = connection.createStatement()) {
            truncateVinyls.executeUpdate(TRUNCATE_VINYLS);
            vinylList.get(0).setRelease(null);
            assertThrows(RuntimeException.class, () -> {
                vinylDao.saveVinyls(vinylList);
            });
        }
    }

    @Test
    void saveVinylsWithNullArtistITest() throws SQLException {
        try (Statement truncateVinyls = connection.createStatement()) {
            truncateVinyls.executeUpdate(TRUNCATE_VINYLS);
            vinylList.get(0).setArtist(null);
            assertThrows(RuntimeException.class, () -> {
                vinylDao.saveVinyls(vinylList);
            });
        }
    }

    @Test
    void saveVinylsWithNullLinkToVinylITest() throws SQLException {
        try (Statement truncateVinyls = connection.createStatement()) {
            truncateVinyls.executeUpdate(TRUNCATE_VINYLS);
            vinylList.get(0).setVinylLink(null);
            assertThrows(RuntimeException.class, () -> {
                vinylDao.saveVinyls(vinylList);
            });
        }
    }

    @Test
    void saveVinylsWithNullLinkToImageITest() throws SQLException {
        try (Statement truncateVinyls = connection.createStatement()) {
            truncateVinyls.executeUpdate(TRUNCATE_VINYLS);
            vinylList.get(0).setImageLink(null);
            assertThrows(RuntimeException.class, () -> {
                vinylDao.saveVinyls(vinylList);
            });
        }
    }

    @Test
    void saveVinylsWithZeroShopIdITest() throws SQLException {
        try (Statement truncateVinyls = connection.createStatement()) {
            truncateVinyls.executeUpdate(TRUNCATE_VINYLS);
            List<Vinyl> newVinylList = new ArrayList<>();
            Vinyl vinyl = new Vinyl();
            vinyl.setRelease("cool release");
            vinyl.setArtist("best artist");
            vinyl.setFullNameVinyl("cool release - best artist");
            vinyl.setGenre("pop");
            vinyl.setPrice("55 uah");
            vinyl.setVinylLink("https://vinylstore.com/somewhere/coolrelease");
            vinyl.setImageLink("https://imagestore.com/somewhere/image.jpg");
            vinyl.setUniqueVinylId(1);
            newVinylList.add(vinyl);
            assertThrows(RuntimeException.class, () -> {
                vinylDao.saveVinyls(newVinylList);
            });
        }
    }

    @Test
    void saveVinylsWithZeroUniqueVinylIdITest() throws SQLException {
        try (Statement truncateVinyls = connection.createStatement()) {
            truncateVinyls.executeUpdate(TRUNCATE_VINYLS);
            List<Vinyl> newVinylList = new ArrayList<>();
            Vinyl vinyl = new Vinyl();
            vinyl.setRelease("cool release");
            vinyl.setArtist("best artist");
            vinyl.setFullNameVinyl("cool release - best artist");
            vinyl.setGenre("pop");
            vinyl.setPrice("55 uah");
            vinyl.setVinylLink("https://vinylstore.com/somewhere/coolrelease");
            vinyl.setImageLink("https://imagestore.com/somewhere/image.jpg");
            vinyl.setShopId(1);
            newVinylList.add(vinyl);
            assertThrows(RuntimeException.class, () -> {
                vinylDao.saveVinyls(newVinylList);
            });
        }
    }

    @Test
    void getUniqueVinylsITest() {
        List<Vinyl> uniqueVinyls = vinylDao.getUniqueVinyls();

        assertEquals(1, uniqueVinyls.get(0).getVinylId());
        assertEquals("cool release", uniqueVinyls.get(0).getRelease());
        assertEquals("best artist", uniqueVinyls.get(0).getArtist());
        assertEquals("cool release - best artist", uniqueVinyls.get(0).getFullNameVinyl());
        assertEquals("https://imagestore.com/somewhere/image.jpg", uniqueVinyls.get(0).getImageLink());

        assertEquals(2, uniqueVinyls.get(1).getVinylId());
        assertEquals("wow effect", uniqueVinyls.get(1).getRelease());
        assertEquals("best of the best", uniqueVinyls.get(1).getArtist());
        assertEquals("wow effect - best of the best", uniqueVinyls.get(1).getFullNameVinyl());
        assertEquals("https://imagestore.com/somewhere/wow.jpg", uniqueVinyls.get(1).getImageLink());
    }

    @Test
    void getVinylsITest() {
        List<Vinyl> allVinyls = vinylDao.getVinyls();

        assertEquals(1, allVinyls.get(0).getVinylId());
        assertEquals("cool release", allVinyls.get(0).getRelease());
        assertEquals("best artist", allVinyls.get(0).getArtist());
        assertEquals("cool release - best artist", allVinyls.get(0).getFullNameVinyl());
        assertEquals("pop", allVinyls.get(0).getGenre());
        assertEquals("55 uah", allVinyls.get(0).getPrice());
        assertEquals("https://vinylstore.com/somewhere/coolrelease", allVinyls.get(0).getVinylLink());
        assertEquals("https://imagestore.com/somewhere/image.jpg", allVinyls.get(0).getImageLink());
        assertEquals(1, allVinyls.get(0).getShopId());
        assertEquals(1, allVinyls.get(0).getUniqueVinylId());

        assertEquals(2, allVinyls.get(1).getVinylId());
        assertEquals("cool release (Vinyl)", allVinyls.get(1).getRelease());
        assertEquals("best artist", allVinyls.get(1).getArtist());
        assertEquals("cool release (Vinyl) - best artist", allVinyls.get(1).getFullNameVinyl());
        assertEquals("pop", allVinyls.get(1).getGenre());
        assertEquals("200 uah", allVinyls.get(1).getPrice());
        assertEquals("https://vinylsite.com/there/coolreleasevinyl", allVinyls.get(1).getVinylLink());
        assertEquals("https://imagestore.com/there/imagerelease.jpg", allVinyls.get(1).getImageLink());
        assertEquals(2, allVinyls.get(1).getShopId());
        assertEquals(1, allVinyls.get(1).getUniqueVinylId());

        assertEquals(3, allVinyls.get(2).getVinylId());
        assertEquals("wow effect", allVinyls.get(2).getRelease());
        assertEquals("best of the best", allVinyls.get(2).getArtist());
        assertEquals("wow effect - best of the best", allVinyls.get(2).getFullNameVinyl());
        assertEquals("rock", allVinyls.get(2).getGenre());
        assertEquals("1500 euro", allVinyls.get(2).getPrice());
        assertEquals("https://bestvinyl.com/here/woweffect", allVinyls.get(2).getVinylLink());
        assertEquals("https://imagestore.com/somewhere/wow.jpg", allVinyls.get(2).getImageLink());
        assertEquals(1, allVinyls.get(2).getShopId());
        assertEquals(2, allVinyls.get(2).getUniqueVinylId());
    }

    @Test
    void getUniqueVinylByIdITest() {
        Vinyl uniqueVinyl = vinylDao.getUniqueVinylById(2);
        assertEquals(2, uniqueVinyl.getVinylId());
        assertEquals("wow effect", uniqueVinyl.getRelease());
        assertEquals("best of the best", uniqueVinyl.getArtist());
        assertEquals("wow effect - best of the best", uniqueVinyl.getFullNameVinyl());
        assertEquals("https://imagestore.com/somewhere/wow.jpg", uniqueVinyl.getImageLink());
    }

    @Test
    void getUniqueVinylByNonExistedIdITest() {
        assertThrows(RuntimeException.class, () -> {
            vinylDao.getUniqueVinylById(15);
        });
    }

    @Test
    void getVinylByIdITest() {
        Vinyl vinyl = vinylDao.getVinylById(2);
        assertEquals(2, vinyl.getVinylId());
        assertEquals("cool release (Vinyl)", vinyl.getRelease());
        assertEquals("best artist", vinyl.getArtist());
        assertEquals("cool release (Vinyl) - best artist", vinyl.getFullNameVinyl());
        assertEquals("pop", vinyl.getGenre());
        assertEquals("200 uah", vinyl.getPrice());
        assertEquals("https://vinylsite.com/there/coolreleasevinyl", vinyl.getVinylLink());
        assertEquals("https://imagestore.com/there/imagerelease.jpg", vinyl.getImageLink());
        assertEquals(2, vinyl.getShopId());
        assertEquals(1, vinyl.getUniqueVinylId());
    }

    @Test
    void getVinylByNonExistedIdITest() {
        assertThrows(RuntimeException.class, () -> {
            vinylDao.getVinylById(15);
        });
    }

    @AfterAll
    void afterAll() throws SQLException {
        try (Statement truncateUniqueVinyls = connection.createStatement();
             Statement truncateVinyls = connection.createStatement()) {
            truncateUniqueVinyls.executeUpdate(TRUNCATE_UNIQUE_VINYLS);
            truncateVinyls.executeUpdate(TRUNCATE_VINYLS);
        }
        connection.close();
    }
}