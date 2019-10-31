package com.httpblade.common.task;

import com.httpblade.base.Callback;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

public abstract class Task implements Runnable {

    private Callback callback;

    protected Task(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void run() {
        execute();
    }

    public abstract void execute();

    void executeOn(AsyncTaskExecutor executor, ExecutorService executorService) {
        assert !Thread.holdsLock(executor);
        try {
            executorService.execute(this);
        } catch (RejectedExecutionException e) {
            if (callback != null) {
                callback.error(e);
            }
        } finally {
            executor.finished(this);
        }

    }

}
