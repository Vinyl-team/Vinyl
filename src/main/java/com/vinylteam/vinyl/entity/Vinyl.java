package com.vinylteam.vinyl.entity;

import java.util.Objects;

public class Vinyl {

    private String release;
    private String artist;
    private String price;
    private String vinylLink;
    private String imageLink;
    private String genre;

    public void setRelease(String release) {
        this.release = release;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setPrice(String price) {
        this.price = price;
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

    public String getRelease() {
        return release;
    }

    public String getArtist() {
        return artist;
    }

    public String getPrice() {
        return price;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vinyl vinyl = (Vinyl) o;
        return Objects.equals(release, vinyl.release) &&
                Objects.equals(artist, vinyl.artist) &&
                Objects.equals(price, vinyl.price) &&
                Objects.equals(vinylLink, vinyl.vinylLink) &&
                Objects.equals(imageLink, vinyl.imageLink);
    }

    @Override
    public int hashCode() {
        return Objects.hash(release, artist, price, vinylLink, imageLink);
    }
}
