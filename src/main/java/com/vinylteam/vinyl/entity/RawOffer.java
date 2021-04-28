package com.vinylteam.vinyl.entity;

import java.util.Optional;

public class RawOffer {

    private int shopId;
    private String release;
    private String artist;
    private Double price;
    private Optional<Currency> currency;
    private String genre;
    private String offerLink;
    private String imageLink;

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public void setRelease(String release) {
        this.release = release;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setCurrency(Optional<Currency> currency) {
        this.currency = currency;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setOfferLink(String offerLink) {
        this.offerLink = offerLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public int getShopId() {
        return shopId;
    }

    public String getRelease() {
        return release;
    }

    public String getArtist() {
        return artist;
    }

    public Double getPrice() {
        return price;
    }

    public Optional<Currency> getCurrency() {
        return currency;
    }

    public String getGenre() {
        return genre;
    }

    public String getOfferLink() {
        return offerLink;
    }

    public String getImageLink() {
        return imageLink;
    }

    @Override
    public String toString() {
        return "\nRawOffer{" +
                "shopId=" + shopId +
                ", release='" + release + '\'' +
                ", artist='" + artist + '\'' +
                ", price=" + price +
                ", currency=" + currency +
                ", offerLink='" + offerLink + '\'' +
                ", imageLink='" + imageLink + '\'' +
                ", genre='" + genre + '\'' +
                '}';
    }
}
