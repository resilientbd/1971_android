package com.w3engineers.core.videon.data.remote;

import com.w3engineers.core.videon.data.remote.home.RemoteVideoApiInterface;
import com.w3engineers.core.videon.data.remote.myprofileedit.RemoteMyProfileEditRequestApi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RemoteApiProvider {

    private static final String BASE_URL = "http://192.168.63.108/videon-admin/";
    private static RemoteApiProvider mInstance;
    private Retrofit retrofit;


    private RemoteApiProvider() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized RemoteApiProvider getInstance() {
        if (mInstance == null) {
            mInstance = new RemoteApiProvider();
        }
        return mInstance;
    }


    /**
     * get remote video api for featured and most popular
     * @return retrofit create
     */
    public RemoteVideoApiInterface getRemoteHomeVideoApi(){
        return retrofit.create(RemoteVideoApiInterface.class);
    }


    /**
     * Update my profile
     * @return retrofit create
     */
    public RemoteMyProfileEditRequestApi getRemoteMyProfileApi(){
        return retrofit.create(RemoteMyProfileEditRequestApi.class);
    }

    public static String getBaseUrl() {
        return BASE_URL;
    }
}
