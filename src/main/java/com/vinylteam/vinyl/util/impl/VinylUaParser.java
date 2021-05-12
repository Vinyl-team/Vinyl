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
    private final String START_LINK = "http://vinyl.ua";
    private final String SELECTOR_SCRIPT_WITH_HIGH_RES_IMAGE_LINK = "script:containsData(openPhotoSwipe)";
    private final String SELECTOR_RELEASE = "div.boxed div.col-sm-7 > h3.normal-text";
    private final String SELECTOR_ARTIST = "div.boxed div.col-sm-7 > h4.normal-text > span.text-ellipsis > a";
    private final String SELECTOR_PRICE_DETAILS = "div.boxed div.col-sm-4 > button.btn-success > b";
    private final String SELECTOR_GENRE = "div.boxed div.cd-xs-12 table.list-meta td.text-right:contains(Жанр:) + td";
    private final String classContainingGenresLinks = "dropdown-menu dropdown-menu-left";
    private final String classContainingPagesLinks = "pagination-wrapper margin-top-20";
    private final String classContainingOffersLinks = "full-space";

    HashSet<String> getGenresLinks() {
        HashSet<String> genreLinks = new HashSet<>();
        Document document;
        try {
            document = Jsoup.connect(START_LINK).get();
        } catch (IOException e) {
            logger.error("Error while getting document by link {'link':{}}", START_LINK, e);
            throw new RuntimeException("Fail while getting a document by " + START_LINK, e);
        }
        logger.debug("Got document out of start link {'startLink':{}, 'document':{}",
                START_LINK, document);
        Elements anchors = document.getElementsByClass(classContainingGenresLinks).select("a");
        logger.debug("Got collection of anchors {'anchors':{}}", anchors);
        for (Element anchor : anchors) {
            String anchorLink = anchor.attr("href");
            String link = START_LINK + anchorLink;
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
                String link = START_LINK + anchorLink;
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
                String offerLink = START_LINK + anchor.attr("href");
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
            RawOffer rawOffer = getRawOfferFromOfferLink(offerLink);
            rawOfferSet.add(rawOffer);
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
        String release = document.select(SELECTOR_RELEASE).text();
        logger.debug("Got release from page by offer link {'release':{}, 'offerLink':{}}", release, offerLink);
        String artist = document.select(SELECTOR_ARTIST).text();
        if (artist.equals("")) {
            artist = "Various Artists";
        }
        logger.debug("Got artist from page by offer link  {'artist':{}, 'offerLink':{}}", artist, offerLink);
        String priceDetails = document.select(SELECTOR_PRICE_DETAILS).text();
        String priceNumber = priceDetails.substring(0, priceDetails.indexOf(' '));
        String priceCurrency = priceDetails.substring(priceDetails.indexOf(' ') + 1);
        logger.debug("Got price number and currency from page by offer link {'priceNumber':{}, 'priceCurrency':{}, 'offerLink':{}}", priceNumber, priceCurrency, offerLink);
        String scriptWithHighResImageLink = document.select(SELECTOR_SCRIPT_WITH_HIGH_RES_IMAGE_LINK).dataNodes().get(0).getWholeData();
        String highResImageLink;
        int beginIndexOfImageLink = scriptWithHighResImageLink.indexOf("src: '") + "src: '".length();
        if (beginIndexOfImageLink != -1) {
            int endIndexOfImageLink = scriptWithHighResImageLink.indexOf('\'', beginIndexOfImageLink);
            highResImageLink = scriptWithHighResImageLink.substring(beginIndexOfImageLink, endIndexOfImageLink);
        } else {
            highResImageLink = "img/goods/no_image.jpg";
        }
        logger.debug("Got image with high resolution link from page by offer link {'highResImageLink':{}, 'offerLink':{}}", highResImageLink, offerLink);
        String genre = document.select(SELECTOR_GENRE).text();
        logger.debug("Got genre from page by offer link {'genre':{}, 'offerLink':{}}", genre, offerLink);

        rawOffer.setShopId(1);
        rawOffer.setRelease(release);
        rawOffer.setArtist(artist);
        rawOffer.setPrice(Double.parseDouble(priceNumber));
        rawOffer.setCurrency(Currency.getCurrency(priceCurrency));
        rawOffer.setGenre(genre);
        rawOffer.setOfferLink(offerLink);
        rawOffer.setImageLink(highResImageLink);
        logger.debug("Parsed page link {'offerLink':{}}", offerLink);
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
        HashSet<RawOffer> rawOfferSet = readRawOffersFromAllOfferLinks(offerLinks);
        logger.info("read {} rawOffers from all offer links", rawOfferSet.size());
        List<RawOffer> rawOffers = new ArrayList<>(rawOfferSet);
        logger.debug("Resulting list of raw offers from vinyl.ua is {'rawOffers':{}}", rawOffers);
        return rawOffers;
    }

}