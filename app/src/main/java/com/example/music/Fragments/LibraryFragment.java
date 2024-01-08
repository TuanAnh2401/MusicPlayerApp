package com.example.music.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music.Activities.SongPlayerActivity;
import com.example.music.Adapters.LibraryAdapter;
import com.example.music.Database.AppDatabase;
import com.example.music.Entity.SongEntity;
import com.example.music.Models.SongModel;
import com.example.music.R;
import com.example.music.Viewmodel.HomeViewModel;
import com.example.music.Viewmodel.ViewModelFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LibraryFragment extends Fragment {
    private RecyclerView libraryRecyclerView;
    private LibraryAdapter libraryAdapter;
    private HomeViewModel homeViewModel;
    private Button btnShuffle;
    private ImageButton btnBack;
    private ArrayList<SongModel> songModels;

    private AppDatabase appDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library, container, false);

        btnShuffle = view.findViewById(R.id.btnShuffle);
        btnBack = view.findViewById(R.id.btnBack);

        appDatabase = AppDatabase.getInstance(getContext());

        libraryRecyclerView = view.findViewById(R.id.libraryRecyclerView);
        libraryAdapter = new LibraryAdapter(getContext(), appDatabase);

        homeViewModel = new ViewModelProvider(this, new ViewModelFactory(appDatabase)).get(HomeViewModel.class);

        loadLibraryData();
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        return view;
    }
    private void onBackPressed() {
        if (getFragmentManager() != null) {
            getFragmentManager().popBackStack();
        }
    }
    private void loadLibraryData() {
        btnShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playRandomSong(songModels);
            }
        });
        homeViewModel.getAllSongsDataFromDao().observe(getViewLifecycleOwner(), new Observer<List<SongEntity>>() {
            @Override
            public void onChanged(List<SongEntity> allSongs) {
                songModels = convertSongEntitiesToSongModels(allSongs);
                libraryAdapter.setData(songModels);
            }
        });
    }

    private ArrayList<SongModel> convertSongEntitiesToSongModels(List<SongEntity> songEntities) {
        ArrayList<SongModel> songModels = new ArrayList<>();
        for (SongEntity entity : songEntities) {
            SongModel songModel = new SongModel();
            songModel.setId(entity.getId());
            songModel.setCategory(entity.getCategory());
            songModel.setDateTime(entity.getDateTime());
            songModel.setLinkImage(entity.getLinkImage());
            songModel.setLinkMP3(entity.getLinkMP3());
            songModel.setLyric(entity.getLyric());
            songModel.setName(entity.getName());
            songModel.setSinger(entity.getSinger());

            songModels.add(songModel);
        }
        return songModels;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        libraryRecyclerView.setLayoutManager(layoutManager);
        libraryRecyclerView.setAdapter(libraryAdapter);
    }
    private void playRandomSong(ArrayList<SongModel> allSongs) {
        if (allSongs != null && allSongs.size() > 0) {
            Collections.shuffle(allSongs);
            Intent intent = new Intent(getContext(), SongPlayerActivity.class);
            intent.putParcelableArrayListExtra("songList", allSongs);
            intent.putExtra("position", 0);
            startActivity(intent);
        }
    }

}
