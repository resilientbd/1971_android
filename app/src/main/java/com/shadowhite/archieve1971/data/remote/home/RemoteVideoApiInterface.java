package com.shadowhite.archieve1971.data.remote.home;

import com.shadowhite.archieve1971.data.local.apimodels.AdResponse;
import com.shadowhite.archieve1971.data.local.audio.Audio;
import com.shadowhite.archieve1971.data.local.audiocategories.AudioCategories;
import com.shadowhite.archieve1971.data.local.authentication.AuthenticationModel;
import com.shadowhite.archieve1971.data.local.commondatalistresponse.ApiCommonDetailListResponse;
import com.shadowhite.archieve1971.data.local.commondatalistresponse.VideoDetailsByIdResponseModel;
import com.shadowhite.archieve1971.data.local.document.Documents;
import com.shadowhite.archieve1971.data.local.documentcategories.DocumentCategories;
import com.shadowhite.archieve1971.data.local.downloadsetting.DownloadSettingResponse;
import com.shadowhite.archieve1971.data.local.imagecategories.ImageCategories;
import com.shadowhite.archieve1971.data.local.images.ImageModel;
import com.shadowhite.archieve1971.data.local.userstatus.UserStatus;
import com.shadowhite.archieve1971.ui.videodetails.ViewCountResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface RemoteVideoApiInterface {
    /**
     * Request to server for registration
     * */
    @FormUrlEncoded
    @POST("public/api/user/register.php")
    Call<AuthenticationModel> preformRegistrationRequest(@Field("type") int type, @Field("social_id") String social_id, @Field("username") String username, @Field("email") String email, @Field("password") String password, @Field("api_token") String api_token);
    /**
     * Request to server for login
     * */
    @FormUrlEncoded
    @POST("public/api/user/login.php")
    Call<AuthenticationModel> preformLoginRequest(@Field("email") String email,@Field("password") String password,@Field("api_token") String api_token);
    /**
     * Request to server for the live tv channels
     * */
    @FormUrlEncoded
    @POST("public/api/livetv/all.php")
    Call<ApiCommonDetailListResponse> getLiveTVChannelVideos(@Field("api_token") String api_token, @Field("page") String pageNo);



    /**
     * Request to server for the featured videos
     * */
    @FormUrlEncoded
    @POST("public/api/video/popular.php")
    Call<ApiCommonDetailListResponse> getFeaturedVideos(@Field("api_token") String api_token, @Field("page") String pageNo);



    /**
     * Request to server for the popular videos
     * */
    @FormUrlEncoded
    @POST("public/api/video/recent.php")
    Call<ApiCommonDetailListResponse> getMostPopularVideos(@Field("api_token") String api_token, @Field("page") String pageNo);



    /**
     * Request to server for the category list
     * */
    @FormUrlEncoded
    @POST("public/api/category/all.php")
    Call<ApiCommonDetailListResponse> getCategoryVideos(@Field("api_token") String api_token, @Field("page") String pageNo);


    /**
     * Request to server for the category list
     * */
    @FormUrlEncoded
    @POST("public/api/video/by-category.php")
    Call<ApiCommonDetailListResponse> getVideosByCategory(@Field("api_token") String api_token, @Field("category") String categoryId, @Field("page") String pageNo);


    /**
     * Request to server for the category list
     * */
    @FormUrlEncoded
    @POST
    Call<ApiCommonDetailListResponse> getSingleCategoryVideos(@Url String url, @Field("api_token") String api_token, @Field("category") String categoryId, @Field("page") String pageNo);

    /**
     * Request to server for the recent video list
     * */
    @FormUrlEncoded
    @POST("public/api/video/recent.php")
    Call<ApiCommonDetailListResponse> getRecentVideo(@Field("api_token") String api_token, @Field("page") String pageNo);
    /**
     * Request to server for the recent video list
     * */
    @FormUrlEncoded
    @POST("public/api/video/one.php")
    Call<VideoDetailsByIdResponseModel> getVideoDetailsByID(@Field("api_token") String api_token, @Field("id") String id);
    /**
     * Request to server for the suggested video
     * */
    @FormUrlEncoded
    @POST("public/api/video/suggested.php")
    Call<ApiCommonDetailListResponse> getSimilarVideos(@Field(value = "id", encoded = true) String video_id, @Field("api_token") String api_token);
    /**
     * Request to server for suggested live video
     * for live tv
     * */
    @FormUrlEncoded
    @POST("public/api/livetv/suggested.php")
    Call<ApiCommonDetailListResponse> getSimilarLiveVideos(@Field(value = "id", encoded = true) String video_id, @Field("api_token") String api_token);

    /**
     * Post to server for add rating
     * */
    @FormUrlEncoded
    @POST("public/api/review/add.php")
    Call<AuthenticationModel> setRating(@Field("user_id") String userid,@Field("video_id") String videoId,@Field("rating") String rating_value,@Field("review") String rating_text,@Field("api_token") String api_token);

    /**
     * Post to server for remove favourite
     * */
    @FormUrlEncoded
    @POST("public/api/favourite/remove.php")
    Call<AuthenticationModel> removeFavourite(@Field("user_id") String userid,@Field("video_id") String videoId,@Field("api_token") String api_token);

    /**
     * Post to server for add favourite
     * */
    @FormUrlEncoded
    @POST("public/api/favourite/add.php")
    Call<AuthenticationModel> addFavourite(@Field("user_id") String userid,@Field("video_id") String videoId,@Field("api_token") String api_token);

    /**
     * Post to server for increment view count
     * */
    @FormUrlEncoded
    @POST("public/api/video/add-view-count.php")
    Call<ViewCountResponse> incrementViewCount(@Field(value = "id", encoded = true) String video_id, @Field("api_token") String api_token);



    /**
     * Get all videos by catory from server
     * */
    @FormUrlEncoded
    @POST
    Call<ApiCommonDetailListResponse> getSeeAllVideos(@Url String url, @Field("api_token") String api_token, @Field("page") String pageNo);

    /**
     * Get all videos by catory from server
     * */

    @FormUrlEncoded
    @POST("public/api/favourite/by-user.php")
    Call<ApiCommonDetailListResponse> getAllFavVideosByUser(@Field("api_token") String api_token, @Field("page") String pageNo, @Field("user_id") String user_id);

    /**
     * Get all videos by catory from server
     * */
    @FormUrlEncoded
    @POST("public/api/playlist/by-user.php")
    Call<ApiCommonDetailListResponse> getPlaylistByUser(@Field("api_token") String api_token, @Field("user_id") String user_id, @Field("page") String pageNo);

    @FormUrlEncoded
    @POST
    Call<ApiCommonDetailListResponse> getChannelListVideos(@Url String url, @Field("api_token") String api_token, @Field("page") String pageNo);


    @FormUrlEncoded
    @POST("public/api/video/search.php")
    Call<ApiCommonDetailListResponse> getSearchVideos(@Field("search") String search, @Field("api_token") String api_token, @Field("page") String pageNo);



    /**
     * Post to server for add playlist by user
     * */

    @FormUrlEncoded
    @POST("public/api/playlist/add.php")
    Call<AuthenticationModel> addToPlayList(@Field("user_id") String userid,@Field("video_id") String videoId,@Field("api_token") String api_token);

    /**
     * Post to server for remove playlist
     * */
    @FormUrlEncoded
    @POST("public/api/playlist/remove.php")
    Call<AuthenticationModel> removeFromPlayList(@Field("user_id") String userid,@Field("video_id") String videoId,@Field("api_token") String api_token);
    // http://192.168.2.121/videon/videon-admin/frontend/web/index.php/api/check_all?user_id=8&video_id=30



    /**
     * Get all video status(isFab?isPlayLIsted?isRated) by userid and video id
     * */
    @FormUrlEncoded
    @POST("public/api/video/check-video.php")
    Call<UserStatus>getAllStatus(@Field(value = "user_id", encoded = true) String user_id, @Field(value = "video_id", encoded = true) String video_id, @Field("api_token") String api_token);

    /**
     * Admob api
     * */
    @FormUrlEncoded
    @POST("public/api/admobs/admob.php")
    Call<AdResponse> getAdUnits(@Field("api_token") String api_token);

    /**
     * update password
     * params 3
     * */
    @FormUrlEncoded
    @POST("public/api/user/email-verification.php")
    Call<AuthenticationModel> validateToken(@Field("api_token") String api_token,@Field("email") String email,@Field("verification_token") String token);
    /**
     * Password validation api
     * */
    @FormUrlEncoded
    @POST("public/api/user/send-code.php")
    Call<AuthenticationModel> validateToken(@Field("api_token") String api_token,@Field("email") String email);
    /**
     * Password validation api
     * */
    @FormUrlEncoded
    @POST("public/api/user/send-code.php")
    Call<AuthenticationModel> validateToken(@Field("api_token") String api_token,@Field("email") String email,@Field("token") String token,@Field("new_pass") String password);

    /**
     * Sign up code resend api
     * */
    @FormUrlEncoded
    @POST("public/api/user/email-verification.php")
    Call<ViewCountResponse> signUpValidation(@Field("api_token") String api_token,@Field("email") String email);
    /**
     * Signup code verification api
     * */
    @FormUrlEncoded
    @POST("public/api/user/email-verification.php")
    Call<AuthenticationModel> signUpValidation(@Field("api_token") String api_token,@Field("email") String email,@Field("verification_token") String token);

    /**
     * Download setting on and off
     * @param api_token api_token
     * @return  DownloadSettingResponse
     */
    @FormUrlEncoded
    @POST("public/api/settings/setting.php")
    Call<DownloadSettingResponse> downLoadSetting(@Field("api_token") String api_token);
    /**
     * Get All image categories
     */
    @FormUrlEncoded
    @POST("public/api/category/image-category.php")
    Call<ImageCategories> getAllImageCategories(@Field("api_token") String api_token, @Field("page") String page);

    /**
     * request for images by category
     */
    @FormUrlEncoded
    @POST("public/api/image/by-category.php")
    Call<ImageModel> getImagesByCategory(@Field("api_token") String api_token, @Field("cat_id") String cat_id, @Field("page") String page);
    /**
     * Get All audio categories
     */
    @FormUrlEncoded
    @POST("public/api/category/audio-category.php")
    Call<AudioCategories> getAllAudioCategories(@Field("api_token") String api_token, @Field("page") String page);

    /**
     * request for audio by category
     */
    @FormUrlEncoded
    @POST("public/api/audio/by-category.php")
    Call<Audio> getAudioByCategory(@Field("api_token") String api_token, @Field("cat_id") String cat_id, @Field("page") String page);

    /**
     * Get All document categories
     */
    @FormUrlEncoded
    @POST("public/api/category/document-category.php")
    Call<DocumentCategories> getDocumentCategories(@Field("api_token") String api_token, @Field("page") String page);

    /**
     * request for document by category
     */
    @FormUrlEncoded
    @POST("public/api/document/by-category.php")
    Call<Documents> getDocumentById(@Field("api_token") String api_token, @Field("cat_id") String cat_id, @Field("page") String page);

}
