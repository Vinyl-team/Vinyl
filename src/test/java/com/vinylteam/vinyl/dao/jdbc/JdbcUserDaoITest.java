package com.vinylteam.vinyl.dao.jdbc;

import com.vinylteam.vinyl.entity.Role;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.util.DatabasePreparerForITests;
import com.vinylteam.vinyl.util.ListPreparerForTests;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JdbcUserDaoITest {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final DatabasePreparerForITests databasePreparer = new DatabasePreparerForITests();
    private final JdbcUserDao jdbcUserDao = new JdbcUserDao(databasePreparer.getDataSource());
    private final ListPreparerForTests listPreparer = new ListPreparerForTests();
    private final List<User> users = listPreparer.getUsersList();

    @BeforeAll
    void beforeAll() throws SQLException {
        databasePreparer.truncateCascadeUsers();
    }

    @AfterAll
    void afterAll() throws SQLException {
        databasePreparer.truncateCascadeUsers();
    }

    @BeforeEach
    void beforeEach() throws SQLException {
        databasePreparer.insertUsers(users);
    }

    @AfterEach
    void afterEach() throws SQLException {
        databasePreparer.truncateCascadeUsers();
    }

    @Test
    @DisplayName("Checks the number of rows")
    void countAllFilledTest() {
        assertEquals(2, jdbcUserDao.countAll());
    }

    @Test
    @DisplayName("Checks the number of rows when table is empty")
    void countAllEmptyTest() throws SQLException {
        //prepare
        databasePreparer.truncateCascadeUsers();
        //when
        assertEquals(0, jdbcUserDao.countAll());
    }

    @Test
    @DisplayName("Gets an existing user from db")
    void getByExistingEmailTest() {
        //prepare
        Optional<User> optionalUserGottenByExistingEmail;
        User expectedUser = new User(users.get(0));
        //when
        optionalUserGottenByExistingEmail = jdbcUserDao.getByEmail("user1@waxdeals.com");
        //then
        assertFalse(optionalUserGottenByExistingEmail.isEmpty());
        assertEquals(expectedUser, optionalUserGottenByExistingEmail.get());
    }

    @Test
    @DisplayName("Gets not existing user from db")
    void getByNotExistingEmailTest() {
        //when
        Optional<User> optionalUserGottenByNonexistentEmail = jdbcUserDao.getByEmail("user3@waxdeals.com");
        //then
        assertFalse(optionalUserGottenByNonexistentEmail.isPresent());
        assertEquals(2, jdbcUserDao.countAll());
    }

    @Test
    @DisplayName("Adds user to db")
    void addNewTest() {
        //prepare
        User expectedUser = new User();
        expectedUser.setEmail("user3@waxdeals.com");
        expectedUser.setPassword("hash3");
        expectedUser.setDiscogsUserName("discogsUserName");
        expectedUser.setSalt("salt3");
        expectedUser.setIterations(3);
        expectedUser.setRole(Role.USER);
        expectedUser.setStatus(true);
        //when
        assertTrue(jdbcUserDao.add(expectedUser));
        //then
        assertEquals(3, jdbcUserDao.countAll());
        Optional<User> optionalAddedUser = jdbcUserDao.getByEmail("user3@waxdeals.com");
        assertFalse(optionalAddedUser.isEmpty());
        assertEquals(expectedUser, optionalAddedUser.get());
    }

    @Test
    @DisplayName("Adds existing user with the same password")
    void addExistingWithSamePasswordTest() {
        //when
        assertFalse(jdbcUserDao.add(users.get(0)));
        //then
        assertEquals(2, jdbcUserDao.countAll());
    }

    @Test
    @DisplayName("Adds new user with existing discogsUserName")
    void addNewWithExistingDiscogsUserNameTest() {
        //prepare
        User expectedUser = new User();
        expectedUser.setEmail("user3@waxdeals.com");
        expectedUser.setPassword("hash3");
        expectedUser.setDiscogsUserName("discogsUserName1");
        expectedUser.setSalt("salt3");
        expectedUser.setIterations(3);
        expectedUser.setRole(Role.USER);
        expectedUser.setStatus(true);
        //when
        assertFalse(jdbcUserDao.add(expectedUser));
        //then
        assertEquals(2, jdbcUserDao.countAll());
    }

    @Test
    @DisplayName("Adds existing user with new password")
    void addExistingWithNewPasswordTest() {
        //prepare
        User user = new User(users.get(0));
        System.out.println(users.get(0).getPassword());
        //when
        assertFalse(jdbcUserDao.add(user));
        //then
        assertEquals(2, jdbcUserDao.countAll());
    }

    @Test
    @DisplayName("Edit non-existent user in db")
    void editNonExistentUserInDbTest() {
        String oldNonExistentEmail = "user4@waxdeals.com";
        User user = new User();
        user.setEmail("user5@waxdeals.com");
        user.setPassword("HASH4");
        user.setDiscogsUserName("discogsUserName");
        user.setSalt("");
        user.setIterations(0);
        user.setRole(Role.USER);
        user.setStatus(true);

        assertFalse(jdbcUserDao.edit(oldNonExistentEmail, user));
    }

    @Test
    @DisplayName("Edit existing user in db")
    void editWithAnExistingUserInDbTest() {
        String oldExistingEmail = "user2@waxdeals.com";
        User user = new User();
        user.setEmail("user3@waxdeals.com");
        user.setPassword("HASH2");
        user.setDiscogsUserName("newDiscogsUserName");
        user.setSalt("");
        user.setIterations(0);
        user.setRole(Role.USER);
        user.setStatus(true);

        assertTrue(jdbcUserDao.edit(oldExistingEmail, user));
        assertEquals("user3@waxdeals.com", jdbcUserDao.getByEmail("user3@waxdeals.com").orElse(new User()).getEmail());
    }

    @Test
    @DisplayName("Edit user and try to change discogsUserName that already exist in db")
    void editWithAnExistingUserInDbAndTryToChangeDiscogsUserNameThatAlreadyExistTest() {
        String oldExistingEmail = "user2@waxdeals.com";
        User user = new User();
        user.setEmail("user3@waxdeals.com");
        user.setPassword("HASH2");
        user.setDiscogsUserName("discogsUserName1");
        user.setSalt("");
        user.setIterations(0);
        user.setRole(Role.USER);
        user.setStatus(true);

        assertFalse(jdbcUserDao.edit(oldExistingEmail, user));
        assertEquals("user2@waxdeals.com", jdbcUserDao.getByEmail("user2@waxdeals.com").orElse(new User()).getEmail());
    }

}