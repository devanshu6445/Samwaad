package com.india.chat.samwaad.Model;

public class Contacts {

    private String name;
    private String phone_number;

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
}
