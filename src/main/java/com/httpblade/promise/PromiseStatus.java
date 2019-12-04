package com.httpblade.promise;

interface PromiseStatus {

    int PENDING = 1;// 等待状态，未执行或正在执行中
    int FULFILLED = 2;// 完成状态，执行完成
    int REJECTED = 3;// 拒绝状态，产生异常

}
