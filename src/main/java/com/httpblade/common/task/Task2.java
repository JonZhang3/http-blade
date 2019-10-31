package com.httpblade.common.task;

import com.httpblade.base.Callback;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class Task2 {

    private CompletableFuture future;

    public Task2(Callback callback) {
        if(callback == null) {
            future = CompletableFuture.supplyAsync(new Supplier<Object>() {
                @Override
                public Object get() {

                    return null;
                }
            });
        }
    }

}
