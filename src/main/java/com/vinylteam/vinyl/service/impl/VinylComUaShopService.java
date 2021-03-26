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

public class VinylComUaShopService implements ShopService {

    HashSet<String> getGenreLinks() throws IOException {
        HashSet<String> genreLinks = new HashSet<>();
        Document doc = Jsoup.connect("https://vinyl.com.ua/vinyl/").get();
        Elements innerLinks = doc.getElementsByClass("popular-tag").select("a");
        for (Element innerLink : innerLinks) {
            String link = innerLink.attr("href");
            if (link.contains("-vinyl")) {
                genreLinks.add(link);
            }
        }
        return genreLinks;
    }

    HashSet<String> getPageLinks(HashSet<String> genreLinks) throws IOException {
        LinkedHashSet<String> pageLinks = new LinkedHashSet<>();
        for (String genreLink : genreLinks) {
            pageLinks.add(genreLink);
            Document doc = Jsoup.connect(genreLink).get();
            Elements nextPageLinks = doc.getElementsByClass("hidden-xs").select("a");
            for (Element link : nextPageLinks) {
                if (link.text().equals("2")) {
                    String nextPageLink = link.attr("href");
                    String idGenre = nextPageLink.replace("https://vinyl.com.ua/vinyl/?lt=2&page=2&d%5B3%5D%5B0%5D=", "");
                    String lastPageLink = genreLink + "/?page=100000&d[3][]=" + idGenre;
                    Document lastPageDoc = Jsoup.connect(lastPageLink).get();
                    int numberOfLastPage = Integer.parseInt(lastPageDoc.getElementsByClass("hidden-xs active").text());
                    for (int i = 2; i <= numberOfLastPage; i++) {
                        String necessaryNextLink = nextPageLink.replace("&page=2&", "&page=" + i + "&");
                        pageLinks.add(necessaryNextLink);
                    }
                }
            }
        }
        return pageLinks;
    }

    HashSet<Vinyl> readProductDataFromPage(HashSet<String> pageLinks) throws IOException {
        HashSet<Vinyl> dataOfProducts = new HashSet<>();
        String genre = "";
        for (String pageLink : pageLinks) {
            Document doc = Jsoup.connect(pageLink).get();
            Elements pageElements = doc.getElementsByClass("row modern");
            for (Element pageElement : pageElements) {
                String release = pageElement.getElementsByClass("sr-gallery-item-heading-title").text();
                //FIXME think about empty Artist
                String artist = "";
                String price = pageElement.getElementsByClass("pricik").text();
                String vinylLink = pageElement.getElementsByClass("sr-gallery-item-heading-title").select("a").attr("href");
                String imageLink = "https:"+pageElement.getElementsByClass("noblur-img").select("img").attr("src");
                if (!pageLink.contains("&page=")){
                    String[] linkComponents = pageLink.split("[/-]");
                    genre = linkComponents[3];
                }
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
        HashSet<String> genreLinks = getGenreLinks();
        HashSet<String> pageLinks = getPageLinks(genreLinks);
        HashSet<Vinyl> vinyls = readProductDataFromPage(pageLinks);
        return new ArrayList<>(vinyls);
    }
}
