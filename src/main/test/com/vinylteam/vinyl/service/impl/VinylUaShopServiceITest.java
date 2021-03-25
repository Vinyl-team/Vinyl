package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.entity.Vinyl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VinylUaShopServiceITest {
    private VinylUaShopService vinylUaShopService;
    private HashSet<String> oneTestLink;

    @BeforeEach
    void setUp() {
        vinylUaShopService = new VinylUaShopService();
        oneTestLink = new HashSet<>();
        oneTestLink.add("http://vinyl.ua/showcase/reggae");
    }

    @Test
    void getShowcaseLinksTest() throws IOException {
        assertFalse(vinylUaShopService.getShowcaseLinks().isEmpty());
    }

    @Test
    void getPageLinksTest() throws IOException {
        assertFalse(vinylUaShopService.getPageLinks(oneTestLink).isEmpty());
    }

    @Test
    void readProductDataFromPageTest() throws IOException {
        assertFalse(vinylUaShopService.readProductDataFromPage(oneTestLink).isEmpty());
    }

    @Test
    void getDataProduct() throws IOException {
        List<Vinyl> actualVinyls = vinylUaShopService.getDataProduct();
        assertFalse(actualVinyls.isEmpty());
    }
}