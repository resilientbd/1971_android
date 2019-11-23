package com.shadowhite.archieve1971.ui.seeallvideos;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.shadowhite.archieve1971.R;
import com.shadowhite.archieve1971.data.local.commondatalistresponse.ApiCommonDetailListResponse;
import com.shadowhite.archieve1971.data.local.commondatalistresponse.Datum;
import com.shadowhite.archieve1971.data.remote.RemoteApiProvider;
import com.shadowhite.archieve1971.data.remote.home.RemoteVideoApiInterface;
import com.shadowhite.archieve1971.databinding.ActivitySeeAllVideosBinding;
import com.shadowhite.archieve1971.ui.home.UICommunicator;
import com.shadowhite.archieve1971.ui.videodetails.VideoDetailsActivity;
import com.shadowhite.util.CheckVideoTypeUtil;
import com.shadowhite.util.NetworkURL;
import com.shadowhite.util.helper.AdHelper;
import com.shadowhite.util.helper.ApiToken;
import com.shadowhite.util.helper.AppConstants;
import com.shadowhite.util.helper.CheckNetworkAvailabilityAndPermission;
import com.shadowhite.util.helper.Constants;
import com.shadowhite.util.helper.IntentKeys;
import com.shadowhite.util.helper.KeyboardUtils;
import com.shadowhite.util.helper.TransactionHelper;
import com.w3engineers.ext.strom.application.ui.base.BaseActivity;
import com.w3engineers.ext.strom.application.ui.base.ItemClickListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.w3engineers.ext.strom.App.getContext;


public class SeeAllVideosActivity extends BaseActivity implements UICommunicator {

    private SeeAllVideosAdapter mSeeAllVideosAdapter;
    private ActivitySeeAllVideosBinding mBinding;

    //This list for search
    public static List<Datum> mModelList = new ArrayList<>();

    private RemoteVideoApiInterface mRemoteVideoApiInterface;
    private boolean isLoadingDataInProgress = false;
    private int pageNumberToLoadData = 1;
    String categoryId;



    CheckNetworkAvailabilityAndPermission mCheckNetworkAvailabilityAndPermission = new CheckNetworkAvailabilityAndPermission();
    private boolean hasNetWork;

    public static void runActivity(Context context, String seeAllVideosPage, int categoryId) {
        Intent intent = new Intent(context, SeeAllVideosActivity.class);
        intent.putExtra(IntentKeys.ACTIVITY_NAME, seeAllVideosPage);
        intent.putExtra(IntentKeys.CATEGORY_ID, categoryId);
        runCurrentActivity(context, intent);
    }

    public static void runActivity(Context context, String seeAllVideosPage, String categoryId, String categoryName) {
        Intent intent = new Intent(context, SeeAllVideosActivity.class);
        intent.putExtra(IntentKeys.ACTIVITY_NAME, seeAllVideosPage);
        intent.putExtra(IntentKeys.CATEGORY_ID, categoryId);
        intent.putExtra(IntentKeys.CATEGORY_NAME, categoryName);
        runCurrentActivity(context, intent);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_see_all_videos;
    }

    @Override
    protected void startUI() {
        mBinding = (ActivitySeeAllVideosBinding) getViewDataBinding();
        mRemoteVideoApiInterface = RemoteApiProvider.getInstance().getRemoteHomeVideoApi();
        AdHelper.loadBannerAd(this, (LinearLayout) mBinding.adView);
        setSupportActionBar(mBinding.toolbarHome);
        mBinding.toolbarHome.setTitle(getIntent().getStringExtra(IntentKeys.CATEGORY_NAME));
        getIntentValue();
        initRecyclerView();

        textChangeListenerForSearch(mBinding.editTextSearch);

        checkInternetConnection();
        KeyboardUtils.hideSoftInput(this);
        retrieveAllCategories(0);

    }

    /**
     * init recycler view
     */
    private void initRecyclerView() {
        mSeeAllVideosAdapter = new SeeAllVideosAdapter(getContext(), this);
        mBinding.baseRecyclerViewSeeAllVideos.setLayoutManager(new LinearLayoutManager(this));
        mBinding.baseRecyclerViewSeeAllVideos.setAdapter(mSeeAllVideosAdapter);
       mSeeAllVideosAdapter.setItemClickListener(new ItemClickListener<Datum>() {
            @Override
            public void onItemClick(View view, Datum item) {
                goToVideoDetails( item);
            }
        });
    }

