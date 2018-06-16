package com.padcmyanmar.sfc.data.models;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.padcmyanmar.sfc.SFCNewsApp;
import com.padcmyanmar.sfc.data.db.AppDatabase;
import com.padcmyanmar.sfc.data.vo.NewsVO;
import com.padcmyanmar.sfc.data.vo.PublicationVO;
import com.padcmyanmar.sfc.events.RestApiEvents;
import com.padcmyanmar.sfc.network.MMNewsAPI;
import com.padcmyanmar.sfc.network.MMNewsDataAgentImpl;
import com.padcmyanmar.sfc.network.reponses.GetNewsResponse;
import com.padcmyanmar.sfc.utils.AppConstants;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by aung on 12/3/17.
 */

public class NewsModel {

    private static NewsModel objInstance;

    private AppDatabase mAppDatabase;

    private List<NewsVO> mNews;
    private int mmNewsPageIndex = 1;

    private MMNewsAPI mmNewsAPI;

    private NewsModel(Context context) {
        mAppDatabase = AppDatabase.getNewsDatabase(context);
        EventBus.getDefault().register(this);
        mNews = new ArrayList<>();
        initMMNewsAPI();
    }

    public static void initDatabase(Context context) {
        objInstance = new NewsModel(context);
    }

    public static NewsModel getInstance() {
        if (objInstance != null) {
            return objInstance;
        }
        throw new RuntimeException("Error");
    }

    public void startLoadingMMNews(final PublishSubject<GetNewsResponse> publishSubject) {
        // MMNewsDataAgentImpl.getInstance().loadMMNews(AppConstants.ACCESS_TOKEN, mmNewsPageIndex);

        Observable<GetNewsResponse> getNewsResponseObservable = mmNewsAPI.loadMMNews(mmNewsPageIndex, AppConstants.ACCESS_TOKEN);
        getNewsResponseObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GetNewsResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(GetNewsResponse getNewsResponse) {
                        publishSubject.onNext(getNewsResponse);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
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

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onNewsDataLoaded(RestApiEvents.NewsDataLoadedEvent event) {
        for (NewsVO news : event.getLoadNews()) {
            long insertPublicationId = mAppDatabase.publicationDao().insertPublication(news.getPublication());
            Log.d(SFCNewsApp.LOG_TAG, "Total inserted count : " + insertPublicationId);
            mAppDatabase.newsDao().getNews();
            List<PublicationVO> publicationVOS = (List<PublicationVO>) mAppDatabase.publicationDao().getPublication();
        }
    }
}
