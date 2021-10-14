package com.india.chat.samwaad;

public class information {
    private String email;
    private  String name;
    private String user_uid;
    private String phone_number;
    private String gender;

    public information(String email, String name, String user_uid, String phone_number, String gender) {
        this.email = email;
        this.name = name;
        this.user_uid = user_uid;
        this.phone_number = phone_number;
        this.gender = gender;
    }

    public information(){

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser_uid() {
        return user_uid;
    }

    public void setUser_uid(String user_uid) {
        this.user_uid = user_uid;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

}
