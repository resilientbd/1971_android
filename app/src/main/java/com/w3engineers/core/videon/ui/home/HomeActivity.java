package com.w3engineers.core.videon.ui.home;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.w3engineers.core.util.CheckVideoTypeUtil;
import com.w3engineers.core.util.LocaleUtils;
import com.w3engineers.core.util.helper.ApiToken;
import com.w3engineers.core.util.helper.AppConstants;
import com.w3engineers.core.util.helper.CheckNetworkAvailabilityAndPermission;
import com.w3engineers.core.util.helper.Constants;
import com.w3engineers.core.util.helper.MySessionManager;
import com.w3engineers.core.util.helper.PrefType;
import com.w3engineers.core.util.helper.PreferenceKey;
import com.w3engineers.core.util.helper.ProgressbarHandler;
import com.w3engineers.core.util.helper.SharedPref;
import com.w3engineers.core.util.helper.TransactionHelper;
import com.w3engineers.core.videon.R;
import com.w3engineers.core.videon.data.local.Enums;
import com.w3engineers.core.videon.data.local.adaptermodel.Model;
import com.w3engineers.core.videon.data.local.commondatalistresponse.ApiCommonDetailListResponse;
import com.w3engineers.core.videon.data.local.commondatalistresponse.Datum;
import com.w3engineers.core.videon.data.remote.RemoteApiProvider;
import com.w3engineers.core.videon.data.remote.home.RemoteVideoApiInterface;
import com.w3engineers.core.videon.databinding.ActivityHomeBinding;
import com.w3engineers.core.videon.ui.adapter.CommonDataAdapter;
import com.w3engineers.core.videon.ui.adapter.HomeCategoryAdapter;
import com.w3engineers.core.videon.ui.channellist.ChannelListActivity;
import com.w3engineers.core.videon.ui.downloadmanager.DownloadManagerActivity;
import com.w3engineers.core.videon.ui.empty.EmptyActivity;
import com.w3engineers.core.videon.ui.login.LoginActivity;
import com.w3engineers.core.videon.ui.myprofile.MyProfileActivity;
import com.w3engineers.core.videon.ui.searchmovies.SearchMoviesActivity;
import com.w3engineers.core.videon.ui.seeallvideos.SeeAllVideosActivity;
import com.w3engineers.core.videon.ui.setting.SettingActivity;
import com.w3engineers.core.videon.ui.videodetails.VideoDetailsActivity;
import com.w3engineers.core.videon.ui.videos.VideosActivity;
import com.w3engineers.ext.strom.application.ui.base.BaseActivity;
import com.w3engineers.ext.strom.application.ui.base.ItemClickListener;
import com.w3engineers.ext.strom.application.ui.widget.BaseRecyclerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeActivity extends BaseActivity implements ItemClickListener<Model>, UICommunicator,
        MySessionManager.CallBackListener, TextView.OnEditorActionListener,
        SwipeRefreshLayout.OnRefreshListener {

    private ActivityHomeBinding mBinding;
    private HomeCategoryAdapter mCategoryAdapter;
    private CommonDataAdapter liveDataAdapter;
    private CommonDataAdapter mFeaturedVideoAdapter;
    private CommonDataAdapter mMostPopularVideoAdapter;
    private CommonDataAdapter mMostRecentVideoAdapter;
    private boolean isLoadingDataInProgress = false;
    private int pageNumberToLoadDataRecentVideos = 1;
    private int pageNumberToLoadDataFeaturedVideos = 1;
    private int pageNumberToLoadDataPopularVideos = 1;
    private int pageNumberToLoadDataLiveTv = 1;
    private int pageNumberToLoadDataCategory = 1;
    private boolean hasNetWork;
    private SharedPreferences preferences;
    private Menu mMenuHome;
    private MenuItem logoutButton;
    final Handler handler = new Handler();
    CheckNetworkAvailabilityAndPermission mCheckNetworkAvailabilityAndPermission = new CheckNetworkAvailabilityAndPermission();

    private RemoteVideoApiInterface mRemoteVideoApiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
    }

    public static void runActivity(Context context) {
        Intent intent = new Intent(context, HomeActivity.class);
        runCurrentActivity(context, intent);

    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    protected int getMenuId() {
        return R.menu.menu_home;
    }

    @Override
    protected void startUI() {

        mBinding = (ActivityHomeBinding) getViewDataBinding();
        mBinding.videosection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VideosActivity.runActivity(HomeActivity.this);
                Log.d("datacheck","clicked");
            }
        });
        enableProgressLayer();
        FirebaseMessaging.getInstance().subscribeToTopic("message");
        mRemoteVideoApiInterface = RemoteApiProvider.getInstance().getRemoteHomeVideoApi();

        //initialize the adapters
        initCategoryAdapter();
        initLiveTvAdapter();
        initFeaturedVideoAdapter();
        initMostPopularVideoAdapter();
        initMostRecentVideoAdapter();

        setSupportActionBar(mBinding.toolbarHome);

        mBinding.editTextSearch.setOnEditorActionListener(this);

        mBinding.swipeRefresh.setOnRefreshListener(this);
        //disable swipte to refresh temporary
        mBinding.swipeRefresh.setEnabled(false);
        mBinding.swipeRefresh.setRefreshing(false);

        //click listener
        setClickListener(mBinding.buttonSeeAllLive, mBinding.buttonSeeAllFeatured,
                mBinding.buttonSeeAllMostPopular, mBinding.buttonSeeAllMostRecent, mBinding.floatingActionButton);
        scrollListenerAndHitServerForVideos();
        //get videos data from server
       // getLiveTVChannelsFromServer();
        getFeaturedVideosFromServer();
        getMostPopularVideosFromServer();
       // getCategoryVideosFromServer();
        //retrieveAllPostRecentVideosDataFromServer();


        checkInternetConnection();
        checkNotificationPreference();
        addShadowInToolbar();
        delay();
        settingOfLanguage();



    }


    /**
     * Setting language
     */
    private void settingOfLanguage() {
        String lan = readSharedPref(SharedPref.LAN);
        if (!lan.equals("")) {
            if (lan.equals(Enums.CHINESE)) {
                LocaleUtils.initialize(this, LocaleUtils.CHINESE);

            } else {
                LocaleUtils.initialize(this, LocaleUtils.ENGLISH);
            }
        }else {
            LocaleUtils.initialize(this, LocaleUtils.ENGLISH);
        }
    }

    /**
     * update views
     */
    private void updateViews() {
        mBinding.textViewLiveTv.setText(getResources().getString(R.string.home_live_tv));
        mBinding.buttonSeeAllLive.setText(getResources().getString(R.string.button_see_all_featured));

        mBinding.textViewFeatured.setText(getResources().getString(R.string.home_featured));
        mBinding.buttonSeeAllFeatured.setText(getResources().getString(R.string.button_see_all_featured));

        mBinding.textViewMostPopular.setText(getResources().getString(R.string.text_home_most_popular));
        mBinding.buttonSeeAllMostPopular.setText(getResources().getString(R.string.button_see_all_featured));

        mBinding.textViewMostRecent.setText(getResources().getString(R.string.home_text_most_recent));
        mBinding.buttonSeeAllMostRecent.setText(getResources().getString(R.string.button_see_all_featured));

    }

    /**
     * Will delay some time
     */
    private void delay() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                disabpleProgressLayer();
                //ProgressbarHandler.DismissProgress(HomeActivity.this);
            }
        }, 3000);
    }


    /**
     * Hit server for videos
     */
    private void scrollListenerAndHitServerForVideos() {

        mBinding.baseRecyclerViewCategory.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    boolean canScrollDownMore = recyclerView.canScrollHorizontally(1);
                    if (!canScrollDownMore)
                        if (!isLoadingDataInProgress) {
                            getCategoryVideosFromServer();
                        }
                }
            }

        });


        mBinding.baseRecyclerViewFeatured.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    boolean canScrollDownMore = recyclerView.canScrollHorizontally(1);
                    if (!canScrollDownMore)
                        if (!isLoadingDataInProgress) {
                            getFeaturedVideosFromServer();
                        }
                }
            }

        });


        mBinding.baseRecyclerViewMostPopular.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    boolean canScrollDownMore = recyclerView.canScrollHorizontally(1);
                    if (!canScrollDownMore)
                        if (!isLoadingDataInProgress) {
                            getMostPopularVideosFromServer();
                        }
                }
            }

        });


        mBinding.baseRecyclerViewLiveTv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    boolean canScrollDownMore = recyclerView.canScrollHorizontally(1);
                    if (!canScrollDownMore)
                        if (!isLoadingDataInProgress) {
                            getLiveTVChannelsFromServer();
                        }
                }
            }

        });


    }

    /**
     * Add Shadow to the toolbar when scrolled
     */
    private void addShadowInToolbar() {
        if (mBinding.nestedScrollView != null) {
            mBinding.nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                    if (scrollY > oldScrollY) {
                        mBinding.viewToolbarShadow.setVisibility(View.VISIBLE);//Scroll DOWN
                    }
                    if (scrollY < oldScrollY) {
                        mBinding.viewToolbarShadow.setVisibility(View.VISIBLE);//Scroll UP
                    }

                    if (scrollY == 0) {
                        mBinding.viewToolbarShadow.setVisibility(View.GONE);//TOP SCROLL
                    }

                    if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {//BOTTOM SCROLL
                        mBinding.viewToolbarShadow.setVisibility(View.VISIBLE);
                        if (!isLoadingDataInProgress)///Checking if retrofit is busy to load data or not
                            retrieveAllPostRecentVideosDataFromServer();
                    }
                }
            });
        }

    }

    /**
     * Check internet connection for first time
     */
    private void checkInternetConnection() {
        hasNetWork = mCheckNetworkAvailabilityAndPermission.checkPermissions(this) && mCheckNetworkAvailabilityAndPermission.checkIfHasNetwork(this);
        if (hasNetWork) {
            startProgressBarForLoading();
        } else {
            goneProgressBarForFirstTimeLoading();
        }
    }

    /**
     * Start progress bar category, live tv, featured, most popular
     * For first time
     */
    private void startProgressBarForLoading() {
        mBinding.progressBarCategory.setProgress(AppConstants.progress);
        mBinding.progressBarLiveTv.setProgress(AppConstants.progress);
        mBinding.progressBarCategory.setProgress(AppConstants.progress);
        mBinding.progressBarMostRecent.setProgress(AppConstants.progress);
    }


    /**
     * Gone progress bar category, live tv, featured, most popular
     * For first time if net working not ready
     */
    private void goneProgressBarForFirstTimeLoading() {
        mBinding.progressBarCategory.setVisibility(View.GONE);
        mBinding.progressBarLiveTv.setVisibility(View.GONE);
        mBinding.progressBarFeatured.setVisibility(View.GONE);
        mBinding.progressBarMostRecent.setVisibility(View.GONE);
    }

    public void disabpleProgressLayer() {
        ProgressbarHandler.DismissProgress(this);
        mBinding.progressLayer.setVisibility(View.GONE);
    }

    public void enableProgressLayer() {
        ProgressbarHandler.ShowLoadingProgress(this);
        mBinding.progressLayer.setVisibility(View.VISIBLE);
    }


    /**
     * Get live TV channels from server
     */
    private void getLiveTVChannelsFromServer() {
        Call<ApiCommonDetailListResponse> call = mRemoteVideoApiInterface.getLiveTVChannelVideos(ApiToken.GET_TOKEN(getBaseContext()), String.valueOf(getPageNumberToLoadDataLiveTvVideos()));
        getDataItemFromServer(call, mBinding.progressBarLiveTv, liveDataAdapter,
                mBinding.baseRecyclerViewLiveTv, mBinding.constraintLayoutLiveTvSection, Constants.SeeAllVideos.LIVE_TV);
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
     * Gone progress bar when it is call
     *
     * @param progressBar progress bar
     */
    private void goneProgressBar(ProgressBar progressBar) {
        progressBar.setVisibility(View.GONE);
    }


    /**
     * Get Featured videos channels from server
     */
    private void getFeaturedVideosFromServer() {
        Call<ApiCommonDetailListResponse> call = mRemoteVideoApiInterface.getFeaturedVideos(ApiToken.GET_TOKEN(getBaseContext()), String.valueOf(getPageNumberToLoadDataFeaturedVideos()));
        getDataItemFromServer(call, mBinding.progressBarFeatured, mFeaturedVideoAdapter,
                mBinding.baseRecyclerViewFeatured, mBinding.constraintLayoutFeaturedSection, Constants.SeeAllVideos.FEATURED_VIDEO_PAGE);
    }

    /**
     * @param call             object for call api
     * @param progressBar      progressbar of corresponding xml
     * @param mBaseRecycleView recycleview of corresponding item
     */
    public void getDataItemFromServer(Call call, ProgressBar progressBar, CommonDataAdapter mAdapter,
                                      BaseRecyclerView mBaseRecycleView, ConstraintLayout mLayout, String pageName) {

        isLoadingDataInProgress = true;
        call.enqueue(new Callback<ApiCommonDetailListResponse>() {
            @Override
            public void onResponse(Call<ApiCommonDetailListResponse> call, Response<ApiCommonDetailListResponse> response) {
                isLoadingDataInProgress = false;
                if (response.isSuccessful()) {
                    ApiCommonDetailListResponse apiCommonDetailListResponse = response.body();//it is a list
                    List<Datum> dataList = apiCommonDetailListResponse.getData();
                    goneProgressBarAndShowRecyclerView(progressBar, mBaseRecycleView);
                    if (apiCommonDetailListResponse.getData() == null && mAdapter.getItems().size() != 0) {
                        return; //last page
                    }
                    int recycleViewSize = mAdapter.getItemCount();
                    setPageNumberForPagination(pageName);

                    if (dataList != null) {
                        mAdapter.addAllItemToPosition(dataList, recycleViewSize);
                    }

                    //Click listener
                    mAdapter.setItemClickListener(new ItemClickListener<Datum>() {
                        @Override
                        public void onItemClick(View view, Datum item) {

                            goToNextPage(String.valueOf(item.getType()), item.getLink(), item);
                        }
                    });
                    if (mAdapter.getItemCount() == 0) {
                        if (mLayout != null) {
                            mLayout.setVisibility(View.GONE);
                        }
                    }
                }

            }

            @Override
            public void onFailure(Call<ApiCommonDetailListResponse> call, Throwable t) {
                isLoadingDataInProgress = false;
                goneProgressBar(progressBar);
                if (mLayout != null)
                    mLayout.setVisibility(View.GONE);
            }
        });
    }


    /**
     * @param call             object for call api
     * @param progressBar      progressbar of corresponding xml
     * @param mBaseRecycleView recycleview of corresponding item
     */
    public void getCategoryFromServer(Call call, ProgressBar progressBar, HomeCategoryAdapter mAdapter,
                                      BaseRecyclerView mBaseRecycleView, ConstraintLayout mLayout, String pageName) {

        isLoadingDataInProgress = true;
        call.enqueue(new Callback<ApiCommonDetailListResponse>() {
            @Override
            public void onResponse(Call<ApiCommonDetailListResponse> call, Response<ApiCommonDetailListResponse> response) {
                isLoadingDataInProgress = false;
                if (response.isSuccessful()) {
                    ApiCommonDetailListResponse apiCommonDetailListResponse = response.body();//it is a list
                    List<Datum> dataList = apiCommonDetailListResponse.getData();
                    goneProgressBarAndShowRecyclerView(progressBar, mBaseRecycleView);
                    if (apiCommonDetailListResponse.getData() == null && mAdapter.getItems().size() != 0) {
                        return; //last page
                    }
                    int recycleViewSize = mAdapter.getItemCount();
                    setPageNumberForPagination(pageName);

                    if (dataList != null) {
                        mAdapter.addAllItemToPosition(dataList, recycleViewSize);
                    }

                    //Click listener
                    mAdapter.setItemClickListener(new ItemClickListener<Datum>() {
                        @Override
                        public void onItemClick(View view, Datum item) {

                            goToNextPage(String.valueOf(item.getType()), item.getLink(), item);
                        }
                    });
                    if (mAdapter.getItemCount() == 0) {
                        if (mLayout != null) {
                            mLayout.setVisibility(View.GONE);
                        }
                    }
                }

            }

            @Override
            public void onFailure(Call<ApiCommonDetailListResponse> call, Throwable t) {
                isLoadingDataInProgress = false;
                goneProgressBar(progressBar);
                if (mLayout != null)
                    mLayout.setVisibility(View.GONE);
            }
        });
    }

    /**
     * Set page number for pagination
     */
    private void setPageNumberForPagination(String pageName) {
        if (pageName.equals(Constants.SeeAllVideos.MOST_RECENT_VIDEO_PAGE)) {
            setPageNumberToLoadDataRecentVideos(getPageNumberToLoadDataRecentVideos() + 1);
        } else if (pageName.equals(Constants.SeeAllVideos.MOST_POPULAR_VIDEO_PAGE)) {
            setPageNumberToLoadDataPopularVideos(getPageNumberToLoadDataPopularVideos() + 1);
        } else if (pageName.equals(Constants.SeeAllVideos.CATEGORY_PAGE)) {
            setPageNumberToLoadDataCategoryVideos(getPageNumberToLoadDataCategoryVideos() + 1);
        } else if (pageName.equals(Constants.SeeAllVideos.FEATURED_VIDEO_PAGE)) {
            setPageNumberToLoadDataFeaturedVideos(getPageNumberToLoadDataFeaturedVideos() + 1);
        } else if (pageName.equals(Constants.SeeAllVideos.LIVE_TV)) {
            setPageNumberToLoadDataLiveTvVideos(getPageNumberToLoadDataLiveTvVideos() + 1);

        }
    }

    // Constants.SeeAllVideos.MOST_POPULAR_VIDEO_PAGE, Constants.SeeAllVideos.NOT_CATEGORY);

    /**
     * Go to next page
     */
    private void goToNextPage(String type, String link, Datum datum) {
        if (type.equals("0") && link == null) {
            //category
            SeeAllVideosActivity.runActivity(this, Constants.SeeAllVideos.CATEGORY_PAGE, datum.getId(), datum.getTitle());
        } else {
            //all
            if (datum.getViewCount() == null) {
                goToVideoDetailsFromLiveTv(datum);
            } else {
                goToVideoDetails(datum);
            }

        }
    }


    /**
     * Go video details from live tv
     *
     * @param datum datum
     */
    public void goToVideoDetailsFromLiveTv(Datum datum) {
            switch (datum.getType()) {
                case Enums.YOUTUBE_VIDEO:
                    try{
                        if (datum.getLink().contains("=")){
                            String[] vidIdYoutube = datum.getLink().split("=");
                            datum.setLink("" + vidIdYoutube[1]);
                        }else{
                            datum.setLink(datum.getLink());
                        }
                    }catch (ArrayIndexOutOfBoundsException e){
                        Toast.makeText(this, getResources().getString(R.string.see_all_invalid_link), Toast.LENGTH_LONG).show();
                        return;
                    }
                    break;
                case Enums.live_link_m3u8:
                    datum.setLink(datum.getLiveLinkm3u8());
                    break;
                case Enums.live_rtmp:
                    //datum.setLink(datum.getLiveRtmp());
                    datum.setLink(convertRtmpTom3u8(datum.getLiveRtmp()));
                    datum.setType(Enums.live_link_m3u8);
                    datum.setLiveLinkm3u8(convertRtmpTom3u8(datum.getLiveRtmp()));
                    break;

        }
        VideoDetailsActivity.runActivity(HomeActivity.this, datum);
        TransactionHelper.TransactionLeftToRight(HomeActivity.this);
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
     * Get Most popular videos channels from server
     */
    private void getMostPopularVideosFromServer() {
        Call<ApiCommonDetailListResponse> call = mRemoteVideoApiInterface.getMostPopularVideos(ApiToken.GET_TOKEN(getBaseContext()),
                String.valueOf(getPageNumberToLoadDataPopularVideos()));
        getDataItemFromServer(call, mBinding.progressBarMostPopular, mMostPopularVideoAdapter,
                mBinding.baseRecyclerViewMostPopular, mBinding.constraintLayoutPopularSection, Constants.SeeAllVideos.MOST_POPULAR_VIDEO_PAGE);

    }


    /**
     * Get category videos from server
     */
    private void getCategoryVideosFromServer() {
        Call<ApiCommonDetailListResponse> call = mRemoteVideoApiInterface.getCategoryVideos(ApiToken.GET_TOKEN(getBaseContext()), String.valueOf(getPageNumberToLoadDataCategoryVideos()));
        getCategoryFromServer(call, mBinding.progressBarCategory, mCategoryAdapter,
                mBinding.baseRecyclerViewCategory, mBinding.constraintLayoutCategorySection, Constants.SeeAllVideos.CATEGORY_PAGE);
    }


    /**
     * initialize the category adapter
     */
    private void initCategoryAdapter() {
        mCategoryAdapter = new HomeCategoryAdapter(this);
        mBinding.baseRecyclerViewCategory.setAdapter(mCategoryAdapter);
        mBinding.baseRecyclerViewCategory.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,
                        false));
    }

    /**
     * initialize the TV adapter
     */
    private void initLiveTvAdapter() {
        liveDataAdapter = new CommonDataAdapter(this);
        mBinding.baseRecyclerViewLiveTv.setAdapter(liveDataAdapter);
        mBinding.baseRecyclerViewLiveTv.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,
                        false));

    }

    /**
     * initialize the Featured adapter
     */
    private void initFeaturedVideoAdapter() {
        mFeaturedVideoAdapter = new CommonDataAdapter(getBaseContext());
        mBinding.baseRecyclerViewFeatured.setAdapter(mFeaturedVideoAdapter);
        mBinding.baseRecyclerViewFeatured.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,
                        false));

    }

    /**
     * initialize the Most popular video adapter
     */
    private void initMostPopularVideoAdapter() {

        mMostPopularVideoAdapter = new CommonDataAdapter(getBaseContext());
        mBinding.baseRecyclerViewMostPopular.setAdapter(mMostPopularVideoAdapter);
        mBinding.baseRecyclerViewMostPopular.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,
                        false));

    }

    /**
     * initialize the Most recent video adapter
     */
    /**
     * initialize the Most recent video adapter
     */
    @Override
    public void LoadImage(ImageView imageView, ProgressBar progressBar, String imageLink, Integer[] heightWidth) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RequestOptions requestOptions = new RequestOptions();
                requestOptions = requestOptions.transforms(new FitCenter(), new RoundedCorners(5))
                        .override(heightWidth[0], heightWidth[1]).error(R.drawable.default_img); // resizes the image to these dimensions (in pixel)
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


    /**
     * Init most recent video Adapter
     */
    private void initMostRecentVideoAdapter() {
        mMostRecentVideoAdapter = new CommonDataAdapter(getBaseContext(), true);
        mBinding.baseRecyclerViewMostRecent.setAdapter(mMostRecentVideoAdapter);
        mBinding.baseRecyclerViewMostRecent.setLayoutManager(
                new GridLayoutManager(this, 2));
        mBinding.baseRecyclerViewMostRecent.setNestedScrollingEnabled(false);



        mMostRecentVideoAdapter.setItemClickListener(new ItemClickListener<Datum>() {
            @Override
            public void onItemClick(View view, Datum item) {
                goToVideoDetails(item);
            }
        });
    }

    /**
     * go to video details
     *
     * @param datum date
     */
    public void goToVideoDetails(Datum datum) {
        if (datum.getLink() == null) {
            CheckVideoTypeUtil.checkVideoType(datum);
        }
        VideoDetailsActivity.runActivity(HomeActivity.this, datum);
        overridePendingTransition(R.anim.enter,R.anim.exit);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_see_all_live:
                ChannelListActivity.runActivity(this);
                break;
            case R.id.button_see_all_featured:
                SeeAllVideosActivity.runActivity(this, Constants.SeeAllVideos.FEATURED_VIDEO_PAGE, Constants.SeeAllVideos.NOT_CATEGORY);
                break;
            case R.id.button_see_all_most_popular:
                SeeAllVideosActivity.runActivity(this, Constants.SeeAllVideos.MOST_POPULAR_VIDEO_PAGE, Constants.SeeAllVideos.NOT_CATEGORY);
                break;
            case R.id.button_see_all_most_recent:
                SeeAllVideosActivity.runActivity(this, Constants.SeeAllVideos.MOST_RECENT_VIDEO_PAGE, Constants.SeeAllVideos.NOT_CATEGORY);
                break;
            case R.id.floatingActionButton:
                startActivityForResult(new Intent(HomeActivity.this, DownloadManagerActivity.class), 111);
                break;
        }
    }

    @Override
    public void onItemClick(View view, Model item) {
        TransactionHelper.TransactionLeftToRight(HomeActivity.this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        SharedPref.init(getBaseContext());
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        String loginText = "";
        this.mMenuHome = menu;
        if (SharedPref.readBoolean(PrefType.AUTH_STATUS) == PrefType.STATUS_SUCCESS) {
            loginText = getResources().getString(R.string.log_out);
        } else {
            loginText = getResources().getString(R.string.login);
        }
        this.logoutButton = menu.findItem(R.id.action_login);
        menu.findItem(R.id.action_login).setTitle(loginText);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_profile:
                if (!SharedPref.read(PrefType.USER_REGID).equals("")) {
                    //login state
                    MyProfileActivity.runActivity(this);
                } else {
                    gotToEmptyActivity();

                }
                return true;
            case R.id.action_settings:
                //login state
                SettingActivity.runActivity(this);

                return true;
            case R.id.action_login:
                if (!SharedPref.read(PrefType.USER_REGID).equals("")) {
                    //login state
                    alertDialog();
                } else {
                    LoginActivity.runActivity(this);
                    //  finish();
                }
                return true;
            case R.id.action_playlist:
                if (!SharedPref.read(PrefType.USER_REGID).equals("")) {
                    //login state
                    MyProfileActivity.runActivity(this);
                } else {
                    gotToEmptyActivity();
                }
                return true;

            case android.R.id.home:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Update the menu title
     */
    private void updateMenuTitles(Menu menu) {
        MenuItem profile, setting, playList;

        profile = menu.findItem(R.id.action_profile);
        profile.setTitle(getResources().getString(R.string.menu_profile));

        setting = menu.findItem(R.id.action_settings);
        setting.setTitle(getResources().getString(R.string.Setting_text));

        playList = menu.findItem(R.id.action_playlist);
        playList.setTitle(getResources().getString(R.string.menu_playlist));

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (Build.VERSION.SDK_INT >= 16) {
            invalidateOptionsMenu();
            updateMenuTitles(menu);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void complete(boolean isSuccess) {
        logOutAlert();
    }

    public void logOutAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getResources().getString(R.string.logout_success));
        builder.setMessage(getResources().getString(R.string.sign_out_success));

        builder.setPositiveButton(getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                try {
                    dialog.dismiss();
                } catch (Exception e) {
                }
            }
        });


        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Log out current user
     *
     * @param callBackListener MySessionManager.CallBackListener
     */
    private void revokeAccess(MySessionManager.CallBackListener callBackListener) {

        if (SharedPref.readInt(PrefType.AUTH_TYPE) == Enums.GOOGLE_LOGIN) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)

                    .requestEmail()
                    .build();
            GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

            mGoogleSignInClient.revokeAccess()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // ...
                            new MySessionManager().googleLogOut(getBaseContext(), callBackListener);

                        }
                    });
            logoutButton.setTitle(getResources().getString(R.string.login));
        } else if (SharedPref.readInt(PrefType.AUTH_TYPE) == Enums.FACEBOOK_LOGIN) {
            LoginManager.getInstance().logOut();
            new MySessionManager().facebookLogOut(getBaseContext(), callBackListener);
            logoutButton.setTitle(getResources().getString(R.string.login));
        } else if (SharedPref.readInt(PrefType.AUTH_TYPE) == Enums.EMAIL_LGOIN) {
            new MySessionManager().emailLogOut(getBaseContext(), callBackListener);
            logoutButton.setTitle(getResources().getString(R.string.login));
        }
    }

    int notracker = 1;

    /**
     * get the most recent videos from server
     */
    public void retrieveAllPostRecentVideosDataFromServer() {

        if (!new CheckNetworkAvailabilityAndPermission().checkIfHasNetwork(this)) {
            ProgressbarHandler.DismissProgress(this);
            mBinding.progressLayer.setVisibility(View.GONE);
            return;
        }

        isLoadingDataInProgress = true;
        Call<ApiCommonDetailListResponse> call = mRemoteVideoApiInterface.getRecentVideo(ApiToken.GET_TOKEN(getBaseContext()), String.valueOf(getPageNumberToLoadDataRecentVideos()));
        getDataItemFromServer(call, mBinding.progressBarMostRecent, mMostRecentVideoAdapter,
                mBinding.baseRecyclerViewMostRecent, mBinding.mostRecentLayer, Constants.SeeAllVideos.MOST_RECENT_VIDEO_PAGE);
    }

    /**
     * Get page number
     *
     * @return pageNumber
     */
    public int getPageNumberToLoadDataRecentVideos() {
        return pageNumberToLoadDataRecentVideos;
    }

    /**
     * Set page number to load data
     *
     * @param pageNumberToLoadData page number
     */
    public void setPageNumberToLoadDataRecentVideos(int pageNumberToLoadData) {
        this.pageNumberToLoadDataRecentVideos = pageNumberToLoadData;
    }

    /**
     * Get page number
     *
     * @return pageNumber
     */
    public int getPageNumberToLoadDataFeaturedVideos() {
        return pageNumberToLoadDataFeaturedVideos;
    }

    /**
     * Set page number to load data
     *
     * @param pageNumberToLoadData page number
     */
    public void setPageNumberToLoadDataFeaturedVideos(int pageNumberToLoadData) {
        this.pageNumberToLoadDataFeaturedVideos = pageNumberToLoadData;
    }

    /**
     * Get page number
     *
     * @return pageNumber
     */
    public int getPageNumberToLoadDataPopularVideos() {
        return pageNumberToLoadDataPopularVideos;
    }

    /**
     * Set page number to load data
     *
     * @param pageNumberToLoadData page number
     */
    public void setPageNumberToLoadDataPopularVideos(int pageNumberToLoadData) {
        this.pageNumberToLoadDataPopularVideos = pageNumberToLoadData;
    }

    /**
     * Get page number
     *
     * @return pageNumber
     */
    public int getPageNumberToLoadDataCategoryVideos() {
        return pageNumberToLoadDataCategory;
    }

    /**
     * Set page number to load data
     *
     * @param pageNumberToLoadData page number
     */
    public void setPageNumberToLoadDataCategoryVideos(int pageNumberToLoadData) {
        this.pageNumberToLoadDataCategory = pageNumberToLoadData;
    }

    /**
     * Get page number
     *
     * @return pageNumber
     */
    public int getPageNumberToLoadDataLiveTvVideos() {
        return pageNumberToLoadDataLiveTv;
    }

    /**
     * Set page number to load data
     *
     * @param pageNumberToLoadData page number
     */
    public void setPageNumberToLoadDataLiveTvVideos(int pageNumberToLoadData) {
        this.pageNumberToLoadDataLiveTv = pageNumberToLoadData;
    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            String searchText = mBinding.editTextSearch.getText().toString().trim();
            if (searchText.length() > AppConstants.zero) {
                SearchMoviesActivity.runActivity(this, searchText);
            }
            return true;
        }
        return false;
    }


    /**
     * Dialog for to do login
     */
    public void gotToEmptyActivity() {
        EmptyActivity.runActivity(this);
        //  finish();
    }

    /**
     * Check Notification
     */
    public void checkNotificationPreference() {
        try {
            SharedPref.init(getBaseContext());
            if (!SharedPref.readBoolean(PreferenceKey.PREVIOUS_OPEN)) {
                SharedPref.write(PreferenceKey.PREVIOUS_OPEN, true);
                SharedPref.write(PreferenceKey.NOTIFICATION_STATUS, true);
            }
        } catch (Exception e) {
            SharedPref.write(PreferenceKey.PREVIOUS_OPEN, true);
            SharedPref.write(PreferenceKey.NOTIFICATION_STATUS, true);
        }
    }

    /***
     * Alert Dialog for log out
     */
    private void alertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getResources().getString(R.string.dialog_confirm_login));
        builder.setMessage(getResources().getString(R.string.sign_out_warning));

        builder.setPositiveButton(getResources().getString(R.string.dialog_yes_login), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                try {
                    revokeAccess(HomeActivity.this);
                } catch (Exception e) {
                }
            }
        });

        builder.setNegativeButton(getResources().getString(R.string.dialog_no_login), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }


    /**
     * Get data from server
     */
    private void swipeToRefreshAndNetworkCall() {
        enableProgressLayer();
        liveDataAdapter.clear();
        mFeaturedVideoAdapter.clear();
        mMostPopularVideoAdapter.clear();
        mCategoryAdapter.clear();
        mMostRecentVideoAdapter.clear();
        getLiveTVChannelsFromServer();
        getFeaturedVideosFromServer();
        getMostPopularVideosFromServer();
        getCategoryVideosFromServer();
        retrieveAllPostRecentVideosDataFromServer();
    }

    @Override
    public void onRefresh() {
        swipeToRefreshAndNetworkCall();
    }


    /**
     * @param key desire string value
     * @return value of the preference
     */
    public String readSharedPref(String key) {
        if (preferences == null) {
            preferences = getSharedPreferences("local", Context.MODE_PRIVATE);
        }
        return preferences.getString(key, "");
    }


    @Override
    protected void onResume() {
        super.onResume();
        updateViews();
    }
}
