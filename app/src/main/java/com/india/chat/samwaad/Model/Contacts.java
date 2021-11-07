package com.india.chat.samwaad.Model;

public class Contacts {

    private String name;
    private String phone_number;
    private String ImageURL;
    private String Id;

    public Contacts(String name, String phone_number, String ImageURL,String Id) {
        this.name = name;
        this.phone_number = phone_number;
        this.ImageURL = ImageURL;
        this.Id = Id;
    }

    public Contacts(String name, String phone_number, String imageURL) {
        this.name = name;
        this.phone_number = phone_number;
        ImageURL = imageURL;
    }

    public Contacts(String name, String phone_number) {
        this.name = name;
        this.phone_number = phone_number;
    }

    public Contacts() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getImageURL() {
        return ImageURL;
    }

    public void setImageURL(String imageURL) {
        ImageURL = imageURL;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }
}

