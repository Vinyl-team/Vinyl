package com.vinylteam.vinyl.util;

import com.vinylteam.vinyl.entity.RawOffer;
import com.vinylteam.vinyl.service.HasShopIdentifier;

import java.util.List;

public interface VinylParser extends HasShopIdentifier {

    List<RawOffer> getRawOffersList();

    RawOffer getRawOfferFromOfferLink(String offerLink);

    boolean isValid(RawOffer rawOffer);
}
