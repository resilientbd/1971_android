package com.w3engineers.core.videon.ui.imagepager;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.w3engineers.core.util.NetworkURL;
import com.w3engineers.core.videon.R;
import com.w3engineers.core.videon.data.local.images.Datum;
import com.w3engineers.core.videon.databinding.FragmentImageDetailsBinding;
import com.w3engineers.core.videon.ui.imagedetails.ImageDetailsActivity;
import com.w3engineers.ext.strom.application.ui.base.BaseFragment;

import java.util.List;

public class ImagePagerFragment extends BaseFragment {
    FragmentImageDetailsBinding mBinding;
    Datum image;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_image_details;
    }

    @Override
    protected void startUI() {
    mBinding= (FragmentImageDetailsBinding) getViewDataBinding();
    setClickListener(mBinding.backBtn,mBinding.upbtn,mBinding.imgmain,mBinding.sharebtn);
    if(image!=null)
    {
        this.image=image;
        mBinding.doctitle.setText(image.getImgTitle());
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.error(R.drawable.default_img); // resizes the image to these dimensions (in pixel)
        Glide.with(getActivity()).load(NetworkURL.videosEndPointURL+image.getImgUrl())
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


        mBinding.doctitle.setText(""+image.getImgTitle());
        mBinding.desc.setText(Html.fromHtml(""+image.getImgDesc()));
    }
    }

    public void setImagelist(Datum image)
    {
       this.image=image;
    }
    void sharecontent()
    {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "Content From 1971 App. Title:"+image.getImgTitle();
        String shareSub = "Url:"+NetworkURL.videosEndPointURL+image.getImgUrl();
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
                getActivity().finish();
                break;
            case R.id.sharebtn:
               sharecontent();
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
