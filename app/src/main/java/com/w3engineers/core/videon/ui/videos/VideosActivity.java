package com.w3engineers.core.videon.ui.videos;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.w3engineers.core.util.CheckVideoTypeUtil;
import com.w3engineers.core.util.helper.AppConstants;
import com.w3engineers.core.util.helper.MySessionManager;
import com.w3engineers.core.util.helper.PrefType;
import com.w3engineers.core.util.helper.SharedPref;
import com.w3engineers.core.videon.R;
import com.w3engineers.core.videon.data.local.Enums;
import com.w3engineers.core.videon.data.local.commondatalistresponse.ApiCommonDetailListResponse;
import com.w3engineers.core.videon.data.local.commondatalistresponse.Datum;
import com.w3engineers.core.videon.data.remote.RemoteApiProvider;
import com.w3engineers.core.videon.data.remote.home.RemoteVideoApiInterface;
import com.w3engineers.core.videon.databinding.ActivityEmptyBinding;
import com.w3engineers.core.videon.databinding.ActivityVideosBinding;
import com.w3engineers.core.videon.ui.adapter.CategoryTabsAdapter;
import com.w3engineers.core.videon.ui.adapter.VideoAdapter;
import com.w3engineers.core.videon.ui.login.LoginActivity;
import com.w3engineers.core.videon.ui.myprofile.MyProfileActivity;
import com.w3engineers.core.videon.ui.searchmovies.SearchMoviesActivity;
import com.w3engineers.core.videon.ui.setting.SettingActivity;
import com.w3engineers.core.videon.ui.videodetails.VideoDetailsActivity;
import com.w3engineers.ext.strom.application.ui.base.BaseActivity;
import com.w3engineers.ext.strom.application.ui.base.ItemClickListener;
import com.w3engineers.ext.strom.util.helper.Toaster;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class VideosActivity extends BaseActivity {

    private ActivityVideosBinding mBinding;
    //This list for search
    public  List<Datum> mModelList = new ArrayList<>();

    private RemoteVideoApiInterface mRemoteVideoApiInterface;
    private CategoryTabsAdapter adapter;
    private VideoAdapter videoadapter;
    public static void runActivity(Context context) {
        Intent intent = new Intent(context, VideosActivity.class);
        runCurrentActivity(context, intent);
    }

    private void initTabs(List<Datum> tabs)
    {
        for(Datum datum:tabs){
            mBinding.categoryTabLayout.addTab(mBinding.categoryTabLayout.newTab().setText(datum.getTitle()));
        }

    }
    @Override
    protected int getLayoutId() {
        return R.layout.activity_videos;
    }

    @Override
    protected void startUI() {
         mBinding=(ActivityVideosBinding)getViewDataBinding();
        mRemoteVideoApiInterface= RemoteApiProvider.getInstance().getRemoteHomeVideoApi();
        adapter=new CategoryTabsAdapter(this);
        videoadapter=new VideoAdapter(this);


        mBinding.categoryTabLayout.setSelectedTabIndicatorColor(Color.TRANSPARENT);
        mBinding.categoryTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
               getVideosByCategory(mModelList.get(tab.getPosition()).getId());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        setClickListener(mBinding.imageView2);
        initRecyclerView();
        getVideoCategories();
    }
    private void initRecyclerView(){



        mBinding.videosRecyclerview.setAdapter(videoadapter);
        mBinding.videosRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        videoadapter.setItemClickListener(new ItemClickListener<Datum>() {
            @Override
            public void onItemClick(View view, Datum item) {
                CheckVideoTypeUtil.checkVideoType(item);
                VideoDetailsActivity.runActivity(VideosActivity.this,item);
            }
        });
    }

    private void getVideoCategories()
    {
        mRemoteVideoApiInterface.getCategoryVideos(getResources().getString(R.string.api_token_value),"0").enqueue(new Callback<ApiCommonDetailListResponse>() {
            @Override
            public void onResponse(Call<ApiCommonDetailListResponse> call, Response<ApiCommonDetailListResponse> response) {
                if(response.isSuccessful())
                {
                    ApiCommonDetailListResponse apiCommonDetailListResponse=response.body();
                    //adapter.addItem
                    Log.d("datacheck","data:"+apiCommonDetailListResponse.toString());
                    mModelList=apiCommonDetailListResponse.getData();

                    initTabs(mModelList);
                    getVideosByCategory(mModelList.get(0).getId());


                }
                else {
                    Log.d("datacheck","error:"+response.code());

                }
            }

            @Override
            public void onFailure(Call<ApiCommonDetailListResponse> call, Throwable t) {
                Log.d("datacheck","failure:"+t.getMessage());

            }
        });
    }

    private void getVideosByCategory(String cat_id)
    {
        mRemoteVideoApiInterface.getVideosByCategory(getResources().getString(R.string.api_token_value),cat_id,"0").enqueue(new Callback<ApiCommonDetailListResponse>() {
            @Override
            public void onResponse(Call<ApiCommonDetailListResponse> call, Response<ApiCommonDetailListResponse> response) {

                if(response.isSuccessful())
                {
                    List<Datum> videoData=response.body().getData();
                    videoadapter.clear();
                    if(videoData!=null)
                    {
                        videoadapter.addItems(videoData);
                    }
                    Log.d("datacheck","error:"+response.body());
                }
                else {
                    Log.d("datacheck","error:"+response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiCommonDetailListResponse> call, Throwable t) {
                Log.d("datacheck","error:"+t.getMessage());
            }
        });
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if(view.getId()==mBinding.imageView2.getId())
        {
            finish();
        }
    }
}
