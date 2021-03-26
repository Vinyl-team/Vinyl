package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.entity.Vinyl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VinylComUaShopServiceTest {
    private VinylComUaShopService vinylComUaShopService;
    private HashSet<String> oneTestLink;

    @BeforeEach
    void setUp(){
        vinylComUaShopService = new VinylComUaShopService();
        oneTestLink = new HashSet<>();
        oneTestLink.add("https://vinyl.com.ua/jazz-vinyl/");
    }

    @Test
    void getGenreLinks() throws IOException {
        assertFalse(vinylComUaShopService.getGenreLinks().isEmpty());
    }

    @Test
    void getPageLinks() throws IOException {
        assertFalse(vinylComUaShopService.getPageLinks(oneTestLink).isEmpty());
    }

    @Test
    void readProductDataFromPage() throws IOException {
        assertFalse(vinylComUaShopService.readProductDataFromPage(oneTestLink).isEmpty());
    }

    @Test
    void getDataProduct() throws IOException {
        List<Vinyl> actualVinyls = vinylComUaShopService.getDataProduct();
        assertFalse(actualVinyls.isEmpty());
    }
}