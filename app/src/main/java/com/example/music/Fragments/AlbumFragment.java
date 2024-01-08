package com.example.music.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.music.Activities.SongPlayerActivity;
import com.example.music.Adapters.ListSongAdapter;
import com.example.music.Database.AppDatabase;
import com.example.music.Models.AlbumModel;
import com.example.music.Models.SongModel;
import com.example.music.R;

import java.util.ArrayList;
import java.util.Collections;

public class AlbumFragment extends Fragment {

    private ImageView albumImage;
    private TextView albumName;
    private Button btnShuffle;
    private RecyclerView songRecyclerView;
    private ImageButton btnBack;
    AppDatabase appDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album, container, false);
        appDatabase = AppDatabase.getInstance(requireContext());

        albumImage = view.findViewById(R.id.albumImage);
        albumName = view.findViewById(R.id.txtAlbumName);
        btnShuffle = view.findViewById(R.id.btnShuffle);
        btnBack = view.findViewById(R.id.btnBack);
        songRecyclerView = view.findViewById(R.id.songRecyclerView);

        Bundle bundle = getArguments();
        if (bundle != null) {
            AlbumModel selectedAlbum = bundle.getParcelable("selectedAlbum");
            ArrayList<SongModel> allSongs = bundle.getParcelableArrayList("allSongs");

            if (allSongs != null && !allSongs.isEmpty()) {
                loadAllAlbumsData(allSongs);
            }

            if (selectedAlbum != null) {
                updateUI(selectedAlbum);
            }
        }

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

    private void loadAllAlbumsData(ArrayList<SongModel> allSongs) {
        btnShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playRandomSong(allSongs);
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        songRecyclerView.setLayoutManager(layoutManager);

        ListSongAdapter listSongAdapter = new ListSongAdapter(allSongs, getContext(), appDatabase);
        songRecyclerView.setAdapter(listSongAdapter);
    }

    private void updateUI(AlbumModel selectedAlbum) {
        if (getContext() != null) {
            Glide.with(getContext())
                    .load(selectedAlbum.getImageURL())
                    .into(albumImage);
        }
        albumName.setText(selectedAlbum.getNameAlbum());
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
