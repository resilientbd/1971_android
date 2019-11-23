package com.shadowhite.archieve1971.ui.channellist;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.shadowhite.archieve1971.R;
import com.shadowhite.archieve1971.data.local.Enums;
import com.shadowhite.archieve1971.data.local.commondatalistresponse.ApiCommonDetailListResponse;
import com.shadowhite.archieve1971.data.local.commondatalistresponse.Datum;
import com.shadowhite.archieve1971.data.remote.RemoteApiProvider;
import com.shadowhite.archieve1971.data.remote.home.RemoteVideoApiInterface;
import com.shadowhite.archieve1971.databinding.ActivityChannelListBinding;
import com.shadowhite.archieve1971.ui.home.UICommunicator;
import com.shadowhite.archieve1971.ui.videodetails.VideoDetailsActivity;
import com.shadowhite.util.NetworkURL;
import com.shadowhite.util.helper.AdHelper;
import com.shadowhite.util.helper.ApiToken;
import com.shadowhite.util.helper.AppConstants;
import com.shadowhite.util.helper.CheckNetworkAvailabilityAndPermission;
import com.shadowhite.util.helper.KeyboardUtils;
import com.shadowhite.util.helper.TransactionHelper;
import com.w3engineers.ext.strom.application.ui.base.BaseActivity;
import com.w3engineers.ext.strom.application.ui.base.ItemClickListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChannelListActivity extends BaseActivity {

    private ActivityChannelListBinding mBinding;
    private ChannelListAdapter mChannelListAdapter;
    public List<Datum> mTotalList=new ArrayList<>();

    private boolean isLoadingDataInProgress = false;
    private int pageNumberToLoadData = 1;
    private RemoteVideoApiInterface mRemoteVideoApiInterface;

    CheckNetworkAvailabilityAndPermission mCheckNetworkAvailabilityAndPermission = new CheckNetworkAvailabilityAndPermission();
    private boolean hasNetWork;

    /**
     * Run activity ChannelList
     * @param context context
     */
    public static void runActivity(Context context) {
        Intent intent = new Intent(context, ChannelListActivity.class);
        runCurrentActivity(context, intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_channel_list;
    }

    @Override
    protected void startUI() {
        mBinding = (ActivityChannelListBinding) getViewDataBinding();
        mRemoteVideoApiInterface = RemoteApiProvider.getInstance().getRemoteHomeVideoApi();

        AdHelper.loadBannerAd(this,(LinearLayout) mBinding.buttonLayout);
        setSupportActionBar(mBinding.toolbarHome);

        KeyboardUtils.hideSoftInput(this);
        initLiveTvAdapter();
        textChangeListenerForSearch(mBinding.editTextSearch);
        retrieveChannelListVideosDataFromServer();
        checkInternetConnection();
    }

    /**
     * Check internet connection for first time
     */
    private void checkInternetConnection() {
        hasNetWork = mCheckNetworkAvailabilityAndPermission.checkPermissions(this) && mCheckNetworkAvailabilityAndPermission.checkIfHasNetwork(this);
        if (hasNetWork) {
            mBinding.progressBarCircular.setProgress(AppConstants.progress);
        } else {
            mBinding.progressBarCircular.setVisibility(View.GONE);
        }
    }

    /**
     * Text change listener for search
     *
     * @param editText search text
     */
    private void textChangeListenerForSearch(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                filter(s.toString());
            }
        });

    }

    /**
     * Create new list for filter
     *
     * @param text search text of user
     */
    private void filter(String text) {
        List<Datum> filteredList = new ArrayList<>();
        //search in list of the actual adapter
        for (Datum liveTVChannelsModel : mTotalList) {
            if (liveTVChannelsModel.getTitle().toLowerCase().contains(
                    text.toLowerCase())) {
                //all search items add the search array list for show the search result
                filteredList.add(liveTVChannelsModel);
            }
        }
        if(filteredList.size()<=AppConstants.zero){
            mBinding.constraintLayoutDataNotFound.setVisibility(View.VISIBLE);
        }else{
            mBinding.constraintLayoutDataNotFound.setVisibility(View.GONE);
        }

        if (text.length()==AppConstants.zero){
            mChannelListAdapter.clear();
            mChannelListAdapter.addItems(mTotalList);
            mBinding.constraintLayoutDataNotFound.setVisibility(View.GONE);
        }else {
            mChannelListAdapter.clear();
            mChannelListAdapter.addItems(filteredList);
        }

    }


    /**
     * initialize the channel list adapter
     */
    private void initLiveTvAdapter() {
        mChannelListAdapter = new ChannelListAdapter(ChannelListActivity.this, new UICommunicator() {
            @Override
            public void LoadImage(ImageView imageView, ProgressBar progressBar, String imageLink, Integer[] heightWidth) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        RequestOptions requestOptions = new RequestOptions();


                        requestOptions = requestOptions.transforms(new FitCenter(), new RoundedCorners(16))
                                .override(heightWidth[0],heightWidth[1])
                                .error(R.drawable.default_category_img)
                                .placeholder(R.drawable.default_category_img); // resizes the image to these dimensions (in pixel)


                        Glide.with(getBaseContext())
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
                    }
                });

            }
        });
        mBinding.baseRecyclerViewChannelList.setAdapter(mChannelListAdapter);
        mBinding.baseRecyclerViewChannelList.setLayoutManager(new GridLayoutManager(this, 3));

        mBinding.baseRecyclerViewChannelList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    boolean canScrollDownMore = recyclerView.canScrollVertically(1);
                    if (!canScrollDownMore)
                        if (!isLoadingDataInProgress)///Checking if retrofit is busy to load data or not
                            retrieveChannelListVideosDataFromServer();//Now recycler view is in bottom . So loading more data .
                }
            }

        });
        mChannelListAdapter.setItemClickListener(new ItemClickListener<Datum>() {
            @Override
            public void onItemClick(View view, Datum item) {
                goToVideoDetails(item);
            }
        });
    }


    /**
     * Go video details
     * @param datum datum
     */
    public void goToVideoDetails(Datum datum) {
            switch (datum.getType()) {
                case Enums.YOUTUBE_VIDEO:
                    try{
                        if(datum.getLink().contains("=")){
                            String[] vidIdYoutube = datum.getLink().split("=");
                            datum.setLink("" + vidIdYoutube[1]);
                        }else {
                            datum.setLink("" +datum.getYoutube());
                        }
                    }catch (Exception e){
                        Toast.makeText(this, getResources().getString(R.string.see_all_invalid_link), Toast.LENGTH_LONG).show();
                        return;
                    }
                    break;
                case Enums.live_link_m3u8:
                    datum.setLink(datum.getLiveLinkm3u8());
                    break;
                case Enums.live_rtmp:
                    datum.setLink(convertRtmpTom3u8(datum.getLiveRtmp()));
                    datum.setType(Enums.live_link_m3u8);
                    datum.setLiveLinkm3u8(convertRtmpTom3u8(datum.getLiveRtmp()));
                    break;
            }

        VideoDetailsActivity.runActivity(ChannelListActivity.this, datum);
        TransactionHelper.TransactionLeftToRight(ChannelListActivity.this);
    }

    /**
     * Convert Rtmp to m3u8
     * @param rtml rtml
     * @return string
     */
    private String convertRtmpTom3u8(String rtml)
    {
        String convertedUrl="";
        String str="";
        for(int i=4;i<rtml.length();i++)
        {
            str=str+rtml.charAt(i);
        }
        str="http"+str+"/playlist.m3u8";
        Log.d("rtmlcheck",str);
        return str;
    }


    /**
     * Gone progress bar and show the recycler view
     *
     * @param progressBar  get progressbar input
     * @param recyclerView get recycler input
     */
    private void goneProgressBarAndShowRecyclerView(ProgressBar progressBar, RecyclerView recyclerView) {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * get the most recent videos from server
     */
    public void retrieveChannelListVideosDataFromServer() {

        if (!new CheckNetworkAvailabilityAndPermission().checkIfHasNetwork(this)) {
            mBinding.progressBarCircular.setVisibility(View.GONE);
            return;
        }

        isLoadingDataInProgress = true;

        Call<ApiCommonDetailListResponse> mostRecentVideosDataRetrieveCall =
                mRemoteVideoApiInterface.getChannelListVideos(NetworkURL.channelListEndPointURL,
                        ApiToken.GET_TOKEN(getBaseContext()),Integer.toString(getPageNumberToLoadData()));

        mostRecentVideosDataRetrieveCall.enqueue(new Callback<ApiCommonDetailListResponse>() {
            @Override
            public void onResponse(Call<ApiCommonDetailListResponse> call, Response<ApiCommonDetailListResponse> response) {
                goneProgressBarAndShowRecyclerView(mBinding.progressBarCircular, mBinding.baseRecyclerViewChannelList);
                isLoadingDataInProgress = false;
                mBinding.progressBarCircular.setVisibility(View.GONE);
                mBinding.constraintLayoutDataNotFound.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    ApiCommonDetailListResponse apiCommonDetailListResponse = response.body();//it is a list
                    if (apiCommonDetailListResponse.getStatusCode().equals(AppConstants.SUCCESS)){
                        List<Datum> dataList = apiCommonDetailListResponse.getData();
                        if ( apiCommonDetailListResponse.getData() == null  && mChannelListAdapter.getItems().size() != 0) {
                            return;//last page
                        }

                        int rvSize = mChannelListAdapter.getItemCount();
                        if (dataList!=null){
                            populateData(dataList,rvSize);
                        }
                        setPageNumberToLoadData(getPageNumberToLoadData() + 1);
                        if (dataList!=null){
                            mChannelListAdapter.addAllItemToPosition(dataList, rvSize);
                        }
                    }else {
                        mBinding.progressBarCircular.setVisibility(View.GONE);
                    }

                }else {
                    mBinding.progressBarCircular.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ApiCommonDetailListResponse> call, Throwable t) {
                mBinding.progressBarCircular.setVisibility(View.GONE);
                isLoadingDataInProgress = false;
            }
        });

    }

    /**
     * Get data from server added into list
     */
    private void populateData(List<Datum> channelList, int position){
        if (mTotalList != null){
            mTotalList.addAll(position,channelList);
        }
    }


    /**
     * get Page number to load
     *
     * @return page number
     */
    public int getPageNumberToLoadData() {
        return pageNumberToLoadData;
    }

    /**
     * set page number
     *
     * @param pageNumberToLoadData get page number
     */
    public void setPageNumberToLoadData(int pageNumberToLoadData) {
        this.pageNumberToLoadData = pageNumberToLoadData;
    }


}
