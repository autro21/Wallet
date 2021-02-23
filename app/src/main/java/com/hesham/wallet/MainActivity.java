package com.hesham.wallet;


import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.hesham.wallet.database.UserDB;

public class MainActivity extends AppCompatActivity {
    private static Fragment fragment1, fragment2;
    private static FragmentManager manager;
    public static FragmentTransaction transaction;
    public static UserDB database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        database = UserDB.getInstance(getApplicationContext());
    }
    private void init()
    {
        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();
        fragment2 = new RegisterFragment();
        fragment1 = new LoginFragment();
        transaction.add(R.id.fragment_main, fragment1, "fragment1");
        transaction.commit();
    }


    public static void FragmentCommit(String fTag)
    {
        transaction = manager.beginTransaction();

        /**
        fragment = manager.findFragmentByTag(fTag);

        if(fragment == null)
        {
            fragment = fTag == "fragment1" ? new LoginFragment() : new RegisterFragment();
            transaction.add(R.id.fragment_main, fragment, fTag);
        }
        else{
            fragment = manager.findFragmentByTag(fTag);
            transaction.replace(R.id.fragment_main, fragment, fTag);
            transaction.commit();
            }

         **/
        Fragment fragment;
        fragment = fTag == "fragment1" ? fragment1 : fragment2;
        transaction.replace(R.id.fragment_main, fragment, fTag);
        transaction.commit();
    }

}