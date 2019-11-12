package com.w3engineers.core.videon.ui.seeallvideos;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.util.Log;
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
import com.w3engineers.core.videon.databinding.ItemSeeAllVideosBinding;
import com.w3engineers.core.videon.ui.home.UICommunicator;

public class SeeAllVideosAdapter extends ProjectBaseAdapter<Datum> {

    private Context mContext;
    private UICommunicator uiCommunicator;

    SeeAllVideosAdapter(Context context, UICommunicator uiCommunicator) {
        mContext = context;
        this.uiCommunicator = uiCommunicator;
    }


    @Override
    public boolean isEqual(Datum left, Datum right) {
        return false;
    }

    @Override
    public BaseAdapterViewHolder<Datum> newViewHolder(ViewGroup parent, int viewType) {
        return new SeeAllViewHolder(inflate(parent, R.layout.item_see_all_videos));
    }

    class SeeAllViewHolder extends BaseAdapterViewHolder<Datum> {


        private ItemSeeAllVideosBinding mItemSeeAllVideosBinding;

        SeeAllViewHolder(ViewDataBinding viewDataBinding) {
            super(viewDataBinding);
            mItemSeeAllVideosBinding = (ItemSeeAllVideosBinding) viewDataBinding;
        }

        @Override
        public void bind(Datum item) {
            mItemSeeAllVideosBinding.progressBarCircular.setProgress(AppConstants.progress);

            mItemSeeAllVideosBinding.textViewViewsCount.setText("" + item.getViewCount());
            mItemSeeAllVideosBinding.textViewUploadTime.setText(""+ item.getCreated());
            mItemSeeAllVideosBinding.textViewVideoTitle.setText(item.getTitle());
            if (item.getCategory()!=null){
                mItemSeeAllVideosBinding.textViewCatName.setText(""+item.getCategory());
            }
            if (item.getDuration() !=null){
                mItemSeeAllVideosBinding.textViewDuration.setText(TimeConverter.ConvertSecondsToHourMinute(Integer.parseInt(item.getDuration())));
            }

            CheckVideoTypeUtil.checkVideoType(item);

            String imageUrl = NetworkURL.imageEndPointURL + item.getImageName();
            ViewGroup.LayoutParams params = mItemSeeAllVideosBinding.roundImageViewVideo.getLayoutParams();
            Integer[] reqHeight = ResoulationConverter.ConvertResoulationHeight(mContext, item.getImageResolution(), (float) params.width);
            uiCommunicator.LoadImage(mItemSeeAllVideosBinding.roundImageViewVideo, mItemSeeAllVideosBinding.progressBarCircular, imageUrl, reqHeight);


        }

    }
}
