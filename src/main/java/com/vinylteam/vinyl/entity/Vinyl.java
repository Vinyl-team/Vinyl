package com.vinylteam.vinyl.entity;

import java.util.Objects;
import java.util.Optional;

public class Vinyl {

    private long vinylId;
    private int shopId;
    private long uniqueVinylId;
    private String release;
    private String artist;
    private String fullNameVinyl;
    private Double price;
    private Optional<Currency> currency;
    private String vinylLink;
    private String imageLink;
    private String genre;

    public void setVinylId(long vinylId) {
        this.vinylId = vinylId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public void setUniqueVinylId(long uniqueVinylId) {
        this.uniqueVinylId = uniqueVinylId;
    }

    public void setRelease(String release) {
        this.release = release;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setFullNameVinyl(String fullNameVinyl) {
        this.fullNameVinyl = fullNameVinyl;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setCurrency(Optional<Currency> currency) {
        this.currency = currency;
    }

    public void setVinylLink(String vinylLink) {
        this.vinylLink = vinylLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public long getVinylId() {
        return vinylId;
    }

    public int getShopId() {
        return shopId;
    }

    public long getUniqueVinylId() {
        return uniqueVinylId;
    }

    public String getRelease() {
        return release;
    }

    public String getArtist() {
        return artist;
    }

    public String getFullNameVinyl() {
        return fullNameVinyl;
    }

    public Double getPrice() {
        return price;
    }

    public Optional<Currency> getCurrency() {
        return currency;
    }

    public String getVinylLink() {
        return vinylLink;
    }

    public String getImageLink() {
        return imageLink;
    }

    public String getGenre() {
        return genre;
    }

    @Override
    public String toString() {
        return "Vinyl{" +
                "vinylId=" + vinylId +
                ", shopId=" + shopId +
                ", uniqueVinylId=" + uniqueVinylId +
                ", release='" + release + '\'' +
                ", artist='" + artist + '\'' +
                ", fullNameVinyl='" + fullNameVinyl + '\'' +
                ", price='" + price + '\'' +
                ", currency='" + currency + '\'' +
                ", vinylLink='" + vinylLink + '\'' +
                ", imageLink='" + imageLink + '\'' +
                ", genre='" + genre + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vinyl vinyl = (Vinyl) o;
        return vinylId == vinyl.vinylId &&
                shopId == vinyl.shopId &&
                uniqueVinylId == vinyl.uniqueVinylId &&
                release.equals(vinyl.release) &&
                artist.equals(vinyl.artist) &&
                fullNameVinyl.equals(vinyl.fullNameVinyl) &&
                vinylLink.equals(vinyl.vinylLink) &&
                imageLink.equals(vinyl.imageLink);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vinylId, shopId, uniqueVinylId, release, artist, fullNameVinyl, vinylLink, imageLink);
    }


}
