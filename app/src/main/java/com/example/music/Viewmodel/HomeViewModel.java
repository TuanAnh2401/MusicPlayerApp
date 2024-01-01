package com.example.music.Viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.music.Models.CategoryModel;
import com.example.music.Models.SliderModel;
import com.example.music.Models.SongModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {
    private MutableLiveData<List<CategoryModel>> categoryData = new MutableLiveData<>();
    private MutableLiveData<List<SliderModel>> sliderData = new MutableLiveData<>();
    private MutableLiveData<List<SongModel>> songData = new MutableLiveData<>();

    public LiveData<List<CategoryModel>> getCategoryData() {
        loadCategoryData();
        return categoryData;
    }

    public LiveData<List<SliderModel>> getSliderData() {
        loadSliderData();
        return sliderData;
    }

    public LiveData<List<SongModel>> getSongData() {
        loadSongData();
        return songData;
    }

    private void loadCategoryData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("categories")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<CategoryModel> categoryList = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String categoryName = documentSnapshot.getString("categoryName");
                        List<SongModel> songs = new ArrayList<>();
                        db.collection("songs")
                                .whereEqualTo("category", categoryName)
                                .get()
                                .addOnSuccessListener(songQueryDocumentSnapshots -> {
                                    for (QueryDocumentSnapshot songDocumentSnapshot : songQueryDocumentSnapshots) {
                                        SongModel song = songDocumentSnapshot.toObject(SongModel.class);
                                        songs.add(song);
                                    }

                                    // Log danh sách bài hát trong mỗi loại
                                    Log.d("CategoryData", "Category: " + categoryName);
                                    for (SongModel song : songs) {
                                        Log.d("CategoryData", "Song: " + song.getName());
                                    }

                                    CategoryModel category = new CategoryModel(categoryName, songs);
                                    categoryList.add(category);
                                    categoryData.setValue(categoryList);
                                });
                    }
                });
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

    private void loadSongData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("songs")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<SongModel> songList = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        SongModel songModel = documentSnapshot.toObject(SongModel.class);
                        songList.add(songModel);
                    }
                    songData.setValue(songList);
                });
    }
}

