package com.shadowhite.archieve1971.data.remote.myprofileedit;

import com.shadowhite.archieve1971.data.local.authentication.AuthenticationModel;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RemoteMyProfileEditRequestApi {

    @FormUrlEncoded
    @POST("public/api/user/update-profile.php")
    Call<AuthenticationModel> preformMyProfileEditRequest(@Field("id") int userId, @Field("username") String name, @Field("password") String OldPassword, @Field("new_password") String NewPassword, @Field("api_token") String token);

    @FormUrlEncoded
    @POST("public/api/user/update-profile.php")
    Call<AuthenticationModel> preformMyProfileEditForUserNameRequest(@Field("api_token") String token, @Field("id") String userId, @Field("username") String name);
}
