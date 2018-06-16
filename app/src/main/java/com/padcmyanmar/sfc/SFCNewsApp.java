package com.padcmyanmar.sfc;

import android.app.Application;

import com.google.gson.Gson;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.padcmyanmar.sfc.data.models.NewsModel;
import com.padcmyanmar.sfc.data.vo.NewsVO;
import com.padcmyanmar.sfc.network.MMNewsAPI;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by aung on 11/4/17.
 */

public class SFCNewsApp extends Application {
    private MMNewsAPI mmNewsAPI;
    public static final String LOG_TAG = "SFCNewsApp";

    @Override
    public void onCreate() {
        super.onCreate();
        NewsModel.initDatabase(getApplicationContext());
        initMMNewsAPI();
    }

    public MMNewsAPI getMMNewsAPI() {
        initMMNewsAPI();
        return mmNewsAPI;
    }

    private void initMMNewsAPI() {
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("http://padcmyanmar.com/padc-3/mm-news/apis/")
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        mmNewsAPI = retrofit.create(MMNewsAPI.class);
    }
}
