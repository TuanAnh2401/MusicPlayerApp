package com.example.music.Models;

import java.util.List;

public class CategoryModel {
    private String nameCategory;
    private List<SongModel> songs;

    public CategoryModel(String nameCategory, List<SongModel> songs) {
        this.nameCategory = nameCategory;
        this.songs = songs;
    }
    public String getNameCategory() {
        return nameCategory;
    }

    public void setNameCategory(String nameCategory) {
        this.nameCategory = nameCategory;
    }

    public List<SongModel> getSongs() {
        return songs;
    }

    public void setSongs(List<SongModel> songs) {
        this.songs = songs;
    }
}
