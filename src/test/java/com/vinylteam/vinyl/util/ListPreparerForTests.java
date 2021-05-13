package com.vinylteam.vinyl.util;

import com.vinylteam.vinyl.entity.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ListPreparerForTests {

    public List<Shop> getShopsList() {
        List<Shop> shops = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Shop shop = new Shop();
            shop.setId(i + 1);
            shop.setName("shop" + (i + 1));
            shop.setMainPageLink(shop.getName() + "/main");
            shop.setImageLink(shop.getName() + "/image.png");
            shops.add(shop);
        }
        return shops;
    }

    public List<UniqueVinyl> getUniqueVinylsList() {
        List<UniqueVinyl> uniqueVinyls = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            UniqueVinyl uniqueVinyl = new UniqueVinyl();
            uniqueVinyl.setId(i + 1);
            uniqueVinyl.setRelease("release" + (i + 1));
            uniqueVinyl.setArtist("artist" + (i + 1));
            uniqueVinyl.setFullName(uniqueVinyl.getRelease() + " - " + uniqueVinyl.getArtist());
            uniqueVinyl.setImageLink("/image" + (i + 1));
            uniqueVinyl.setHasOffers(true);
            uniqueVinyls.add(uniqueVinyl);
        }
        uniqueVinyls.get(3).setHasOffers(false);
        return uniqueVinyls;
    }

    public List<Offer> getOffersList() {
        List<Offer> offers = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 2; j++) {
                Offer offer = new Offer();
                offer.setId(i * 2 + j + 1);
                offer.setUniqueVinylId(i + 1);
                offer.setShopId(j + 1);
                offer.setPrice((i + 1) * 10. + j + 1);
                offer.setCurrency(Optional.of(Currency.UAH));
                offer.setGenre("genre" + (i + 1));
                offer.setOfferLink("shop" + offer.getShopId() + "/release" + (i + 1));
                offers.add(offer);
            }
        }
        return offers;
    }

    public List<User> getUsersList() {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            User user = new User();
            user.setEmail("user" + (i + 1) + "@waxdeals.com");
            user.setPassword("hash" + (i + 1));
            user.setDiscogsUserName("discogsUserName" + (i + 1));
            user.setSalt("salt" + (i + 1));
            user.setIterations(i + 1);
            user.setRole(Role.USER);
            user.setStatus(true);
            users.add(user);
        }
        return users;
    }

    public void fillListsForRawOffersSorterTest(List<RawOffer> rawOffers, List<UniqueVinyl> uniqueVinyls, List<Offer> offers) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 2; j++) {
                RawOffer rawOffer = new RawOffer();
                rawOffer.setShopId(j + 1);
                rawOffer.setRelease("release" + (i + 1));
                rawOffer.setArtist("artist" + (i + 1));
                rawOffer.setPrice((i + 1) * 10. + j + 1);
                rawOffer.setCurrency(Optional.of(Currency.UAH));
                rawOffer.setGenre("genre" + i);
                rawOffer.setOfferLink("shop" + rawOffer.getShopId() + "/" + rawOffer.getRelease());
                rawOffer.setImageLink("/image" + (i + 1));
                rawOffers.add(rawOffer);

                Offer offer = new Offer();
                offer.setUniqueVinylId(i + 1);
                offer.setShopId(rawOffer.getShopId());
                offer.setPrice(rawOffer.getPrice());
                offer.setCurrency(rawOffer.getCurrency());
                offer.setGenre(rawOffer.getGenre());
                offer.setOfferLink(rawOffer.getOfferLink());
                offers.add(offer);
            }
            UniqueVinyl uniqueVinyl = new UniqueVinyl();
            uniqueVinyl.setId(i + 1);
            uniqueVinyl.setRelease(rawOffers.get(i * 2).getRelease());
            uniqueVinyl.setArtist(rawOffers.get(i * 2).getArtist());
            uniqueVinyl.setFullName(uniqueVinyl.getRelease() + " - " + uniqueVinyl.getArtist());
            uniqueVinyl.setImageLink("/image" + (i + 1));
            uniqueVinyl.setHasOffers(true);
            uniqueVinyls.add(uniqueVinyl);
        }
    }

}
