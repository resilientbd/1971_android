package com.shadowhite.archieve1971.ui.seeallvideos;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.view.ViewGroup;

import com.shadowhite.archieve1971.R;
import com.shadowhite.archieve1971.data.local.commondatalistresponse.Datum;
import com.shadowhite.archieve1971.databinding.ItemSeeAllVideosBinding;
import com.shadowhite.archieve1971.ui.home.UICommunicator;
import com.shadowhite.util.CheckVideoTypeUtil;
import com.shadowhite.util.NetworkURL;
import com.shadowhite.util.adapterUtil.ProjectBaseAdapter;
import com.shadowhite.util.helper.AppConstants;
import com.shadowhite.util.helper.ResoulationConverter;
import com.shadowhite.util.helper.TimeConverter;

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
