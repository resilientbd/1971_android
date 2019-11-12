package com.w3engineers.core.videon.ui.adapter;


import android.databinding.ViewDataBinding;

import android.view.View;
import android.view.ViewGroup;

import com.w3engineers.core.util.adapterUtil.ProjectBaseAdapter;

import com.w3engineers.core.videon.R;
import com.w3engineers.core.videon.data.local.adaptermodel.ItemVideoDownload;
import com.w3engineers.core.videon.databinding.ItemDownloadVideoBinding;


/**
 * purpose: Display downloadable video items with available format
 */

public class DownloadVideoAdapter extends ProjectBaseAdapter<ItemVideoDownload> {

    public OnDownloadClick downloadClick;
    @Override
    public boolean isEqual(ItemVideoDownload left, ItemVideoDownload right) {
        return false;
    }

    @Override
    public BaseAdapterViewHolder<ItemVideoDownload> newViewHolder(ViewGroup parent, int viewType) {

            return new CommoniewHolder(inflate(parent, R.layout.item_download_video));


    }


    private class CommoniewHolder extends BaseAdapterViewHolder<ItemVideoDownload>{

        private ItemDownloadVideoBinding mItemDownloadVideoBinding;

        CommoniewHolder(ViewDataBinding viewDataBinding) {
            super(viewDataBinding);

            mItemDownloadVideoBinding = (ItemDownloadVideoBinding) viewDataBinding;
        }

        @Override
        public void bind(ItemVideoDownload item) {
                mItemDownloadVideoBinding.textVideoTitle.setText(""+item.getTitle());
                mItemDownloadVideoBinding.textVideoSize.setText(""+item.getFormat());
                mItemDownloadVideoBinding.btnDownload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(downloadClick!=null)
                        {
                            downloadClick.onItemDownloadClick(item);
                        }
                    }
                });
            }



    }

    public void setDownloadClick(OnDownloadClick downloadClick) {
        this.downloadClick = downloadClick;
    }

    public interface OnDownloadClick
    {
        public void onItemDownloadClick(ItemVideoDownload item);
    }

}
