package com.shadowhite.archieve1971.ui.images;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;

import com.shadowhite.archieve1971.R;
import com.shadowhite.archieve1971.data.local.imagecategories.Datum;
import com.shadowhite.archieve1971.data.local.imagecategories.ImageCategories;
import com.shadowhite.archieve1971.data.local.images.ImageModel;
import com.shadowhite.archieve1971.data.remote.RemoteApiProvider;
import com.shadowhite.archieve1971.data.remote.home.RemoteVideoApiInterface;
import com.shadowhite.archieve1971.databinding.ActivityImagesBinding;
import com.shadowhite.archieve1971.ui.adapter.ImageAdapter;
import com.shadowhite.archieve1971.ui.imagedetails.ImageDetailsPagerActivity;
import com.w3engineers.ext.strom.application.ui.base.BaseActivity;
import com.w3engineers.ext.strom.application.ui.base.ItemClickListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ImageActivity extends BaseActivity {

    private ActivityImagesBinding mBinding;
    //This list for search
    public  List<Datum> mModelList = new ArrayList<>();

    private RemoteVideoApiInterface mRemoteVideoApiInterface;

    private ImageAdapter imageAdapter;
    public static void runActivity(Context context) {
        Intent intent = new Intent(context, ImageActivity.class);
        runCurrentActivity(context, intent);
    }

    private void initTabs(List<Datum> tabs)
    {
        for(Datum datum:tabs){
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

        imageAdapter.setItemClickListener(new ItemClickListener<com.shadowhite.archieve1971.data.local.images.Datum>() {
            @Override
            public void onItemClick(View view, com.shadowhite.archieve1971.data.local.images.Datum item) {
               int position=0;
                for(com.shadowhite.archieve1971.data.local.images.Datum datum:mData)
                {
                    if(datum==item)
                    {
                        break;
                    }
                    position++;
                }
                ImageDetailsPagerActivity.runActivity(ImageActivity.this,mData,position);
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
    private List<com.shadowhite.archieve1971.data.local.images.Datum> mData;
    private void getVideosByCategory(String cat_id)
    {
        mRemoteVideoApiInterface.getImagesByCategory(getResources().getString(R.string.api_token_value),cat_id,"0").enqueue(new Callback<ImageModel>() {
            @Override
            public void onResponse(Call<ImageModel> call, Response<ImageModel> response) {

                if(response.isSuccessful())
                {
                    List<com.shadowhite.archieve1971.data.local.images.Datum> videoData=response.body().getData();
                    imageAdapter.clear();
                    if(videoData!=null)
                    {
                        mData=videoData;
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
