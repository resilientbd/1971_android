package com.shadowhite.archieve1971.ui.adapter;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.shadowhite.archieve1971.R;
import com.shadowhite.archieve1971.data.local.commondatalistresponse.Datum;
import com.shadowhite.archieve1971.databinding.ItemVideosBinding;
import com.shadowhite.util.DurationConverter;
import com.shadowhite.util.NetworkURL;
import com.shadowhite.util.adapterUtil.ProjectBaseAdapter;
import com.shadowhite.util.helper.ResoulationConverter;



public class VideoAdapter extends ProjectBaseAdapter<Datum> {

    private Context mContext;
    public VideoAdapter(Context context){
        this.mContext=context;
    }

    @Override
    public boolean isEqual(Datum left, Datum right) {
        return false;
    }

    @Override
    public BaseAdapterViewHolder<Datum> newViewHolder(ViewGroup parent, int viewType) {
        return new VideoAdapter.CategoryViewHolder(inflate(parent, R.layout.item_videos));
    }


    private class CategoryViewHolder extends BaseAdapterViewHolder<Datum>{

        private ItemVideosBinding mBinding;
        CategoryViewHolder(ViewDataBinding viewDataBinding) {
            super(viewDataBinding);
            mBinding = (ItemVideosBinding) viewDataBinding;
        }

        @Override
        public void bind(Datum item) {
            mBinding.textViewMostRecentVideoTitle.setText(item.getTitle());
            String imageUrl= NetworkURL.imageEndPointURL+item.getImageName();
            ViewGroup.LayoutParams params=mBinding.roundImageViewLiveTv.getLayoutParams();
            Integer[] reqHeight= ResoulationConverter.ConvertResoulationWidth(mContext,item.getImageResolution(),(float)params.width);
            LoadImage(mBinding.roundImageViewLiveTv,mBinding.progressBarCircular,imageUrl,reqHeight);
            mBinding.durationtext.setText(DurationConverter.GetDurationString(Integer.parseInt(item.getDuration())));
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

            requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(16)).error(R.drawable.default_img)
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