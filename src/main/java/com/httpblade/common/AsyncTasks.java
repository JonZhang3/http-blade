package com.httpblade.common;

import com.httpblade.Callback;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AsyncTasks {

    private final Deque<Runnable> queue = new ArrayDeque<>();

    private ExecutorService executorService;

    public AsyncTasks() {
        this.executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
            new SynchronousQueue<>(), r -> new Thread(r, "HttpBlade Async Thread"));
    }

    public void execute(final Runnable runnable) {
        if(runnable == null) {
            return;
        }
        synchronized (this) {
            queue.offer(() -> {
                try {
                    runnable.run();
                } finally {
                    executeTask(queue.poll());
                }
            });
        }
        executeTask(queue.poll());
    }

    private void executeTask(Runnable r) {
        if(r != null) {
            executorService.execute(r);
        }
    }

    public static abstract class Task implements Runnable {

        protected Callback callback;

        public Task(Callback callback) {
            this.callback = callback;
        }

        @Override
        public void run() {
            execute();
        }

        protected abstract void execute();

    }

}
