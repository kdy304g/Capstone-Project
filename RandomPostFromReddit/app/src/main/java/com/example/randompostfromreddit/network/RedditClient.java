package com.example.randompostfromreddit.network;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.randompostfromreddit.ui.MainActivity;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RedditClient {

    public static RedditService getRedditService(){
        Context applicationContext = MainActivity.getContextOfApplication();
        final SharedPreferences pref = applicationContext.getSharedPreferences("AppPref", Context.MODE_PRIVATE);
        final String token = pref.getString("token","");

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        TokenAuthenticator tokenAuthenticator = new TokenAuthenticator();

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request.Builder ongoing = chain.request().newBuilder();
                        ongoing.addHeader("Authorization", "bearer " + token);
                        Response response = chain.proceed((ongoing.build()));
                        return response;
                    }
                }).authenticator(tokenAuthenticator)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://oauth.reddit.com")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();

        return retrofit.create(RedditService.class);
    }

}
