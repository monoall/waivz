package com.gmail.hookmailua.waivz.app.entities;

public class User {

    private int id;
    private String first_name;
    private String last_name;
    private String photo_max;
    private String bdate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getPhoto_max() {
        return photo_max;
    }

    public void setPhoto_max(String photo_max) {
        this.photo_max = photo_max;
    }

    public String getBdate() {
        return bdate;
    }

    public void setBdate(String bdate) {
        this.bdate = bdate;
    }
}
