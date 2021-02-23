package com.hesham.wallet;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.hesham.wallet.ToDoModel.todoAdapter;
import com.hesham.wallet.database.AppExecutor;
import com.hesham.wallet.database.UserDB;
import com.hesham.wallet.entity.ToDo;

import java.util.Calendar;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ToDoTaskActivity extends AppCompatActivity {
    private EditText task, date, time;
    private CheckBox finished;
    ToDo toDo;
    private UserDB database;
    private AppExecutor appExecutor;
    private int id;
    private boolean Changed = false, cbVal = false;
    private TextView started_time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_task);

        init();
        getsData();

    }

    private void init(){

        id = todoAdapter.getIdForTodo();
        started_time = findViewById(R.id.started_time_view);
        task = findViewById(R.id.task_edit_text);
        date = findViewById(R.id.date_edit_text);
        time = findViewById(R.id.time_edit_text);
        finished = findViewById(R.id.task_finished_cb);
        database = UserDB.getInstance(getApplicationContext());
        appExecutor = new AppExecutor();
        task.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Changed = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void getsData(){
        int x = 0;
        ExecutorService es = Executors.newSingleThreadExecutor();
        Future<Integer> result = es.submit(new Callable<Integer>() {
            public Integer call() throws Exception {
                toDo = database.toDoDao().getToDoTask(id);

                task.setText(toDo.getTask());
                date.setText(toDo.getEndDate());
                time.setText(toDo.getTime());
                started_time.setText(toDo.getDate());
                cbVal = toDo.getStatus() == 1;
                if(toDo == null || toDo.getStatus() == 0)
                    finished.setChecked(false);
                else
                    finished.setChecked(true);

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
            setListeners();
        }

    }
    private void setListeners(){

        final Calendar c = Calendar.getInstance();
        final int year = c.get(Calendar.YEAR);
        final int month = c.get(Calendar.MONTH);
        final int mDay = c.get(Calendar.DAY_OF_MONTH);
        final int mHour = c.get(Calendar.HOUR_OF_DAY);
        final int mMinute = c.get(Calendar.MINUTE);

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                DatePickerDialog datePickerDialog = new DatePickerDialog(ToDoTaskActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                String finalDate = year + "/" + (month + 1) + "/" + mDay;

                                date.setText(finalDate);
                                Changed = true;
                            }
                        }, year, month, mDay);
                datePickerDialog.show();
            }

        });
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(ToDoTaskActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String finalTime;
                        if(hourOfDay > 12)
                            finalTime = (hourOfDay - 12) + ":" + minute + " PM";
                        else
                            finalTime = hourOfDay + ":" + minute + " AM";
                        time.setText(finalTime);
                        Changed = true;
                    }

                }, mHour, mMinute, true);
                timePickerDialog.show();

            }
        });

        finished.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toDo.setStatus(isChecked ? 1 : 0);

                cbVal = isChecked;

                Changed = true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.task_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int chosen = item.getItemId();
        switch (chosen) {
            case R.id.menu_share:
                ShareIt();
                return true;

            case R.id.save_task_info:
                SaveTask();
                return true;

            case R.id.delete_task_option:
                deleteTask();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void ShareIt(){
        Intent i = new Intent();
        i.setAction(Intent.ACTION_SEND);
        i.putExtra(Intent.EXTRA_TEXT,  toDo.getTask());
        i.setType("text/plain");
        Intent j = Intent.createChooser(i,"Here's My New Task To Accomplish");
        startActivity(j);
    }

    private void deleteTask(){
        appExecutor.getmDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                database.toDoDao().DeleteTaskByID(id);

                Intent i = new Intent();
                setResult(5250);
                ToDoTaskActivity.this.finish();
            }
        });
    }

    private void SaveTask(){
        appExecutor.getmDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                Calendar dateNow = Calendar.getInstance();
                String startDate = dateNow.get(Calendar.YEAR) + "/"
                        + (dateNow.get(Calendar.MONTH) + 1) + "/"
                        + dateNow.get(Calendar.DAY_OF_MONTH);

                toDo.setEndDate(date.getText().toString());
                toDo.setDate(startDate);

                toDo.setTime(time.getText().toString());
                toDo.setStatus(cbVal == true ? 1: 0);
                toDo.setTask(task.getText().toString());
                Log.d("ToDoTaskActivity", "run-sam-sara: " + date.getText().toString() + " "
                + time.getText().toString() + "\t" +  cbVal);
                toDo.setUser(UserDB.getUserLogged());
                database.toDoDao().insertTask(toDo);
                Intent i = new Intent();
                i.putExtra("ID", id);
                setResult(4240);

                ToDoTaskActivity.this.finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (Changed)
        {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Quit without Saving?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        }
        else
        {
            finish();
        }
    }
}