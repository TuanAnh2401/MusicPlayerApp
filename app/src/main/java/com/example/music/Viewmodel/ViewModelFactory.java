package com.example.music.Viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.music.Database.AppDatabase;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private AppDatabase appDatabase;

    public ViewModelFactory(AppDatabase appDatabase) {
        this.appDatabase = appDatabase;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(HomeViewModel.class)) {
            return (T) new HomeViewModel(appDatabase);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}

