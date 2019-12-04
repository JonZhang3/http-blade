package com.httpblade.promise;

public class Promise<T> {

    /**
     * 包装所有的 Promise，当所有的被包装的 promise 都完成时回调完成（resolve）；
     * 如果有一个失败，此实例回调失败（reject），失败原因的是第一个失败 promise 的结果。
     *
     * @param promises Promises 列表
     * @return Promise 实例
     */
    public static <T> Promise<T> all(Promise... promises) {
        return null;
    }

    public static <T> Promise<T> allSettled(Promise... promises) {
        return null;
    }

    public Promise then(T t) {
        return this;
    }

    public Promise catches() {
        return this;
    }

}
