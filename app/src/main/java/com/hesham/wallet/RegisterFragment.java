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
import androidx.fragment.app.Fragment;
import com.hesham.wallet.database.UserDB;
import com.hesham.wallet.entity.User;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class RegisterFragment extends Fragment {
    private Button btnSubmit;
    private EditText username, password, confirmPassword;

    private String user, Pass, confirmPass;

    public static final String error = "Should be filled";
    private View v;
    private TextView login_view;
    UserDB database;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_register, container, false);

        init();

        setSubmit();
        login_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.FragmentCommit("fragment1");
            }
        });
        // Inflate the layout for this fragment
        return v;
    }

    private void init()
    {
        database = UserDB.getInstance(getActivity().getApplicationContext());

        login_view = v.findViewById(R.id.login_view);
        btnSubmit = v.findViewById(R.id.submit_reg);
        username = v.findViewById(R.id.username_reg);
        password = v.findViewById(R.id.password_reg);
        confirmPassword = v.findViewById(R.id.confirm_pass_reg);

    }

    private boolean getUser()
    {
        user = username.getText().toString();
        if(user.isEmpty())
        {
            username.setError(error);
            return false;
        }
        return true;
    }

    private boolean getPass()
    {
        Pass = password.getText().toString();
        confirmPass = confirmPassword.getText().toString();
        if(Pass.isEmpty() || confirmPass.isEmpty())
        {

            if(Pass.isEmpty())
                password.setError(error);

            if(confirmPass.isEmpty())
                confirmPassword.setError(error);

            return false;
        }

        return Pass.equals(confirmPass);
    }

    private void setSubmit(){
        btnSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (getUser() && getPass())
                {
                    UserDB.setUserLogged(user);
                    CreateUser();
                }
            }
        });
    }
    private void CreateUser() {

        int retVal;
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Integer> future = executorService.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {

                if (database.userDao().userExistence(user).size() > 0) {
                    return 1;

                } else {
                    database.userDao().insertNewUser(new User(user, Pass));
                    return 2;
                }
            }
        });
        try {
            retVal = future.get();
        } catch (Exception exception) {
            retVal = -1;
        }
        if (retVal == 1)
        {
            username.setError("User Already Exists");
            Toast.makeText(getActivity(), "Please choose another username", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(getActivity(), "Registered successfully", Toast.LENGTH_LONG).show();
            Intent i = new Intent(getContext(), ToDoActivity.class);
            startActivity(i);
            getActivity().finish();
        }
    }
}