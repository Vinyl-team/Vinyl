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

@Slf4j
public class DecksParser implements VinylParser {
    protected static final String BASE_LINK = "https://www.decks.de";
    private static final String START_PAGE_LINK = BASE_LINK + "/decks/workfloor/lists/list_db.php";

    private static final String SELECTOR_OFFERS_ANCHORS = "body.bodyWORKFLOOR > div.allVinyls > div#tabchktk.oneLine.firstOneLine.tablenorm2 > div.ATLBlock.ATLBlockwlist > div.AT_chaoj.ATBlock > div.LArtist.maxOneLine > a";

    @Override
    public List<RawOffer> getRawOffersList() {
        HashSet<String> genresLinks = getGenresLinks();
        HashSet<String> pageLinks = getPageLinks(genresLinks);
        HashSet<String> offerLinks = getOfferLinks(pageLinks);
        HashSet<RawOffer> rawOfferSet = readRawOffersFromAllOfferLinks(offerLinks);
        ArrayList<RawOffer> rawOffers = new ArrayList<>(rawOfferSet);
        return rawOffers;
    }

    @Override
    public RawOffer getRawOfferFromOfferLink(String offerLink) {
        RawOffer rawOffer = new RawOffer();
        Optional<Document> offerDocument = getDocument(offerLink);
        if (offerDocument.isPresent()) {
            rawOffer.setShopId(5);
            rawOffer.setRelease(getReleaseFromDocument(offerDocument.get()));
            rawOffer.setArtist(getArtistFromDocument(offerDocument.get()));
            rawOffer.setPrice(getPriceFromDocument(offerDocument.get()));
            rawOffer.setCurrency(getOptionalCurrencyFromDocument(offerDocument.get()));
            rawOffer.setGenre(getGenreFromDocument(offerDocument.get()));
            rawOffer.setOfferLink(offerLink);
            rawOffer.setImageLink(getHighResImageLinkFromDocument(offerDocument.get()));

        }
        return rawOffer;
    }

    HashSet<String> getGenresLinks() {
        HashSet<String> genreLinks = new HashSet<>();
        Optional<Document> startDocument = getDocument(START_PAGE_LINK);
        if (startDocument.isPresent()){
            Element iframeElement = startDocument.get().select("iframe").first();
            Elements elementsWithGenreLinks = iframeElement.parents().get(0).getElementsByClass("menueBodySub").select("a");
            for (Element elementWithGenreLink : elementsWithGenreLinks){
                String genresLink = elementWithGenreLink.attr("href");
                if (!genresLink.equals("javascript:void(0);")){
                    genreLinks.add(BASE_LINK + genresLink);
                }
            }
        }
        return genreLinks;
    }

    private String getHighResImageLinkFromDocument(Document offerDocument) {
        Element iframeElement = offerDocument.select("iframe").first();
        String imageLink = iframeElement.parents().get(0).getElementsByClass("bigCoverDetail").select("img").attr("src");
        return imageLink;
    }

    String getGenreFromDocument(Document offerDocument) {
        Element iframeElement = offerDocument.select("iframe").first();
        String genre = iframeElement.parents().get(0).getElementsByClass("LStylehead").text();
        return genre;
    }

    Optional<Currency> getOptionalCurrencyFromDocument(Document offerDocument) {
        Element iframeElement = offerDocument.select("iframe").first();
        String priceDetails = iframeElement.parents().get(0).getElementsByClass("preisschild").text();
        if (priceDetails.indexOf(' ') != -1){
            String currency = priceDetails.substring(priceDetails.indexOf(' ') + 1, priceDetails.indexOf('*') - 1);
            Optional<Currency> optionalCurrency = Currency.getCurrency(currency);
            return optionalCurrency;
        }
        return Optional.empty();
    }

    double getPriceFromDocument(Document offerDocument) {
        double price = 0.;
        Element iframeElement = offerDocument.select("iframe").first();
        String priceDetails = iframeElement.parents().get(0).getElementsByClass("preisschild").text();
        if (priceDetails.indexOf(' ') != -1){
            price = Double.parseDouble(priceDetails.substring(0, priceDetails.indexOf(' ')));
        }
        return price;
    }

