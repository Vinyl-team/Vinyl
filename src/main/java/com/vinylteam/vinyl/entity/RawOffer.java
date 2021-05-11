package com.vinylteam.vinyl.entity;

import java.util.Objects;
import java.util.Optional;

public class RawOffer {

    private int shopId;
    private String release;
    private String artist;
    private double price;
    private Optional<Currency> currency;
    private String catNumber;
    private String genre;
    private String offerLink;
    private String imageLink;

    public RawOffer() {
    }

    public RawOffer(RawOffer rawOffer) {
        this.shopId = rawOffer.shopId;
        this.release = rawOffer.release;
        this.artist = rawOffer.artist;
        this.price = rawOffer.price;
        this.currency = rawOffer.currency;
        this.catNumber = rawOffer.catNumber;
        this.genre = rawOffer.genre;
        this.offerLink = rawOffer.offerLink;
        this.imageLink = rawOffer.imageLink;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public String getRelease() {
        return release;
    }

    public void setRelease(String release) {
        this.release = release;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Optional<Currency> getCurrency() {
        return currency;
    }

    public void setCurrency(Optional<Currency> currency) {
        this.currency = currency;
    }

    public String getCatNumber() {
        return catNumber;
    }

    public void setCatNumber(String catNumber) {
        this.catNumber = catNumber;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getOfferLink() {
        return offerLink;
    }

    public void setOfferLink(String offerLink) {
        this.offerLink = offerLink;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RawOffer)) return false;
        RawOffer rawOffer = (RawOffer) o;
        return shopId == rawOffer.shopId &&
                Double.compare(rawOffer.price, price) == 0 &&
                Objects.equals(release, rawOffer.release) &&
                Objects.equals(artist, rawOffer.artist) &&
                Objects.equals(currency, rawOffer.currency) &&
                Objects.equals(catNumber, rawOffer.catNumber) &&
                Objects.equals(genre, rawOffer.genre) &&
                Objects.equals(offerLink, rawOffer.offerLink) &&
                Objects.equals(imageLink, rawOffer.imageLink);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shopId, release, artist, price, currency, catNumber, genre, offerLink, imageLink);
    }

    @Override
    public String toString() {
        return "\nRawOffer{" +
                "shopId=" + shopId +
                ", release='" + release + '\'' +
                ", artist='" + artist + '\'' +
                ", price=" + price +
                ", currency=" + currency +
                ", catNumber='" + catNumber + '\'' +
                ", genre='" + genre + '\'' +
                ", offerLink='" + offerLink + '\'' +
                ", imageLink='" + imageLink + '\'' +
                '}';
    }

}
