package com.w3engineers.core.videon.ui.channellist;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.view.ViewGroup;
import com.w3engineers.core.util.NetworkURL;
import com.w3engineers.core.util.adapterUtil.ProjectBaseAdapter;
import com.w3engineers.core.util.helper.AppConstants;
import com.w3engineers.core.util.helper.ResoulationConverter;
import com.w3engineers.core.videon.R;
import com.w3engineers.core.videon.data.local.commondatalistresponse.Datum;
import com.w3engineers.core.videon.databinding.ItemChannelListBinding;
import com.w3engineers.core.videon.ui.home.UICommunicator;


public class ChannelListAdapter  extends ProjectBaseAdapter<Datum> {

    private Context mContext;
    private UICommunicator uiCommunicator;
    ChannelListAdapter(Context context, UICommunicator uiCommunicator){
        mContext=context;
        this.uiCommunicator=uiCommunicator;
    }

    @Override
    public boolean isEqual(Datum left, Datum right) {
        return false;
    }

    @Override
    public BaseAdapterViewHolder<Datum> newViewHolder(ViewGroup parent, int viewType) {
        return new ChannelListAdapter.ChannelListViewHolder(inflate(parent, R.layout.item_channel_list));
    }


    private class ChannelListViewHolder extends BaseAdapterViewHolder<Datum>{

        private ItemChannelListBinding mBinding;
        ChannelListViewHolder(ViewDataBinding viewDataBinding) {
            super(viewDataBinding);
            mBinding = (ItemChannelListBinding) viewDataBinding;
        }

        @Override
        public void bind(Datum item) {
            mBinding.textViewChannelName.setText(item.getTitle());
            String imageUrl= NetworkURL.imageEndPointURL+item.getImageName();
            ViewGroup.LayoutParams params=mBinding.roundImageViewChannel.getLayoutParams();
            Integer[] reqHeight= ResoulationConverter.ConvertResoulationHeight(mContext,item.getImageResolution(),(float)params.width);
            uiCommunicator.LoadImage(mBinding.roundImageViewChannel,mBinding.progressBarCircular,imageUrl,reqHeight);
        }
    }
}
