package com.example.music.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class AlbumModel implements Parcelable {
    private String imageURL;
    private String nameAlbum;
    private List<SongModel> songs;

    public AlbumModel(String imageURL, String nameAlbum, List<SongModel> songs) {
        this.imageURL = imageURL;
        this.nameAlbum = nameAlbum;
        this.songs = songs;
    }

    protected AlbumModel(Parcel in) {
        imageURL = in.readString();
        nameAlbum = in.readString();
        songs = in.createTypedArrayList(SongModel.CREATOR);
    }

    public static final Creator<AlbumModel> CREATOR = new Creator<AlbumModel>() {
        @Override
        public AlbumModel createFromParcel(Parcel in) {
            return new AlbumModel(in);
        }

        @Override
        public AlbumModel[] newArray(int size) {
            return new AlbumModel[size];
        }
    };

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getNameAlbum() {
        return nameAlbum;
    }

    public void setNameAlbum(String nameAlbum) {
        this.nameAlbum = nameAlbum;
    }

    public List<SongModel> getSongs() {
        return songs;
    }

    public void setSongs(List<SongModel> songs) {
        this.songs = songs;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public AlbumModel() {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imageURL);
        dest.writeString(nameAlbum);
        dest.writeTypedList(songs);
    }
}
