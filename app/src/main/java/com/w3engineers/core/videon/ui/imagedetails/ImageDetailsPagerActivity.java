package com.w3engineers.core.videon.ui.imagedetails;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.w3engineers.core.util.NetworkURL;
import com.w3engineers.core.videon.R;
import com.w3engineers.core.videon.data.local.images.Datum;
import com.w3engineers.core.videon.data.remote.RemoteApiProvider;
import com.w3engineers.core.videon.data.remote.home.RemoteVideoApiInterface;
import com.w3engineers.core.videon.databinding.ActivityImageDetailsBinding;
import com.w3engineers.core.videon.databinding.ActivityImageDetailsPagerBinding;
import com.w3engineers.core.videon.ui.home.UICommunicator;
import com.w3engineers.core.videon.ui.imagepager.ImagePagerAdapter;
import com.w3engineers.ext.strom.application.ui.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;


public class ImageDetailsPagerActivity extends BaseActivity {


    private ActivityImageDetailsPagerBinding mBinding;
    private ImagePagerAdapter mAdapter;
    public static void runActivity(Context context) {
        Intent intent = new Intent(context, ImageDetailsPagerActivity.class);
        runCurrentActivity(context, intent);
    }
    public static void runActivity(Context context, List<Datum> datum, int position) {
        Intent intent = new Intent(context, ImageDetailsPagerActivity.class);
        Gson gson=new Gson();
        intent.putExtra("image",gson.toJson(datum));
        intent.putExtra("position",position);
        runCurrentActivity(context, intent);
    }



    @Override
    protected int getLayoutId() {
        return R.layout.activity_image_details_pager;
    }

    @Override
    protected void startUI() {
         mBinding=(ActivityImageDetailsPagerBinding)getViewDataBinding();
         setClickListener(mBinding.backBtn);

        Gson gson=new Gson();
        try{
            List<Datum> datalist=new ArrayList<>();
            List<Datum> imageList=gson.fromJson(getIntent().getStringExtra("image"),datalist.getClass());
            int position=getIntent().getIntExtra("position",0);
            if(imageList.size()>0){
                mAdapter=new ImagePagerAdapter(getSupportFragmentManager(),imageList);
                mBinding.viewpager.setAdapter(mAdapter);
                mBinding.viewpager.setCurrentItem(position,true);
            }
        }catch (Exception e)
        {
            Log.d("imageparseerror","Exception:"+e);
        }
    }



}
