package com.hesham.wallet.ToDoModel;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.hesham.wallet.AddNewTask;
import com.hesham.wallet.R;
import com.hesham.wallet.ToDoActivity;
import com.hesham.wallet.database.AppExecutor;
import com.hesham.wallet.database.UserDB;
import com.hesham.wallet.entity.ToDo;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class todoAdapter extends RecyclerView.Adapter<todoAdapter.VH> {

    private List<ToDo> toDoList;
    private final ToDoActivity activity;
    private final UserDB database;
    private final AppExecutor appExecutor;
    private onClickInterface clickInterface;
    private static int idForTodo;

    public static int getIdForTodo() {
        return idForTodo;
    }

    public todoAdapter(ToDoActivity activity) {
        this.activity = activity;
        database = UserDB.getInstance(activity.getApplicationContext());
        appExecutor = new AppExecutor();
        toDoList = new ArrayList<>();
        this.clickInterface = activity;
    }

    public void setToDoList(List<ToDo> toDoList) {
        this.toDoList = toDoList;
    }

    @NonNull
    @NotNull
    @Override
    public VH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.todo_task_item, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull VH holder, final int position) {
        final ToDo tmp = toDoList.get(position);
        holder.task.setText(tmp.getTask());
        String endDate = tmp.getEndDate();
        if(endDate != null)
        {
            holder.due_view.setVisibility(View.VISIBLE);
            holder.txt_end.setText(endDate + " " + tmp.getTime());
        }

        holder.task.setChecked(getStatus(tmp.getStatus()));
        holder.task.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    tmp.setStatus(1);
                else {
                    tmp.setStatus(0);
                }
                appExecutor.getmDiskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        tmp.setUser(UserDB.getUserLogged());
                        database.toDoDao().insertTask(tmp);
                    }
                });
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                idForTodo = tmp.getID();

                clickInterface.onItemClick(position);

            }
        });
    }

    boolean getStatus(int value) {
        return value != 0;
    }

    @Override
    public int getItemCount() {
        if (toDoList != null)
            return toDoList.size();
        else return 0;
    }

    public Context getContext() {
        return activity;
    }

    public void deleteItem(final int position) {
        int x = 0;
        ExecutorService es = Executors.newSingleThreadExecutor();
        Future<Integer> result = es.submit(new Callable<Integer>() {
            public Integer call() throws Exception {
                database.toDoDao().DeleteTask(toDoList.get(position));
                toDoList.remove(position);
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
            notifyItemRemoved(position);
        }
    }

    public void editItem(int position) {
        ToDo item = toDoList.get(position);
        Bundle bundle = new Bundle();
        bundle.putInt("id", item.getID());
        bundle.putInt("status", item.getStatus());
        bundle.putString("task", item.getTask());
        bundle.putString("startDate", item.date);
        bundle.putString("endDate", item.endDate);

        AddNewTask fragment = new AddNewTask();
        fragment.setArguments(bundle);
        fragment.show(activity.getSupportFragmentManager(), AddNewTask.TAG);
    }

    class VH extends RecyclerView.ViewHolder {
        CheckBox task;
        TextView txt_end, due_view;
        public VH(View itemView) {
            super(itemView);
            task = itemView.findViewById(R.id.todoCheckbox);
            due_view = itemView.findViewById(R.id.due_view);
            txt_end = itemView.findViewById(R.id.txt_end_date);
        }
    }
    public interface onClickInterface{
        void onItemClick(int position);
    }
}
