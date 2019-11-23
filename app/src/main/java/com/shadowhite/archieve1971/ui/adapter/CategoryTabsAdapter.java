package com.shadowhite.archieve1971.ui.adapter;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.view.ViewGroup;

import com.shadowhite.archieve1971.R;
import com.shadowhite.archieve1971.data.local.commondatalistresponse.Datum;
import com.shadowhite.archieve1971.databinding.ItemVideoCategoriesBinding;
import com.shadowhite.util.adapterUtil.ProjectBaseAdapter;



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
