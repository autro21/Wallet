package com.hesham.wallet.database;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExecutor {
    private final Executor mDiskIO;
    private final Executor mNetwork;
    private final Executor mMqinThread;

    private AppExecutor(Executor mDiskIO, Executor mNetwork, Executor mMqinThread) {
        this.mDiskIO = mDiskIO;
        this.mNetwork = mNetwork;
        this.mMqinThread = mMqinThread;
    }

    public AppExecutor() {
        this(Executors.newSingleThreadExecutor(),
                Executors.newFixedThreadPool(3),
                new  MainExecutor());
    }

    public Executor getmDiskIO() {
        return mDiskIO;
    }

    public Executor getmNetwork() {
        return mNetwork;
    }

    public Executor getmMqinThread() {
        return mMqinThread;
    }

}

class MainExecutor implements Executor{
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void execute(Runnable command) {
        handler.post(command);
    }

}
