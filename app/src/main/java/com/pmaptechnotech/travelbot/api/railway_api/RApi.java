package com.pmaptechnotech.travelbot.api.railway_api;

import android.content.Context;
import android.content.SharedPreferences;

import com.pmaptechnotech.travelbot.api.IPActivity;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Admin on 1/20/2018.
 */

public class RApi {
    public static Retrofit getRetrofitBuilder(Context context, String url) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(IPActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        //String url = P.RAILWAY_URL;//sharedpreferences.getString("url", P.URL);

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);


        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();


        return retrofit;

    }

    public static String getNameToCodeUrl(String stationName) {
        return "https://api.railwayapi.com/v2/name-to-code/station/" + stationName + "/apikey/" ;
    }

    public static String getTrainsBetween(String src, String dest, String date) {
        return "https://api.railwayapi.com/v2/between/source/"+src+"/dest/"+dest+"/date/"+date+"/apikey/";
    }



}