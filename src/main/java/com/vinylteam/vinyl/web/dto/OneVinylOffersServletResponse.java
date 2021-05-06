package com.vinylteam.vinyl.web.dto;
//TODO: Rename.
//TODO: fix imageLink if needed
public class OneVinylOffersServletResponse {
    private Double price;
    private String vinylLink;
    private String imageLink;

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getVinylLink() {
        return vinylLink;
    }

    public void setVinylLink(String vinylLink) {
        this.vinylLink = vinylLink;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }
}
