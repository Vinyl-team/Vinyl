package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.entity.Vinyl;
import com.vinylteam.vinyl.service.ShopService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

public class VinylShopService implements ShopService {
    private final String startLink = "http://vinyl.ua";

    HashSet<String> getShowcaseLinks() throws IOException {
        HashSet<String> showcaseLinks = new HashSet<>();
        Document doc = Jsoup.connect(startLink).get();
        Elements innerLinks;
        innerLinks = doc.getElementsByTag("a");

        for (Element innerLink : innerLinks) {
            String link;
            if (innerLink.attr("href").contains("www") || innerLink.attr("href").contains("http")) {
                link = innerLink.attr("href");
            } else {
                link = startLink + innerLink.attr("href");
            }
            if (link.contains("http://vinyl.ua/showcase/")) {
                showcaseLinks.add(link);
            }
        }

        return showcaseLinks;
    }

    HashSet<String> getPageLinks(HashSet<String> showcaseLinks) throws IOException {
        LinkedHashSet<String> pageLinks = new LinkedHashSet<>();

        for (String showcaseLink : showcaseLinks) {
            Document doc = Jsoup.connect(showcaseLink).get();
            Elements innerLinks = doc.getElementsByTag("a");

            for (Element innerLink : innerLinks) {
                String link;
                if (innerLink.attr("href").contains("www") || innerLink.attr("href").contains("http")) {
                    link = innerLink.attr("href");
                } else {
                    link = startLink + innerLink.attr("href");
                }
                if (link.contains("?page=") && !link.contains("ussr?page=2")) {
                    pageLinks.add(link);
                }
            }
        }

        return pageLinks;
    }

    HashSet<Vinyl> readProductDataFromPage(HashSet<String> pageLinks) throws IOException {
        HashSet<Vinyl> dataOfProducts = new HashSet<>();

        for (String pageLink : pageLinks) {
            Document doc = Jsoup.connect(pageLink).get();
            Elements pageElements = doc.getElementsByClass("vinyl-release showcase");
            for (Element pageElement : pageElements) {
                String release = pageElement.getElementsByClass("margin-top-clear margin-bot-5").text();
                String artist = pageElement.getElementsByClass("text-ellipsis").select("a").text();

                //FIXME http://vinyl.ua/release/567/various-phatmix-volume-2 - there is problem, because there is no teg <a>, because there is no name Artist
                if (artist.equals("")) {
                    artist = pageElement.getElementsByClass("text-ellipsis").select("span").text();
                }

                String price = pageElement.getElementsByClass("pull-left margin-top-5 showcase-release-price").text();
                String vinylLink = startLink + pageElement.getElementsByClass("img-showcase-release").select("a").attr("href");
                String[] imageLinks = pageElement.getElementsByClass("img-showcase-release").attr("style").split("'");
                String imageLink = imageLinks[1];
                String[] linkComponents = pageLink.split("[/?]");
                String genre = linkComponents[4];

                Vinyl vinyl = new Vinyl();
                vinyl.setRelease(release);
                vinyl.setArtist(artist);
                vinyl.setPrice(price);
                vinyl.setVinylLink(vinylLink);
                vinyl.setImageLink(imageLink);
                vinyl.setGenre(genre);
                dataOfProducts.add(vinyl);
            }
        }

        return dataOfProducts;
    }

    @Override
    public List<Vinyl> getDataProduct() throws IOException {
        HashSet<String> showcaseLinks = getShowcaseLinks();
        HashSet<String> pageLinks = getPageLinks(showcaseLinks);
        HashSet<Vinyl> vinyls = readProductDataFromPage(pageLinks);
        return new ArrayList<>(vinyls);
    }
}