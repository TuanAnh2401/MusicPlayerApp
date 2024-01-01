package com.example.music.Models;

public class SongModel {
    private String category;
    private String dateTime;
    private String linkImage;
    private String linkMP3;
    private String lyric;
    private String name;
    private String singer;

    public SongModel(String category, String dateTime, String linkImage, String linkMP3, String lyric, String name, String singer) {
        this.category = category;
        this.dateTime = dateTime;
        this.linkImage = linkImage;
        this.linkMP3 = linkMP3;
        this.lyric = lyric;
        this.name = name;
        this.singer = singer;
    }

    public SongModel() {
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getLinkImage() {
        return linkImage;
    }

    public void setLinkImage(String linkImage) {
        this.linkImage = linkImage;
    }

    public String getLinkMP3() {
        return linkMP3;
    }

    public void setLinkMP3(String linkMP3) {
        this.linkMP3 = linkMP3;
    }

    public String getLyric() {
        return lyric;
    }

    public void setLyric(String lyric) {
        this.lyric = lyric;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }
}
