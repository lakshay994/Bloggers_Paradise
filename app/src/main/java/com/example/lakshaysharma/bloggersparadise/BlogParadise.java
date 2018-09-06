package com.example.lakshaysharma.bloggersparadise;

public class BlogParadise {

    private static String title, description,
                            imageURL, username;

    public BlogParadise() {
    }

    public BlogParadise(String title, String description, String imageURL, String username) {

        this.title = title;
        this.description = description;
        this.imageURL = imageURL;
        this.username = username;
    }

    public static void setTitle(String title) {
        BlogParadise.title = title;
    }

    public static void setDescription(String description) {
        BlogParadise.description = description;
    }

    public static void setImageURL(String imageURL) {
        BlogParadise.imageURL = imageURL;
    }

    public static void setUsername(String username) {
        BlogParadise.username = username;
    }

    public static String getTitle() {
        return title;
    }

    public static String getDescription() {
        return description;
    }

    public static String getImageURL() {
        return imageURL;
    }

    public static String getUsername() {
        return username;
    }
}
