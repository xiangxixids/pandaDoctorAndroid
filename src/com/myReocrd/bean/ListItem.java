package com.myReocrd.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by Administrator on 15-6-23.
 */
public class ListItem {
    private Drawable image;
    private String title;

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
