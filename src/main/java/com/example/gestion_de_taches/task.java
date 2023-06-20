package com.example.gestion_de_taches;

import java.io.Serializable;

public class task implements Serializable {
    private String title;
    private String description;

    private String user;

    public task(String title, String description,String user) {
        this.title = title;
        this.description = description;
        this.user=user;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUser(){return user;}
}




