package com.example.music.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.music.Models.AlbumModel;
import com.example.music.R;

import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {

    private List<AlbumModel> mListAlbum;
    private OnAlbumClickListener onAlbumClickListener;

    public interface OnAlbumClickListener {
        void onAlbumClick(AlbumModel album);
    }

    public void setOnAlbumClickListener(OnAlbumClickListener listener) {
        this.onAlbumClickListener = listener;
    }

    public void setData(List<AlbumModel> list) {
        this.mListAlbum = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_item, parent, false);
        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {
        AlbumModel album = mListAlbum.get(position);
        if (album == null) {
            return;
        }

        Glide.with(holder.itemView.getContext())
                .load(album.getImageURL())
                .into(holder.imgAlbum);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onAlbumClickListener != null) {
                    onAlbumClickListener.onAlbumClick(album);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mListAlbum != null) {
            return mListAlbum.size();
        }
        return 0;
    }

    static class AlbumViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgAlbum;

        AlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAlbum = itemView.findViewById(R.id.img_album);
        }
    }
}
