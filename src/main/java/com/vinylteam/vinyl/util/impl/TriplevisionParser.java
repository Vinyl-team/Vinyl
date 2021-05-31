package com.vinylteam.vinyl.util.impl;

import com.vinylteam.vinyl.entity.Currency;
import com.vinylteam.vinyl.entity.RawOffer;
import com.vinylteam.vinyl.util.VinylParser;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.*;

@Slf4j
public class TriplevisionParser implements VinylParser {

    private static final String BASE_LINK = "https://www.triplevision.nl";
    private static final String START_PAGE_LINK = BASE_LINK + "/releases";
    private static final int SHOP_ID = 10;

    int i = 0;

    @Override
    public List<RawOffer> getRawOffersList() {
        HashSet<String> pageLinks = getPageLinks();
        HashSet<String> offerLinks = getOfferLinks(pageLinks);
        HashSet<RawOffer> rawOfferSet = readRawOffersFromAllOfferLinks(offerLinks);
        return new ArrayList<>(rawOfferSet);
    }

    @Override
    public RawOffer getRawOfferFromOfferLink(String offerLink) {
        RawOffer rawOffer = new RawOffer();
        Optional<Document> offerDocument = getDocument(offerLink);
        if (offerDocument.isPresent()) {
            rawOffer.setShopId(SHOP_ID);
            rawOffer.setRelease(getReleaseFromDocument(offerDocument.get()));
            rawOffer.setArtist(getArtistFromDocument(offerDocument.get()));
            rawOffer.setPrice(getPriceFromDocument(offerDocument.get()));
            rawOffer.setCurrency(getOptionalCurrencyFromDocument(offerDocument.get()));
            rawOffer.setGenre(getGenreFromDocument(offerDocument.get()));
            rawOffer.setCatNumber(getCatNumberFromDocument(offerDocument.get()));
            rawOffer.setInStock(getInStockInfoFromDocument(offerDocument.get()));
            rawOffer.setOfferLink(offerLink);
            rawOffer.setImageLink(getHighResImageLinkFromDocument(offerDocument.get()));
        }
        return rawOffer;
    }

    String getCatNumberFromDocument(Document offerDocument) {
        Element iframeElement = offerDocument.select("iframe").first();
        String[] catNumbers = iframeElement.parents().get(0).getElementsByClass("detail_label").text().split("[/ ]");
        String catNumber = catNumbers[catNumbers.length - 1];
        return catNumber;
    }

    boolean getInStockInfoFromDocument(Document offerDocument) {
        boolean inStock = true;
        Element iframeElement = offerDocument.select("iframe").first();
        String inStockText = iframeElement.parents().get(0).getElementsByClass("stockBlockaround").text();
        if ("out of stock".contains(inStockText)) {
            inStock = false;
        }
        return inStock;
    }

    String getHighResImageLinkFromDocument(Document offerDocument) {
        Element iframeElement = offerDocument.select("iframe").first();
        return iframeElement.parents().get(0).getElementsByClass("bigCoverDetail").select("img")
                .attr("src");
    }

    String getGenreFromDocument(Document offerDocument) {
        Element iframeElement = offerDocument.select("iframe").first();
        return iframeElement.parents().get(0).getElementsByClass("LStylehead").text();
    }

    Optional<Currency> getOptionalCurrencyFromDocument(Document offerDocument) {
        Element iframeElement = offerDocument.select("iframe").first();
        String priceDetails = iframeElement.parents().get(0).getElementsByClass("preisschild").text();
        if (priceDetails.indexOf(' ') != -1) {
            String currency = priceDetails.substring(priceDetails.indexOf(' ') + 1, priceDetails.indexOf('*') - 1);
            return Currency.getCurrency(currency);
        }
        return Optional.empty();
    }

    double getPriceFromDocument(Document offerDocument) {
        double price = 0.;
        Element iframeElement = offerDocument.select("iframe").first();
        String priceDetails = iframeElement.parents().get(0).getElementsByClass("preisschild").text();
        if (priceDetails.indexOf(' ') != -1) {
            price = Double.parseDouble(priceDetails.substring(0, priceDetails.indexOf(' ')));
        }
        return price;
    }

    String getArtistFromDocument(Document offerDocument) {
        Element iframeElement = offerDocument.select("iframe").first();
        return iframeElement.parents().get(0).getElementsByClass("detail_artist  lightCol mainColBG ")
                .select("h1").text();
    }

