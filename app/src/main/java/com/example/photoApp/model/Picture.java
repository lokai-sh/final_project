package com.example.photoApp.model;

public class Picture {
    private String filepath;
    private Boolean favorite;
    //Todo: Implement the favorite property when a picture is flagged as a favorite and saved

    public Picture(String filepath) {
        this.filepath = filepath;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getCaption() {
        //Todo: This comes from the filepath should it be a property instead?
        String[] attr = filepath.split("_");
        return attr[1];
    }

    public String getTimeStamp() {
        //Todo: This comes from the filepath should it be a property instead?
        String[] attr = filepath.split("_");
        return attr[2];
    }

    public Boolean getFavorite() {
        return favorite;
    }

    public void setFavorite(Boolean favorite) {
        this.favorite = favorite;
    }
}
