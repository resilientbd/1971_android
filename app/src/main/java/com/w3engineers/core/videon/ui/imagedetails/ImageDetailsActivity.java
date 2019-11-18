package com.w3engineers.core.videon.ui.imagedetails;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.w3engineers.core.util.NetworkURL;
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
            Glide.with(ImageDetailsActivity.this).load(NetworkURL.videosEndPointURL+datum.getImgUrl()).into(mBinding.imgmain);
            mBinding.doctitle.setText(""+datum.getImgTitle());
            mBinding.desc.setText(Html.fromHtml(""+datum.getImgDesc()));
        }catch (Exception e)
        {

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
