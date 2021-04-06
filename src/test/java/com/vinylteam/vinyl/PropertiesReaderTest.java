package com.vinylteam.vinyl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PropertiesReaderTest {

    @Test
    @DisplayName("Checks properties read from the right file when the project is running locally.")
    void testPropertiesReaderAsOnLocalMachine() throws Exception {
        final PropertiesReader[] propertiesReader = new PropertiesReader[1];
        withEnvironmentVariable("env", null)
                .execute(() -> propertiesReader[0] = new PropertiesReader());

        assertEquals("postgres", propertiesReader[0].getJdbcUser());
        assertEquals("org.postgresql.Driver", propertiesReader[0].getJdbcDriver());
        assertEquals("5", propertiesReader[0].getJdbcMaximumPoolSize());
    }

    @Test
    @DisplayName("Checks properties read from the right file when the project is running on Heroku.")
    void testPropertiesReaderAsOnHeroku() throws Exception {
        final PropertiesReader[] propertiesReader = new PropertiesReader[1];
        withEnvironmentVariable("env", "PROD")
                .and("DATABASE_URL", "postgres://allconsonantsuser:longpassword" +
                        "@some-amazonw:5122/lostamongotherdatabases")
                .and("PORT", "4125")
                .execute(() -> propertiesReader[0] = new PropertiesReader());
        withEnvironmentVariable("env", "PROD");
        assertEquals("allconsonantsuser", propertiesReader[0].getJdbcUser());
        assertEquals("longpassword", propertiesReader[0].getJdbcPassword());
        assertEquals("jdbc:postgresql://some-amazonw:5122/lostamongotherdatabases",
                propertiesReader[0].getJdbcUrl());
        assertEquals("org.postgresql.Driver", propertiesReader[0].getJdbcDriver());
        assertEquals("5", propertiesReader[0].getJdbcMaximumPoolSize());
        assertEquals("4125", propertiesReader[0].getAppPort());
    }

    @Test
    @DisplayName("Checks properties read from the right file when the project is running on Travis CI.")
    void testPropertiesReaderAsOnTravis() throws Exception {
        final PropertiesReader[] propertiesReader = new PropertiesReader[1];
        withEnvironmentVariable("env", "DEV")
                .execute(() -> propertiesReader[0] = new PropertiesReader());
        assertEquals("xcqnbxpbnrplli", propertiesReader[0].getJdbcUser());
        assertEquals("cc9e5ab179e0a7a954d240e6eaa73c2e19a4740aebbf6b3bfa8fa94e769dabc1",
                propertiesReader[0].getJdbcPassword());
        assertEquals("jdbc:postgresql://ec2-52-1-115-6.compute-1.amazonaws.com:" +
                "5432/d60ghtqgakdp60", propertiesReader[0].getJdbcUrl());
        assertEquals("org.postgresql.Driver", propertiesReader[0].getJdbcDriver());
        assertEquals("5", propertiesReader[0].getJdbcMaximumPoolSize());
    }
}