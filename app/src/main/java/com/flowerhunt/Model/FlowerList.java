package com.flowerhunt.Model;

import android.graphics.drawable.Drawable;

public class FlowerList {
    private String flower_name;
    private String number_of_roses;
    private int image_url;

    public FlowerList(String flower_name, String number_of_roses, int image_url) {
        this.flower_name = flower_name;
        this.number_of_roses = number_of_roses;
        this.image_url = image_url;
    }

    public String getFlower_name() {
        return flower_name;
    }

    public void setFlower_name(String flower_name) {
        this.flower_name = flower_name;
    }

    public String getNumber_of_roses() {
        return number_of_roses;
    }

    public void setNumber_of_roses(String number_of_roses) {
        this.number_of_roses = number_of_roses;
    }

    public int getImage_url() {
        return image_url;
    }

    public void setImage_url(int image_url) {
        this.image_url = image_url;
    }
}
