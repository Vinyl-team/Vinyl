package com.vinylteam.vinyl.util.impl;

import com.vinylteam.vinyl.entity.Currency;
import com.vinylteam.vinyl.entity.Vinyl;
import com.vinylteam.vinyl.util.VinylParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

public class VinylUaParser implements VinylParser {
    private final String startLink = "http://vinyl.ua";

    HashSet<String> getGenresLinks() throws IOException {
        HashSet<String> genreLinks = new HashSet<>();
        Document doc = Jsoup.connect(startLink).get();
        Elements innerLinks = doc.getElementsByClass("dropdown-menu dropdown-menu-left").select("a");
        for (Element innerLink : innerLinks) {
            String link = startLink + innerLink.attr("href");
            genreLinks.add(link);
        }
        return genreLinks;
    }

    HashSet<String> getPageLinks(HashSet<String> genreLinks) throws IOException {
        LinkedHashSet<String> pageLinks = new LinkedHashSet<>();
        for (String genreLink : genreLinks) {
            Document doc = Jsoup.connect(genreLink).get();
            Elements innerLinks = doc.getElementsByClass("pagination-wrapper margin-top-20").select("a");
            for (Element innerLink : innerLinks) {
                String link = startLink + innerLink.attr("href");
                if (link.contains("?page=") && !link.contains("ussr?page=2")) {
                    pageLinks.add(link);
                }
            }
        }
        return pageLinks;
    }

    HashSet<Vinyl> readVinylsDataFromAllPages(HashSet<String> pageLinks) throws IOException {
        HashSet<Vinyl> vinylsData = new HashSet<>();

        for (String pageLink : pageLinks) {
            Document doc = Jsoup.connect(pageLink).get();
            Elements pageElements = doc.getElementsByClass("vinyl-release showcase");
            for (Element pageElement : pageElements) {
                String release = pageElement.getElementsByClass("margin-top-clear margin-bot-5").text();
                String artist = pageElement.getElementsByClass("text-ellipsis").select("a").text();
                if (artist.equals("")) {
                    artist = "Various Artists";
                }
                String fullNameVinyl = release + " - " + artist;
                String priceDetails = pageElement.getElementsByClass("pull-left margin-top-5 showcase-release-price").text();
                String priceNumber = priceDetails.substring(0, priceDetails.indexOf(' '));
                String priceCurrency = priceDetails.substring(priceDetails.indexOf(' ') + 1);
                String vinylLink = startLink + pageElement.getElementsByClass("img-showcase-release").select("a").attr("href");
                String[] imageLinks = pageElement.getElementsByClass("img-showcase-release").attr("style").split("'");
                String imageLink = imageLinks[1];
                String[] linkComponents = pageLink.split("[/?]");
                String genre = linkComponents[4];

                Vinyl vinyl = new Vinyl();
                vinyl.setShopId(1);
                vinyl.setRelease(release);
                vinyl.setArtist(artist);
                vinyl.setFullNameVinyl(fullNameVinyl);
                vinyl.setPrice(Double.parseDouble(priceNumber));
                vinyl.setCurrency(Currency.getCurrency(priceCurrency));
                vinyl.setVinylLink(vinylLink);
                vinyl.setImageLink(imageLink);
                vinyl.setGenre(genre);
                vinylsData.add(vinyl);
            }
        }
        return vinylsData;
    }

    @Override
    public List<Vinyl> getAllVinylsFromShopList() throws IOException {
        HashSet<String> genresLinks = getGenresLinks();
        HashSet<String> pageLinks = getPageLinks(genresLinks);
        HashSet<Vinyl> vinyls = readVinylsDataFromAllPages(pageLinks);
        return new ArrayList<>(vinyls);
    }
}