package com.vnest.ca.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.vnest.ca.database.model.PoiModel;
import com.vnest.ca.entity.Message;

import java.util.List;

@Dao
public interface PoiDao {
    @Query("SELECT * FROM POIMODEL")
    List<PoiModel> getAll();

    @Insert
    void insert(PoiModel... poiModels);

    @Delete
    void delete(PoiModel... poiModels);
}
