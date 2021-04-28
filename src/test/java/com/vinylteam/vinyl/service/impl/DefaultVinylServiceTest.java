/*
package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.jdbc.JdbcVinylDao;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DefaultVinylServiceTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final VinylDao mockedVinylDao = mock(JdbcVinylDao.class);
    private final Vinyl vinyl = new Vinyl();
    private final Vinyl uniqueVinyl = new Vinyl();
    private final List<Vinyl> uniqueVinyls = List.of(uniqueVinyl);
    private final List<Vinyl> vinyls = List.of(vinyl);
    private final VinylService vinylService = new DefaultVinylService(mockedVinylDao);

    @BeforeAll
    void beforeAll() {
        when(mockedVinylDao.getManyRandomUnique(anyInt())).thenReturn(uniqueVinyls);
        when(mockedVinylDao.getManyFilteredUnique(anyString())).thenReturn(uniqueVinyls);
        when(mockedVinylDao.getManyUniqueByArtist(anyString())).thenReturn(uniqueVinyls);
        when(mockedVinylDao.getManyByUniqueVinylId(anyLong())).thenReturn(vinyls);
        when(mockedVinylDao.getById(anyLong())).thenReturn(vinyl);
        when(mockedVinylDao.getUniqueById(anyLong())).thenReturn(uniqueVinyl);
    }

    @Test
    @DisplayName("Checks that when amount>0 VinylDao.getManyRandomUnique(amount) is called, it's result is returned")
    void getManyRandomUniqueValidAmountTest() {
        int amount = 300;
        List<Vinyl> actualUniqueVinyls = vinylService.getManyRandomUnique(amount);

        assertSame(uniqueVinyls, actualUniqueVinyls);
        verify(mockedVinylDao).getManyRandomUnique(amount);
    }

    @Test
    @DisplayName("Checks that when amount<=0 VinylDao.getManyRandomUnique(amount) is not called, empty list is returned")
    void getManyRandomUniqueInvalidAmountTest() {
        int amount = -300;
        List<Vinyl> actualUniqueVinyls = vinylService.getManyRandomUnique(amount);

        assertTrue(actualUniqueVinyls.isEmpty());
        verify(mockedVinylDao, never()).getManyRandomUnique(amount);
    }

    @Test
    @DisplayName("Checks that when matcher is not null VinylDao.getManyFilteredUnique(matcher) is called, it's result is returned")
    void getManyFilteredUniqueNotNullMatcherTest() {
        String matcher = "release1";
        List<Vinyl> actualUniqueVinyls = vinylService.getManyFilteredUnique(matcher);

        assertSame(uniqueVinyls, actualUniqueVinyls);
        verify(mockedVinylDao).getManyFilteredUnique(matcher);
    }

    @Test
    @DisplayName("Checks that when matcher is null VinylDao.getManyFilteredUnique(matcher) is not called, empty list is returned")
    void getManyFilteredUniqueNullMatcherTest() {
        List<Vinyl> actualUniqueVinyls = vinylService.getManyFilteredUnique(null);

        assertTrue(actualUniqueVinyls.isEmpty());
        verify(mockedVinylDao, never()).getManyFilteredUnique(null);
    }

    @Test
    @DisplayName("Checks that when matcher is not null VinylDao.getManyUniqueByArtist(artist) is called, it's result is returned")
    void getManyUniqueByArtistNotNullArtistTest() {
        String artist = "artist1";
        List<Vinyl> actualUniqueVinyls = vinylService.getManyUniqueByArtist(artist);

        assertSame(uniqueVinyls, actualUniqueVinyls);
        verify(mockedVinylDao).getManyUniqueByArtist(artist);
    }

    @Test
    @DisplayName("Checks that when artist is null VinylDao.getManyUniqueByArtist(artist) is not called, empty list is returned")
    void getManyUniqueByArtistNullArtistTest() {
        List<Vinyl> actualUniqueVinyls = vinylService.getManyUniqueByArtist(null);

        assertTrue(actualUniqueVinyls.isEmpty());
        verify(mockedVinylDao, never()).getManyUniqueByArtist(null);
    }

    @Test
    @DisplayName("Checks that when id>0 VinylDao.getManyByUniqueVinylId(id) is called, it's result is returned")
    void getManyByUniqueVinylIdValidIdTest() {
        long id = 1;
        List<Vinyl> actualVinyls = vinylService.getManyByUniqueVinylId(id);

        assertSame(vinyls, actualVinyls);
        verify(mockedVinylDao).getManyByUniqueVinylId(id);
    }

    @Test
    @DisplayName("Checks that when id<=0 getManyByUniqueVinylId(id) is not called, Runtime exception is thrown")
    void getManyByUniqueVinylIdNotValidIdTest() {
        long id = -1;

        assertThrows(RuntimeException.class, () -> {
            List<Vinyl> actualVinyls = vinylService.getManyByUniqueVinylId(id);
        });
        verify(mockedVinylDao, never()).getManyByUniqueVinylId(id);
    }

    @Test
    @DisplayName("Checks that when id>0 VinylDao.getUniqueById(id) is called, it's result is returned")
    void getUniqueByValidIdTest() {
        long id = 1;
        Vinyl actualUniqueVinyl = vinylService.getUniqueById(id);

        assertSame(uniqueVinyl, actualUniqueVinyl);
        verify(mockedVinylDao).getUniqueById(id);
    }

    @Test
    @DisplayName("Checks that when id<=0 VinylDao.getUniqueById(id) is not called, Runtime exception is thrown")
    void getUniqueByInvalidIdTest() {
        long id = -1;

        assertThrows(RuntimeException.class, () -> {
            Vinyl actualUniqueVinyl = vinylService.getUniqueById(id);
        });
        verify(mockedVinylDao, never()).getUniqueById(id);
    }

    @Test
    @DisplayName("Checks that when id>0 VinylDao.getUniqueById(id) is called, it's result is returned")
    void getByValidIdTest() {
        long id = 3;
        Vinyl actualVinyl = vinylService.getById(id);

        assertSame(vinyl, actualVinyl);
        verify(mockedVinylDao).getById(id);
    }

    @Test
    @DisplayName("Checks that when id<=0 VinylDao.getUniqueById(id) is not called, Runtime exception is thrown")
    void getUniqueByNotValidIdTest() {
        long id = -3;

        assertThrows(RuntimeException.class, () -> {
            Vinyl actualVinyl = vinylService.getById(id);
        });
        verify(mockedVinylDao, never()).getById(id);
    }

    @Test
    @DisplayName("Gets list of unique shop id-s from list of vinyls with duplicate shop id-s")
    void getListOfShopsIdsWithDuplicateShopIdVinylsTest() {
        Vinyl firstVinyl = new Vinyl();
        Vinyl duplicateShopIdVinyl = new Vinyl();
        Vinyl newShopIdVinyl = new Vinyl();
        firstVinyl.setShopId(1);
        duplicateShopIdVinyl.setShopId(1);
        newShopIdVinyl.setShopId(2);
        List<Vinyl> vinylsList = List.of(firstVinyl, duplicateShopIdVinyl, newShopIdVinyl);

        List<Integer> actualIds = vinylService.getListOfShopIds(vinylsList);

        assertEquals(2, actualIds.size());

        assertEquals(1, actualIds.get(0));
        assertEquals(2, actualIds.get(1));
    }

    @Test
    @DisplayName("Gets empty list of unique shop id-s from empty list of vinyls")
    void getListOfShopsIdsWithEmptyVinylListTest() {

        List<Vinyl> emptyVinylsList = new ArrayList<>();

        List<Integer> actualIds = vinylService.getListOfShopIds(emptyVinylsList);

        assertTrue(actualIds.isEmpty());
    }

    @Test
    @DisplayName("Checks that when list of vinyls is null method returns empty list")
    void getListOfShopsIdsWithNullVinylListTest() {
        List<Integer> actualIds = vinylService.getListOfShopIds(null);

        assertTrue(actualIds.isEmpty());
    }

}*/
