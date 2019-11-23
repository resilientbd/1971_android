package com.shadowhite.archieve1971.ui.imagedetails;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;
import com.shadowhite.archieve1971.R;
import com.shadowhite.archieve1971.data.local.images.Datum;
import com.shadowhite.archieve1971.databinding.ActivityImageDetailsPagerBinding;
import com.shadowhite.archieve1971.ui.imagepager.ImagePagerAdapter;
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
