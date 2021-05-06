package com.vinylteam.vinyl.util;

import com.vinylteam.vinyl.entity.Offer;
import com.vinylteam.vinyl.entity.RawOffer;
import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.service.OfferService;
import com.vinylteam.vinyl.service.UniqueVinylService;
import com.vinylteam.vinyl.util.impl.VinylUaParser;

import java.util.ArrayList;
import java.util.List;

public class Updater {

    private final UniqueVinylService uniqueVinylService;
    private final OfferService offerService;
    private final ShopsParser shopsParser;
    private final RawOffersSorter sorter;

    public Updater(UniqueVinylService uniqueVinylService, OfferService offerService, ShopsParser shopsParser, RawOffersSorter sorter) {
        this.uniqueVinylService = uniqueVinylService;
        this.offerService = offerService;
        this.shopsParser = shopsParser;
        this.sorter = sorter;
    }
//TODO: List of parsers.
    public void updateUniqueVinylsRewriteOffers() {
        List<UniqueVinyl> uniqueVinyls = uniqueVinylService.findAll();
        List<RawOffer> rawOffers = shopsParser.getRawVinylDataFromAll(new ArrayList<>(List.of(new VinylUaParser())));
        List<Offer> newOffers = sorter.getOffersUpdateUniqueVinyls(rawOffers, uniqueVinyls);
        offerService.updateUniqueVinylsRewriteAll(uniqueVinyls, newOffers);
    }

}
