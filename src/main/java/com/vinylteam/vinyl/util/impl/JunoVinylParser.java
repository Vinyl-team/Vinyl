package com.vinylteam.vinyl.util.impl;

import com.vinylteam.vinyl.entity.Currency;
import com.vinylteam.vinyl.entity.RawOffer;
import com.vinylteam.vinyl.util.VinylParser;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toSet;

@Slf4j
public class JunoVinylParser implements VinylParser {

    private final String startBaseLink = "https://www.juno.co.uk/all/";
    private final String startLink = startBaseLink + "back-cat/2/?media_type=vinyl";

    private final String vinylItemsListSelector = "div.product-list";
    private final String priceBlockSelector = "div.pl-buy";
    private final String priceLineSelector = "span.price_lrg.text-cta";
    private final String vinylItemSelector = "div.dv-item";
    private final String imageLinkSelector = "img.lazy_img.img-fluid";
    private final String vinylInfoBlockSelector = "div.pl-info";
    private final String vinylInfoItemsSelector = "div.vi-text";

    private final Pattern pageNumberPattern = Pattern.compile("/([0-9]+)/");

    @Override
    public List<RawOffer> getRawOffersList() {
        Set<String> pageLinks = getPresentPageLinks();
        Set<RawOffer> rawOffers = readVinylsDataFromAllPages(pageLinks);
        List<RawOffer> junoRawOfferList = new ArrayList<>(rawOffers);
        log.debug("Resulting list of vinyls from www.juno.co.uk is {'junoRawOffersList':{}}", junoRawOfferList);
        return junoRawOfferList;
    }

    Set<String> getPresentPageLinks() {
        var startDocument = getDocument(startLink);
        var pages = startDocument
                .stream()
                .flatMap(document -> document.select("a").stream())
                .filter(supposedPageLink -> supposedPageLink.text().matches("[0-9]+"))
                .filter(pageLink -> pageLink.attr("href").startsWith(startBaseLink))
                .map(pageLink -> pageLink.attr("href"))
                .collect(toSet());
        pages = getFullPageLinksList(pages);
        return pages;
    }

    Set<String> getFullPageLinksList(Set<String> pageLinks) {
        int maxPageNumber = countPageLinks(pageLinks);
        var fullListOfPageLinks =
                IntStream.rangeClosed(1, maxPageNumber)
                        .mapToObj(pageNumber -> startLink.replaceAll(pageNumberPattern.toString(), "/" + pageNumber + "/"))
                        .collect(toSet());
        log.debug("Resulting hash set of page links is {'pageLinks':{}}", pageLinks);
        return fullListOfPageLinks;
    }

    int countPageLinks(Set<String> pageLinks) {
        return pageLinks
                .stream()
                .map(pageNumberPattern::matcher)
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
                .map(document -> document.get().select(vinylItemsListSelector))
                .flatMap(vinylItemsList -> vinylItemsList.select(vinylItemSelector).stream())
                .flatMap(item -> this.itemToRawOffer(item).stream())
                .collect(toSet());
        log.debug("Resulting set of raw offers is {'rawOfferSet':{}}", rawOfferSet);
        return rawOfferSet;
    }

    Optional<RawOffer> itemToRawOffer(Element item) {
        try {
            var imageLink = resolveVinylImageLink(item.select(imageLinkSelector).get(0));
            var vinylLink = item.select("a").get(0).attr("href");
            var info = item.select(vinylInfoBlockSelector);
            var infoDetails = info.select(vinylInfoItemsSelector);
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
            rawOffer.setOfferLink(vinylLink);
            rawOffer.setImageLink(imageLink);
            rawOffer.setGenre(genre);
            return Optional.of(rawOffer);
        } catch (Exception e) {
            log.error("Error during RawOffer creation from the HTML element {}", item, e);
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
            log.error("Error while getting document by link {'link':{}}", url, e);
            return Optional.empty();
        }
    }
}
