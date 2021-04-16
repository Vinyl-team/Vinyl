package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.VinylDao;
import com.vinylteam.vinyl.dao.jdbc.JdbcVinylDao;
import com.vinylteam.vinyl.entity.Currency;
import com.vinylteam.vinyl.entity.Vinyl;
import com.vinylteam.vinyl.service.VinylService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DefaultVinylServiceTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final VinylDao mockedVinylDao = mock(JdbcVinylDao.class);
    private final Vinyl vinyl = new Vinyl();
    private final Vinyl uniqueVinyl = new Vinyl();
    private final List<Vinyl> uniqueVinyls = new ArrayList<>(List.of(uniqueVinyl));
    private final VinylService vinylService = new DefaultVinylService(mockedVinylDao);

    @BeforeAll
    void beforeAll() {
        vinyl.setVinylId(1);
        vinyl.setShopId(1);
        vinyl.setUniqueVinylId(1);
        vinyl.setRelease("release1");
        vinyl.setArtist("artist1");
        vinyl.setFullNameVinyl("release1 - artist1");
        vinyl.setPrice(20.);
        vinyl.setCurrency(Optional.ofNullable(Currency.UAH));
        vinyl.setVinylLink("/release1");
        vinyl.setImageLink("/release1.png");

        uniqueVinyl.setVinylId(1);
        uniqueVinyl.setRelease("release1");
        uniqueVinyl.setArtist("artist1");
        uniqueVinyl.setFullNameVinyl("release1 - artist1");
        uniqueVinyl.setImageLink("/release1.png");

        when(mockedVinylDao.getManyRandomUnique(anyInt())).thenReturn(uniqueVinyls);
        when(mockedVinylDao.getManyFilteredUnique(anyString())).thenReturn(uniqueVinyls);
        when(mockedVinylDao.getById(anyLong())).thenReturn(vinyl);
        when(mockedVinylDao.getUniqueById(anyLong())).thenReturn(uniqueVinyl);
    }

    @Test
    @DisplayName("Checks that when amount>0 VinylDao.getManyRandomUnique(amount) is called.")
    void getManyRandomUniqueValidAmountTest() {
        int amount = 300;
        List<Vinyl> actualUniqueVinyls = vinylService.getManyRandomUnique(amount);

        assertSame(uniqueVinyls, actualUniqueVinyls);
        verify(mockedVinylDao).getManyRandomUnique(amount);
    }

    @Test
    @DisplayName("Checks that when amount<=0 VinylDao.getManyRandomUnique(amount) is not called.")
    void getManyRandomUniqueInvalidAmountTest() {
        int amount = -300;
        List<Vinyl> actualUniqueVinyls = vinylService.getManyRandomUnique(amount);

        assertTrue(actualUniqueVinyls.isEmpty());
        verify(mockedVinylDao, never()).getManyRandomUnique(amount);
    }

    @Test
    @DisplayName("Checks that when matcher is not null VinylDao.getManyFilteredUnique(matcher) is called.")
    void getManyFilteredUniqueNotNullMatcherTest() {
        String matcher = "release1";
        List<Vinyl> actualUniqueVinyls = vinylService.getManyFilteredUnique(matcher);

        assertSame(uniqueVinyls, actualUniqueVinyls);
        verify(mockedVinylDao).getManyFilteredUnique(matcher);
    }

    @Test
    @DisplayName("Checks that when matcher is null VinylDao.getManyFilteredUnique(matcher) is not called.")
    void getManyFilteredUniqueNullMatcherTest() {
        List<Vinyl> actualUniqueVinyls = vinylService.getManyFilteredUnique(null);

        assertTrue(actualUniqueVinyls.isEmpty());
        verify(mockedVinylDao, never()).getManyFilteredUnique(null);
    }

    @Test
    @DisplayName("Checks that when id>0 VinylDao.getUniqueById(id) is called.")
    void getUniqueByValidIdTest() {
        long id = 1;
        Vinyl actualUniqueVinyl = vinylService.getUniqueById(id);

        assertSame(uniqueVinyl, actualUniqueVinyl);
        verify(mockedVinylDao).getUniqueById(id);
    }

    @Test
    @DisplayName("Checks that when id<=0 VinylDao.getUniqueById(id) is not called.")
    void getUniqueByInvalidIdTest() {
        long id = -1;

        assertThrows(RuntimeException.class, () -> {
            Vinyl actualUniqueVinyl = vinylService.getUniqueById(id);
        });
        verify(mockedVinylDao, never()).getUniqueById(id);
    }

    @Test
    @DisplayName("Checks that when id>0 VinylDao.getUniqueById(id) is called.")
    void getByValidIdTest() {
        long id = 3;
        Vinyl actualVinyl = vinylService.getById(id);

        assertSame(vinyl, actualVinyl);
        verify(mockedVinylDao).getById(id);
    }

    @Test
    @DisplayName("Checks that when id<=0 VinylDao.getUniqueById(id) is not called.")
    void getUniqueByNotValidIdTest() {
        long id = -3;

        assertThrows(RuntimeException.class, () -> {
            Vinyl actualVinyl = vinylService.getById(id);
        });
        verify(mockedVinylDao, never()).getById(id);
    }

}