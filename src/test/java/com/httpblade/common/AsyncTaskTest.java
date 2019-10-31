package com.httpblade.common;

import com.httpblade.common.task.AsyncTaskExecutor;
import com.httpblade.common.task.Task;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class AsyncTaskTest {

    @Test
    public void test() {
        AsyncTaskExecutor executor = new AsyncTaskExecutor();
        CountDownLatch latch = new CountDownLatch(100);
        AtomicInteger ai = new AtomicInteger(0);
        for (int i = 0; i < 100; i++) {
            executor.enqueue(new Task(null) {
                @Override
                public void execute() {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ignore) {
                    }
                    ai.incrementAndGet();
                    latch.countDown();
                }
            });
        }
        assertTrue(true);
        try {
            latch.await();
        } catch (InterruptedException ignore) {
        }
        assertEquals(100, ai.get());

    }

}
