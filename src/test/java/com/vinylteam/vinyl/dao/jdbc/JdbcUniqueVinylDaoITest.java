package com.vinylteam.vinyl.dao.jdbc;

import com.vinylteam.vinyl.dao.DBDataSource;
import com.vinylteam.vinyl.dao.UniqueVinylDao;
import com.vinylteam.vinyl.entity.Offer;
import com.vinylteam.vinyl.entity.Shop;
import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.util.DatabasePreparerForITests;
import com.vinylteam.vinyl.util.ListPreparerForTests;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JdbcUniqueVinylDaoITest {

    private Connection connection;
    private final UniqueVinylDao uniqueVinylDao = new JdbcUniqueVinylDao();
    private final DatabasePreparerForITests databasePreparer = new DatabasePreparerForITests(connection);
    private final List<Shop> shops = new ArrayList<>();
    private final List<UniqueVinyl> uniqueVinyls = new ArrayList<>();
    private final List<Offer> offers = new ArrayList<>();
    private final ListPreparerForTests listPreparer = new ListPreparerForTests();

    @BeforeAll
    void beforeAll() throws SQLException {
        connection = DBDataSource.getConnection();
        listPreparer.fillShopsList(shops);
        listPreparer.fillUniqueVinylsList(uniqueVinyls);
        listPreparer.fillOffersList(offers);
        databasePreparer.truncateAllVinylTables();
    }

    @AfterAll
    void afterAll() throws SQLException {
        databasePreparer.truncateAllVinylTables();
        connection.close();
    }

    @BeforeEach
    void beforeEach() throws SQLException {
        databasePreparer.insertShops(shops);
        databasePreparer.insertUniqueVinyls(uniqueVinyls);
        databasePreparer.insertOffers(offers);
    }

    @AfterEach
    void afterEach() throws SQLException {
        databasePreparer.truncateAllVinylTables();
    }

    @Test
    @DisplayName("Returns true after adding elements of valid filled list")
    void addAllTest() throws SQLException {
        //prepare
        databasePreparer.truncateCascadeUniqueVinyls();
        //when
        boolean actualIsAdded = uniqueVinylDao.addAll(uniqueVinyls);
        //then
        assertTrue(actualIsAdded);
    }

    @Test
    @DisplayName("Returns false when empty list is passed")
    void addAllEmptyListTest() {
        //when
        boolean actualIsAdded = uniqueVinylDao.addAll(new ArrayList<>());
        //then
        assertFalse(actualIsAdded);
    }

    @Test
    @DisplayName("Throws RuntimeException when null is passed")
    void addAllNullListTest() {
        //when
        assertThrows(RuntimeException.class, () -> uniqueVinylDao.addAll(null));
    }

    @Test
    @DisplayName("Returns false when one of elements of passed list has id that exists in db")
    void addAllExistingIdListTest() {
        //prepare
        List<UniqueVinyl> existingIdUniqueVinyls = new ArrayList<>(uniqueVinyls);
        existingIdUniqueVinyls.get(1).setId(4);
        existingIdUniqueVinyls.get(2).setId(5);
        //when
        boolean actualIsAdded = uniqueVinylDao.addAll(existingIdUniqueVinyls);
        //then
        assertFalse(actualIsAdded);
    }

    @Test
    @DisplayName("Returns false when two elements of passed list have same id that doesn't exist in db")
    void addAllDuplicateNewIdListTest() throws SQLException {
        //prepare
        databasePreparer.truncateCascadeUniqueVinyls();
        List<UniqueVinyl> duplicateIdsUniqueVinyls = new ArrayList<>(uniqueVinyls);
        duplicateIdsUniqueVinyls.get(1).setId(1);
        //when
        boolean actualIsAdded = uniqueVinylDao.addAll(duplicateIdsUniqueVinyls);
        //then
        assertFalse(actualIsAdded);
    }

    @Test
    @DisplayName("Returns false when one of elements of passed list has null release")
    void addAllNullReleaseListTest() throws SQLException {
        //prepare
        databasePreparer.truncateCascadeUniqueVinyls();
        List<UniqueVinyl> nullReleaseUniqueVinyls = new ArrayList<>(uniqueVinyls);
        nullReleaseUniqueVinyls.get(0).setRelease(null);
        //when
        boolean actualIsAdded = uniqueVinylDao.addAll(nullReleaseUniqueVinyls);
        //then
        assertFalse(actualIsAdded);
    }

    @Test
    @DisplayName("Returns false when one of elements of passed list has null artist")
    void addAllNullArtistListTest() throws SQLException {
        //prepare
        databasePreparer.truncateCascadeUniqueVinyls();
        List<UniqueVinyl> nullArtistUniqueVinyls = new ArrayList<>(uniqueVinyls);
        nullArtistUniqueVinyls.get(0).setArtist(null);
        //when
        boolean actualIsAdded = uniqueVinylDao.addAll(nullArtistUniqueVinyls);
        //then
        assertFalse(actualIsAdded);
    }

    @Test
    @DisplayName("Returns false when one of elements of passed list has null fullName")
    void addAllNullFullNameListTest() throws SQLException {
        //prepare
        databasePreparer.truncateCascadeUniqueVinyls();
        List<UniqueVinyl> nullFullNameUniqueVinyls = new ArrayList<>(uniqueVinyls);
        nullFullNameUniqueVinyls.get(0).setFullName(null);
        //when
        boolean actualIsAdded = uniqueVinylDao.addAll(nullFullNameUniqueVinyls);
        //then
        assertFalse(actualIsAdded);
    }

    @Test
    @DisplayName("Returns false when one of elements of passed list has null imageLink")
    void addAllNullImageLinkListTest() throws SQLException {
        //prepare
        databasePreparer.truncateCascadeUniqueVinyls();
        List<UniqueVinyl> nullImageLinkUniqueVinyls = new ArrayList<>(uniqueVinyls);
        nullImageLinkUniqueVinyls.get(0).setImageLink(null);
        //when
        boolean actualIsAdded = uniqueVinylDao.addAll(nullImageLinkUniqueVinyls);
        //then
        assertFalse(actualIsAdded);
    }

    @Test
    @DisplayName("Returns filled list with all unique vinyls from table that isn't empty")
    void findAllTest() {
        //when
        List<UniqueVinyl> actualUniqueVinyls = uniqueVinylDao.findAll();
        //then
        assertEquals(uniqueVinyls, actualUniqueVinyls);
    }

    @Test
    @DisplayName("Returns empty list from empty table")
    void findAllEmptyTableTest() throws SQLException {
        //prepare
        databasePreparer.truncateCascadeUniqueVinyls();
        //when
        List<UniqueVinyl> actualUniqueVinyls = uniqueVinylDao.findAll();
        //then
        assertTrue(actualUniqueVinyls.isEmpty());
    }

    @Test
    @DisplayName("Returns uniqueVinyl by id match from table")
    void findByIdTest() {
        //when
        UniqueVinyl actualUniqueVinyl = uniqueVinylDao.findById(1);
        //then
        assertEquals(uniqueVinyls.get(0), actualUniqueVinyl);
    }

    @Test
    @DisplayName("Throws RuntimeException when id has no matches")
    void findByNoMatchIdTest() {
        //when
        assertThrows(RuntimeException.class, () -> uniqueVinylDao.findById(4));
    }

    @Test
    @DisplayName("Throws RuntimeException when finding by id in empty table")
    void findByIdEmptyTableTest() throws SQLException {
        //prepare
        databasePreparer.truncateCascadeUniqueVinyls();
        //when
        assertThrows(RuntimeException.class, () -> uniqueVinylDao.findById(3));
    }

    @Test
    @DisplayName("Returns filled list with exact amount of different unique vinyls selected randomly when requested amount is less than amount of rows in table")
    void findManyRandomTest() {
        //when
        List<UniqueVinyl> actualUniqueVinyls = uniqueVinylDao.findManyRandom(2);
        //then
        assertEquals(2, actualUniqueVinyls.size());
        assertNotEquals(actualUniqueVinyls.get(0), actualUniqueVinyls.get(1));
    }

    @Test
    @DisplayName("Returns filled list with all unique vinyls from table selected randomly when requested amount is equal or bigger than amount of rows in table")
    void findManyRandomAmountBiggerThanTableSizeTest() {
        //when
        List<UniqueVinyl> actualUniqueVinyls = uniqueVinylDao.findManyRandom(4);
        //then
        assertEquals(3, actualUniqueVinyls.size());
        assertTrue(uniqueVinyls.containsAll(actualUniqueVinyls));
        assertNotEquals(actualUniqueVinyls.get(0), actualUniqueVinyls.get(1));
        assertNotEquals(actualUniqueVinyls.get(1), actualUniqueVinyls.get(2));
        assertNotEquals(actualUniqueVinyls.get(2), actualUniqueVinyls.get(0));
    }

    @Test
    @DisplayName("Returns empty list when requested valid amount from empty table")
    void findManyRandomEmptyTableTest() throws SQLException {
        //prepare
        databasePreparer.truncateCascadeUniqueVinyls();
        //when
        List<UniqueVinyl> actualUniqueVinyls = uniqueVinylDao.findManyRandom(3);
        //then
        assertTrue(actualUniqueVinyls.isEmpty());
    }

    @Test
    @DisplayName("Returns empty list when requested amount 0 from filled table")
    void findManyRandomAmountZeroTest() {
        //when
        List<UniqueVinyl> actualUniqueVinyls = uniqueVinylDao.findManyRandom(0);
        //then
        assertTrue(actualUniqueVinyls.isEmpty());
    }

    @Test
    @DisplayName("Throws RuntimeException when amount is less then 0")
    void findManyRandomNegativeAmountTest() {
        //when
        assertThrows(RuntimeException.class, () -> uniqueVinylDao.findManyRandom(-3));
    }

    @Test
    @DisplayName("Returns filled list by full name substring matcher that has matches in table")
    void findManyFilteredTest() {
        //prepare
        List<UniqueVinyl> expectedUniqueVinyls = new ArrayList<>(List.of(uniqueVinyls.get(0)));
        //when
        List<UniqueVinyl> actualUniqueVinyls = uniqueVinylDao.findManyFiltered("1");
        //then
        assertEquals(expectedUniqueVinyls, actualUniqueVinyls);
    }

    @Test
    @DisplayName("Returns empty list by full name substring matcher that has no matches in table")
    void findManyFilteredZeroMatchesTest() {
        //when
        List<UniqueVinyl> actualUniqueVinyls = uniqueVinylDao.findManyFiltered("4");
        //then
        assertTrue(actualUniqueVinyls.isEmpty());
    }

    @Test
    @DisplayName("Returns filled list of all rows when finding by full name substring matcher that is empty string")
    void findManyFilteredEmptyMatcherTest() {
        //when
        List<UniqueVinyl> actualUniqueVinyls = uniqueVinylDao.findManyFiltered("");
        //then
        assertEquals(uniqueVinyls, actualUniqueVinyls);
    }

    @Test
    @DisplayName("Throws RuntimeException when matcher is null")
    void findManyFilteredNullMatcherTest() {
        //when
        assertThrows(RuntimeException.class, () -> uniqueVinylDao.findManyFiltered(null));
    }

    @Test
    @DisplayName("Throws RuntimeException when matcher is null and table is empty")
    void findManyFilteredNullMatcherEmptyTableTest() throws SQLException {
        //prepare
        databasePreparer.truncateCascadeUniqueVinyls();
        //when
        assertThrows(RuntimeException.class, () -> uniqueVinylDao.findManyFiltered(null));
    }

    @Test
    @DisplayName("Returns empty list when finding by matcher in empty table")
    void findManyFilteredEmptyTableTest() throws SQLException {
        //prepare
        databasePreparer.truncateCascadeUniqueVinyls();
        //when
        List<UniqueVinyl> actualUniqueVinyls = uniqueVinylDao.findManyFiltered("1");
        //then
        assertTrue(actualUniqueVinyls.isEmpty());
    }

    @Test
    @DisplayName("Returns filled list by artist that has matches in the table")
    void findManyByArtistTest() {
        //prepare
        List<UniqueVinyl> expectedUniqueVinyls = new ArrayList<>(List.of(uniqueVinyls.get(0)));
        //when
        List<UniqueVinyl> actualUniqueVinyls = uniqueVinylDao.findManyByArtist("artist1");
        //then
        assertEquals(expectedUniqueVinyls, actualUniqueVinyls);
    }

    @Test
    @DisplayName("Returns empty list by artist that has no matches in the table")
    void findManyByArtistNoMatchesTest() {
        //when
        List<UniqueVinyl> actualUniqueVinyls = uniqueVinylDao.findManyByArtist("artist4");
        //then
        assertTrue(actualUniqueVinyls.isEmpty());
    }

    @Test
    @DisplayName("Throws RuntimeException when passed artist is null")
    void findManyByNullArtistTest() {
        //when
        assertThrows(RuntimeException.class, () -> uniqueVinylDao.findManyByArtist(null));
    }

    @Test
    @DisplayName("Throws RuntimeException when passed artist is null and table is empty")
    void findManyByNullArtistEmptyTableTest() throws SQLException {
        //prepare
        databasePreparer.truncateCascadeUniqueVinyls();
        //when
        assertThrows(RuntimeException.class, () -> uniqueVinylDao.findManyByArtist(null));
    }

    @Test
    @DisplayName("Returns empty list when table is empty")
    void findManyByArtistEmptyTableTest() throws SQLException {
        //prepare
        databasePreparer.truncateCascadeUniqueVinyls();
        //when
        List<UniqueVinyl> actualUniqueVinyls = uniqueVinylDao.findManyByArtist("artist1");
        //then
        assertTrue(actualUniqueVinyls.isEmpty());
    }

   /* @Test
    @DisplayName("Tests if unique vinyls were added accurately to the table public.unique_vinyls.")
    void addAllUniqueTest() throws SQLException {
        try (Statement truncateUniqueVinyls = connection.createStatement()) {
            truncateUniqueVinyls.executeUpdate(TRUNCATE_UNIQUE_VINYLS);
            uniqueVinylDao.addAllUnique(vinylList);

            List<Vinyl> vinyls = uniqueVinylDao.getAllUnique();

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
                uniqueVinylDao.addAllUnique(vinylList);
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
                uniqueVinylDao.addAllUnique(vinylList);
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
                uniqueVinylDao.addAllUnique(vinylList);
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
                uniqueVinylDao.addAllUnique(vinylList);
            });
        }
    }

    @Test
    @DisplayName("Tests if (not unique) vinyls were added accurately to the table public.vinyls.")
    void addAllTest() throws SQLException {
        try (Statement truncateVinyls = connection.createStatement()) {
            truncateVinyls.executeUpdate(TRUNCATE_VINYLS);
            uniqueVinylDao.addAll(vinylList);

            List<Vinyl> vinyls = uniqueVinylDao.getAll();

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
                uniqueVinylDao.addAll(vinylList);
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
                uniqueVinylDao.addAll(vinylList);
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
                uniqueVinylDao.addAll(vinylList);
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
                uniqueVinylDao.addAll(vinylList);
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
                uniqueVinylDao.addAll(vinylList);
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
                uniqueVinylDao.addAll(vinylList);
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
                uniqueVinylDao.addAll(vinylList);
            });
        }
    }

    @Test
    @DisplayName("Gets all unique vinyls from db")
    void getAllUniqueTest() {
        List<Vinyl> uniqueVinyls = uniqueVinylDao.getAllUnique();

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

        List<Vinyl> uniqueVinyls = uniqueVinylDao.getAllUnique();

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

        List<Vinyl> uniqueVinyls = uniqueVinylDao.getManyRandomUnique(2);

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
        List<Vinyl> uniqueVinyls = uniqueVinylDao.getManyRandomUnique(0);

        assertTrue(uniqueVinyls.isEmpty());
    }

    @Test
    @DisplayName("Gets random unique vinyls from db by amount 1")
    void getManyRandomUniqueWithAmountOneTest() {
        List<Vinyl> uniqueVinyls = uniqueVinylDao.getManyRandomUnique(1);

        assertEquals(1, uniqueVinyls.size());
    }

    @Test
    @DisplayName("Gets random unique vinyls from db by amount larger than amount of rows in the table")
    void getManyRandomUniqueWithAmountBiggerThanCountTest() {
        List<Vinyl> uniqueVinyls = uniqueVinylDao.getManyRandomUnique(10);

        assertEquals(2, uniqueVinyls.size());
        assertNotEquals(uniqueVinyls.get(1).getVinylId(), uniqueVinyls.get(0).getVinylId());
    }

    @Test
    @DisplayName("Check that getting negative amount of random unique vinyls from db throws RuntimeException.")
    void getManyRandomUniqueWithAmountBelowZeroTest() {
        assertThrows(RuntimeException.class, () -> {
            List<Vinyl> uniqueVinyls = uniqueVinylDao.getManyRandomUnique(-2);
        });
    }

    @Test
    @DisplayName("Tests that a list of random unique vinyls from empty table is empty.")
    void getManyRandomUniqueFromEmptyTableTest() throws SQLException {
        try (Statement truncateUniqueVinyls = connection.createStatement()) {
            truncateUniqueVinyls.executeUpdate(TRUNCATE_UNIQUE_VINYLS);
        }

        List<Vinyl> uniqueVinyls = uniqueVinylDao.getManyRandomUnique(1);

        assertEquals(0, uniqueVinyls.size());
    }

    @Test
    @DisplayName("Gets random unique vinyls from db by amount 0")
    void getManyFilteredUniqueWithMatcherThatMatchesAllTest() {
        List<Vinyl> uniqueVinyls = uniqueVinylDao.getManyFilteredUnique("release");

        assertEquals(2, uniqueVinyls.size());
        assertNotEquals(uniqueVinyls.get(1), uniqueVinyls.get(0));
    }

    @Test
    @DisplayName("Gets random unique vinyls from db by amount 0")
    void getManyFilteredUniqueWithMatcherThatMatchesOneTest() {
        List<Vinyl> uniqueVinyls = uniqueVinylDao.getManyFilteredUnique("release2");

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
        List<Vinyl> uniqueVinyls = uniqueVinylDao.getManyFilteredUnique("release3");

        assertTrue(uniqueVinyls.isEmpty());
    }

    @Test
    @DisplayName("Tests that a list of filtered unique vinyls from empty table is empty.")
    void getManyFilteredUniqueFromEmptyTableTest() throws SQLException {
        try (Statement truncateUniqueVinyls = connection.createStatement()) {
            truncateUniqueVinyls.executeUpdate(TRUNCATE_UNIQUE_VINYLS);
        }

        List<Vinyl> uniqueVinyls = uniqueVinylDao.getManyFilteredUnique("release");

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

        List<Vinyl> uniqueVinyls = uniqueVinylDao.getManyUniqueByArtist(artist);

        assertEquals(2, uniqueVinyls.size());
        assertEquals(uniqueVinyls.get(0).getArtist(), uniqueVinyls.get(1).getArtist());
        assertNotEquals(uniqueVinyls.get(0).getVinylId(), uniqueVinyls.get(1).getVinylId());
    }

    @Test
    @DisplayName("Gets empty list of unique vinyls from db by nonexistent artist")
    void getManyUniqueByArtistWithNonExistentArtistTest() {
        List<Vinyl> uniqueVinyls = uniqueVinylDao.getManyUniqueByArtist("artist3");

        assertTrue(uniqueVinyls.isEmpty());
    }

    @Test
    @DisplayName("Gets empty list of unique vinyls from empty table")
    void getManyUniqueByArtistFromEmptyTable() throws SQLException {
        try (Statement truncateVinyls = connection.createStatement()) {
            truncateVinyls.executeUpdate(TRUNCATE_UNIQUE_VINYLS);
        }

        List<Vinyl> uniqueVinyls = uniqueVinylDao.getManyUniqueByArtist("artist");

        assertTrue(uniqueVinyls.isEmpty());
    }

    @Test
    @DisplayName("Gets all vinyls from db")
    void getAllTest() {
        List<Vinyl> allVinyls = uniqueVinylDao.getAll();

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

        List<Vinyl> allVinyls = uniqueVinylDao.getAll();

        assertEquals(0, allVinyls.size());
    }

    @Test
    @DisplayName("Gets a list of vinyls from db by uniqueVinylId")
    void getManyByUniqueVinylIdTest() {
        long uniqueVinylId = 1;
        List<Vinyl> vinyls = uniqueVinylDao.getManyByUniqueVinylId(uniqueVinylId);

        assertEquals(2, vinyls.size());
        assertNotEquals(vinyls.get(0).getVinylId(), vinyls.get(1).getVinylId());
        assertEquals(vinyls.get(0).getUniqueVinylId(), vinyls.get(1).getUniqueVinylId());
        assertEquals(vinyls.get(0).getUniqueVinylId(), uniqueVinylId);
    }

    @Test
    @DisplayName("Gets empty list of vinyls by nonexistent uniqueVinylId from db")
    void getManyByUniqueVinylIdWithNonExistentUniqueVinylIdTest() {
        long uniqueVinylId = 3;
        List<Vinyl> vinyls = uniqueVinylDao.getManyByUniqueVinylId(uniqueVinylId);

        assertTrue(vinyls.isEmpty());
    }

    @Test
    @DisplayName("Gets empty list of vinyls by uniqueVinylId from empty table")
    void getManyByUniqueVinylIdFromEmptyTableTest() throws SQLException {
        try (Statement truncateUniqueVinyls = connection.createStatement()) {
            truncateUniqueVinyls.executeUpdate(TRUNCATE_VINYLS);
        }
        long uniqueVinylId = 1;
        List<Vinyl> vinyls = uniqueVinylDao.getManyByUniqueVinylId(uniqueVinylId);

        assertTrue(vinyls.isEmpty());
    }

    @Test
    @DisplayName("Gets unique vinyl from db by id.")
    void getUniqueByIdTest() {
        Vinyl uniqueVinyl = uniqueVinylDao.getUniqueById(2);

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
            uniqueVinylDao.getUniqueById(15);
        });
    }

    @Test
    @DisplayName("Gets vinyl from db by id.")
    void getByIdTest() {
        Vinyl vinyl = uniqueVinylDao.getById(2);

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
            uniqueVinylDao.getById(15);
        });
    }
*/
}