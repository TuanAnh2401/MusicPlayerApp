package com.example.music.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.music.Adapters.ListSongAdapter;
import com.example.music.Models.AlbumModel;
import com.example.music.Models.SongModel;
import com.example.music.R;

import java.util.ArrayList;

public class AlbumFragment extends Fragment {

    private ImageView albumImage;
    private TextView albumName;

    private Button btnShuffle;
    private RecyclerView songRecyclerView;
    private  ImageButton btnBack;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album, container, false);

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
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        songRecyclerView.setLayoutManager(layoutManager);

        ListSongAdapter listSongAdapter = new ListSongAdapter(allSongs);
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
}
