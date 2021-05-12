package com.vinylteam.vinyl.util.impl;

import com.vinylteam.vinyl.entity.Currency;
import com.vinylteam.vinyl.entity.RawOffer;
import com.vinylteam.vinyl.util.VinylParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

public class VinylUaParser implements VinylParser {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final String startLink = "http://vinyl.ua";
    private final String classContainingGenresLinks = "dropdown-menu dropdown-menu-left";
    private final String classContainingPagesLinks = "pagination-wrapper margin-top-20";
    private final String classContainingOffersLinks = "full-space";
    private final String classContainingUniqueVinylDetails = "vinyl-release showcase";
    private final String classContainingRelease = "margin-top-clear margin-bot-5";
    private final String classContainingArtist = "text-ellipsis";
    private final String classContainingPriceDetails = "pull-left margin-top-5 showcase-release-price";

    HashSet<String> getGenresLinks() {
        HashSet<String> genreLinks = new HashSet<>();
        Document document;
        try {
            document = Jsoup.connect(startLink).get();
        } catch (IOException e) {
            logger.error("Error while getting document by link {'link':{}}", startLink, e);
            throw new RuntimeException("Fail while getting a document by " + startLink, e);
        }
        logger.debug("Got document out of start link {'startLink':{}, 'document':{}",
                startLink, document);
        Elements anchors = document.getElementsByClass(classContainingGenresLinks).select("a");
        logger.debug("Got collection of anchors {'anchors':{}}", anchors);
        for (Element anchor : anchors) {
            String anchorLink = anchor.attr("href");
            String link = startLink + anchorLink;
            genreLinks.add(link);
            logger.debug("Added link {'link':{}}", link);
        }
        logger.debug("Resulting hash set of genre links is {'genreLinks':{}}", genreLinks);
        return genreLinks;
    }

    HashSet<String> getPageLinks(HashSet<String> genreLinks) {
        LinkedHashSet<String> pageLinks = new LinkedHashSet<>();
        for (String genreLink : genreLinks) {
            Document document;
            try {
                document = Jsoup.connect(genreLink).get();
            } catch (IOException e) {
                logger.error("Error while getting document by link {'link':{}}", genreLink, e);
                throw new RuntimeException("Fail while getting a document by " + genreLink, e);
            }
            logger.debug("Got document out of genre link {'genreLink':{}, 'document':{}", genreLink, document);
            Elements anchors = document.getElementsByClass(classContainingPagesLinks).select("a");
            logger.debug("Got collection of anchors {'anchors':{}}", anchors);
            for (Element anchor : anchors) {
                String anchorLink = anchor.attr("href");
                String link = startLink + anchorLink;
                if (link.contains("?page=") && !link.contains("ussr?page=2")) {
                    pageLinks.add(link);
                    logger.debug("Added link {'link':{}}", link);
                }
            }
        }
        logger.debug("Resulting hash set of page links is {'pageLinks':{}}", pageLinks);
        return pageLinks;
    }

    HashSet<String> getOfferLinks(HashSet<String> pageLinks) {
        HashSet<String> offerLinks = new HashSet<>();
        for (String pageLink : pageLinks) {
            Document document;
            try {
                document = Jsoup.connect(pageLink).get();
            } catch (IOException e) {
                logger.error("Error while getting document by link {'link':{}}", pageLink, e);
                throw new RuntimeException("Fail while getting a document by " + pageLink, e);
            }
            logger.debug("Got document out of page link {'pageLink':{}, 'document':{}", pageLink, document);
            Elements anchors = document.getElementsByClass(classContainingOffersLinks);
            logger.debug("Got collection of inner anchors with links to offers {'anchors':{}}", anchors);

            for (Element anchor : anchors) {
                String offerLink = startLink + anchor.attr("href");
                offerLinks.add(offerLink);
                logger.debug("Added offer link to hash set{'offerLink':{}}", offerLink);
            }
            logger.debug("Parsed page link {'pageLink':{}}", pageLink);
        }
        logger.debug("Resulting hash set of offer links is {'offerLinks':{}}", offerLinks);
        return offerLinks;

    }

