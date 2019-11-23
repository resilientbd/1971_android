package com.shadowhite.archieve1971.ui.playlist;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.view.ViewGroup;

import com.shadowhite.archieve1971.R;
import com.shadowhite.archieve1971.data.local.commondatalistresponse.Datum;
import com.shadowhite.archieve1971.databinding.ItemSearchMoviesBinding;
import com.shadowhite.archieve1971.ui.home.UICommunicator;
import com.shadowhite.util.CheckVideoTypeUtil;
import com.shadowhite.util.NetworkURL;
import com.shadowhite.util.adapterUtil.ProjectBaseAdapter;
import com.shadowhite.util.helper.AppConstants;
import com.shadowhite.util.helper.ResoulationConverter;
import com.shadowhite.util.helper.TimeConverter;


public class PlayListAdapter extends ProjectBaseAdapter<Datum> {
    private Context context;
    private UICommunicator uiCommunicator;

    public PlayListAdapter(Context context, UICommunicator uiCommunicator) {
        this.context = context;
        this.uiCommunicator = uiCommunicator;
    }

    @Override
    public boolean isEqual(Datum left, Datum right) {
        return false;
    }


    @Override
    public BaseAdapterViewHolder<Datum> newViewHolder(ViewGroup parent, int viewType) {
        return new PlayListViewHolder(inflate(parent, R.layout.item_search_movies));
    }

    private class PlayListViewHolder extends BaseAdapterViewHolder<Datum> {

        private ItemSearchMoviesBinding mItemSeeAllVideosBinding;

        PlayListViewHolder(ViewDataBinding viewDataBinding) {
            super(viewDataBinding);
            mItemSeeAllVideosBinding = (ItemSearchMoviesBinding) viewDataBinding;
        }

        @Override
        public void bind(Datum item) {
            mItemSeeAllVideosBinding.progressBarCircular.setProgress(AppConstants.progress);
            mItemSeeAllVideosBinding.textViewViewsCount.setText("" + item.getViewCount());
            mItemSeeAllVideosBinding.textViewCatName.setText(""+item.getCategory());//" + /*item.getCreatedAt()*/ + "
            mItemSeeAllVideosBinding.textViewVideoTitle.setText(item.getTitle());
            mItemSeeAllVideosBinding.textViewUploadTime.setText(""+ item.getCreated());
            mItemSeeAllVideosBinding.textViewDuration.setText(TimeConverter.ConvertSecondsToHourMinute(Integer.parseInt(item.getDuration())));

            CheckVideoTypeUtil.checkVideoType(item);

           // image section
            String imageUrl = NetworkURL.imageEndPointURL + item.getImageName();
            ViewGroup.LayoutParams params = mItemSeeAllVideosBinding.roundImageViewVideo.getLayoutParams();
            Integer[] reqHeight = ResoulationConverter.ConvertResoulationHeight(context, item.getImageResolution(), (float) params.width);
            uiCommunicator.LoadImage(mItemSeeAllVideosBinding.roundImageViewVideo, mItemSeeAllVideosBinding.progressBarCircular, imageUrl, reqHeight);
        }


    }
}
