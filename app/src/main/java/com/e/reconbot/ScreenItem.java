package com.e.reconbot;

public class ScreenItem {

    String Title,Description;
    int ScreenImg;

    public ScreenItem (String title, String description,int screenImg){
        Title = title;
        Description = description;
        ScreenImg = screenImg;
    }

    // Get
    public void setTitle(String title) {
        Title = title;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public void setScreenImg(int screenImg) {
        ScreenImg = screenImg;
    }


    // Set
    public String getTitle() {
        return Title;
    }

    public String getDescription() {
        return Description;
    }

    public int getScreenImg() {
        return ScreenImg;
    }

}
