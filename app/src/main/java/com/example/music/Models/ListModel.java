package com.example.music.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class ListModel implements Parcelable {
    private String nameAlbum;
    private List<AlbumModel> album;

    public ListModel(String nameAlbum, List<AlbumModel> album) {
        this.nameAlbum = nameAlbum;
        this.album = album;
    }

    protected ListModel(Parcel in) {
        nameAlbum = in.readString();
        album = in.createTypedArrayList(AlbumModel.CREATOR);
    }

    public static final Creator<ListModel> CREATOR = new Creator<ListModel>() {
        @Override
        public ListModel createFromParcel(Parcel in) {
            return new ListModel(in);
        }

        @Override
        public ListModel[] newArray(int size) {
            return new ListModel[size];
        }
    };

    public String getNameAlbum() {
        return nameAlbum;
    }

    public void setNameAlbum(String nameAlbum) {
        this.nameAlbum = nameAlbum;
    }

    public List<AlbumModel> getAlbum() {
        return album;
    }

    public void setAlbum(List<AlbumModel> album) {
        this.album = album;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nameAlbum);
        dest.writeTypedList(album);
    }
}
