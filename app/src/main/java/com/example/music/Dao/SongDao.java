package com.example.music.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.music.Entity.SongEntity;

import java.util.List;

@Dao
public interface SongDao {
    @Insert
    void insertSong(SongEntity song);
    @Insert
    void insertAllSongs(List<SongEntity> songs);
    @Query("DELETE FROM songs WHERE id = :songId")
    void deleteSongById(String songId);
    @Query("SELECT * FROM songs WHERE id = :songId")
    SongEntity getSongById(String songId);
    @Query("SELECT * FROM songs")
    LiveData<List<SongEntity>> getAllSongsLiveData();
}

