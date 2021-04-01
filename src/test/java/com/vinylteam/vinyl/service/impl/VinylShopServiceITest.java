package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.entity.Vinyl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

class VinylShopServiceITest {
    private VinylShopService vinylShopService;
    private HashSet<String> oneTestLink;

    @BeforeEach
    void setUp() {
        vinylShopService = new VinylShopService();
        oneTestLink = new HashSet<>();
        oneTestLink.add("http://vinyl.ua/showcase/reggae");
    }

    @Test
    void getShowcaseLinksTest() throws IOException {
        assertFalse(vinylShopService.getShowcaseLinks().isEmpty());
    }

    @Test
    void getPageLinksTest() throws IOException {
        assertFalse(vinylShopService.getPageLinks(oneTestLink).isEmpty());
    }

    @Test
    void readProductDataFromPageTest() throws IOException {
        assertFalse(vinylShopService.readProductDataFromPage(oneTestLink).isEmpty());
    }

    @Test
    void getDataProduct() throws IOException {
        List<Vinyl> actualVinyls = vinylShopService.getDataProduct();
        assertFalse(actualVinyls.isEmpty());
    }
}