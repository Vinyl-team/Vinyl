package com.vinylteam.vinyl.dao.jdbc;

import com.vinylteam.vinyl.dao.DBDataSource;
import com.vinylteam.vinyl.dao.VinylDao;
import com.vinylteam.vinyl.entity.Currency;
import com.vinylteam.vinyl.entity.Shop;
import com.vinylteam.vinyl.entity.Vinyl;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JdbcVinylDaoITest {

    private final String TRUNCATE_SHOPS = "TRUNCATE shops RESTART IDENTITY CASCADE";
    private final String TRUNCATE_UNIQUE_VINYLS = "TRUNCATE unique_vinyls RESTART IDENTITY CASCADE";
    private final String TRUNCATE_VINYLS = "TRUNCATE vinyls RESTART IDENTITY";
    private final String INSERT_SHOPS = "INSERT INTO shops(id, link_to_main_page, link_to_image, name)" +
            "VALUES(?, ?, ?, ?), (?, ?, ?, ?)";
    private final String INSERT_UNIQUE_VINYLS = "INSERT INTO unique_vinyls(id, release, artist, full_name, link_to_image)" +
            "VALUES(?, ?, ?, ?, ?), (?, ?, ?, ?, ?)";
    private final String INSERT_VINYLS = "INSERT INTO vinyls(release, artist, full_name, genre," +
            " price, currency, link_to_vinyl, link_to_image, shop_id, unique_vinyl_id)" +
            "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?), (?, ?, ?, ?, ?, ?, ?, ?, ?, ?), (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private final VinylDao vinylDao = new JdbcVinylDao();

    private List<Shop> shopList;
    private List<Vinyl> vinylList;
    private Connection connection;

    @BeforeAll
    void beforeAll() throws SQLException {
        connection = DBDataSource.getConnection();
        try (Statement truncateShops = connection.createStatement();
             Statement truncateUniqueVinyls = connection.createStatement();
             Statement truncateVinyls = connection.createStatement()) {
            truncateShops.executeUpdate(TRUNCATE_SHOPS);
            truncateUniqueVinyls.executeUpdate(TRUNCATE_UNIQUE_VINYLS);
            truncateVinyls.executeUpdate(TRUNCATE_VINYLS);
        }
        shopList = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Shop shop = new Shop();
            shop.setId(i + 1);
            shop.setName("shop" + (i + 1));
            shop.setMainPageLink(shop.getName() + "/main");
            shop.setImageLink(shop.getName() + "/image.png");
            shopList.add(shop);
        }
    }

    @AfterAll
    void afterAll() throws SQLException {
        try (Statement truncateShops = connection.createStatement();
             Statement truncateUniqueVinyls = connection.createStatement();
             Statement truncateVinyls = connection.createStatement()) {
            truncateShops.executeUpdate(TRUNCATE_SHOPS);
            truncateUniqueVinyls.executeUpdate(TRUNCATE_UNIQUE_VINYLS);
            truncateVinyls.executeUpdate(TRUNCATE_VINYLS);
        }
        connection.close();
    }

    @BeforeEach
    void beforeEach() throws SQLException {
        try (PreparedStatement insertShops = connection.prepareStatement(INSERT_SHOPS);
             PreparedStatement insertUniqueVinyls = connection.prepareStatement(INSERT_UNIQUE_VINYLS);
             PreparedStatement insertVinyls = connection.prepareStatement(INSERT_VINYLS)) {
            for (int i = 0; i < 2; i++) {
                insertShops.setInt(1 + i * 4, shopList.get(i).getId());
                insertShops.setString(2 + i * 4, shopList.get(i).getMainPageLink());
                insertShops.setString(3 + i * 4, shopList.get(i).getImageLink());
                insertShops.setString(4 + i * 4, shopList.get(i).getName());
            }
            insertShops.executeUpdate();

            insertUniqueVinyls.setLong(1, 1);
            insertUniqueVinyls.setString(2, "release1");
            insertUniqueVinyls.setString(3, "artist1");
            insertUniqueVinyls.setString(4, "release1 - artist1");
            insertUniqueVinyls.setString(5, "https://imagestore.com/somewhere/image1.jpg");
            insertUniqueVinyls.setLong(6, 2);
            insertUniqueVinyls.setString(7, "release2");
            insertUniqueVinyls.setString(8, "artist2");
            insertUniqueVinyls.setString(9, "release2 - artist2");
            insertUniqueVinyls.setString(10, "https://imagestore.com/somewhere/image2.jpg");
            insertUniqueVinyls.executeUpdate();

            insertVinyls.setString(1, "release1");
            insertVinyls.setString(2, "artist1");
            insertVinyls.setString(3, "release1 - artist1");
            insertVinyls.setString(4, "pop");
            insertVinyls.setDouble(5, 55);
            insertVinyls.setString(6, Currency.UAH.toString());
            insertVinyls.setString(7, "https://vinylstore.com/somewhere/release1");
            insertVinyls.setString(8, "https://imagestore.com/somewhere/image1.jpg");
            insertVinyls.setInt(9, 1);
            insertVinyls.setLong(10, 1);
            insertVinyls.setString(11, "release1 (Vinyl)");
            insertVinyls.setString(12, "artist1");
            insertVinyls.setString(13, "release1 (Vinyl) - artist1");
            insertVinyls.setString(14, "pop");
            insertVinyls.setDouble(15, 200);
            insertVinyls.setString(16, Currency.UAH.toString());
            insertVinyls.setString(17, "https://vinylsite.com/there/release1vinyl");
            insertVinyls.setString(18, "https://imagestore.com/there/image1vinyl.jpg");
            insertVinyls.setInt(19, 2);
            insertVinyls.setLong(20, 1);
            insertVinyls.setString(21, "release2");
            insertVinyls.setString(22, "artist2");
            insertVinyls.setString(23, "release2 - artist2");
            insertVinyls.setString(24, "rock");
            insertVinyls.setDouble(25, 1500);
            insertVinyls.setString(26, Currency.EUR.toString());
            insertVinyls.setString(27, "https://bestvinyl.com/here/release2");
            insertVinyls.setString(28, "https://imagestore.com/somewhere/image2.jpg");
            insertVinyls.setInt(29, 1);
            insertVinyls.setLong(30, 2);
            insertVinyls.executeUpdate();
        }
        vinylList = new ArrayList<>();

        Vinyl firstVinyl = new Vinyl();
        firstVinyl.setVinylId(1);
        firstVinyl.setRelease("release1");
        firstVinyl.setArtist("artist1");
        firstVinyl.setFullNameVinyl("release1 - artist1");
        firstVinyl.setGenre("pop");
        firstVinyl.setPrice(55.0);
        firstVinyl.setCurrency(Optional.of(Currency.UAH));
        firstVinyl.setVinylLink("https://vinylstore.com/somewhere/release1");
        firstVinyl.setImageLink("https://imagestore.com/somewhere/image1.jpg");
        firstVinyl.setShopId(1);
        firstVinyl.setUniqueVinylId(1);
        vinylList.add(firstVinyl);

        Vinyl secondVinyl = new Vinyl();
        secondVinyl.setVinylId(2);
        secondVinyl.setRelease("release2");
        secondVinyl.setArtist("artist2");
        secondVinyl.setFullNameVinyl("release2 - artist2");
        secondVinyl.setGenre("rock");
        secondVinyl.setPrice(1500.0);
        secondVinyl.setCurrency(Optional.of(Currency.EUR));
        secondVinyl.setVinylLink("https://bestvinyl.com/here/release2");
        secondVinyl.setImageLink("https://imagestore.com/somewhere/image2.jpg");
        secondVinyl.setShopId(1);
        secondVinyl.setUniqueVinylId(2);
        vinylList.add(secondVinyl);

    }

    @AfterEach
    void afterEach() throws SQLException {
        try (Statement truncateShops = connection.createStatement();
             Statement truncateUniqueVinyls = connection.createStatement();
             Statement truncateVinyls = connection.createStatement()) {
            truncateShops.executeUpdate(TRUNCATE_SHOPS);
            truncateUniqueVinyls.executeUpdate(TRUNCATE_UNIQUE_VINYLS);
            truncateVinyls.executeUpdate(TRUNCATE_VINYLS);
        }
    }

    @Test
    @DisplayName("Tests if unique vinyls were added accurately to the table public.unique_vinyls.")
    void addAllUniqueTest() throws SQLException {
        try (Statement truncateUniqueVinyls = connection.createStatement()) {
            truncateUniqueVinyls.executeUpdate(TRUNCATE_UNIQUE_VINYLS);
            vinylDao.addAllUnique(vinylList);

            List<Vinyl> vinyls = vinylDao.getAllUnique();

            assertEquals(1, vinyls.get(0).getVinylId());
            assertEquals("release1", vinyls.get(0).getRelease());
            assertEquals("artist1", vinyls.get(0).getArtist());
            assertEquals("release1 - artist1", vinyls.get(0).getFullNameVinyl());
            assertEquals("https://imagestore.com/somewhere/image1.jpg", vinyls.get(0).getImageLink());

            assertEquals(2, vinyls.get(1).getVinylId());
            assertEquals("release2", vinyls.get(1).getRelease());
            assertEquals("artist2", vinyls.get(1).getArtist());
            assertEquals("release2 - artist2", vinyls.get(1).getFullNameVinyl());
            assertEquals("https://imagestore.com/somewhere/image2.jpg", vinyls.get(1).getImageLink());
        }
    }

    @Test
    @DisplayName("Tests if adding multiple unique vinyls with same id causes RuntimeException().")
    void addAllUniqueWithDuplicatedUniqueIdTest() throws SQLException {
        try (Statement truncateUniqueVinyls = connection.createStatement()) {
            truncateUniqueVinyls.executeUpdate(TRUNCATE_UNIQUE_VINYLS);
            vinylList.get(1).setUniqueVinylId(1);

            assertThrows(RuntimeException.class, () -> {
                vinylDao.addAllUnique(vinylList);
            });
        }
    }

    @Test
    @DisplayName("Tests if adding unique vinyls where one vinyl is with null release causes RuntimeException().")
    void addAllUniqueWithNullReleaseTest() throws SQLException {
        try (Statement truncateUniqueVinyls = connection.createStatement()) {
            truncateUniqueVinyls.executeUpdate(TRUNCATE_UNIQUE_VINYLS);
            vinylList.get(0).setRelease(null);

            assertThrows(RuntimeException.class, () -> {
                vinylDao.addAllUnique(vinylList);
            });
        }
    }

    @Test
    @DisplayName("Tests if adding unique vinyls where one vinyl is with null artist causes RuntimeException().")
    void addAllUniqueWithNullArtistTest() throws SQLException {
        try (Statement truncateUniqueVinyls = connection.createStatement()) {
            truncateUniqueVinyls.executeUpdate(TRUNCATE_UNIQUE_VINYLS);
            vinylList.get(0).setArtist(null);

            assertThrows(RuntimeException.class, () -> {
                vinylDao.addAllUnique(vinylList);
            });
        }
    }

    @Test
    @DisplayName("Tests if adding unique vinyls where one vinyl is with null link to image causes RuntimeException().")
    void addAllUniqueWithNullLinkToImageTest() throws SQLException {
        try (Statement truncateUniqueVinyls = connection.createStatement()) {
            truncateUniqueVinyls.executeUpdate(TRUNCATE_UNIQUE_VINYLS);
            vinylList.get(0).setImageLink(null);

            assertThrows(RuntimeException.class, () -> {
                vinylDao.addAllUnique(vinylList);
            });
        }
    }

    @Test
    @DisplayName("Tests if (not unique) vinyls were added accurately to the table public.vinyls.")
    void addAllTest() throws SQLException {
        try (Statement truncateVinyls = connection.createStatement()) {
            truncateVinyls.executeUpdate(TRUNCATE_VINYLS);
            vinylDao.addAll(vinylList);

            List<Vinyl> vinyls = vinylDao.getAll();

            assertEquals(1, vinyls.get(0).getVinylId());
            assertEquals("release1", vinyls.get(0).getRelease());
            assertEquals("artist1", vinyls.get(0).getArtist());
            assertEquals("release1 - artist1", vinyls.get(0).getFullNameVinyl());
            assertEquals("pop", vinyls.get(0).getGenre());
            assertEquals(55, vinyls.get(0).getPrice());
            assertEquals(Currency.UAH, vinyls.get(0).getCurrency().get());
            assertEquals("https://vinylstore.com/somewhere/release1", vinyls.get(0).getVinylLink());
            assertEquals("https://imagestore.com/somewhere/image1.jpg", vinyls.get(0).getImageLink());
            assertEquals(1, vinyls.get(0).getShopId());
            assertEquals(1, vinyls.get(0).getUniqueVinylId());

            assertEquals(2, vinyls.get(1).getVinylId());
            assertEquals("release2", vinyls.get(1).getRelease());
            assertEquals("artist2", vinyls.get(1).getArtist());
            assertEquals("release2 - artist2", vinyls.get(1).getFullNameVinyl());
            assertEquals("rock", vinyls.get(1).getGenre());
            assertEquals(1500, vinyls.get(1).getPrice());
            assertEquals(Currency.EUR, vinyls.get(1).getCurrency().get());
            assertEquals("https://bestvinyl.com/here/release2", vinyls.get(1).getVinylLink());
            assertEquals("https://imagestore.com/somewhere/image2.jpg", vinyls.get(1).getImageLink());
            assertEquals(1, vinyls.get(1).getShopId());
            assertEquals(2, vinyls.get(1).getUniqueVinylId());
        }
    }

    @Test
    @DisplayName("Tests if adding (not unique) vinyls where one vinyl is with null release causes RuntimeException().")
    void addAllWithNullReleaseTest() throws SQLException {
        try (Statement truncateVinyls = connection.createStatement()) {
            truncateVinyls.executeUpdate(TRUNCATE_VINYLS);
            vinylList.get(0).setRelease(null);

            assertThrows(RuntimeException.class, () -> {
                vinylDao.addAll(vinylList);
            });
        }
    }

    @Test
    @DisplayName("Tests if adding (not unique) vinyls where one vinyl is with null artist causes RuntimeException().")
    void addVinylsWithNullArtistTest() throws SQLException {
        try (Statement truncateVinyls = connection.createStatement()) {
            truncateVinyls.executeUpdate(TRUNCATE_VINYLS);
            vinylList.get(0).setArtist(null);

            assertThrows(RuntimeException.class, () -> {
                vinylDao.addAll(vinylList);
            });
        }
    }

    @Test
    @DisplayName("Tests if adding (not unique) vinyls where one vinyl is with null artist causes RuntimeException().")
    void addAllWithEmptyCurrencyTest() throws SQLException {
        try (Statement truncateVinyls = connection.createStatement()) {
            truncateVinyls.executeUpdate(TRUNCATE_VINYLS);
            vinylList.get(0).setCurrency(Optional.ofNullable(null));

            assertThrows(RuntimeException.class, () -> {
                vinylDao.addAll(vinylList);
            });
        }
    }

    @Test
    @DisplayName("Tests if adding (not unique) vinyls where one vinyl is with null link to vinyl causes RuntimeException().")
    void addAllWithNullLinkToVinylTest() throws SQLException {
        try (Statement truncateVinyls = connection.createStatement()) {
            truncateVinyls.executeUpdate(TRUNCATE_VINYLS);
            vinylList.get(0).setVinylLink(null);

            assertThrows(RuntimeException.class, () -> {
                vinylDao.addAll(vinylList);
            });
        }
    }

    @Test
    @DisplayName("Tests if adding (not unique) vinyls where one vinyl is with null link to image causes RuntimeException().")
    void addAllWithNullLinkToImageTest() throws SQLException {
        try (Statement truncateVinyls = connection.createStatement()) {
            truncateVinyls.executeUpdate(TRUNCATE_VINYLS);
            vinylList.get(0).setImageLink(null);

            assertThrows(RuntimeException.class, () -> {
                vinylDao.addAll(vinylList);
            });
        }
    }

    @Test
    @DisplayName("Tests if adding (not unique) vinyls where one vinyl is with null shopID causes RuntimeException().")
    void addAllWithZeroShopIdTest() throws SQLException {
        try (Statement truncateVinyls = connection.createStatement()) {
            truncateVinyls.executeUpdate(TRUNCATE_VINYLS);
            vinylList.get(0).setShopId(0);

            assertThrows(RuntimeException.class, () -> {
                vinylDao.addAll(vinylList);
            });
        }
    }

    @Test
    @DisplayName("Tests if adding (not unique) vinyls where one vinyl is with null uniqueVinylId causes RuntimeException().")
    void addAllWithZeroUniqueVinylIdTest() throws SQLException {
        try (Statement truncateVinyls = connection.createStatement()) {
            truncateVinyls.executeUpdate(TRUNCATE_VINYLS);
            vinylList.get(0).setUniqueVinylId(0);

            assertThrows(RuntimeException.class, () -> {
                vinylDao.addAll(vinylList);
            });
        }
    }

    @Test
    @DisplayName("Gets all unique vinyls from db")
    void getAllUniqueTest() {
        List<Vinyl> uniqueVinyls = vinylDao.getAllUnique();

        assertEquals(1, uniqueVinyls.get(0).getVinylId());
        assertEquals("release1", uniqueVinyls.get(0).getRelease());
        assertEquals("artist1", uniqueVinyls.get(0).getArtist());
        assertEquals("release1 - artist1", uniqueVinyls.get(0).getFullNameVinyl());
        assertEquals("https://imagestore.com/somewhere/image1.jpg", uniqueVinyls.get(0).getImageLink());

        assertEquals(2, uniqueVinyls.get(1).getVinylId());
        assertEquals("release2", uniqueVinyls.get(1).getRelease());
        assertEquals("artist2", uniqueVinyls.get(1).getArtist());
        assertEquals("release2 - artist2", uniqueVinyls.get(1).getFullNameVinyl());
        assertEquals("https://imagestore.com/somewhere/image2.jpg", uniqueVinyls.get(1).getImageLink());
    }

    @Test
    @DisplayName("Tests that a list of all unique vinyls from empty table is empty.")
    void getAllUniqueFromEmptyTableTest() throws SQLException {
        try (Statement truncateUniqueVinyls = connection.createStatement()) {
            truncateUniqueVinyls.executeUpdate(TRUNCATE_UNIQUE_VINYLS);
        }

        List<Vinyl> uniqueVinyls = vinylDao.getAllUnique();

        assertEquals(0, uniqueVinyls.size());
    }

    @Test
    @DisplayName("Gets random unique vinyls from db")
    void getManyRandomUniqueTest() throws SQLException {
        try (PreparedStatement insertUniqueVinyls = connection.prepareStatement(INSERT_UNIQUE_VINYLS)) {
            insertUniqueVinyls.setLong(1, 3);
            insertUniqueVinyls.setString(2, "release3");
            insertUniqueVinyls.setString(3, "artist3");
            insertUniqueVinyls.setString(4, "release3 - artist3");
            insertUniqueVinyls.setString(5, "https://imagestore.com/somewhere/image3.jpg");
            insertUniqueVinyls.setLong(6, 4);
            insertUniqueVinyls.setString(7, "release4");
            insertUniqueVinyls.setString(8, "artist4");
            insertUniqueVinyls.setString(9, "release4 - artist4");
            insertUniqueVinyls.setString(10, "https://imagestore.com/somewhere/image4.jpg");
            insertUniqueVinyls.executeUpdate();
        }

        List<Vinyl> uniqueVinyls = vinylDao.getManyRandomUnique(2);

        assertEquals(2, uniqueVinyls.size());
        assertNotEquals(uniqueVinyls.get(1).getVinylId(), uniqueVinyls.get(0).getVinylId());
        assertNotEquals(uniqueVinyls.get(1).getRelease(), uniqueVinyls.get(0).getRelease());
        assertNotEquals(uniqueVinyls.get(1).getArtist(), uniqueVinyls.get(0).getArtist());
        assertNotEquals(uniqueVinyls.get(1).getFullNameVinyl(), uniqueVinyls.get(0).getFullNameVinyl());
        assertNotEquals(uniqueVinyls.get(1).getImageLink(), uniqueVinyls.get(0).getImageLink());
    }

    @Test
    @DisplayName("Gets random unique vinyls from db by amount 0")
    void getManyRandomUniqueWithAmountZeroTest() {
        List<Vinyl> uniqueVinyls = vinylDao.getManyRandomUnique(0);

        assertTrue(uniqueVinyls.isEmpty());
    }

    @Test
    @DisplayName("Gets random unique vinyls from db by amount 1")
    void getManyRandomUniqueWithAmountOneTest() {
        List<Vinyl> uniqueVinyls = vinylDao.getManyRandomUnique(1);

        assertEquals(1, uniqueVinyls.size());
    }

    @Test
    @DisplayName("Gets random unique vinyls from db by amount larger than amount of rows in the table")
    void getManyRandomUniqueWithAmountBiggerThanCountTest() {
        List<Vinyl> uniqueVinyls = vinylDao.getManyRandomUnique(10);

        assertEquals(2, uniqueVinyls.size());
        assertNotEquals(uniqueVinyls.get(1).getVinylId(), uniqueVinyls.get(0).getVinylId());
    }

    @Test
    @DisplayName("Check that getting negative amount of random unique vinyls from db throws RuntimeException.")
    void getManyRandomUniqueWithAmountBelowZeroTest() {
        assertThrows(RuntimeException.class, () -> {
            List<Vinyl> uniqueVinyls = vinylDao.getManyRandomUnique(-2);
        });
    }

    @Test
    @DisplayName("Tests that a list of random unique vinyls from empty table is empty.")
    void getManyRandomUniqueFromEmptyTableTest() throws SQLException {
        try (Statement truncateUniqueVinyls = connection.createStatement()) {
            truncateUniqueVinyls.executeUpdate(TRUNCATE_UNIQUE_VINYLS);
        }

        List<Vinyl> uniqueVinyls = vinylDao.getManyRandomUnique(1);

        assertEquals(0, uniqueVinyls.size());
    }

    @Test
    @DisplayName("Gets random unique vinyls from db by amount 0")
    void getManyFilteredUniqueWithMatcherThatMatchesAllTest() {
        List<Vinyl> uniqueVinyls = vinylDao.getManyFilteredUnique("release");

        assertEquals(2, uniqueVinyls.size());
        assertNotEquals(uniqueVinyls.get(1), uniqueVinyls.get(0));
    }

    @Test
    @DisplayName("Gets random unique vinyls from db by amount 0")
    void getManyFilteredUniqueWithMatcherThatMatchesOneTest() {
        List<Vinyl> uniqueVinyls = vinylDao.getManyFilteredUnique("release2");

        assertEquals(1, uniqueVinyls.size());
        assertEquals(2, uniqueVinyls.get(0).getVinylId());
        assertEquals("release2", uniqueVinyls.get(0).getRelease());
        assertEquals("artist2", uniqueVinyls.get(0).getArtist());
        assertEquals("release2 - artist2", uniqueVinyls.get(0).getFullNameVinyl());
        assertEquals("https://imagestore.com/somewhere/image2.jpg", uniqueVinyls.get(0).getImageLink());
    }

    @Test
    @DisplayName("Gets random unique vinyls from db by amount 1")
    void getManyFilteredUniqueWithMatcherThatMatchesZeroTest() {
        List<Vinyl> uniqueVinyls = vinylDao.getManyFilteredUnique("release3");

        assertTrue(uniqueVinyls.isEmpty());
    }

    @Test
    @DisplayName("Tests that a list of filtered unique vinyls from empty table is empty.")
    void getManyFilteredUniqueFromEmptyTableTest() throws SQLException {
        try (Statement truncateUniqueVinyls = connection.createStatement()) {
            truncateUniqueVinyls.executeUpdate(TRUNCATE_UNIQUE_VINYLS);
        }

        List<Vinyl> uniqueVinyls = vinylDao.getManyFilteredUnique("release");

        assertEquals(0, uniqueVinyls.size());
    }

    @Test
    @DisplayName("Gets list of unique vinyls from db by artist")
    void getManyUniqueByArtistTest() throws SQLException {
        try (PreparedStatement insertUniqueVinyls = connection.prepareStatement(INSERT_UNIQUE_VINYLS);
             PreparedStatement insertVinyls = connection.prepareStatement(INSERT_VINYLS)) {
            insertUniqueVinyls.setLong(1, 3);
            insertUniqueVinyls.setString(2, "release3");
            insertUniqueVinyls.setString(3, "artist1");
            insertUniqueVinyls.setString(4, "release3 - artist1");
            insertUniqueVinyls.setString(5, "https://imagestore.com/somewhere/image3.jpg");
            insertUniqueVinyls.setLong(6, 4);
            insertUniqueVinyls.setString(7, "release4");
            insertUniqueVinyls.setString(8, "artist2");
            insertUniqueVinyls.setString(9, "release4 - artist2");
            insertUniqueVinyls.setString(10, "https://imagestore.com/somewhere/image4.jpg");
            insertUniqueVinyls.executeUpdate();
        }
        String artist = "artist1";

        List<Vinyl> uniqueVinyls = vinylDao.getManyUniqueByArtist(artist);

        assertEquals(2, uniqueVinyls.size());
        assertEquals(uniqueVinyls.get(0).getArtist(), uniqueVinyls.get(1).getArtist());
        assertNotEquals(uniqueVinyls.get(0).getVinylId(), uniqueVinyls.get(1).getVinylId());
    }

    @Test
    @DisplayName("Gets empty list of unique vinyls from db by nonexistent artist")
    void getManyUniqueByArtistWithNonExistentArtistTest() {
        List<Vinyl> uniqueVinyls = vinylDao.getManyUniqueByArtist("artist3");

        assertTrue(uniqueVinyls.isEmpty());
    }

    @Test
    @DisplayName("Gets empty list of unique vinyls from empty table")
    void getManyUniqueByArtistFromEmptyTable() throws SQLException {
        try (Statement truncateVinyls = connection.createStatement()) {
            truncateVinyls.executeUpdate(TRUNCATE_UNIQUE_VINYLS);
        }

        List<Vinyl> uniqueVinyls = vinylDao.getManyUniqueByArtist("artist");

        assertTrue(uniqueVinyls.isEmpty());
    }

    @Test
    @DisplayName("Gets all vinyls from db")
    void getAllTest() {
        List<Vinyl> allVinyls = vinylDao.getAll();

        assertEquals(1, allVinyls.get(0).getVinylId());
        assertEquals("release1", allVinyls.get(0).getRelease());
        assertEquals("artist1", allVinyls.get(0).getArtist());
        assertEquals("release1 - artist1", allVinyls.get(0).getFullNameVinyl());
        assertEquals("pop", allVinyls.get(0).getGenre());
        assertEquals(55, allVinyls.get(0).getPrice());
        assertEquals(Currency.UAH, allVinyls.get(0).getCurrency().get());
        assertEquals("https://vinylstore.com/somewhere/release1", allVinyls.get(0).getVinylLink());
        assertEquals("https://imagestore.com/somewhere/image1.jpg", allVinyls.get(0).getImageLink());
        assertEquals(1, allVinyls.get(0).getShopId());
        assertEquals(1, allVinyls.get(0).getUniqueVinylId());

        assertEquals(2, allVinyls.get(1).getVinylId());
        assertEquals("release1 (Vinyl)", allVinyls.get(1).getRelease());
        assertEquals("artist1", allVinyls.get(1).getArtist());
        assertEquals("release1 (Vinyl) - artist1", allVinyls.get(1).getFullNameVinyl());
        assertEquals("pop", allVinyls.get(1).getGenre());
        assertEquals(200, allVinyls.get(1).getPrice());
        assertEquals(Currency.UAH, allVinyls.get(1).getCurrency().get());
        assertEquals("https://vinylsite.com/there/release1vinyl", allVinyls.get(1).getVinylLink());
        assertEquals("https://imagestore.com/there/image1vinyl.jpg", allVinyls.get(1).getImageLink());
        assertEquals(2, allVinyls.get(1).getShopId());
        assertEquals(1, allVinyls.get(1).getUniqueVinylId());

        assertEquals(3, allVinyls.get(2).getVinylId());
        assertEquals("release2", allVinyls.get(2).getRelease());
        assertEquals("artist2", allVinyls.get(2).getArtist());
        assertEquals("release2 - artist2", allVinyls.get(2).getFullNameVinyl());
        assertEquals("rock", allVinyls.get(2).getGenre());
        assertEquals(1500, allVinyls.get(2).getPrice());
        assertEquals(Currency.EUR, allVinyls.get(2).getCurrency().get());
        assertEquals("https://bestvinyl.com/here/release2", allVinyls.get(2).getVinylLink());
        assertEquals("https://imagestore.com/somewhere/image2.jpg", allVinyls.get(2).getImageLink());
        assertEquals(1, allVinyls.get(2).getShopId());
        assertEquals(2, allVinyls.get(2).getUniqueVinylId());
    }

    @Test
    @DisplayName("Tests that a list of all vinyls from empty table is empty.")
    void getAllFromEmptyVinylsTableTest() throws SQLException {
        try (Statement truncateVinyls = connection.createStatement()) {
            truncateVinyls.executeUpdate(TRUNCATE_VINYLS);
        }

        List<Vinyl> allVinyls = vinylDao.getAll();

        assertEquals(0, allVinyls.size());
    }

    @Test
    @DisplayName("Gets a list of vinyls from db by uniqueVinylId")
    void getManyByUniqueVinylIdTest() {
        long uniqueVinylId = 1;
        List<Vinyl> vinyls = vinylDao.getManyByUniqueVinylId(uniqueVinylId);

        assertEquals(2, vinyls.size());
        assertNotEquals(vinyls.get(0).getVinylId(), vinyls.get(1).getVinylId());
        assertEquals(vinyls.get(0).getUniqueVinylId(), vinyls.get(1).getUniqueVinylId());
        assertEquals(vinyls.get(0).getUniqueVinylId(), uniqueVinylId);
    }

    @Test
    @DisplayName("Gets empty list of vinyls by nonexistent uniqueVinylId from db")
    void getManyByUniqueVinylIdWithNonExistentUniqueVinylIdTest() {
        long uniqueVinylId = 3;
        List<Vinyl> vinyls = vinylDao.getManyByUniqueVinylId(uniqueVinylId);

        assertTrue(vinyls.isEmpty());
    }

    @Test
    @DisplayName("Gets empty list of vinyls by uniqueVinylId from empty table")
    void getManyByUniqueVinylIdFromEmptyTableTest() throws SQLException {
        try (Statement truncateUniqueVinyls = connection.createStatement()) {
            truncateUniqueVinyls.executeUpdate(TRUNCATE_VINYLS);
        }
        long uniqueVinylId = 1;
        List<Vinyl> vinyls = vinylDao.getManyByUniqueVinylId(uniqueVinylId);

        assertTrue(vinyls.isEmpty());
    }

    @Test
    @DisplayName("Gets unique vinyl from db by id.")
    void getUniqueByIdTest() {
        Vinyl uniqueVinyl = vinylDao.getUniqueById(2);

        assertEquals(2, uniqueVinyl.getVinylId());
        assertEquals("release2", uniqueVinyl.getRelease());
        assertEquals("artist2", uniqueVinyl.getArtist());
        assertEquals("release2 - artist2", uniqueVinyl.getFullNameVinyl());
        assertEquals("https://imagestore.com/somewhere/image2.jpg", uniqueVinyl.getImageLink());
    }

    @Test
    @DisplayName("Tests that getting unique vinyl from db by non-existent id throws a RuntimeException.")
    void getUniqueByIdWithNonExistentIdTest() {
        assertThrows(RuntimeException.class, () -> {
            vinylDao.getUniqueById(15);
        });
    }

    @Test
    @DisplayName("Gets vinyl from db by id.")
    void getByIdTest() {
        Vinyl vinyl = vinylDao.getById(2);

        assertEquals(2, vinyl.getVinylId());
        assertEquals("release1 (Vinyl)", vinyl.getRelease());
        assertEquals("artist1", vinyl.getArtist());
        assertEquals("release1 (Vinyl) - artist1", vinyl.getFullNameVinyl());
        assertEquals("pop", vinyl.getGenre());
        assertEquals(200, vinyl.getPrice());
        assertEquals(Currency.UAH, vinyl.getCurrency().get());
        assertEquals("https://vinylsite.com/there/release1vinyl", vinyl.getVinylLink());
        assertEquals("https://imagestore.com/there/image1vinyl.jpg", vinyl.getImageLink());
        assertEquals(2, vinyl.getShopId());
        assertEquals(1, vinyl.getUniqueVinylId());
    }

    @Test
    @DisplayName("Tests that getting vinyl from db by non-existent id throws a RuntimeException.")
    void getByIdWithNonExistentIdTest() {
        assertThrows(RuntimeException.class, () -> {
            vinylDao.getById(15);
        });
    }

}