    String getArtistFromDocument(Document offerDocument) {
        Element iframeElement = offerDocument.select("iframe").first();
        String artist = iframeElement.parents().get(0).getElementsByClass("detail_artist  lightCol mainColBG ").select("h1").text();
        return artist;
    }

    String getReleaseFromDocument(Document offerDocument){
        Element iframeElement = offerDocument.select("iframe").first();
        String release = iframeElement.parents().get(0).getElementsByClass("detail_titel  lightCol mainCol ").select("h1").text();
        return release;
    }

    public HashSet<RawOffer> readRawOffersFromAllOfferLinks(HashSet<String> offerLinks) {
        HashSet<RawOffer> rawOfferSet = new HashSet<>();
        for (String offerLink : offerLinks) {
            RawOffer rawOffer = getRawOfferFromOfferLink(offerLink);
            if (isValid(rawOffer)){
                rawOfferSet.add(rawOffer);
            } else {
                log.warn("Can't fill raw offer by offer link, not adding it to set {'rawOffer':{}, 'offerLink':{}}", rawOffer, offerLink);
            }
        }
        return rawOfferSet;
    }

    boolean isValid(RawOffer rawOffer) {
        boolean isValid = false;
        if (rawOffer.getPrice() != 0.
                && rawOffer.getCurrency().isPresent()
                && !("".equals(rawOffer.getRelease()))
                && rawOffer.getOfferLink() != null) {
            isValid = true;
        }
        return isValid;
    }

    public HashSet<String> getOfferLinks(HashSet<String> pageLinks) {
        HashSet<String> offerLinks = new HashSet<>();
        for (String pageLink : pageLinks){
            Optional<Document> pageDocument = getDocument(pageLink);
            if (pageDocument.isPresent()){
                Element iframeElement = pageDocument.get().select("iframe").first();
                Elements elementsWithOfferLinks = iframeElement.parents().get(0).getElementsByClass("cover1").select("a");
                for (Element elementWithOfferLink : elementsWithOfferLinks) {
                    String offerLink = elementWithOfferLink.attr("href");
                    if (offerLink.contains(BASE_LINK)){
                        offerLinks.add(offerLink);
                    }
                }
            }
        }
        return offerLinks;
    }

    HashSet<String> getPageLinks(HashSet<String> genresLinks) {
        LinkedHashSet<String> pageLinks = new LinkedHashSet<>();
        String pageCount;
        for (String genreLink : genresLinks){
            List<String> pageCountList = new ArrayList<>();
//            pageLinks.add(genreLink);
            Optional<Document> genreDocument = getDocument(genreLink);
            if (genreDocument.isPresent()){
                Element iframeElement = genreDocument.get().select("iframe").first();
                Elements elementsWithLinkToPages = iframeElement.parents().get(0).getElementsByClass("pager").select("a");
                String templateLink = START_PAGE_LINK + elementsWithLinkToPages.get(0).attr("href");
                for (Element elementWithLinkToPages : elementsWithLinkToPages){
                    pageCountList.add(elementWithLinkToPages.text());
                }
                if (pageCountList.size() > 1) {
                    pageCount = pageCountList.get(pageCountList.size() - 2);
                } else {
                    pageCount = pageCountList.get(pageCountList.size() - 1);
                }
                for (int i = 0; i < Integer.parseInt(pageCount); i++) {
                    pageLinks.add(templateLink.replace("aktuell=0", "aktuell=" + i));
                }
//                for (int i = 0; i < pageCount.get(pageCount.size()-1); i++) {
//
//                }
            }
        }
        return pageLinks;
    }

    private Optional<Document> getDocument(String url) {
        try {
//            Document document = Jsoup.connect(url).get();
//            Element element = document.select("iframe").first();
//            Elements bodyWORKFLOOR1 = element.parents().get(0).getElementsByClass("divlink");
//            ----
//            String iFrameSrc = BASE_LINK + element.attr("src");
//            Document iframeDocument = Jsoup.connect(iFrameSrc).get();
//            Elements bodyWORKFLOOR = iframeDocument.getElementsByClass("bodyWORKFLOOR");
//
//
//            Document iFrame = Jsoup.parse(elements.get(0).data());
            return Optional.ofNullable(Jsoup.connect(url).get());
        } catch (IOException e) {
            log.warn("Page represented by the link will be skipped, since some error happened while getting document by link {'link':{}}", url, e);
            return Optional.empty();
        }
    }
}
