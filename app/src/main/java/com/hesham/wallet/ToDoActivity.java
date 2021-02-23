package com.hesham.wallet;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hesham.wallet.ToDoModel.todoAdapter;
import com.hesham.wallet.database.AppExecutor;
import com.hesham.wallet.database.UserDB;
import com.hesham.wallet.entity.ToDo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ToDoActivity extends AppCompatActivity implements DialogCloseListener, todoAdapter.onClickInterface {
    private RecyclerView todoRv;
    private todoAdapter adapter;
    private List<ToDo> toDoList;
    private UserDB database;
    private FloatingActionButton fab;
    private AppExecutor appExecutor;
    private static int pos = -1, idChosen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do);
        appExecutor = new AppExecutor();


        toDoList = new ArrayList<>();
        database = UserDB.getInstance(getApplicationContext());

        adapter = new todoAdapter(this);
        initData();

        todoRv = findViewById(R.id.rv_tasks);
        todoRv.setAdapter(adapter);
        todoRv.setLayoutManager(new LinearLayoutManager(this));

        fab = findViewById(R.id.fab);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new TouchHelper(adapter));
        itemTouchHelper.attachToRecyclerView(todoRv);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG);
            }
        });
    }

    @Override
    public void handleDialogClose(DialogInterface dialog) {
        int x = 0;
        ExecutorService es = Executors.newSingleThreadExecutor();
        Future<Integer> result = es.submit(new Callable<Integer>() {
            public Integer call() throws Exception {
                toDoList = database.toDoDao().getTasks(UserDB.getUserLogged());
                adapter.setToDoList(toDoList);
                return 1;
            }
        });
        try {
            x = result.get();
        } catch (Exception e) {
            x = -1;
            // failed
        }
        es.shutdown();
        if (x == 1) {
            adapter.notifyDataSetChanged();
        }

    }

    void initData() {
        int x = 0;
        ExecutorService es = Executors.newSingleThreadExecutor();
        Future<Integer> result = es.submit(new Callable<Integer>() {
            public Integer call() throws Exception {
                toDoList = database.toDoDao().getTasks(UserDB.getUserLogged());
                adapter.setToDoList(toDoList);
                return 1;
            }
        });
        try {
            x = result.get();
        } catch (Exception e) {
            x = -1;
            // failed
        }
        es.shutdown();
        if (x == 1) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(int position) {
        Intent i = new Intent(this, ToDoTaskActivity.class);

        startActivityForResult(i, 150);
        pos = position;
        idChosen = toDoList.get(pos).getID();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 150) {
            //Deleted Task
            if (resultCode == 5250) {
                toDoList.remove(pos);
                adapter.notifyItemRemoved(pos);
            }

            //Modified Task
            if (resultCode == 4240) {
                final ToDo[] tmp = new ToDo[1];
                int x;
                ExecutorService es = Executors.newSingleThreadExecutor();
                Future<Integer> result = es.submit(new Callable<Integer>() {
                    public Integer call() throws Exception {
                        toDoList.set(pos,
                                database.toDoDao()
                                        .getToDoTask(getIntent()
                                                .getIntExtra("ID", idChosen)));
                        return 1;
                    }
                });
                try {
                    x = result.get();
                } catch (Exception e) {
                    x = -1;
                    e.printStackTrace();
                    // failed
                }

                //Log.d("ToDoActivity", "sam-sara: " + x + "\t ID: " + idChosen + " = " + getIntent().getIntExtra("id", -1));

                es.shutdown();
                if (x == 1) {

                    /*
                    Log.d("ToDoActivity", "run-sam-sara2: " + toDoList.get(pos).getTask() + " "
                          + toDoList.get(pos).getTime() + "\t" +  toDoList.get(pos).getStatus());
                          */

                    adapter.notifyItemChanged(pos);
                }
            }
        }
    }
}