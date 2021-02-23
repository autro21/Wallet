package com.hesham.wallet.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.hesham.wallet.R;
import com.hesham.wallet.entity.ToDo;
import com.hesham.wallet.entity.User;

@Database(entities = {User.class, ToDo.class}, version = 1)
public abstract class UserDB extends RoomDatabase {
    static UserDB instance;

    public static String getUserLogged() {
        return userLogged;
    }

    public static void setUserLogged(String userLogged) {
        UserDB.userLogged = userLogged;
    }

    static String userLogged;
    public static  UserDB getInstance(final Context context){
        if(instance == null)
        {
            synchronized (UserDB.class){
                if(instance == null)
                {
                    instance = createDatabase(context);
                }
            }
        }
        return instance;
    }

    private static UserDB createDatabase(Context context){
        UserDB dbObject = Room.databaseBuilder(context, UserDB.class, context.getString(R.string.user_db))
                .build();
        return dbObject;
    }
    public abstract UserDao userDao();
    public abstract ToDoDao toDoDao();

}
