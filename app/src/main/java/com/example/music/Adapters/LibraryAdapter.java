package com.example.music.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.music.Activities.SongPlayerActivity;
import com.example.music.Database.AppDatabase;
import com.example.music.Models.SongModel;
import com.example.music.R;

import java.util.ArrayList;
import java.util.List;

public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.LibraryViewHolder> {
    private Context mContext;
    private List<SongModel> allSongs;
    private AppDatabase appDatabase;

    public LibraryAdapter(Context mContext, AppDatabase appDatabase) {
        this.mContext = mContext;
        this.appDatabase = appDatabase;
        this.allSongs = new ArrayList<>();
    }


    public LibraryAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setData(List<SongModel> allSongs) {
        this.allSongs = allSongs;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LibraryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.library_item, parent, false);
        return new LibraryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LibraryViewHolder holder, int position) {
        SongModel song = allSongs.get(position);
        if (song == null) {
            return;
        }

        Glide.with(holder.itemView.getContext())
                .load(song.getLinkImage())
                .placeholder(R.drawable.error_image)
                .into(holder.img_library_song);


        holder.library_song_name.setText(song.getName());
        holder.library_singer_name.setText(song.getSinger());

        int adapterPosition = holder.getAdapterPosition();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    Intent intent = new Intent(mContext, SongPlayerActivity.class);
                    intent.putExtra("position", adapterPosition);
                    intent.putParcelableArrayListExtra("songList", new ArrayList<>(allSongs));
                    mContext.startActivity(intent);
                }
            }
        });
        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    deleteSongFromDatabase(song.getId());
                }
            }
        });
    }
    private void deleteSongFromDatabase(String songId) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                appDatabase.songDao().deleteSongById(songId);

                if (mContext instanceof Activity) {
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext, "Bài hát đã được xóa thành công", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return allSongs != null ? allSongs.size() : 0;
    }

    public static class LibraryViewHolder extends RecyclerView.ViewHolder {
        private ImageView img_library_song;
        private TextView library_song_name;
        private TextView library_singer_name;
        private ImageView btn_delete;

        public LibraryViewHolder(@NonNull View itemView) {
            super(itemView);
            img_library_song = itemView.findViewById(R.id.img_library_song);
            library_song_name = itemView.findViewById(R.id.library_song_name);
            library_singer_name = itemView.findViewById(R.id.library_singer_name);
            btn_delete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
