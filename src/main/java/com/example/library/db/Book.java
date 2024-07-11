package com.example.library.db;

import java.net.URL;
import java.util.Date;

public class Book {
    private String name;
    private int count;
    private Date date_add;
    private String image;
    private String genre;
    private String author;
    private Date pub_year;
    private String pub_house;

    public Book(String name, int count, String image, String author) {
        this.name = name;
        this.count = count;
        this.image = image;
        this.author = author;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    public Date getDate_add() {
        return date_add;
    }

    public String getImage() {
        return image;
    }

    public String getGenre() {
        return genre;
    }

    public String getAuthor() {
        return author;
    }

    public Date getPub_year() {
        return pub_year;
    }

    public String getPub_house() {
        return pub_house;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setDate_add(Date date_add) {
        this.date_add = date_add;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setPub_year(Date pub_year) {
        this.pub_year = pub_year;
    }

    public void setPub_house(String pub_house) {
        this.pub_house = pub_house;
    }
}
