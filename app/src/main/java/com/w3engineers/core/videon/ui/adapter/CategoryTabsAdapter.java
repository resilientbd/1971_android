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
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.w3engineers.core.util.NetworkURL;
import com.w3engineers.core.util.adapterUtil.ProjectBaseAdapter;
import com.w3engineers.core.util.helper.ResoulationConverter;
import com.w3engineers.core.videon.R;
import com.w3engineers.core.videon.data.local.commondatalistresponse.Datum;
import com.w3engineers.core.videon.databinding.ItemHomeCategoryBinding;
import com.w3engineers.core.videon.databinding.ItemVideoCategoriesBinding;


public class CategoryTabsAdapter extends ProjectBaseAdapter<Datum> {

    private Context mContext;
    public CategoryTabsAdapter(Context context){
        this.mContext=context;
    }

    @Override
    public boolean isEqual(Datum left, Datum right) {
        return false;
    }

    @Override
    public BaseAdapterViewHolder<Datum> newViewHolder(ViewGroup parent, int viewType) {
        return new CategoryTabsAdapter.CategoryViewHolder(inflate(parent, R.layout.item_video_categories));
    }


    private class CategoryViewHolder extends BaseAdapterViewHolder<Datum>{

        private ItemVideoCategoriesBinding mBinding;
        CategoryViewHolder(ViewDataBinding viewDataBinding) {
            super(viewDataBinding);
            mBinding = (ItemVideoCategoriesBinding) viewDataBinding;
        }

        @Override
        public void bind(Datum item) {
            mBinding.categoryText.setText(item.getTitle());

        }
    }



}
