package com.httpblade.okhttp;

import com.httpblade.common.HttpHeader;
import com.httpblade.common.HttpStatus;
import com.httpblade.common.Utils;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.Util;
import okhttp3.internal.http.HttpMethod;

import java.io.IOException;

public class RedirectInterceptor implements Interceptor {

    private int maxRedirectCount = 0;

    RedirectInterceptor(int maxRedirectCount) {
        if (maxRedirectCount >= 0) {
            this.maxRedirectCount = maxRedirectCount;
        }
    }

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);

        if(maxRedirectCount < 1) {
            return response;
        }

        int code = response.code();
        String method = request.method();
        // @see okhttp3.internal.http.RetryAndFollowUpInterceptor
        switch (code) {
            case HttpStatus.TEMP_REDIRECT:
            case HttpStatus.PERM_REDIRECT:
                if (!method.equals("GET") && !method.equals("HEAD")) {
                    return response;
                }
            case HttpStatus.MULT_CHOICE:
            case HttpStatus.MOVED_PERM:
            case HttpStatus.MOVED_TEMP:
            case HttpStatus.SEE_OTHER:
                int count = 1;

                while (count <= maxRedirectCount) {
                    String location = response.header(HttpHeader.LOCATION);
                    if (Utils.isEmpty(location)) {
                        return response;
                    }
                    HttpUrl url = response.request().url().resolve(location);
                    if (url == null) {
                        return response;
                    }
                    Request.Builder newBuilder = response.request().newBuilder();
                    if (HttpMethod.permitsRequestBody(method)) {
                        final boolean maintainBody = HttpMethod.redirectsWithBody(method);
                        if (HttpMethod.redirectsToGet(method)) {
                            newBuilder.method(com.httpblade.common.HttpMethod.GET.value(), null);
                        } else {
                            RequestBody requestBody = maintainBody ? response.request().body() : null;
                            newBuilder.method(method, requestBody);
                        }
                        if (!maintainBody) {
                            newBuilder.removeHeader("Transfer-Encoding");
                            newBuilder.removeHeader("Content-Length");
                            newBuilder.removeHeader("Content-Type");
                        }
                    }
                    if (!Util.sameConnection(response.request().url(), url)) {
                        newBuilder.removeHeader(HttpHeader.AUTHORIZATION);
                    }
                    Request newRequest = newBuilder.url(url).build();
                    response = chain.proceed(newRequest);
                }
                return response;
        }
        return response;
    }

}