    HashSet<RawOffer> readRawOffersFromAllOfferLinks(HashSet<String> offerLinks) {
        HashSet<RawOffer> rawOfferSet = new HashSet<>();
        for (String offerLink : offerLinks) {
            rawOfferSet.add(getRawOfferFromOfferLink(offerLink));
        }
        logger.debug("Resulting hash set of rawOfferSet is {'rawOfferSet':{}}", rawOfferSet);
        return rawOfferSet;
    }

    public RawOffer getRawOfferFromOfferLink(String offerLink) {
        RawOffer rawOffer = new RawOffer();
        Document document;
        try {
            document = Jsoup.connect(offerLink).get();
            logger.debug("Got document out of offer link {'offerLink':{}, 'document':{}", offerLink, document);
        } catch (IOException e) {
            logger.error("Error while getting document by link {'link':{}}", offerLink, e);
            throw new RuntimeException("Fail while getting a document by " + offerLink, e);
        }
        Elements uniqueVinylDetailsElements = document.getElementsByClass(classContainingUniqueVinylDetails);
        Elements offerReleaseDetailsElements = document.getElementsByClass("list-meta");
        Elements offerPriceDetailsElements = document.getElementsByClass("btn btn-lg btn-success btn-bevel full-width");
        logger.debug("Got collections of unique vinyl details elements, offer release details elements, offer price details elements {'uniqueVinylDetailsElements':{}, " +
                "'offerReleaseDetailsElements':{}, 'offerPriceDetailsElements':{}}", uniqueVinylDetailsElements, offerReleaseDetailsElements, offerPriceDetailsElements);
        Element uniqueVinylDetails = uniqueVinylDetailsElements.first();
        Element offerReleaseDetails = offerReleaseDetailsElements.first();
        Element offerPriceDetails = offerPriceDetailsElements.first();
        String release = uniqueVinylDetails.getElementsByClass(classContainingRelease).text();
        String artist = uniqueVinylDetails.getElementsByClass(classContainingArtist).select("a").text();
        if (artist.equals("")) {
            artist = "Various Artists";
        }
        String priceDetails = uniqueVinylDetails.getElementsByClass(classContainingPriceDetails).text();
        String priceNumber = priceDetails.substring(0, priceDetails.indexOf(' '));
        String priceCurrency = priceDetails.substring(priceDetails.indexOf(' ') + 1);
        String vinylLink = startLink + uniqueVinylDetails
                .getElementsByClass(classContainingOffersLinks).select("a").attr("href");
        String[] imageLinks = uniqueVinylDetails
                .getElementsByClass(classContainingOffersLinks).attr("style").split("'");
        String imageLink = imageLinks[1];
        if (!imageLink.contains("amazonaws.com")) {
            imageLink = "img/goods/no_image.jpg";
        }
        String[] linkComponents = offerLink.split("[/?]");
        String genre = linkComponents[4];

        rawOffer.setShopId(1);
        rawOffer.setRelease(release);
        rawOffer.setArtist(artist);
        rawOffer.setPrice(Double.parseDouble(priceNumber));
        rawOffer.setCurrency(Currency.getCurrency(priceCurrency));
        rawOffer.setGenre(genre);
        rawOffer.setOfferLink(vinylLink);
        rawOffer.setImageLink(imageLink);
        logger.debug("Parsed page link {'offerLink':{}}",offerLink);
        logger.debug("Resulting raw offer is {'rawOffer':{}}", rawOffer);
        return rawOffer;
}

    @Override
    public List<RawOffer> getRawOffersList() {
        HashSet<String> genresLinks = getGenresLinks();
        logger.info("got genre links {'genreLinks':{}}", genresLinks);
        HashSet<String> pageLinks = getPageLinks(genresLinks);
        logger.info("got page links {'pageLinks':{}}", pageLinks);
        HashSet<String> offerLinks = getOfferLinks(pageLinks);
        logger.info("got offer links {'offerLinks':{}}", offerLinks);
        HashSet<RawOffer> rawOfferSet = readRawOffersFromAllOfferLinks(pageLinks);
        logger.info("read {} rawOffers from all offer links", rawOfferSet.size());
        List<RawOffer> rawOffers = new ArrayList<>(rawOfferSet);
        logger.debug("Resulting list of raw offers from vinyl.ua is {'rawOffers':{}}", rawOffers);
        return rawOffers;
    }

}