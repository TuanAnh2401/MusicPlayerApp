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
import com.example.music.Entity.SongEntity;
import com.example.music.Models.SongModel;
import com.example.music.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ListSongAdapter extends RecyclerView.Adapter<ListSongAdapter.ListSongViewHolder> {
    private List<SongModel> songList;
    private Context mContext;
    private AppDatabase appDatabase;

    public ListSongAdapter(List<SongModel> songList, Context mContext, AppDatabase appDatabase) {
        this.mContext = mContext;
        this.songList = songList;
        this.appDatabase = appDatabase;
    }

    public void setData(List<SongModel> list) {
        this.songList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ListSongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_song_item, parent, false);
        return new ListSongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListSongViewHolder holder, int position) {
        SongModel song = songList.get(position);
        if (song == null) {
            return;
        }

        Glide.with(holder.itemView.getContext())
                .load(song.getLinkImage())
                .into(holder.imgSong);

        holder.txtNameSong.setText(song.getName());
        holder.txtSingerSong.setText(song.getSinger());

        int adapterPosition = holder.getAdapterPosition();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    Intent intent = new Intent(mContext, SongPlayerActivity.class);
                    intent.putExtra("position", adapterPosition);
                    intent.putParcelableArrayListExtra("songList", new ArrayList<>(songList));
                    mContext.startActivity(intent);
                }
            }
        });

        holder.btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    saveSongToDatabase(song);
                }
            }
        });
    }

    private void saveSongToDatabase(SongModel song) {
        SongEntity songEntity = new SongEntity();
        songEntity.setId(song.getId());
        songEntity.setCategory(song.getCategory());
        songEntity.setDateTime(getCurrentDateTime());
        songEntity.setLinkImage(song.getLinkImage());
        songEntity.setLinkMP3(song.getLinkMP3());
        songEntity.setLyric(song.getLyric());
        songEntity.setName(song.getName());
        songEntity.setSinger(song.getSinger());

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                SongEntity existingSong = appDatabase.songDao().getSongById(song.getId());
                if (existingSong == null) {
                    appDatabase.songDao().insertSong(songEntity);

                    if (mContext instanceof Activity) {
                        ((Activity) mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, "Tải bài hát thành công", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    handleDuplicateIdErrorOnUiThread();
                }
            }
        });
    }


    private void handleDuplicateIdErrorOnUiThread() {
        if (mContext instanceof Activity) {
            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, "Bài hát đã tồn tại trong danh sách", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private String getCurrentDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return dateFormat.format(new Date());
    }

    @Override
    public int getItemCount() {
        return songList != null ? songList.size() : 0;
    }

    public class ListSongViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgSong;
        private TextView txtNameSong;
        private TextView txtSingerSong;
        private ImageView btn_download;

        public ListSongViewHolder(@NonNull View itemView) {
            super(itemView);
            imgSong = itemView.findViewById(R.id.img_list_song);
            txtNameSong = itemView.findViewById(R.id.tv_song_name);
            txtSingerSong = itemView.findViewById(R.id.tv_singer_name);
            btn_download = itemView.findViewById(R.id.btn_download);
        }
    }
}
