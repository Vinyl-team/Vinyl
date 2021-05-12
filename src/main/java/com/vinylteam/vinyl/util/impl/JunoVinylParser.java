package com.vinylteam.vinyl.util.impl;

import com.vinylteam.vinyl.entity.Currency;
import com.vinylteam.vinyl.entity.RawOffer;
import com.vinylteam.vinyl.util.VinylParser;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toSet;

@Slf4j
public class JunoVinylParser implements VinylParser {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final String START_BASE_LINK = "https://www.juno.co.uk/all/";
    private final String START_LINK = START_BASE_LINK + "back-cat/2/?media_type=vinyl";

    private final String VINYL_ITEM_LIST_SELECTOR = "div.product-list";
    private final String PRICE_BLOCK_SELECTOR = "div.pl-buy";
    private final String PRICE_LINE_SELECTOR = "span.price_lrg.text-cta";
    private final String VINYL_ITEM_SELECTOR = "div.dv-item";
    private final String VINYL_IMAGE_LINK_SELECTOR = "img.lazy_img.img-fluid";
    private final String VINYL_INFO_BLOCK_SELECTOR = "div.pl-info";
    private final String VINYL_INFO_ITEMS_SELECTOR = "div.vi-text";

    private final Pattern PAGE_NUMBER_PATTERN = Pattern.compile("/([0-9]+)/");

    @Override
    public List<RawOffer> getRawOffersList() {
        Set<String> pageLinks = getPresentPageLinks();
        Set<RawOffer> rawOffersSet = readVinylsDataFromAllPages(pageLinks);
        List<RawOffer> rawOffersList = new ArrayList<>(rawOffersSet);
        logger.debug("Resulting list of vinyls from www.juno.co.uk is {'rawOffersList':{}}", rawOffersList);
        return rawOffersList;
    }

    @Override
    public RawOffer getRawOfferFromOfferLink(String offerLink) {
        return null;
    }

    Set<String> getPresentPageLinks() {
        var startDocument = getDocument(START_LINK);
        var pageLinksShownFromStartList = startDocument
                .stream()
                .flatMap(document -> document.select("a").stream())
                .filter(supposedPageLink -> supposedPageLink.text().matches("[0-9]+"))
                .filter(pageLink -> pageLink.attr("href").startsWith(START_BASE_LINK))
                .map(pageLink -> pageLink.attr("href"))
                .collect(toSet());
        return getFullPageLinksList(pageLinksShownFromStartList);
    }

    Set<String> getFullPageLinksList(Set<String> pageLinks) {
        int maxPageNumber = countPageLinks(pageLinks);
        logger.debug("Pages found {'maxPageNumber':{}}", maxPageNumber);
        var fullListOfPageLinks =
                IntStream.rangeClosed(1, maxPageNumber)
                        .mapToObj(pageNumber -> START_LINK.replaceAll(PAGE_NUMBER_PATTERN.toString(), "/" + pageNumber + "/"))
                        .collect(toSet());
        logger.info("Resulting set of page links (with no gaps in sequence) is {'pageLinks':{}}", pageLinks);
        return fullListOfPageLinks;
    }

    int countPageLinks(Set<String> pageLinks) {
        return pageLinks
                .stream()
                .map(PAGE_NUMBER_PATTERN::matcher)
                .filter(Matcher::find)
                .map(pageLinkMatcher -> pageLinkMatcher.group(1))
                .map(Integer::parseInt)
                .max(Comparator.naturalOrder())
                .orElse(0);
    }

    Set<RawOffer> readVinylsDataFromAllPages(Set<String> pageLinks) {
        var rawOfferSet = pageLinks
                .stream()
                .map(this::getDocument)
                .filter(Optional::isPresent)
                .map(document -> document.get().select(VINYL_ITEM_LIST_SELECTOR))
                .flatMap(vinylItemsList -> vinylItemsList.select(VINYL_ITEM_SELECTOR).stream())
                .flatMap(item -> this.itemToRawOffer(item).stream())
                .collect(toSet());
        logger.debug("Resulting set of raw offers is {'rawOfferSet':{}}", rawOfferSet);
        return rawOfferSet;
    }

    Optional<RawOffer> itemToRawOffer(Element item) {
        try {
            var imageLink = resolveVinylImageLink(item.select(VINYL_IMAGE_LINK_SELECTOR).get(0));
            var vinylLink = item.select("a").get(0).attr("href");
            var info = item.select(VINYL_INFO_BLOCK_SELECTOR);
            var infoDetails = info.select(VINYL_INFO_ITEMS_SELECTOR);
            var priceBlock = item.select(PRICE_BLOCK_SELECTOR);
            var priceString = priceBlock.select(PRICE_LINE_SELECTOR).text();
            var price = extractPrice(priceString);
            var priceCurrency = "GBP";
            var artist = infoDetails.get(0).select("a").text();
            var release = infoDetails.get(1).select("a").text();
            var genre = infoDetails.get(4).text();

            RawOffer rawOffer = new RawOffer();
            rawOffer.setShopId(2);
            rawOffer.setRelease(release);
            rawOffer.setArtist(artist);
            rawOffer.setPrice(price);
            rawOffer.setCurrency(Currency.getCurrency(priceCurrency));
            rawOffer.setOfferLink(vinylLink);
            rawOffer.setImageLink(imageLink);
            rawOffer.setGenre(genre);
            return Optional.of(rawOffer);
        } catch (Exception e) {
            logger.warn("Element will be skipped, since some error happened during RawOffer creation from the element: {}", item, e);
            return Optional.empty();
        }
    }

    String resolveVinylImageLink(Element img) {
        return img.attr("src").startsWith("http") ? img.attr("src") : img.attr("data-src");
    }

    double extractPrice(String priceString) {
        var tmpPrice = priceString.replaceAll("^[^0-9]+", "");
        return Double.parseDouble(tmpPrice);
    }

    private Optional<Document> getDocument(String url) {
        try {
            return Optional.ofNullable(Jsoup.connect(url).get());
        } catch (IOException e) {
            logger.warn("Page represented by the link will be skipped, since some error happened while getting document by link {'link':{}}", url, e);
            return Optional.empty();
        }
    }
}
