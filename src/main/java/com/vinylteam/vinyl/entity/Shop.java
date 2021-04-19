package com.vinylteam.vinyl.entity;

import java.util.Objects;

public class Shop {

    int id;
    String mainPageLink;
    String imageLink;
    String name;

public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMainPageLink() {
        return mainPageLink;
    }

    public void setMainPageLink(String mainPageLink) {
        this.mainPageLink = mainPageLink;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shop shop = (Shop) o;
        return id == shop.id &&
                Objects.equals(mainPageLink, shop.mainPageLink) &&
                Objects.equals(imageLink, shop.imageLink) &&
                Objects.equals(name, shop.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, mainPageLink, imageLink, name);
    }

    @Override
    public String toString() {
        return "Shop{" +
                "id=" + id +
                ", mainPageLink='" + mainPageLink + '\'' +
                ", imageLink='" + imageLink + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}