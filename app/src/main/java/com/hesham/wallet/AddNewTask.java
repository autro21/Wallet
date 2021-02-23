package com.hesham.wallet;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.hesham.wallet.database.AppExecutor;
import com.hesham.wallet.database.UserDB;
import com.hesham.wallet.entity.ToDo;
import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

public class AddNewTask extends BottomSheetDialogFragment {

    private EditText newTaskText;
    private Button newTaskSave;
    private static UserDB database;
    private AppExecutor appExecutor;
    private CalendarView cal;
    private String taskDate;
    private final ToDo task = new ToDo();

    private String startDate;
    private boolean isUpdate;
    private Bundle bundle;
    public static String TAG = "ActionBottomDialog";

    public static AddNewTask newInstance() {
        return new AddNewTask();
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.DialogStyle);
        appExecutor = new AppExecutor();
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_todo_task, container, false);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newTaskText = getView().findViewById(R.id.newTodoTask);
        newTaskSave = getView().findViewById(R.id.newTask_btn);
        cal = getView().findViewById(R.id.todo_cal);
        cal.setMinDate(System.currentTimeMillis() - 1000);

        calendarEvent();

        database = UserDB.getInstance(getContext().getApplicationContext());

        isUpdate = false;
        bundle = getArguments();
        if (bundle != null) {
            task.setEndDate(bundle.getString("endDate"));
            isUpdate = true;
            String text = bundle.getString("task");
            newTaskText.setText(text);
            if (text.length() > 0) {
                newTaskSave.setTextColor(ContextCompat.getColor(getContext(), R.color.holo));
            }
        }
        dbUpdate();
        newTaskText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    newTaskSave.setEnabled(false);
                    newTaskSave.setTextColor(Color.GRAY);
                } else {
                    newTaskSave.setEnabled(true);
                    newTaskSave.setTextColor(R.color.holo);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        newTaskSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                dbUpdate();
                appExecutor.getmDiskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        task.setUser(UserDB.getUserLogged());
                        database.toDoDao().insertTask(task);
                        dismiss();
                    }
                });
            }
        });
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        Activity activity = getActivity();
        if (activity instanceof DialogCloseListener) {
            ((DialogCloseListener) activity).handleDialogClose(dialog);

        }
    }

    @Override
    public void onCancel(@NonNull @NotNull DialogInterface dialog) {
        super.onCancel(dialog);
        dismiss();
    }

    private void calendarEvent() {
        cal.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                taskDate = year + "/" + (month + 1) + "/" + dayOfMonth;
                task.setEndDate(taskDate);
            }
        });
    }


    private void dbUpdate() {
        String text = newTaskText.getText().toString();

        int tmpStatus = 0;
        int idTmp = -1;

        Calendar dateNow = Calendar.getInstance();
        startDate = dateNow.get(Calendar.YEAR) + "/"
                + (dateNow.get(Calendar.MONTH) + 1) + "/"
                + dateNow.get(Calendar.DAY_OF_MONTH);

        final boolean finalIsUpdate = isUpdate;
        if (finalIsUpdate) {
            idTmp = bundle.getInt("id");
            tmpStatus = bundle.getInt("status");


            String tmpDate = bundle.getString("startDate");
            if (tmpDate.length() > 0) {
                startDate = tmpDate;
            }
        }
        task.setStatus(tmpStatus);
        task.setTask(text);
        task.setDate(startDate);

        if (idTmp != -1)
            task.setID(idTmp);


    }

}
