package com.hareem.anxietyrelief;

import com.google.gson.annotations.SerializedName;

// Create a model class to represent the response from the server
public class ThemeResponse {

    @SerializedName("wallpaperImage")
    private String wallpaperImage;

    @SerializedName("textColor")
    private int textColor;

    // Add getters and setters


    public String getWallpaperImage() {
        return wallpaperImage;
    }

    public void setWallpaperImage(String wallpaperImage) {
        this.wallpaperImage = wallpaperImage;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }
}
