package com.example.music.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.music.Activities.SongPlayerActivity;
import com.example.music.Models.SongModel;
import com.example.music.R;

import java.util.ArrayList;
import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    private Context mContext;
    private List<SongModel> songList;

    public SongAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setData(List<SongModel> list) {
        this.songList = list;
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
                // Start the SongPlayerActivity with the selected song
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    Intent intent = new Intent(mContext, SongPlayerActivity.class);
                    intent.putExtra("position", adapterPosition);
                    intent.putParcelableArrayListExtra("songList", new ArrayList<>(songList));
                    mContext.startActivity(intent);
                }
            }
        });
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
