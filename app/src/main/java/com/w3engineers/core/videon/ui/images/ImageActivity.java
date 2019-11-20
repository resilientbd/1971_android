package com.w3engineers.core.videon.ui.images;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;

import com.w3engineers.core.util.CheckVideoTypeUtil;
import com.w3engineers.core.videon.R;
import com.w3engineers.core.videon.data.local.commondatalistresponse.ApiCommonDetailListResponse;
import com.w3engineers.core.videon.data.local.imagecategories.ImageCategories;
import com.w3engineers.core.videon.data.local.images.Datum;
import com.w3engineers.core.videon.data.local.images.ImageModel;
import com.w3engineers.core.videon.data.remote.RemoteApiProvider;
import com.w3engineers.core.videon.data.remote.home.RemoteVideoApiInterface;
import com.w3engineers.core.videon.databinding.ActivityImagesBinding;
import com.w3engineers.core.videon.databinding.ActivityVideosBinding;

import com.w3engineers.core.videon.ui.adapter.ImageAdapter;

import com.w3engineers.core.videon.ui.imagedetails.ImageDetailsActivity;
import com.w3engineers.ext.strom.application.ui.base.BaseActivity;
import com.w3engineers.ext.strom.application.ui.base.ItemClickListener;
import com.w3engineers.ext.strom.util.helper.Toaster;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ImageActivity extends BaseActivity {

    private ActivityImagesBinding mBinding;
    //This list for search
    public  List<com.w3engineers.core.videon.data.local.imagecategories.Datum> mModelList = new ArrayList<>();

    private RemoteVideoApiInterface mRemoteVideoApiInterface;

    private ImageAdapter imageAdapter;
    public static void runActivity(Context context) {
        Intent intent = new Intent(context, ImageActivity.class);
        runCurrentActivity(context, intent);
    }

    private void initTabs(List<com.w3engineers.core.videon.data.local.imagecategories.Datum> tabs)
    {
        for(com.w3engineers.core.videon.data.local.imagecategories.Datum datum:tabs){
            mBinding.categoryTabLayout.addTab(mBinding.categoryTabLayout.newTab().setText(datum.getCatName()));
        }

    }
    @Override
    protected int getLayoutId() {
        return R.layout.activity_images;
    }

    @Override
    protected void startUI() {
         mBinding=(ActivityImagesBinding) getViewDataBinding();
        mRemoteVideoApiInterface= RemoteApiProvider.getInstance().getRemoteHomeVideoApi();

        imageAdapter=new ImageAdapter(this);


        mBinding.categoryTabLayout.setSelectedTabIndicatorColor(Color.TRANSPARENT);
        mBinding.categoryTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
               getVideosByCategory(mModelList.get(tab.getPosition()).getImgCatId());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        setClickListener(mBinding.backBtn);
        initRecyclerView();
        getVideoCategories();
    }
    private void initRecyclerView(){



        mBinding.imagesRecyclerview.setAdapter(imageAdapter);
        mBinding.imagesRecyclerview.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        imageAdapter.setItemClickListener(new ItemClickListener<com.w3engineers.core.videon.data.local.images.Datum>() {
            @Override
            public void onItemClick(View view, com.w3engineers.core.videon.data.local.images.Datum item) {
                ImageDetailsActivity.runActivity(ImageActivity.this,item);
            }
        });
    }

    private void getVideoCategories()
    {
        mRemoteVideoApiInterface.getAllImageCategories(getResources().getString(R.string.api_token_value),"0").enqueue(new Callback<ImageCategories>() {
            @Override
            public void onResponse(Call<ImageCategories> call, Response<ImageCategories> response) {
                if(response.isSuccessful())
                {
                    ImageCategories apiCommonDetailListResponse=response.body();
                    //adapter.addItem
                    Log.d("datacheck","data:"+apiCommonDetailListResponse.toString());
                    mModelList=apiCommonDetailListResponse.getData();
                    if(mModelList!=null)
                    {
                        initTabs(mModelList);
                    }
                    //getVideosByCategory(mModelList.get(0).getId());


                }
                else {
                    Log.d("datacheck","error:"+response.code());

                }
            }

            @Override
            public void onFailure(Call<ImageCategories> call, Throwable t) {
                Log.d("datacheck","failure:"+t.getMessage());

            }
        });
    }

    private void getVideosByCategory(String cat_id)
    {
        mRemoteVideoApiInterface.getImagesByCategory(getResources().getString(R.string.api_token_value),cat_id,"0").enqueue(new Callback<ImageModel>() {
            @Override
            public void onResponse(Call<ImageModel> call, Response<ImageModel> response) {

                if(response.isSuccessful())
                {
                    List<Datum> videoData=response.body().getData();
                    imageAdapter.clear();
                    if(videoData!=null)
                    {
                        imageAdapter.addItems(videoData);
                    }
                    Log.d("datacheck","error:"+response.body());
                }
                else {
                    Log.d("datacheck","error:"+response.code());
                }
            }

            @Override
            public void onFailure(Call<ImageModel> call, Throwable t) {
                Log.d("datacheck","error:"+t.getMessage());
            }
        });
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if(view.getId()==mBinding.backBtn.getId())
        {
            finish();
        }
    }
}
