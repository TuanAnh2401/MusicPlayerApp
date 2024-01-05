package com.example.music.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music.Models.AlbumModel;
import com.example.music.Models.ListModel;
import com.example.music.R;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListViewHolder> {

    private Context mContext;
    private List<ListModel> mListAlbum;
    private OnAlbumClickListener onAlbumClickListener;

    public interface OnAlbumClickListener {
        void onAlbumClick(AlbumModel album);
    }

    public void setOnAlbumClickListener(OnAlbumClickListener listener) {
        this.onAlbumClickListener = listener;
    }

    public ListAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setData(List<ListModel> list) {
        this.mListAlbum = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        ListModel albumListModel = mListAlbum.get(position);
        if (albumListModel == null) {
            return;
        }

        holder.nameAlbum.setText(albumListModel.getNameAlbum());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false);
        holder.albums.setLayoutManager(linearLayoutManager);

        AlbumAdapter albumAdapter = new AlbumAdapter();
        albumAdapter.setData(albumListModel.getAlbum());
        albumAdapter.setOnAlbumClickListener(new AlbumAdapter.OnAlbumClickListener() {
            @Override
            public void onAlbumClick(AlbumModel selectedAlbum) {
                if (onAlbumClickListener != null) {
                    onAlbumClickListener.onAlbumClick(selectedAlbum);
                }
            }
        });

        holder.albums.setAdapter(albumAdapter);
    }

    @Override
    public int getItemCount() {
        if (mListAlbum != null) {
            return mListAlbum.size();
        }
        return 0;
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        private TextView nameAlbum;
        private RecyclerView albums;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            nameAlbum = itemView.findViewById(R.id.txtNameListAlbum);
            albums = itemView.findViewById(R.id.rcvList);
        }
    }
}
