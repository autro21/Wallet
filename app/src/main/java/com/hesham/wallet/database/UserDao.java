package com.hesham.wallet.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.hesham.wallet.entity.User;

import java.util.List;

@Dao
public interface UserDao {

    @Insert
    void insertNewUser(User... users);

    @Query("select userName from User where userName = :username")
    List<String> userExistence(String username);

    @Query("select * from User where userName = :username AND password = :password")
    List<User> LoginQuery(String username, String password);


}
