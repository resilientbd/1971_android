package com.w3engineers.core.videon.ui.adapter;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
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
import com.w3engineers.core.util.NetworkURL;
import com.w3engineers.core.util.adapterUtil.ProjectBaseAdapter;
import com.w3engineers.core.util.helper.AppConstants;
import com.w3engineers.core.util.helper.ResoulationConverter;
import com.w3engineers.core.videon.R;
import com.w3engineers.core.videon.data.local.commondatalistresponse.Datum;
import com.w3engineers.core.videon.databinding.ItemLiveTvBinding;
import com.w3engineers.core.videon.databinding.ItemMostRecentVideoBinding;


public class CommonDataAdapter extends ProjectBaseAdapter<Datum> {

    private Context mContext;
    private boolean isRecent;

    public CommonDataAdapter(Context context){
        mContext=context;
    }
    public CommonDataAdapter(Context context, boolean isRecent){
        mContext=context;
        this.isRecent=isRecent;
    }


    @Override
    public boolean isEqual(Datum left, Datum right) {
        return false;
    }

    @Override
    public BaseAdapterViewHolder<Datum> newViewHolder(ViewGroup parent, int viewType) {
        if(!isRecent){
            //It is for popular , live tv,
            return new CommoniewHolder(inflate(parent, R.layout.item_live_tv));
        }
        else {
            return new CommoniewHolder(inflate(parent, R.layout.item_most_recent_video));
        }

    }


    private class CommoniewHolder extends BaseAdapterViewHolder<Datum>{

        private ItemLiveTvBinding mItemLiveTvBinding;
        private ItemMostRecentVideoBinding mItemRecentBinding;
        CommoniewHolder(ViewDataBinding viewDataBinding) {
            super(viewDataBinding);
             if(isRecent) {
                 mItemRecentBinding = (ItemMostRecentVideoBinding) viewDataBinding;
             }
             else {
                 mItemLiveTvBinding = (ItemLiveTvBinding) viewDataBinding;
             }
        }

        @Override
        public void bind(Datum item) {
            boolean isLiveOrCategory=false; //track item from category or live and set rectangle image error placeholder

            if(isRecent)
            {
                //it is a recent video section
                mItemRecentBinding.textViewMostRecentVideoTitle.setText(""+item.getTitle());
                mItemRecentBinding.progressBarCircular.setProgress(AppConstants.progress);
                mItemRecentBinding.textViewCatName.setText(""+item.getCategory());
                String imageUrl= NetworkURL.imageEndPointURL+item.getImageName();
                ViewGroup.LayoutParams params=mItemRecentBinding.roundImageViewLiveTv.getLayoutParams();
                Integer[] reqHeight= ResoulationConverter.ConvertResoulationWidth(mContext,item.getImageResolution(),(float)params.width);
                LoadImage(mItemRecentBinding.roundImageViewLiveTv, mItemRecentBinding.progressBarCircular, imageUrl,reqHeight);
                setClickListener(mItemRecentBinding.constraintLayoutLiveTv);
            }
            else {

                mItemLiveTvBinding.textViewLiveTvTitle.setText(""+item.getTitle());

                if(item.getCategory()!=null) {
                    //category section
                    mItemLiveTvBinding.textViewCatName.setText("" + item.getCategory());
                    mItemLiveTvBinding.textViewCatName.setVisibility(View.VISIBLE);
                    mItemLiveTvBinding.textViewLiveTvTitle.setGravity(Gravity.LEFT);
                    isLiveOrCategory=true;
                }
                else {
                    //live tv section
                    mItemLiveTvBinding.textViewLiveTvTitle.setGravity(Gravity.CENTER);
                }

                //get image url
                mItemLiveTvBinding.progressBarCircular.setProgress(AppConstants.progress);
                String imageUrl= NetworkURL.imageEndPointURL+item.getImageName();
                ViewGroup.LayoutParams params=mItemLiveTvBinding.roundImageViewLiveTv.getLayoutParams();
                Integer[] reqHeight=ResoulationConverter.ConvertResoulationHeight(mContext,item.getImageResolution(),(float)params.width);

                if(!isLiveOrCategory)
                {
                    mItemLiveTvBinding.roundImageViewLiveTv.setImageResource(R.drawable.default_category_img);
                    LoadImage(mItemLiveTvBinding.roundImageViewLiveTv,   mItemLiveTvBinding.progressBarCircular, imageUrl,reqHeight,true);
                }
               else {
                    LoadImage(mItemLiveTvBinding.roundImageViewLiveTv,   mItemLiveTvBinding.progressBarCircular, imageUrl,reqHeight);
                }
                setClickListener(mItemLiveTvBinding.constraintLayoutLiveTv);
            }

        }

    }

    /**
     * load image
     * @param imageView imageView
     * @param progressBar progressBar
     * @param imageLink imageLink
     * @param heightWidth heightWidth
     */
    public void LoadImage(ImageView imageView, ProgressBar progressBar, String imageLink, Integer[] heightWidth) {

            try {
                RequestOptions requestOptions = new RequestOptions();

                requestOptions = requestOptions.transforms(new FitCenter(), new RoundedCorners(5)).override(heightWidth[0], heightWidth[1]).error(R.drawable.default_img)
                        .placeholder(R.drawable.default_img); // resizes the image to these dimensions (in pixel)

                Glide.with(mContext)
                        .load(imageLink)

                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                progressBar.setVisibility(View.GONE);
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
            } catch (Exception e) {
                progressBar.setVisibility(View.GONE);
            }
        }
    public void LoadImage(ImageView imageView, ProgressBar progressBar, String imageLink, Integer[] heightWidth,boolean activeCircleImage) {

        try {
            RequestOptions requestOptions = new RequestOptions();

            requestOptions = requestOptions.transforms(new FitCenter(), new RoundedCorners(5)).override(heightWidth[0], heightWidth[1]).error(R.drawable.default_category_img)
                    .placeholder(R.drawable.default_category_img); // resizes the image to these dimensions (in pixel)

            Glide.with(mContext)
                    .load(imageLink)

                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
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
        } catch (Exception e) {
            progressBar.setVisibility(View.GONE);
        }
    }

}
