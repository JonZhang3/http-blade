package com.httpblade.basehttp;

import com.httpblade.HttpBladeException;
import com.httpblade.Callback;
import com.httpblade.CookieHome;
import com.httpblade.HttpClient;
import com.httpblade.Request;
import com.httpblade.Response;
import com.httpblade.common.Headers;
import com.httpblade.common.task.AsyncTaskExecutor;
import com.httpblade.common.task.Task;

import javax.net.SocketFactory;
import javax.net.ssl.HostnameVerifier;
import java.io.IOException;
import java.net.Proxy;
import java.util.Map;

public class BaseHttpClientImpl extends HttpClient {

    Proxy javaProxy;
    private AsyncTaskExecutor asyncExecutor = new AsyncTaskExecutor();

    BaseHttpClientImpl(String baseUrl, long connectTimeout, long readTimeout, long writeTimeout,
                            int maxRedirectCount, CookieHome cookieHome, HostnameVerifier hostnameVerifier,
                            com.httpblade.common.Proxy proxy, SocketFactory socketFactory, Map<String, Headers> globalHeaders) {
        super(baseUrl, connectTimeout, readTimeout, writeTimeout, maxRedirectCount, cookieHome, hostnameVerifier, proxy, socketFactory, globalHeaders);
    }

    @Override
    public Response request(Request request) {
        BaseHttpRequestImpl requestImpl = (BaseHttpRequestImpl) request;
        try {
            return requestImpl.build(this).execute();
        } catch (IOException e) {
            throw new HttpBladeException(e);
        }
    }

    @Override
    public void requestAsync(Request request, Callback callback) {
        BaseHttpRequestImpl requestImpl = (BaseHttpRequestImpl) request;
        final BaseHttpConnection connection = requestImpl.build(this);
        asyncExecutor.enqueue(new Task(callback) {
            @Override
            public void execute() {
                try {
                    Response response = connection.execute();
                    callback.success(response);
                } catch (Exception e) {
                    if (callback != null) {
                        callback.error(e);
                    }
                }
            }
        });
    }

}
