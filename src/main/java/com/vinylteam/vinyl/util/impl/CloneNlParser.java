package com.vinylteam.vinyl.util.impl;

import com.vinylteam.vinyl.entity.Currency;
import com.vinylteam.vinyl.entity.RawOffer;
import com.vinylteam.vinyl.util.PriceUtils;
import com.vinylteam.vinyl.util.VinylParser;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toSet;

@Slf4j
public class CloneNlParser implements VinylParser {

    protected static final String BASE_LINK = "https://clone.nl";
    private static final String CATALOG_ROOT_LINK = BASE_LINK + "/genres";
    private static final String START_PAGE_LINK = CATALOG_ROOT_LINK;
    private static final String GENRES_SELECTOR = "DIV > H1:contains(Genres) + P > A[href*=genre/]";
    private static final String PRELIMINARY_PAGE_LINK_SELECTOR = "UL.pagination > LI > A";

    private static final Pattern PAGE_NUMBER_PATTERN = Pattern.compile("&page=([0-9]+)");
    private static final String OFFER_LIST_SELECTOR = "DIV.content-container > DIV.main-content";

    private static final String ONE_VINYL_SELECTOR = "DIV.release";
    private static final String ONE_VINYL_FROM_ONE_PAGE_SELECTOR = "DIV.musicrelease";
    private static final int SHOP_ID = 4;

    private final AtomicInteger documentCounter = new AtomicInteger(0);

    private final DetailedVinylParser batchParser = new DefaultDetailedVinylParser();
    private final DetailedVinylParser onePageParser = new OnePageDetailedVinylParser();

    @Override
    public List<RawOffer> getRawOffersList() {
        Set<String> allGenres = getAllGenreLinks();
        Set<String> pageLinks = getAllPagesByGenres(allGenres);
        log.info("got page links {'pageLinks':{}}", pageLinks.size());

        Set<RawOffer> rawOffersSet = readOffersFromAllPages(pageLinks);

        log.info("Read {} rawOffers from all offer pages", rawOffersSet.size());
        List<RawOffer> rawOffersList = new ArrayList<>(rawOffersSet);
        log.debug("Resulting list of vinyls from www.juno.co.uk is {'rawOffersList':{}}", rawOffersList);
        return rawOffersList;
    }

    @Override
    public RawOffer getRawOfferFromOfferLink(String offerLink) {
        return getDocument(offerLink)
                .stream()
                .flatMap(doc -> doc.select(ONE_VINYL_FROM_ONE_PAGE_SELECTOR).stream())
                .map(oneVinyl -> getRawOfferFromElement(oneVinyl, onePageParser))
                .peek(rawOffer -> rawOffer.setOfferLink(offerLink))
                .findFirst()
                .orElse(new RawOffer());
    }

    Set<String> getAllPagesByGenre(String genreLink) {
        var allPages = getDocument(genreLink)
                .stream()
                .flatMap(document -> document.select(PRELIMINARY_PAGE_LINK_SELECTOR).stream())
                .filter(supposedPageLink -> supposedPageLink.text().matches("[0-9]+"))
                .map(pageLink -> pageLink.attr("href"))
                .collect(toSet());
        if (allPages.isEmpty()) {
            return Set.of(genreLink);
        }
        return getAllPageLinksSet(allPages);
    }

    Set<String> getAllPagesByGenres(Set<String> allGenres) {
        return allGenres
                .stream()
                .map(link -> link.replace("/all/", "/instock/"))
                .flatMap(genre -> getAllPagesByGenre(genre).stream())
                .collect(toSet());
    }

    @Override
   public boolean isValid(RawOffer rawOffer) {
        //!rawOffer.isEmpty
        //Default for all parsers
        boolean isValid = rawOffer.getPrice() != 0d
                && rawOffer.getCurrency().isPresent()
                && !rawOffer.getRelease().isEmpty()
                && rawOffer.getOfferLink() != null;
        if (!isValid) {
            log.error("Raw offer isn't valid {'rawOffer':{}}", rawOffer);
        }
        return isValid;
    }

    Set<RawOffer> readOffersFromAllPages(Set<String> pageLinks) {
        var offerLinks = pageLinks
                .stream()
                .flatMap(link -> this.getDocument(link).stream())
                .map(document -> document.select(OFFER_LIST_SELECTOR))
                .flatMap(offersList -> offersList.select(ONE_VINYL_SELECTOR).stream())
                .map((Element releaseElement) -> getRawOfferFromElement(releaseElement, batchParser))
                .filter(this::isValid)
                .collect(toSet());
        log.debug("Resulting set of offer links is {'offerLinks':{}}", offerLinks);
        return offerLinks;
    }

