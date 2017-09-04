package com.wuhenzhizao;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.http.HttpEngine;

/**
 * Created by wuhenzhizao on 2017/6/7.
 */
public class OkHttpClientStub extends OkHttpClient {
    private volatile List<MockCall> mockCallList;

    public OkHttpClientStub() {
        mockCallList = new LinkedList<>();
    }

    public void mockResponse(String path, Response.Builder mockBuilder) {
        MockCall call = new MockCall();
        call.setPath(path);
        call.setResponseBuilder(mockBuilder);
        mockCallList.add(call);
    }

    @Override
    public Call newCall(Request request) {
        String path = request.url().uri().getPath();
        for (MockCall call : mockCallList) {
            if (path.contains(call.getPath())) {
                mockCallList.remove(call);
                call.setRequest(request);
                return call;
            }
        }
        return null;
    }

    public class MockCall implements Call {
        private boolean executed;
        volatile boolean canceled;
        private String path;

        /**
         * The application's original request unadulterated by redirects or auth headers.
         */
        Request originalRequest;
        Response.Builder mockResponseBuilder;
        HttpEngine engine;

        protected MockCall() {
        }

        public void setRequest(Request originalRequest) {
            this.originalRequest = originalRequest;
        }

        public void setResponseBuilder(Response.Builder mockResponseBuilder) {
            this.mockResponseBuilder = mockResponseBuilder;
        }

        @Override
        public Request request() {
            return originalRequest;
        }

        @Override
        public Response execute() throws IOException {
            return mockResponseBuilder.request(originalRequest).build();
        }

        @Override
        public void enqueue(Callback responseCallback) {
            synchronized (this) {
                if (executed) throw new IllegalStateException("Already Executed");
                executed = true;
            }

            int code = mockResponseBuilder.request(originalRequest).build().code();
            if (code >= 200 && code < 300) {
                try {
                    if (mockResponseBuilder != null) {
                        responseCallback.onResponse(this, mockResponseBuilder.build());
                    }
                } catch (IOException e) {
                    // Nothing
                }
            } else {
                responseCallback.onFailure(this, new IOException("Mock responseCallback onFailure"));
            }
        }

        @Override
        public void cancel() {
            canceled = true;
            if (engine != null) engine.cancel();
        }

        @Override
        public synchronized boolean isExecuted() {
            return executed;
        }

        @Override
        public boolean isCanceled() {
            return canceled;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }
}
