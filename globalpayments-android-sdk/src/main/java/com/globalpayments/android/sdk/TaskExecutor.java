package com.globalpayments.android.sdk;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TaskExecutor {
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    private <T> void execute(final Task<T> task) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final T result = task.executeAsync();
                    if (result == null) {
                        callOnError(task, new Exception("Null response"));
                    } else {
                        callOnSuccess(task, result);
                    }
                } catch (final Exception e) {
                    callOnError(task, e);
                }
            }
        });
    }

    private <T> void callOnSuccess(final Task<T> task, final T result) {
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                task.onSuccess(result);
            }
        });
    }

    private <T> void callOnError(final Task<T> task, final Exception exception) {
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                task.onError(exception);
            }
        });
    }

    public static <T> void executeAsync(final Task<T> task) {
        new TaskExecutor().execute(task);
    }

    public interface Task<T> {
        T executeAsync() throws Exception;
        void onSuccess(T value);
        void onError(Exception exception);
    }
}
