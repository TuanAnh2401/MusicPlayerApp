package com.example.music.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.music.Activities.MainActivity;
import com.example.music.Activities.MusicPlayerService;
import com.example.music.Activities.SongPlayerActivity;
import com.example.music.Models.SongModel;
import com.example.music.R;

import java.util.ArrayList;
import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    private Context mContext;
    private List<SongModel> songList;
    private List<SongModel> allSongs;

    public SongAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setData(List<SongModel> list, List<SongModel> allSongs) {
        this.songList = list;
        this.allSongs = allSongs;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        SongModel song = songList.get(position);
        if (song == null) {
            return;
        }
        Glide.with(holder.itemView.getContext())
                .load(song.getLinkImage())
                .into(holder.imgSong);

        holder.txtNameSong.setText(song.getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    int allSongsPosition = findSongPositionInAllSongs(song);
                    if (allSongsPosition != -1) {
                        Intent intent = new Intent(mContext, MainActivity.class);
                        intent.putExtra("position", allSongsPosition);
                        intent.putParcelableArrayListExtra("songList", new ArrayList<>(allSongs));
                        mContext.startService(intent);
                        mContext.startActivity(intent);
                    }
                }
            }
        });
    }
    private int findSongPositionInAllSongs(SongModel targetSong) {
        if (allSongs != null && targetSong != null) {
            for (int i = 0; i < allSongs.size(); i++) {
                SongModel currentSong = allSongs.get(i);
                if (currentSong != null && currentSong.getId().equals(targetSong.getId())) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return songList != null ? songList.size() : 0;
    }

    public class SongViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgSong;
        private TextView txtNameSong;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            imgSong = itemView.findViewById(R.id.img_song);
            txtNameSong = itemView.findViewById(R.id.txtNameSong);
        }
    }
}