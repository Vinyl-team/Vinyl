package com.vinylteam.vinyl.util.impl;

import com.vinylteam.vinyl.entity.Currency;
import com.vinylteam.vinyl.entity.RawOffer;
import com.vinylteam.vinyl.util.VinylParser;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toSet;

@Slf4j
public class JunoVinylParser implements VinylParser {

    private final String startBaseLink = "https://www.juno.co.uk/all/";
    private final String startLink = startBaseLink + "back-cat/2/?media_type=RawOffer";

    private final String RawOfferItemsListSelector = "div.product-list";
    private final String priceBlockSelector = "div.pl-buy";
    private final String priceLineSelector = "span.price_lrg.text-cta";
    private final String RawOfferItemSelector = "div.dv-item";
    private final String imageLinkSelector = "img.lazy_img.img-fluid";
    private final String RawOfferInfoBlockSelector = "div.pl-info";
    private final String RawOfferInfoItemsSelector = "div.vi-text";

    private final Pattern pageNumberPattern = Pattern.compile("/([0-9]+)/");

    @Override
    public List<RawOffer> getRawOffersList() {
        Set<String> pageLinks = getPresentPageLinks();

        Set<RawOffer> RawOffers = readVinylsDataFromAllPages(pageLinks);
        List<RawOffer> RawOffersJunoRawOfferList = new ArrayList<>(RawOffers);
        log.debug("Resulting list of RawOffers from RawOffer.ua is {'RawOffersRawOfferUaList':{}}", RawOffersJunoRawOfferList);
        return RawOffersJunoRawOfferList;
    }

    Set<String> getPresentPageLinks() {
        var startDocument = getDocument(startLink);
        var pages = startDocument
                .map(document -> document.select("a"))
                .stream()
                .filter(supposedPageLink -> supposedPageLink.text().matches("[0-9]+"))
                .filter(pageLink -> pageLink.attr("href").startsWith(startBaseLink))
                .map(pageLink -> pageLink.attr("href"))
                .collect(toSet());
        pages = getFullPageLinksList(pages);
        return pages;
    }

    Set<String> getFullPageLinksList(Set<String> pageLinks) {
        Integer maxPageNumber = countPageLinks(pageLinks);
        var fullListOfPageLinks =
                IntStream.rangeClosed(1, maxPageNumber)
                        .mapToObj(pageNumber -> startLink.replaceAll(pageNumberPattern.toString(), "/" + pageNumber + "/"))
                        .collect(toSet());
        return fullListOfPageLinks;
    }

    Integer countPageLinks(Set<String> pageLinks) {
        var maxPageNumber = pageLinks
                .stream()
                .map(pageLink -> pageNumberPattern.matcher(pageLink))
                .filter(Matcher::find)
                .map(pageLinkMatcher -> pageLinkMatcher.group(1))
                .map(Integer::parseInt)
                .max(Comparator.naturalOrder())
                .orElse(0);
        return maxPageNumber;
    }

    Set<RawOffer> readVinylsDataFromAllPages(Set<String> pageLinks) {
        var rawOfferSet = pageLinks
                .stream()
                .map(this::getDocument)
                .filter(Optional::isPresent)
                .map(document -> document.get().select(RawOfferItemsListSelector))
                .flatMap(RawOfferItemsList -> RawOfferItemsList.select(RawOfferItemSelector).stream())
                .flatMap(item -> this.itemToRawOffer(item).stream())
                .collect(toSet());
        log.debug("Resulting set of rawOffer  is {'rawOfferSet':{}}", rawOfferSet);
        return rawOfferSet;
    }

    Optional<RawOffer> itemToRawOffer(Element item) {
        try {
            var imageLink = resolveProductImageLink(item.select(imageLinkSelector));
            var RawOfferLink = item.select("a").get(0).attr("href");
            var info = item.select(RawOfferInfoBlockSelector);
            var infoDetails = info.select(RawOfferInfoItemsSelector);
            var priceBlock = item.select(priceBlockSelector);
            var priceString = priceBlock.select(priceLineSelector).text();
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
            rawOffer.setOfferLink(RawOfferLink);
            rawOffer.setImageLink(imageLink);
            rawOffer.setGenre(genre);
            return Optional.of(rawOffer);
        } catch (Exception e) {
            log.error("Error during RawOffer creation from the HTML element {}", item, e);
            return Optional.ofNullable(null);
        }
    }

    private String resolveProductImageLink(Elements img) {
        return img.attr("src").startsWith("http") ? img.attr("src") : img.attr("data-src");
    }

    private double extractPrice(String priceString) {
        var tmpPrice = priceString.replaceAll("^[^0-9]+", "");
        return Double.parseDouble(tmpPrice);
    }

    private Optional<Document> getDocument(String url) {
        try {
            return Optional.ofNullable(Jsoup.connect(url).get());
        } catch (IOException e) {
            log.error("Error while getting document by link {'link':{}}", url, e);
            return Optional.ofNullable(null);
        }
    }
}
