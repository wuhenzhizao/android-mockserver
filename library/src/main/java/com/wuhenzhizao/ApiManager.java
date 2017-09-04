package com.wuhenzhizao;

import android.content.Context;
import android.util.Log;

import com.demo.BuildConfig;
import com.google.gson.Gson;

import java.lang.ref.WeakReference;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by wuhenzhizao on 15/12/30.
 */
public class ApiManager {
    private static ApiManager instance;
    private Retrofit retrofit;
    private WeakReference<Context> context;
    private String rootUrl;

    private ApiManager() {
    }

    public static ApiManager instance() {
        if (instance != null) {
            return instance;
        }
        synchronized (ApiManager.class) {
            if (instance == null) {
                instance = new ApiManager();
            }
        }
        return instance;
    }

    public void init(Context context, String rootUrl) {
        this.context = new WeakReference<>(context.getApplicationContext());
        this.rootUrl = rootUrl;
    }

    public String getBaseUrl() {
        return rootUrl;
    }

    private Retrofit.Builder getRetrofitBuilder() {
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(new Gson()));
    }

    public <T> T getService(Class<T> service) {
        if (retrofit == null) {
            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
            if (BuildConfig.DEBUG) {
                clientBuilder.addInterceptor(new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {

                    @Override
                    public void log(String message) {
                        Log.d("api", message);
                    }
                }).setLevel(HttpLoggingInterceptor.Level.BASIC));
            }

            Retrofit.Builder builder = getRetrofitBuilder();
            builder.baseUrl(rootUrl);
            builder.client(clientBuilder.build());

            retrofit = builder.build();
        }
        return retrofit.create(service);
    }
}
