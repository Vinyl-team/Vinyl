package com.vinylteam.vinyl.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShopTest {

    @Test
    @DisplayName("Checks if copied by constructor object equals to its origin")
    void testParametrizedShopConstructor(){
        var shop = new Shop();
        var i = 111;
        shop.setId(i);
        shop.setName("shop" + i);
        shop.setMainPageLink(shop.getName() + "/main");
        shop.setImageLink(shop.getName() + "/image.png");
        shop.setSmallImageLink(shop.getName() + "/smallImage.png");

        var copiedShop = new Shop(shop);
        assertEquals(shop, copiedShop);
    }

}