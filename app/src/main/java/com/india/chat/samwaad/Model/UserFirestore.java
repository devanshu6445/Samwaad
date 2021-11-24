package com.india.chat.samwaad.Model;

public class UserFirestore {
    private String id;
    private String name;
    private String ImageURL;
    private String status;
    private String search;

    public UserFirestore(String id, String name, String imageURL, String status, String search) {
        this.id = id;
        this.name = name;
        ImageURL = imageURL;
        this.status = status;
        this.search = search;
    }

    public UserFirestore() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageURL() {
        return ImageURL;
    }

    public void setImageURL(String imageURL) {
        ImageURL = imageURL;
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
