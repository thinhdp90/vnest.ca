package ai.kitt.snowboy.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;


import ai.kitt.snowboy.entity.Message;

import java.util.List;

@Dao
public interface MessageDao {
    @Query("SELECT * FROM Message")
    List<Message> getAll();

    @Insert
    void insert(Message... messages);

    @Delete
    void delete(Message... messages);
}
