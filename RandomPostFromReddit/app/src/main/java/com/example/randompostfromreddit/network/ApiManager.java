package com.example.randompostfromreddit.network;
import com.example.randompostfromreddit.utils.Constants;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiManager {
    private ApiService service;

//    public ApiService getService(){
//
//        if(service == null) {
//            Retrofit retrofit = new Retrofit.Builder()
//                    .baseUrl(Constants.BASE_URL)
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .build();
//
//            service = retrofit.create(ApiService.class);
//        }
//        return service;
//    }
}
