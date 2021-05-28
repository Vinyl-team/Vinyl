package com.vinylteam.vinyl.util.impl;

import com.vinylteam.vinyl.entity.RawOffer;
import com.vinylteam.vinyl.util.VinylParser;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
public class AbstractVinylParser implements VinylParser {
    @Override
    public long getShopId() {
        return 0;
    }

    @Override
    public List<RawOffer> getRawOffersList() {
        return null;
    }

    @Override
    public RawOffer getRawOfferFromOfferLink(String offerLink) {
        return null;
    }

    @Override
    public boolean isValid(RawOffer rawOffer) {
        boolean isValid = false;
        if (rawOffer.getPrice() != 0.
                && rawOffer.getCurrency().isPresent()
                && !rawOffer.getRelease().isEmpty()
                && rawOffer.getOfferLink() != null) {
            isValid = true;
        }
        return isValid;
    }

    protected Optional<Document> getDocument(String url) {
        try {
            return Optional.ofNullable(Jsoup.connect(url).get());
        } catch (IOException e) {
            log.warn("Page represented by the link will be skipped, since some error happened while getting document by link {'link':{}}", url, e);
            return Optional.empty();
        }
    }

}
