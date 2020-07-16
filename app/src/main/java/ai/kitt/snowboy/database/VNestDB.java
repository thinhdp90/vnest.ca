package ai.kitt.snowboy.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import ai.kitt.snowboy.database.dao.MessageDao;
import ai.kitt.snowboy.database.dao.PoiDao;
import ai.kitt.snowboy.database.model.PoiModel;
import ai.kitt.snowboy.entity.Message;

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
