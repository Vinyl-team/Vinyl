package com.vinylteam.vinyl.dao.jdbc;

import com.vinylteam.vinyl.dao.UserPostDao;
import com.vinylteam.vinyl.entity.UserPost;
import com.vinylteam.vinyl.util.DataGeneratorForTests;
import com.vinylteam.vinyl.util.DatabasePreparerForITests;
import org.junit.jupiter.api.*;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JdbcUserPostDaoITest {
    private final DatabasePreparerForITests databasePreparer = new DatabasePreparerForITests();
    private final DataGeneratorForTests dataGenerator = new DataGeneratorForTests();
    private final UserPostDao userPostDao = new JdbcUserPostDao(databasePreparer.getDataSource());

    @BeforeAll
    void beforeAll() throws SQLException {
        databasePreparer.truncateCascadeUsers();
        databasePreparer.truncateCascadeUserPosts();
        databasePreparer.insertUsers(dataGenerator.getUsersList());
    }

    @AfterAll
    void afterAll() throws SQLException {
        databasePreparer.truncateCascadeUsers();
        databasePreparer.truncateCascadeUserPosts();
    }

    @Test
    @DisplayName("Adds user post to db")
    void addNewUserPostTest() {
        //prepare
        UserPost expectedUserPost = new UserPost(1L, "name", "email", "theme", "message");
        //when
        assertTrue(userPostDao.add(expectedUserPost));
    }
}