    String getReleaseFromDocument(Document offerDocument) {
        Element iframeElement = offerDocument.select("iframe").first();
        return iframeElement.parents().get(0).getElementsByClass("detail_titel  lightCol mainCol ")
                .select("h1").text();
    }

    public HashSet<RawOffer> readRawOffersFromAllOfferLinks(HashSet<String> offerLinks) {
        HashSet<RawOffer> rawOfferSet = new HashSet<>();
        for (String offerLink : offerLinks) {
            RawOffer rawOffer = getRawOfferFromOfferLink(offerLink);
            if (isValid(rawOffer)) {
                rawOfferSet.add(rawOffer);
            } else {
                log.warn("Can't fill raw offer by offer link, not adding it to set {'rawOffer':{}, 'offerLink':{}}",
                        rawOffer, offerLink);
            }
        }
        log.info("Got row offers from Decks.de: {}", rawOfferSet.size());
        return rawOfferSet;
    }

    boolean isValid(RawOffer rawOffer) {
        return rawOffer.getPrice() != 0d
                && rawOffer.getCurrency().isPresent()
                && !rawOffer.getRelease().isEmpty()
                && rawOffer.getOfferLink() != null;
    }

    public HashSet<String> getOfferLinks(HashSet<String> pageLinks) {
        HashSet<String> offerLinks = new HashSet<>();
        for (String pageLink : pageLinks) {
            Optional<Document> pageDocument = getDocument(pageLink);
            if (pageDocument.isPresent()) {
                Element iframeElement = pageDocument.get().select("iframe").first();
                Elements elementsWithOfferLinks = iframeElement.parents().get(0)
                        .getElementsByClass("cover1").select("a");
                for (Element elementWithOfferLink : elementsWithOfferLinks) {
                    String offerLink = elementWithOfferLink.attr("href");
                    if (offerLink.contains(BASE_LINK)) {
                        offerLinks.add(offerLink);
                    }
                }
            }
        }
        log.info("Got offer links from Triplevision.nl: {}", offerLinks.size());
        return offerLinks;
    }

    HashSet<String> getPageLinks() {
        LinkedHashSet<String> pageLinks = new LinkedHashSet<>();
        String nextPage = START_PAGE_LINK + "/";
//        int counter = 0;
        while (!nextPage.isEmpty()){
//            if (counter % 100 == 0){
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
            pageLinks.add(nextPage);
            Optional<Document> genreDocument = getDocument(nextPage);
            if (genreDocument.isPresent()) {
                nextPage = BASE_LINK + genreDocument.get()
                        .getElementsByClass("btn btn-secondary pagination__next d-none")
                        .attr("href");
                log.info("Got pages from Triplevision.nl: {}", pageLinks.size());
                if (nextPage.equals(BASE_LINK)){
                    nextPage = "";
                    log.info("Got last page from Triplevision.nl: {}", pageLinks.size());
                }
            }
//            counter++;
        }
        log.info("Got pages from Decks.de: {}", pageLinks.size());
        return pageLinks;
    }

    Optional<Document> getDocument(String url) {
        try {
//            URL link = new URL(url);
//            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("159.224.235.218", 443)); // or whatever your proxy is
//            HttpURLConnection uc = (HttpURLConnection)link.openConnection(proxy);
//
//            uc.connect();
//
//            String line = null;
//            StringBuffer tmp = new StringBuffer();
//            BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
//            while ((line = in.readLine()) != null) {
//                tmp.append(line);
//            }
//
//            return Optional.ofNullable(Jsoup.parse(String.valueOf(tmp)));
//            return Optional.ofNullable(Jsoup.connect(url)
//                    .proxy("127.0.0.1", 443)
//                    .userAgent("Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2")
//                    .header("Content-Language", "en-US")
//                    .get());
//            Proxy proxy = new Proxy(Proxy.Type.HTTP,
//                    new InetSocketAddress("159.224.235.218", 8080));
//
//            return Optional.ofNullable(Jsoup.connect(url)
//                    .proxy(proxy)
//                    .get());
            return Optional.ofNullable(Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11")
                    //.ignoreContentType(true)
                    //.followRedirects(true)
                    //.ignoreHttpErrors(true)
                    //.validateTLSCertificates(false)
                    //.timeout(TIME_OUT)
                    //.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("", 8080)))
                    .get());
        } catch (IOException e) {
            log.warn("Page represented by the link will be skipped, since some error happened while getting document" +
                    " by link {'link':{}}", url, e);
            return Optional.empty();
        }
    }

}