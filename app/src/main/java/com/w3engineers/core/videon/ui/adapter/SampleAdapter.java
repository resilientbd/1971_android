package com.w3engineers.core.videon.ui.adapter;

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
import com.w3engineers.core.util.NetworkURL;
import com.w3engineers.core.util.adapterUtil.ProjectBaseAdapter;
import com.w3engineers.core.videon.R;
import com.w3engineers.core.videon.data.local.audio.Datum;
import com.w3engineers.core.videon.databinding.ItemDocumentBinding;
import com.w3engineers.core.videon.databinding.ItemSampleBinding;


public class SampleAdapter extends ProjectBaseAdapter<String> {

    private Context mContext;
    public SampleAdapter(Context context){
        this.mContext=context;
    }

    @Override
    public boolean isEqual(String left, String right) {
        return false;
    }

    @Override
    public BaseAdapterViewHolder<String> newViewHolder(ViewGroup parent, int viewType) {
        return new SampleAdapter.CategoryViewHolder(inflate(parent, R.layout.item_sample));
    }


    private class CategoryViewHolder extends BaseAdapterViewHolder<String>{

        private ItemSampleBinding mBinding;
        CategoryViewHolder(ViewDataBinding viewDataBinding) {
            super(viewDataBinding);
            mBinding = (ItemSampleBinding) viewDataBinding;
        }

        @Override
        public void bind(String item) {
            mBinding.textView5.setText("hello....");
//            mBinding.textViewVideoTitle.setText(item.getAudioTitle());
//            mBinding.textViewSub.setText("sk. faisal");
//            String imageUrl= NetworkURL.imageEndPointURL+item.getAudioImg();
//            // ViewGroup.LayoutParams params=mBinding.roundImageViewLiveTv.getLayoutParams();
//            // Integer[] reqHeight= ResoulationConverter.ConvertResoulationWidth(mContext,item.getImageResolution(),(float)params.width);
//            LoadImage(mBinding.roundImageViewVideo,mBinding.progressBarCircular,imageUrl);
            //  mBinding.durationtext.setText(DurationConverter.GetDurationString(Integer.parseInt(item.getDuration())));
        }
    }




}
