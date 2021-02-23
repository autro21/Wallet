package com.hesham.wallet.database;

import androidx.room.*;
import com.hesham.wallet.entity.ToDo;

import java.util.List;

@Dao
public interface ToDoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTask(ToDo... tasks);
    @Query("SELECT * FROM ToDo WHERE user = :user order by Status DESC, date ASC")
    List<ToDo> getTasks(String user);

    @Delete
    void DeleteTask(ToDo... tasks);

    @Query("SELECT * from ToDo where ID = :id")
    ToDo getToDoTask(int id);

    @Query("DELETE from ToDo where ID = :id")
    void DeleteTaskByID(int id);
}