    Set<String> getAllPageLinksSet(Set<String> pageLinks) {
        int maxPageNumber = countPageLinks(pageLinks);
        var pageLinkPattern = pageLinks.iterator().next();
        log.debug("Pages found {'maxPageNumber':{}}", maxPageNumber);
        var fullListOfPageLinks =
                IntStream.rangeClosed(1, maxPageNumber)
                        .mapToObj(pageNumber -> pageLinkPattern.replaceAll(PAGE_NUMBER_PATTERN.toString(), "&page=" + pageNumber))
                        .collect(toSet());
        log.debug("Resulting set of page links (with no gaps in sequence) is {'pageLinks':{}}", pageLinks);
        return fullListOfPageLinks;
    }

    Set<String> getAllGenreLinks() {
        var startDocument = getDocument(START_PAGE_LINK);
        var allGenresLinks = startDocument
                .stream()
                .flatMap(document -> document.select(GENRES_SELECTOR).stream())
                .map(link -> BASE_LINK + link.attr("href"))
                .collect(toSet());
        log.debug("Got genres links {'allGenresLinks':{}}", allGenresLinks);
        log.info("Got genres links totally: {'allGenresLinks':{}}", allGenresLinks.size());
        return allGenresLinks;
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

    public RawOffer getRawOfferFromElement(Element releaseElement, DetailedVinylParser detailedParser) {
        var imageLink = detailedParser.getHighResImageLinkFromDocument(releaseElement);
        var offerLink = detailedParser.getOfferLinkFromDocument(releaseElement);
        var price = detailedParser.getPriceFromDocument(releaseElement);
        var priceCurrency = detailedParser.getOptionalCurrencyFromDocument(releaseElement);
        var artist = detailedParser.getArtistFromDocument(releaseElement);
        var release = detailedParser.getReleaseFromDocument(releaseElement);
        var genre = detailedParser.getGenreFromDocument(releaseElement);
        var catalogNumber = detailedParser.getCatNumberFromDocument(releaseElement);
        var inStock = detailedParser.getInStockInfoFromDocument(releaseElement);

        var rawOffer = new RawOffer();
        rawOffer.setShopId(SHOP_ID);
        rawOffer.setRelease(release);
        rawOffer.setArtist(artist);
        rawOffer.setPrice(price);
        rawOffer.setCurrency(priceCurrency);
        rawOffer.setOfferLink(offerLink);
        rawOffer.setImageLink(imageLink);
        rawOffer.setGenre(genre);
        rawOffer.setCatNumber(catalogNumber);
        rawOffer.setInStock(inStock);
        log.debug("New Raw Offer is Formed {'rawOffer': {}}", rawOffer);
        return rawOffer;
    }

    Optional<Document> getDocument(String url) {
        try {
            documentCounter.addAndGet(1);
            log.info("Document {} was read", documentCounter.get());
            return Optional.ofNullable(Jsoup.connect(url).get());
        } catch (IOException e) {
            log.warn("Page represented by the link will be skipped, since some error happened while getting document by link {'link':{}, 'totalDocuments':{}}", url, documentCounter.get(), e);
            return Optional.empty();
        }
    }

    @Override
    public long getShopId() {
        return SHOP_ID;
    }

    public interface DetailedVinylParser {

        String getGenreFromDocument(Element document);

        String getReleaseFromDocument(Element document);

        String getArtistFromDocument(Element document);

        String getCatNumberFromDocument(Element document);

        Boolean getInStockInfoFromDocument(Element document);

        Optional<Currency> getOptionalCurrencyFromDocument(Element document);

        double getPriceFromDocument(Element document);

        String getHighResImageLinkFromDocument(Element document);

        String getOfferLinkFromDocument(Element document);

    }

    public static class DefaultDetailedVinylParser implements DetailedVinylParser {

        private static final String HIGH_RES_IMAGE_LINK_SELECTOR = "DIV.release IMG";
        private static final String OFFER_LINK_SELECTOR = "DIV.coverimage > A";
        private static final String ARTIST_SELECTOR = "DIV.description > H2 > A";
        private static final String RELEASE_SELECTOR = "DIV.description > H2 + H3 > A";
        private static final String VINYL_GENRES_SELECTOR = "DIV.tagsbuttons > A.label";
        private static final String PRICE_DETAILS_SELECTOR = "DIV.release TABLE.availability A.addtocart";

        @Override
        public String getGenreFromDocument(Element document) {
            return document.select(VINYL_GENRES_SELECTOR).text();
        }

        public String getReleaseFromDocument(Element document) {
            return document.select(RELEASE_SELECTOR).text();
        }

        public String getArtistFromDocument(Element document) {
            return document.select(ARTIST_SELECTOR).text();
        }

        public String getCatNumberFromDocument(Element document) {
            return document.select("span[itemprop=catalogNumber]").text();
        }

        public Boolean getInStockInfoFromDocument(Element document) {
            var inStock = true;
            String inStockText = document.getElementsByClass("col-xs-2 status").text();
            if ("out of stock".contains(inStockText)) {
                inStock = false;
            }
            return inStock;
        }

        public Optional<Currency> getOptionalCurrencyFromDocument(Element document) {
            var pricesBlock = document.select(PRICE_DETAILS_SELECTOR).eachText();
            if (pricesBlock.isEmpty()) {
                return Optional.empty();
            }
            var fullPriceDetails = pricesBlock.get(0);
            log.debug("Got price details from page by offer link {'priceDetails':{}, 'offerLink':{}}", fullPriceDetails, document.ownerDocument().location());
            return PriceUtils.getCurrencyFromString(fullPriceDetails);
        }

        public double getPriceFromDocument(Element document) {
            var pricesBlock = document.select(PRICE_DETAILS_SELECTOR).eachText();
            if (pricesBlock.isEmpty()) {
                return 0d;
            }
            var fullPriceDetails = pricesBlock.get(0);
            log.debug("Got price details from page by offer link {'priceDetails':{}, 'offerLink':{}}", fullPriceDetails, document.ownerDocument().location());
            return PriceUtils.getPriceFromString(fullPriceDetails);
        }

        public String getHighResImageLinkFromDocument(Element document) {
            return document.select(HIGH_RES_IMAGE_LINK_SELECTOR).attr("src");
        }

        public String getOfferLinkFromDocument(Element document) {
            return BASE_LINK + document.select(OFFER_LINK_SELECTOR).attr("href");
        }
    }

    public static class OnePageDetailedVinylParser implements DetailedVinylParser {

        private static final String HIGH_RES_IMAGE_LINK_SELECTOR = "DIV.release IMG";
        private static final String ARTIST_SELECTOR = "H1[itemprop=author] > A";
        private static final String RELEASE_SELECTOR = "H1[itemprop=author] + H2";
        private static final String VINYL_GENRES_SELECTOR = "DIV.tagsbuttons > A.label";
        private static final String PRICE_DETAILS_SELECTOR = "DIV.release TABLE.availability A.addtocart";

        @Override
        public String getGenreFromDocument(Element document) {
            return document.select(VINYL_GENRES_SELECTOR).text();
        }

        public String getReleaseFromDocument(Element document) {
            return document.select(RELEASE_SELECTOR).text();
        }

        public String getArtistFromDocument(Element document) {
            return document.select(ARTIST_SELECTOR).text();
        }

        public String getCatNumberFromDocument(Element document) {
            return document.select("span[itemprop=catalogNumber]").text();
        }

        public Boolean getInStockInfoFromDocument(Element document) {
            var inStock = true;
            String inStockText = document.getElementsByClass("col-xs-2 status").text();
            if ("out of stock".contains(inStockText)) {
                inStock = false;
            }
            return inStock;
        }

        public Optional<Currency> getOptionalCurrencyFromDocument(Element document) {
            var pricesBlock = document.select(PRICE_DETAILS_SELECTOR).eachText();
            if (pricesBlock.isEmpty()) {
                return Optional.empty();
            }
            var fullPriceDetails = pricesBlock.get(0);
            log.debug("Got price details from page by offer link {'priceDetails':{}, 'offerLink':{}}", fullPriceDetails, document.ownerDocument().location());
            return PriceUtils.getCurrencyFromString(fullPriceDetails);
        }

        public double getPriceFromDocument(Element document) {
            var pricesBlock = document.select(PRICE_DETAILS_SELECTOR).eachText();
            if (pricesBlock.isEmpty()) {
                return 0d;
            }
            var fullPriceDetails = pricesBlock.get(0);
            log.debug("Got price details from page by offer link {'priceDetails':{}, 'offerLink':{}}", fullPriceDetails, document.ownerDocument().location());
            return PriceUtils.getPriceFromString(fullPriceDetails);
        }

        public String getHighResImageLinkFromDocument(Element document) {
            return document.select(HIGH_RES_IMAGE_LINK_SELECTOR).attr("src");
        }

        public String getOfferLinkFromDocument(Element document) {
            return "";
        }
    }
}
