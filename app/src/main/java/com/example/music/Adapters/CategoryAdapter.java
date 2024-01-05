package com.example.music.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music.Models.CategoryModel;
import com.example.music.Models.SongModel;
import com.example.music.R;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private Context mContext;
    private List<CategoryModel> mListCategory;
    private List<SongModel> allSongs;

    public void setAllSongs(List<SongModel> allSongs) {
        this.allSongs = allSongs;
        notifyDataSetChanged();
    }
    public CategoryAdapter(Context mContext) {
        this.mContext = mContext;
    }
    public void setData(List<CategoryModel> list){
        this.mListCategory = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryAdapter.CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.CategoryViewHolder holder, int position) {
        CategoryModel category = mListCategory.get(position);
        if(category == null){
            return;
        }

        holder.nameCategory.setText(category.getNameCategory());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false);
        holder.songs.setLayoutManager(linearLayoutManager);

        SongAdapter songAdapter = new SongAdapter(mContext);
        songAdapter.setData(category.getSongs(), allSongs);
        holder.songs.setAdapter(songAdapter);

    }

    @Override
    public int getItemCount() {
        if(mListCategory != null){
            return mListCategory.size();
        }
        return 0;
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        private TextView nameCategory;
        private RecyclerView songs;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            nameCategory = itemView.findViewById(R.id.txtNameList);
            songs = itemView.findViewById(R.id.rcvSong);
        }
    }

}