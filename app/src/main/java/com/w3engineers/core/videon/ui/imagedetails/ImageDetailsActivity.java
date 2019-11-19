package com.w3engineers.core.videon.ui.imagedetails;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.w3engineers.core.util.NetworkURL;
import com.w3engineers.core.util.RatioConverter;
import com.w3engineers.core.videon.R;
import com.w3engineers.core.videon.data.local.images.Datum;
import com.w3engineers.core.videon.data.remote.RemoteApiProvider;
import com.w3engineers.core.videon.data.remote.home.RemoteVideoApiInterface;
import com.w3engineers.core.videon.databinding.ActivityDocViewerBinding;
import com.w3engineers.core.videon.databinding.ActivityImageDetailsBinding;
import com.w3engineers.core.videon.databinding.ActivityImagesBinding;
import com.w3engineers.core.videon.ui.login.LoginActivity;
import com.w3engineers.ext.strom.application.ui.base.BaseActivity;


public class ImageDetailsActivity extends BaseActivity {


    private ActivityImageDetailsBinding mBinding;
    private RemoteVideoApiInterface mRemoteVideoApiInterface;
    public static void runActivity(Context context) {
        Intent intent = new Intent(context, ImageDetailsActivity.class);
        runCurrentActivity(context, intent);
    }
    public static void runActivity(Context context, Datum datum) {
        Intent intent = new Intent(context, ImageDetailsActivity.class);
        Gson gson=new Gson();
        intent.putExtra("image",gson.toJson(datum));
        runCurrentActivity(context, intent);
    }


    private void getImageDetails(String imgid)
    {

    }
    @Override
    protected int getLayoutId() {
        return R.layout.activity_image_details;
    }

    @Override
    protected void startUI() {
         mBinding=(ActivityImageDetailsBinding)getViewDataBinding();
setClickListener(mBinding.backBtn,mBinding.upbtn,mBinding.imgmain);
        mRemoteVideoApiInterface= RemoteApiProvider.getInstance().getRemoteHomeVideoApi();
        Gson gson=new Gson();
        try{
            String string=getIntent().getStringExtra("image");
            Datum datum=gson.fromJson(string,Datum.class);
//            Glide.with(ImageDetailsActivity.this).asBitmap().load(NetworkURL.videosEndPointURL+datum.getImgUrl()).listener(new RequestListener<Bitmap>() {
//                @Override
//                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
//                    Log.d("calculate","error:"+e.getMessage());
//                    return false;
//                }
//
//                @Override
//                public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
////                    Display display = getWindowManager().getDefaultDisplay();
////                    DisplayMetrics outMetrics = new DisplayMetrics ();
////                    float density  = getResources().getDisplayMetrics().density;
////                    float dpHeight = outMetrics.heightPixels / density;
////                    float dpWidth  = outMetrics.widthPixels / density;
//
//                    Configuration configuration = getResources().getConfiguration();
//                    int screenWidthDp = configuration.screenWidthDp; //The current width of the available screen space, in dp units, corresponding to screen width resource qualifier.
//                    int smallestScreenWidthDp = configuration.smallestScreenWidthDp;
//                    Log.d("calculate","Display Height:"+RatioConverter.GetRequiredHeight(resource.getWidth(),resource.getHeight(),smallestScreenWidthDp));
//                     Log.d("calculate","Display Width:"+screenWidthDp);
//                     Log.d("calculate","Height:"+resource.getHeight());
//                      Log.d("calculate","Width:"+resource.getWidth());
//                    ViewGroup.LayoutParams params=mBinding.imgmain.getLayoutParams();
//                    params.height= RatioConverter.GetRequiredHeight(resource.getWidth(),resource.getHeight(),smallestScreenWidthDp);
//                    //mBinding.imgmain.setImageBitmap(resource);
//                    //Glide.with(ImageDetailsActivity.this).load(NetworkURL.videosEndPointURL+datum.getImgUrl()).into(mBinding.imgmain);
//                    //Log.d("calculate","Width:"+mBinding.imgmain);
//                    return false;
//                }
//            }).submit();
        Glide.with(ImageDetailsActivity.this).asBitmap().load(NetworkURL.videosEndPointURL+datum.getImageResolution()).into(mBinding.imgmain);
           // Log.d("calculate","Height:"+bitmap.getHeight());
          //  Log.d("calculate","Width:"+bitmap.getWidth());

            mBinding.doctitle.setText(""+datum.getImgTitle());
            mBinding.desc.setText(Html.fromHtml(""+datum.getImgDesc()));
        }catch (Exception e)
        {
            Log.d("calculate","error:"+e.getMessage());

        }
        mBinding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        mBinding.sharebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharecontent();
            }
        });
    }


    void sharecontent()
    {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "Sharable body...";
        String shareSub = "Sharable Subject";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSub);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share using"));
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.upbtn:
                setContentVisibility();
                break;

            case R.id.imgmain:
                if(mBinding.upbtn.getVisibility()==View.GONE)
                {
                    setContentVisibility();
                }
                break;

            case R.id.back_btn:
                finish();
                break;

        }
    }


private void setContentVisibility()
{
    if(mBinding.upbtn.getVisibility()==View.GONE)
    {
        mBinding.descsection.setVisibility(View.GONE);
        mBinding.upbtn.setVisibility(View.VISIBLE);
    }
    else {
        mBinding.descsection.setVisibility(View.VISIBLE);
        mBinding.upbtn.setVisibility(View.GONE);
    }
}




}
