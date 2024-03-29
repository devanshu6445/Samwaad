package com.india.chat.samwaad.Model;

public class StoryMember {

    String postUri;
    String name;
    String timeEnd;
    String timeUpload;
    String type;
    String uid;

    public StoryMember(){

    }

    public StoryMember(String postUri, String name, String timeEnd, String timeUpload, String type, String uid) {
        this.postUri = postUri;
        this.name = name;
        this.timeEnd = timeEnd;
        this.timeUpload = timeUpload;
        this.type = type;
        this.uid = uid;
    }

    public String getPostUri() {
        return postUri;
    }

    public void setPostUri(String postUri) {
        this.postUri = postUri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getTimeUpload() {
        return timeUpload;
    }

    public void setTimeUpload(String timeUpload) {
        this.timeUpload = timeUpload;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
