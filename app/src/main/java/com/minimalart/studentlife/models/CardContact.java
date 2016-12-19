package com.minimalart.studentlife.models;

/**
 * Created by ytgab on 13.12.2016.
 */

public class CardContact {

    private final String DRAWABLE = "drawable/";
    private final String STRING = "@string/";

    private String title;
    private String description;
    private String imgUri;

    public CardContact(String title, String description, String imgUri) {
        this.title = title;
        this.description = description;
        this.imgUri = imgUri;
    }

    public String getTitle() {
        return STRING + title;
    }

    public String getDescription() {
        return STRING + description;
    }

    public String getImgUri() {
        return DRAWABLE + imgUri;
    }
}
