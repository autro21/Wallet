package com.hesham.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.hesham.wallet.database.UserDB;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class LoginFragment extends Fragment {

    private Button btn_login;
    private EditText username_et, password_et;

    private String username, password;
    private static final String error = "Should be filled";

    private View v;

    private UserDB database;
    private TextView regTrans;


    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_login, container, false);
        // Inflate the layout for this fragment
        init();
        setListener();
        return v;
    }

    void init()
    {
        database = UserDB.getInstance(getActivity().getApplicationContext());
        regTrans = v.findViewById(R.id.register_view);
        btn_login = v.findViewById(R.id.submit_lgn);
        username_et = v.findViewById(R.id.username_lgn);
        password_et = v.findViewById(R.id.password_lgn);
    }
    void setListener(){
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getUser() && getPass())
                {
                    UserDB.setUserLogged(username);
                    checkUserLogin();
                }
            }
        });
        regTrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.FragmentCommit("fragment2");
            }
        });
    }
    void checkUserLogin(){
        int x;
        ExecutorService es = Executors.newSingleThreadExecutor();
        Future<Integer> result = es.submit(new Callable<Integer>() {
            public Integer call() throws Exception {
                if(database.userDao().LoginQuery(username, password).size() > 0)
                    return 1;

                else
                    return 2;
            }
        });
        try {
            x = result.get();
        } catch (Exception e) {
            x = -1;
            // failed
        }
        es.shutdown();
        if(x == 1)
        {
            Toast.makeText(getActivity(), "Login Successfully", Toast.LENGTH_LONG).show();
            Intent i = new Intent(getContext(), ToDoActivity.class);
            startActivity(i);
            getActivity().finish();
        }
        else if(x == 2)
        {
            Toast.makeText(getActivity(), "Not valid Credentials", Toast.LENGTH_LONG).show();
        }
    }
    private boolean getUser()
    {
        username = username_et.getText().toString();
        if(username.isEmpty())
        {
            username_et.setError(error);
            return false;
        }
        return true;
    }

    private boolean getPass()
    {
        password = password_et.getText().toString();
        return !password.isEmpty();
    }
}