package com.w3engineers.core.videon.ui.favourite;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.view.ViewGroup;

import com.w3engineers.core.util.CheckVideoTypeUtil;
import com.w3engineers.core.util.NetworkURL;
import com.w3engineers.core.util.adapterUtil.ProjectBaseAdapter;
import com.w3engineers.core.util.helper.AppConstants;
import com.w3engineers.core.util.helper.ResoulationConverter;
import com.w3engineers.core.util.helper.TimeConverter;
import com.w3engineers.core.util.helper.TimeUtil;
import com.w3engineers.core.videon.R;
import com.w3engineers.core.videon.data.local.Enums;
import com.w3engineers.core.videon.data.local.commondatalistresponse.Datum;
import com.w3engineers.core.videon.databinding.ItemSearchMoviesBinding;
import com.w3engineers.core.videon.ui.home.UICommunicator;

public class
FavouriteAdapter extends ProjectBaseAdapter<Datum> {
    private Context context;
    private UICommunicator uiCommunicator;

    public FavouriteAdapter(Context context, UICommunicator uiCommunicator) {
        this.context = context;
        this.uiCommunicator = uiCommunicator;
    }

    @Override
    public boolean isEqual(Datum left, Datum right) {
        return false;
    }

    @Override
    public BaseAdapterViewHolder<Datum> newViewHolder(ViewGroup parent, int viewType) {
        return new FeaturedVideoViewHolder(inflate(parent, R.layout.item_search_movies));
    }


    private class FeaturedVideoViewHolder extends BaseAdapterViewHolder<Datum> {

        private ItemSearchMoviesBinding mBinding;

        FeaturedVideoViewHolder(ViewDataBinding viewDataBinding) {
            super(viewDataBinding);
            mBinding = (ItemSearchMoviesBinding) viewDataBinding;
        }

        @Override
        public void bind(Datum item) {
            mBinding.textViewVideoTitle.setText(""+item.getTitle());
            mBinding.textViewCatName.setText(""+item.getCategory());
            mBinding.textViewViewsCount.setText(""+item.getViewCount());
            mBinding.textViewUploadTime.setText(""+ item.getCreated());
            mBinding.textViewDuration.setText(TimeConverter.ConvertSecondsToHourMinute(Integer.parseInt(item.getDuration())));

            CheckVideoTypeUtil.checkVideoType(item);

            //image section
            String imageUrl= NetworkURL.imageEndPointURL+item.getImageName();
            ViewGroup.LayoutParams params=mBinding.roundImageViewVideo.getLayoutParams();
            Integer[] reqHeight= ResoulationConverter.ConvertResoulationHeight(context,item.getImageResolution(),(float)params.width);
            uiCommunicator.LoadImage(mBinding.roundImageViewVideo, mBinding.progressBarCircular, imageUrl,reqHeight);
        }


    }
}
