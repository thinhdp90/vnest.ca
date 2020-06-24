package com.vnest.ca.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.vnest.ca.database.dao.MessageDao;
import com.vnest.ca.database.dao.PoiDao;
import com.vnest.ca.database.model.PoiModel;
import com.vnest.ca.entity.Message;

@Database(entities = {Message.class, PoiModel.class}, version = 2)
public abstract class VNestDB extends RoomDatabase {
    public abstract MessageDao messageDao();
    public abstract PoiDao poiDao();

    public static VNestDB INSTANCE = null;

    public static VNestDB getInstances(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), VNestDB.class, "VnestDB")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }
}
