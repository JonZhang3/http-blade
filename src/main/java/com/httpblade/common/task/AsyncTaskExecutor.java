package com.httpblade.common.task;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class AsyncTaskExecutor {

    private final Deque<Task> queue = new ArrayDeque<>();
    private final Deque<Task> runningQueue = new ArrayDeque<>();
    private int maxTasks = 64;

    private ExecutorService executorService;

    public AsyncTaskExecutor() {
        this.executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
            new SynchronousQueue<>(), (r) -> new Thread(r, "HttpBlade Async Thread"));
    }

    public void enqueue(Task task) {
        if (task == null) {
            return;
        }
        synchronized (this) {
            queue.add(task);
        }
        execute();
    }

    private void execute() {
        assert (!Thread.holdsLock(this));

        List<Task> executableRunnables = new LinkedList<>();
        synchronized (this) {
            for (Iterator<Task> iterator = queue.iterator(); iterator.hasNext(); ) {
                Task task = iterator.next();

                if (runningQueue.size() >= maxTasks) {
                    break;
                }

                iterator.remove();
                executableRunnables.add(task);
                runningQueue.add(task);
            }
        }

        for (Task task : executableRunnables) {
            task.executeOn(this, executorService);
        }
    }

    void finished(Task task) {
        synchronized (this) {
            runningQueue.remove(task);
        }
    }

}
