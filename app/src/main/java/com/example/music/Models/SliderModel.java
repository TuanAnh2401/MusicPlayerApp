package com.example.music.Models;

public class SliderModel {
    private String imageUrl;
    private String slideName;

    public SliderModel(String imageUrl, String slideName) {
        this.imageUrl = imageUrl;
        this.slideName = slideName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSlideName() {
        return slideName;
    }

    public void setSlideName(String slideName) {
        this.slideName = slideName;
    }
}
