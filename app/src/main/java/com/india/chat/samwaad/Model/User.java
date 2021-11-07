package com.india.chat.samwaad.Model;

public class User {

    private String id;
    private String username;
    private String ImageURL;
    private String status;
    private String search;

    public User(String id, String username, String ImageURL, String status, String search) {
        this.id = id;
        this.username = username;
        this.ImageURL = ImageURL;
        this.status = status;
        this.search = search;
    }

    public User(String id, String username, String ImageURL, String status){
        this.id = id;
        this.username = username;
        this.ImageURL = ImageURL;
        this.status= status;
    }
    public User(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageURL() {
        return ImageURL;
    }

    public void setImageURL(String imageURL) {
        this.ImageURL = imageURL;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
