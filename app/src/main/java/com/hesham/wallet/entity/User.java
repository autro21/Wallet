package com.hesham.wallet.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class User {

    @PrimaryKey
    @NonNull
    public String userName;

    public String password;
    public boolean rememberMe = false;

    public User(String userName, String password) {

        this.userName = userName;
        this.password = password;
    }
}