    /**
     * Go video details
     * @param datum datum
     */
    public void goToVideoDetails(Datum datum) {
        if (datum.getLink() == null) {
            CheckVideoTypeUtil.checkVideoType(datum);
        }
        VideoDetailsActivity.runActivity(SeeAllVideosActivity.this, datum);
        TransactionHelper.TransactionLeftToRight(SeeAllVideosActivity.this);

    }


    /**
     * Hit server for videos
     *
     * @param url        hit url
     * @param isCategory it is category data or not
     */
    private void scrollListenerAndHitServerForVideos(String url, boolean isCategory) {

        mBinding.baseRecyclerViewSeeAllVideos.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    boolean canScrollDownMore = recyclerView.canScrollVertically(1);
                    if (!canScrollDownMore)
                        if (!isLoadingDataInProgress)///Checking if retrofit is busy to load data or not
                            retrieveSeeAllVideosFromServer(url, isCategory);//Now recycler view is in bottom . So loading more data .
                }
            }

        });


    }

    /**
     * Text change listener for search
     *
     * @param editText editText
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
        for (Datum model : mModelList) {
            if (model.getTitle().toLowerCase().contains(
                    text.toLowerCase())) {
                //all search items add the search array list for show the search result
                filteredList.add(model);
            }
        }

        if (filteredList.size() <= AppConstants.zero) {
            mBinding.constraintLayoutDataNotFound.setVisibility(View.VISIBLE);

        } else {
            mBinding.constraintLayoutDataNotFound.setVisibility(View.GONE);
        }

        if (text.length() == AppConstants.zero) {
            mSeeAllVideosAdapter.clear();
            mSeeAllVideosAdapter.addItems(mModelList);
            mBinding.constraintLayoutDataNotFound.setVisibility(View.GONE);
        } else {
            mSeeAllVideosAdapter.clear();
            mSeeAllVideosAdapter.addItems(filteredList);
        }
    }


    /**
     * get intent Value
     */
    private void getIntentValue() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String pageName = bundle.getString(IntentKeys.ACTIVITY_NAME);
            categoryId = bundle.getString(IntentKeys.CATEGORY_ID);

            callASeeAllVideoPage(pageName);
        }
    }

    /**
     * set title and load see all pagese
     *
     * @param pageName page name for see all
     */
    private void callASeeAllVideoPage(String pageName) {

        if (pageName.equals(Constants.SeeAllVideos.FEATURED_VIDEO_PAGE)) {
            loadFeaturedPage();
        } else if (pageName.equals(Constants.SeeAllVideos.MOST_POPULAR_VIDEO_PAGE)) {
            loadMostPopularPage();

        } else if (pageName.equals(Constants.SeeAllVideos.MOST_RECENT_VIDEO_PAGE)) {
            loadMostRecentPage();
        } else if (pageName.equals(Constants.SeeAllVideos.CATEGORY_PAGE)) {
            loadCategoryPage();
        }

    }

    /**
     * Load featured page
     */
    private void loadFeaturedPage() {
        getSupportActionBar().setTitle(R.string.title_featured_video);
        retrieveSeeAllVideosFromServer(NetworkURL.featuredEndPointURL, false);
        scrollListenerAndHitServerForVideos(NetworkURL.featuredEndPointURL, false);
    }


    /**
     * Load most popular page
     */
    private void loadMostPopularPage() {
        getSupportActionBar().setTitle(R.string.title_most_popular);
        retrieveSeeAllVideosFromServer(NetworkURL.mostPopularEndPointURL, false);
        scrollListenerAndHitServerForVideos(NetworkURL.mostPopularEndPointURL, false);
    }

    /**
     * Load most recent page
     */
    private void loadMostRecentPage() {
        getSupportActionBar().setTitle(R.string.title_most_recent);
        retrieveSeeAllVideosFromServer(NetworkURL.mostRecentEndPointURL, false);
        scrollListenerAndHitServerForVideos(NetworkURL.mostRecentEndPointURL, false);
    }


    /**
     * Load Category page
     */
    private void loadCategoryPage() {
        getSupportActionBar().setTitle(getIntent().getStringExtra(IntentKeys.CATEGORY_NAME));
        String url = NetworkURL.categoryEndPointURL;
        retrieveSeeAllVideosFromServer(url, true);
        scrollListenerAndHitServerForVideos(url, true);
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
     * Gone progress bar and show the recycler view
     *
     * @param progressBar  get progressbar input
     * @param recyclerView get recycler input
     */
    private void goneProgressBarAndShowRecyclerView(ProgressBar progressBar, RecyclerView recyclerView) {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    //retrieve all categories
    public void retrieveAllCategories(int categorytype)
    {
        if (!new CheckNetworkAvailabilityAndPermission().checkIfHasNetwork(this)) {
            mBinding.progressBarCircular.setVisibility(View.GONE);
            return;
        }


    }
    /**
     * retrieve all videos from server
     *
     * @param url hit url
     */
    public void retrieveSeeAllVideosFromServer(String url, boolean isCategory) {

        if (!new CheckNetworkAvailabilityAndPermission().checkIfHasNetwork(this)) {
            mBinding.progressBarCircular.setVisibility(View.GONE);
            return;
        }

        isLoadingDataInProgress = true;
        Call<ApiCommonDetailListResponse> seeAllVideosList;

        if (isCategory) {
            seeAllVideosList = mRemoteVideoApiInterface.getSingleCategoryVideos(url, ApiToken.GET_TOKEN(getBaseContext()), categoryId , String.valueOf(getPageNumberToLoadData()));
        } else {
            seeAllVideosList = mRemoteVideoApiInterface.getSeeAllVideos(url, ApiToken.GET_TOKEN(getBaseContext()), String.valueOf(getPageNumberToLoadData()));
        }



        seeAllVideosList.enqueue(new Callback<ApiCommonDetailListResponse>() {
            @Override
            public void onResponse(Call<ApiCommonDetailListResponse> call, Response<ApiCommonDetailListResponse> response) {
                isLoadingDataInProgress = false;
                mBinding.progressBarCircular.setVisibility(View.GONE);
                mBinding.constraintLayoutDataNotFound.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    goneProgressBarAndShowRecyclerView(mBinding.progressBarCircular, mBinding.baseRecyclerViewSeeAllVideos);
                    ApiCommonDetailListResponse apiCommonDetailListResponse = response.body();//it is a list
                    if (apiCommonDetailListResponse.getStatusCode().equals(AppConstants.SUCCESS)){
                        List<Datum> dataList = apiCommonDetailListResponse.getData();

                        if ( apiCommonDetailListResponse.getData() == null   && mSeeAllVideosAdapter.getItems().size() != 0) {
                            // Toaster.showLong("You've reached last page ");
                            return;
                        }

                        int rvSize = mSeeAllVideosAdapter.getItemCount();
                        if (dataList !=null){
                            populateData(dataList, rvSize);
                        }
                        setPageNumberToLoadData(getPageNumberToLoadData() + 1);
                        if (dataList !=null){
                            mSeeAllVideosAdapter.addAllItemToPosition(dataList, rvSize);
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
                isLoadingDataInProgress = false;
                mBinding.progressBarCircular.setVisibility(View.GONE);
            }
        });

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

    /**
     * Get data from server added into list
     */
    private void populateData(List<Datum> channelList, int position) {
        if (mModelList != null) {
            mModelList.addAll(position, channelList);
        }
    }


    @Override
    public void LoadImage(ImageView imageView, ProgressBar progressBar, String imageLink, Integer[] heightWidth) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RequestOptions requestOptions = new RequestOptions();
                requestOptions = requestOptions.transforms(new FitCenter(), new RoundedCorners(5)).override(heightWidth[0], heightWidth[1]).error(R.drawable.default_img); // resizes the image to these dimensions (in pixel)
                Glide.with(getBaseContext())
                        .load(imageLink)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                progressBar.setVisibility(View.GONE);
                                Log.d("fabtest", "Error: " + e);
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



}
