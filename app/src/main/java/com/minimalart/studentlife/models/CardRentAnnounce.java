package com.minimalart.studentlife.models;

import java.io.Serializable;

/**
 * Created by ytgab on 17.12.2016.
 */

public class CardRentAnnounce implements Serializable {
    private String userUID;
    private String title;
    private String rooms;
    private String price;
    private String location;
    private String description;
    private boolean checked;
    private String announceID;

    public CardRentAnnounce(){

    }

    public CardRentAnnounce(String userUID, String title, String rooms, String price, String location, String description, boolean checked) {
        this.userUID = userUID;
        this.title = title;
        this.rooms = rooms;
        this.price = price;
        this.location = location;
        this.description = description;
        this.checked = checked;
    }

    public String getUserUID() {
        return userUID;
    }

    public String getTitle() {
        return title;
    }

    public String getRooms() {
        return rooms;
    }

    public String getPrice() {
        return price;
    }

    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public boolean isChecked() {
        return checked;
    }

    public String getAnnounceID() {
        return announceID;
    }

    public void setAnnounceID(String announceID) {
        this.announceID = announceID;
    }
}
