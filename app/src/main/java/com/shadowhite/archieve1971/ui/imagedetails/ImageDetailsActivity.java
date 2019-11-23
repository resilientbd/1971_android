package com.shadowhite.archieve1971.ui.imagedetails;

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
import com.shadowhite.archieve1971.R;
import com.shadowhite.archieve1971.data.local.images.Datum;
import com.shadowhite.archieve1971.data.remote.RemoteApiProvider;
import com.shadowhite.archieve1971.data.remote.home.RemoteVideoApiInterface;
import com.shadowhite.archieve1971.databinding.ActivityImageDetailsBinding;
import com.shadowhite.archieve1971.ui.home.UICommunicator;
import com.shadowhite.util.NetworkURL;
import com.w3engineers.ext.strom.application.ui.base.BaseActivity;


public class ImageDetailsActivity extends BaseActivity implements UICommunicator {


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
//
//                   // Log.d("calculate","Display Height:"+);
//                     Log.d("calculate","Display Width:"+screenWidthDp);
//                     Log.d("calculate","Height:"+resource.getHeight());
//                      Log.d("calculate","Width:"+resource.getWidth());
//                      Log.d("calculate","Width:"+RatioConverter.GetRequiredHeight(resource.getWidth(),resource.getHeight(),screenWidthDp));
//                 //  Integer[] height= ResoulationConverter.ConvertResoulationHeight(ImageDetailsActivity.this,resource.getWidth()+":"+resource.getHeight(),(float)screenWidthDp);
//                  //  Log.d("calculate",""+height[0]+":"+height[1]);
//                 //   ViewGroup.LayoutParams params=mBinding.imgmain.getLayoutParams();
//                 //   params.height= height[0];
//                  //  mBinding.imgmain.setLayoutParams(params);
//
//                   // mBinding.imgmain.setImageBitmap(resource);
//                   // RatioConverter.GetResizedBitmap(resource,screenWidthDp,RatioConverter.GetRequiredHeight(resource.getWidth(),resource.getHeight(),screenWidthDp));
//                //  mBinding.imgmain.setImageBitmap(RatioConverter.GetResizedBitmap(resource,screenWidthDp,RatioConverter.GetRequiredHeight(resource.getWidth(),resource.getHeight(),screenWidthDp)));
//                  //  LoadImage(mBinding.imgmain,mBinding.progressBar,NetworkURL.videosEndPointURL+datum.getImgUrl(),height);
//                   Glide.with(ImageDetailsActivity.this).load(NetworkURL.videosEndPointURL+datum.getImgUrl()).into(mBinding.imgmain);
//                    //Log.d("calculate","Width:"+mBinding.imgmain);
//                    return false;
//                }
//            }).submit();
            RequestOptions requestOptions = new RequestOptions();
            requestOptions = requestOptions.error(R.drawable.default_img); // resizes the image to these dimensions (in pixel)
            Glide.with(ImageDetailsActivity.this).load(NetworkURL.videosEndPointURL+datum.getImgUrl())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            mBinding.progressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            mBinding.progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    }).apply(requestOptions).into(mBinding.imgmain);
       // Glide.with(ImageDetailsActivity.this).asBitmap().load(NetworkURL.videosEndPointURL+datum.getImageResolution()).into(mBinding.imgmain);
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


    @Override
    public void LoadImage(ImageView imageView, ProgressBar progressBar, String imageLink, Integer[] heightWidth) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RequestOptions requestOptions = new RequestOptions();
                requestOptions = requestOptions.transforms(new FitCenter(), new RoundedCorners(5)).override(heightWidth[0], heightWidth[1]).error(R.drawable.default_img); // resizes the image to these dimensions (in pixel)
                Glide.with(getBaseContext())
                        .load(imageLink)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                progressBar.setVisibility(View.GONE);
                                Log.d("fabtest", "Error: " + e);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                progressBar.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .apply(requestOptions)
                        .into(imageView);

            }
        });
    }
}
