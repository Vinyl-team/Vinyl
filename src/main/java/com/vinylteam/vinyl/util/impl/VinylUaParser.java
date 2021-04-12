package com.vinylteam.vinyl.util.impl;

import com.vinylteam.vinyl.entity.Currency;
import com.vinylteam.vinyl.entity.Vinyl;
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
    private final String classContainingVinyl = "vinyl-release showcase";

    HashSet<String> getGenresLinks() throws IOException {
        logger.debug("Start of function VinylUaParser.getGenresLinks()");
        HashSet<String> genreLinks = new HashSet<>();
        logger.debug("Created and initialized HashSet<String> object for storing genre links " +
                "{'genreLinks':{}}", genreLinks);
        Document document = Jsoup.connect(startLink).get();
        logger.debug("Got document out of start link " +
                "{'startLink':{}, 'document':{}", startLink, document);
        Elements innerAnchors = document.getElementsByClass(classContainingGenresLinks).select("a");
        logger.debug("Got collection of anchors from container elements with class " +
                "{'containerClass':{}, 'anchors':{}}", classContainingGenresLinks, innerAnchors);
        for (Element anchor : innerAnchors) {
            String anchorLink = anchor.attr("href");
            String link = startLink + anchorLink;
            genreLinks.add(link);
            logger.debug("Added link created by combining start link and anchor's link " +
                    "to hash set of genre links " +
                    "{'startLink':{}, 'anchorLink':{}, 'link':{}}", startLink, anchorLink, link);
        }
        logger.debug("Resulting hash set of genre links is " +
                "{'genreLinks':{}}", genreLinks);
        return genreLinks;
    }

    HashSet<String> getPageLinks(HashSet<String> genreLinks) throws IOException {
        logger.debug("Start of function VinylUaParser.getPageLinks(HashSet<String> genreLinks) with " +
                "{'genreLinks':{}}", genreLinks);
        LinkedHashSet<String> pageLinks = new LinkedHashSet<>();
        logger.debug("Created and initialized HashSet<String> object for storing page links " +
                "from each webpage by genre links" +
                "{'pageLinks':{}, 'genreLinks':{}}", pageLinks, genreLinks);
        for (String genreLink : genreLinks) {
            Document document = Jsoup.connect(genreLink).get();
            logger.debug("Got document out of genre link " +
                    "{'genreLink':{}, 'document':{}", genreLink, document);
            Elements innerAnchors = document.getElementsByClass(classContainingPagesLinks).select("a");
            logger.debug("Got collection of anchors from container elements with class " +
                    "{'containerClass':{}, 'anchors':{}}", classContainingPagesLinks, innerAnchors);
            for (Element anchor : innerAnchors) {
                String anchorLink = anchor.attr("href");
                String link = startLink + anchorLink;
                if (link.contains("?page=") && !link.contains("ussr?page=2")) {
                    pageLinks.add(link);
                    logger.debug("Added link created by combining start link and anchor's link " +
                            "to hash set of page links " +
                            "{'startLink':{}, 'anchorLink':{}, 'link':{}}", startLink, anchorLink, link);
                }
            }
        }
        logger.debug("Resulting hash set of page links is " +
                "{'pageLinks':{}}", pageLinks);
        return pageLinks;
    }

    HashSet<Vinyl> readVinylsDataFromAllPages(HashSet<String> pageLinks) throws IOException {
        logger.debug("Start of function VinylUaParser.readVinylsDataFromAllPages(HashSet<String> pageLinks) with " +
                "{'pageLinks':{}}", pageLinks);
        HashSet<Vinyl> vinyls = new HashSet<>();
        logger.debug("Created and initialized HashSet<Vinyl> object for storing vinyls " +
                "{'vinyls':{}}", vinyls);
        for (String pageLink : pageLinks) {
            Document document = Jsoup.connect(pageLink).get();
            logger.debug("Got document out of page link " +
                    "{'pageLink':{}, 'document':{}", pageLink, document);
            Elements vinylElements = document.getElementsByClass(classContainingVinyl);
            logger.debug("Got collection of vinyl elements from container elements with class " +
                    "{'containerClass':{}, 'vinylElements':{}}", classContainingVinyl, vinylElements);
            for (Element vinylElement : vinylElements) {
                String release = vinylElement.getElementsByClass("margin-top-clear margin-bot-5").text();
                String artist = vinylElement.getElementsByClass("text-ellipsis").select("a").text();
                if (artist.equals("")) {
                    artist = "Various Artists";
                }
                String fullNameVinyl = release + " - " + artist;
                String priceDetails = vinylElement.getElementsByClass("pull-left margin-top-5 showcase-release-price").text();
                String priceNumber = priceDetails.substring(0, priceDetails.indexOf(' '));
                String priceCurrency = priceDetails.substring(priceDetails.indexOf(' ') + 1);
                String vinylLink = startLink + vinylElement.getElementsByClass("img-showcase-release").select("a").attr("href");
                String[] imageLinks = vinylElement.getElementsByClass("img-showcase-release").attr("style").split("'");
                String imageLink = imageLinks[1];
                String[] linkComponents = pageLink.split("[/?]");
                String genre = linkComponents[4];

                Vinyl vinyl = new Vinyl();
                logger.debug("Created and initialized Vinyl object for storing vinyl from vinyl element " +
                        "{'vinyl':{}, 'vinylElement':{}}", vinyl, vinylElement);
                vinyl.setShopId(1);
                vinyl.setRelease(release);
                vinyl.setArtist(artist);
                vinyl.setFullNameVinyl(fullNameVinyl);
                vinyl.setPrice(Double.parseDouble(priceNumber));
                vinyl.setCurrency(Currency.getCurrency(priceCurrency));
                vinyl.setVinylLink(vinylLink);
                vinyl.setImageLink(imageLink);
                vinyl.setGenre(genre);
                vinyls.add(vinyl);
                logger.debug("Added vinyl filled with processed data from parsed vinylElement " +
                        "to hash set of all vinyls " +
                        "{'vinyl':{}}", vinyl);
            }
        }
        logger.debug("Resulting hash set of vinyls is " +
                "{'vinyls':{}}", vinyls);
        return vinyls;
    }

    @Override
    public List<Vinyl> getAllVinylsFromShopList() throws IOException {
        logger.debug("Start of function VinylUaParser.getAllVinylsFromShopList()");
        HashSet<String> genresLinks = getGenresLinks();
        HashSet<String> pageLinks = getPageLinks(genresLinks);
        HashSet<Vinyl> vinyls = readVinylsDataFromAllPages(pageLinks);
        List<Vinyl> vinylsVinylUaList = new ArrayList<>(vinyls);
        logger.debug("Resulting list of vinyls from vinyl.ua is " +
                "{'vinylsVinylUaList':{}", vinylsVinylUaList);
        return vinylsVinylUaList;
    }
}