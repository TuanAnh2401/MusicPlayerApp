package com.example.music.Viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.music.Models.AlbumModel;
import com.example.music.Models.CategoryModel;
import com.example.music.Models.ListModel;
import com.example.music.Models.SliderModel;
import com.example.music.Models.SongModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeViewModel extends ViewModel {
    private MutableLiveData<List<CategoryModel>> categoryData = new MutableLiveData<>();
    private MutableLiveData<List<SliderModel>> sliderData = new MutableLiveData<>();
    private MutableLiveData<List<SongModel>> songData = new MutableLiveData<>();
    private MutableLiveData<List<ListModel>> albumData = new MutableLiveData<>();
    private MutableLiveData<List<SongModel>> allSongsData = new MutableLiveData<>();
    public LiveData<List<SongModel>> getAllSongsData() {
        loadAllSongs();
        return allSongsData;
    }

    public LiveData<List<CategoryModel>> getCategoryData() {
        loadCategoryData();
        return categoryData;
    }

    public LiveData<List<SliderModel>> getSliderData() {
        loadSliderData();
        return sliderData;
    }

    public LiveData<List<ListModel>> getAlbumData() {
        loadAlbumData();
        return albumData;
    }

    private void loadSliderData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("slider")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<SliderModel> sliderList = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String imageUrl = documentSnapshot.getString("image");
                        String slideName = documentSnapshot.getString("name");
                        SliderModel sliderModel = new SliderModel(imageUrl, slideName);
                        sliderList.add(sliderModel);
                    }
                    sliderData.setValue(sliderList);
                });
    }
    private void loadCategoryData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<CategoryModel> categoryList = new ArrayList<>();
        List<SongModel> getLatestSongsData = loadLatestSongsData();
        CategoryModel category = new CategoryModel("Mới phát hành", getLatestSongsData);
        categoryList.add(category);

        db.collection("categories")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String categoryName = documentSnapshot.getString("categoryName");
                        List<SongModel> songs = new ArrayList<>();
                        db.collection("songs")
                                .whereEqualTo("category", categoryName)
                                .get()
                                .addOnSuccessListener(songQueryDocumentSnapshots -> {
                                    for (QueryDocumentSnapshot songDocumentSnapshot : songQueryDocumentSnapshots) {
                                        SongModel song = songDocumentSnapshot.toObject(SongModel.class);
                                        song.setId(songDocumentSnapshot.getId());
                                        songs.add(song);
                                    }
                                    CategoryModel categoryNew = new CategoryModel(categoryName, songs);
                                    categoryList.add(categoryNew);
                                    categoryData.setValue(categoryList);
                                });
                    }
                });
    }

    private void loadAlbumData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        ListModel listModel = new ListModel("Album", new ArrayList<>());

        db.collection("album")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String albumName = documentSnapshot.getString("nameAlbum");

                        AlbumModel albumModel = documentSnapshot.toObject(AlbumModel.class);
                        List<SongModel> songs = new ArrayList<>();

                        db.collection("songs")
                                .whereEqualTo("nameAlbum", albumName)
                                .get()
                                .addOnSuccessListener(songQueryDocumentSnapshots -> {
                                    for (QueryDocumentSnapshot songDocumentSnapshot : songQueryDocumentSnapshots) {
                                        SongModel song = songDocumentSnapshot.toObject(SongModel.class);
                                        song.setId(songDocumentSnapshot.getId());
                                        songs.add(song);
                                    }

                                    AlbumModel updatedAlbumModel = new AlbumModel(
                                            albumModel.getImageURL(),
                                            albumModel.getNameAlbum(),
                                            songs
                                    );

                                    listModel.getAlbum().add(updatedAlbumModel);

                                    if (listModel.getAlbum().size() == queryDocumentSnapshots.size()) {
                                        albumData.setValue(Collections.singletonList(listModel));
                                    }
                                });
                    }
                });
    }


    private List<SongModel> loadLatestSongsData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<SongModel> latestSongsList = new ArrayList<>();
        db.collection("songs")
                .orderBy("dateTime", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        SongModel songModel = documentSnapshot.toObject(SongModel.class);
                        songModel.setId(documentSnapshot.getId());
                        latestSongsList.add(songModel);
                    }
                    songData.setValue(latestSongsList);
                });
        return latestSongsList;
    }
    public void loadAllSongs() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("songs")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<SongModel> allSongs = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        SongModel song = documentSnapshot.toObject(SongModel.class);
                        song.setId(documentSnapshot.getId());
                        allSongs.add(song);
                    }
                    allSongsData.setValue(allSongs);
                });

    }

}

