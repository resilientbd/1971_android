package com.w3engineers.core.videon.ui.searchmovies;


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
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.w3engineers.core.util.CheckVideoTypeUtil;
import com.w3engineers.core.util.NetworkURL;
import com.w3engineers.core.util.helper.AdHelper;
import com.w3engineers.core.util.helper.ApiToken;
import com.w3engineers.core.util.helper.AppConstants;
import com.w3engineers.core.util.helper.IntentKeys;
import com.w3engineers.core.util.helper.KeyboardUtils;
import com.w3engineers.core.util.helper.TransactionHelper;
import com.w3engineers.core.videon.R;
import com.w3engineers.core.videon.data.local.Enums;
import com.w3engineers.core.videon.data.local.commondatalistresponse.ApiCommonDetailListResponse;
import com.w3engineers.core.videon.data.local.commondatalistresponse.Datum;
import com.w3engineers.core.videon.data.remote.RemoteApiProvider;
import com.w3engineers.core.videon.data.remote.home.RemoteVideoApiInterface;
import com.w3engineers.core.videon.databinding.ActivitySearchMoviesBinding;
import com.w3engineers.core.videon.ui.home.UICommunicatorInterface;
import com.w3engineers.core.videon.ui.videodetails.VideoDetailsActivity;
import com.w3engineers.ext.strom.application.ui.base.BaseActivity;
import com.w3engineers.ext.strom.application.ui.base.ItemClickListener;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SearchMoviesActivity extends BaseActivity implements TextView.OnEditorActionListener , UICommunicatorInterface {

    private ActivitySearchMoviesBinding mBinding;
    private SearchMoviesAdapter  mSearchMoviesAdapter;
    private RemoteVideoApiInterface mRemoteVideoApiInterface;
    private List<Datum> mDataList;
    private int pageNumberToLoadData = 1;
    private int pageNumberToLoadDataForSearchPage = 1;
    private boolean isLoadingDataInProgress = false;
    private boolean isSearchPage=false;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search_movies;
    }


    public static void runActivity(Context context, String searchText) {
        Intent intent = new Intent(context, SearchMoviesActivity.class);
        intent.putExtra(IntentKeys.SEARCH_TEXT,searchText);
        runCurrentActivity(context, intent);
    }

    @Override
    protected void startUI() {
        mBinding=(ActivitySearchMoviesBinding)getViewDataBinding();
        mRemoteVideoApiInterface = RemoteApiProvider.getInstance().getRemoteHomeVideoApi();
        setSupportActionBar(mBinding.toolbarSearchMovies);
        initSearchMoviesAdapter();
        KeyboardUtils.hideSoftInput(this);
        AdHelper.loadBannerAd(this,(LinearLayout) mBinding.adView);
        getIntendData();
        textChangeListenerForSearch(mBinding.editTextSearch);
        mBinding.editTextSearch.setOnEditorActionListener(this);
        mBinding.progressBarCircular.setProgress(AppConstants.progress);
    }


    /**
     * init Search Movies adapter
     */
    private void initSearchMoviesAdapter(){
        mSearchMoviesAdapter=new SearchMoviesAdapter(this,this);
        mBinding.baseRecyclerViewSearchList.setAdapter(mSearchMoviesAdapter);
        mBinding.baseRecyclerViewSearchList.setLayoutManager(new LinearLayoutManager(this));
        mBinding.baseRecyclerViewSearchList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //not scrolling
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    boolean canScrollDownMore = recyclerView.canScrollVertically(1);
                    if (!canScrollDownMore)
                        if (!isLoadingDataInProgress)///Checking if retrofit is busy to load data or not
                            getSearchVideosFromServer(mBinding.editTextSearch.getText().toString().trim());//Now recycler view is in bottom . So loading more data .
                }
            }

        });
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
     * Get text from the edit text
     * @param s search text
     */
    private void filter(String s) {
        if(s.length()==AppConstants.zero){
            if (mDataList !=null){
                mDataList.clear();
            }
            mSearchMoviesAdapter.clear();
            mBinding.constraintLayoutDataNotFound.setVisibility(View.VISIBLE);
        }
    }

    /**
     * get search text from intent
     */
    private void getIntendData(){
        Bundle bundle=getIntent().getExtras();
        if (bundle != null){
            String searchTextFromHomePage =bundle.getString(IntentKeys.SEARCH_TEXT);
            mBinding.editTextSearch.setText(searchTextFromHomePage);
            mBinding.editTextSearch.setSelection(searchTextFromHomePage.length());
            getSearchVideosFromServer(searchTextFromHomePage);
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            String searchText=mBinding.editTextSearch.getText().toString().trim();
            if (searchText.length()> AppConstants.zero){
                mSearchMoviesAdapter.clear();
                if (mDataList != null){
                    mDataList.clear();
                    isSearchPage=true;
                    mBinding.progressBarCircular.setProgress(AppConstants.progress);
                    getSearchVideosFromServer(searchText);
                    KeyboardUtils.hideSoftInput(this);
                }else {
                    isSearchPage=true;
                    mBinding.progressBarCircular.setProgress(AppConstants.progress);
                    getSearchVideosFromServer(searchText);
                    KeyboardUtils.hideSoftInput(this);
                }
            }
            return true;
        }
        return false;
    }


    /**
     * Get search videos information from server
     * @param searchText user input
     */
    private void getSearchVideosFromServer(String searchText) {
        isLoadingDataInProgress = false;
        Call<ApiCommonDetailListResponse> call;

        if (isSearchPage){
            call= mRemoteVideoApiInterface.getSearchVideos(""+searchText, ApiToken.GET_TOKEN(getBaseContext()), String.valueOf(pageNumberToLoadDataForSearchPage));
        }else {
            call = mRemoteVideoApiInterface.getSearchVideos(""+searchText, ApiToken.GET_TOKEN(getBaseContext()), String.valueOf(getPageNumberToLoadData()));
        }

        call.enqueue(new Callback<ApiCommonDetailListResponse>() {
            @Override
            public void onResponse(Call<ApiCommonDetailListResponse> call, Response<ApiCommonDetailListResponse> response) {
                if (response.isSuccessful()){
                    isLoadingDataInProgress = true;
                    goneProgressBarAndShowRecyclerView(mBinding.progressBarCircular,mBinding.baseRecyclerViewSearchList);
                    ApiCommonDetailListResponse apiCommonDetailListResponse = response.body();//it is a list
                    if (apiCommonDetailListResponse.getStatusCode().equals(AppConstants.SUCCESS)){
                        mDataList = apiCommonDetailListResponse.getData();
                        if (mDataList !=null){
                            if (apiCommonDetailListResponse.getData() == null && mSearchMoviesAdapter.getItems().size() != 0) {
                                return; //last page
                            }
                            mSearchMoviesAdapter.setItemClickListener(new ItemClickListener<Datum>() {
                                @Override
                                public void onItemClick(View view, Datum item) {
                                    goToVideoDetails(item);
                                }
                            });
                            int rvSize = mSearchMoviesAdapter.getItemCount();
                        /*    if (isSearchPage){
                                pageNumberToLoadDataForSearchPage=pageNumberToLoadDataForSearchPage+ 1;
                            }else {
                                setPageNumberToLoadData(getPageNumberToLoadData() + 1);
                            }*/

                            mSearchMoviesAdapter.addAllItemToPosition(mDataList, rvSize);

                            if (mDataList.size()==AppConstants.zero){
                                mBinding.progressBarCircular.setVisibility(View.GONE);
                                mBinding.constraintLayoutDataNotFound.setVisibility(View.VISIBLE);
                            }else {
                                mBinding.constraintLayoutDataNotFound.setVisibility(View.GONE);
                            }
                        }else {
                            mBinding.progressBarCircular.setVisibility(View.GONE);
                            mBinding.constraintLayoutDataNotFound.setVisibility(View.VISIBLE);
                        }
                    }else {
                        mBinding.progressBarCircular.setVisibility(View.GONE);
                        mBinding.constraintLayoutDataNotFound.setVisibility(View.VISIBLE);
                    }

                }
            }

            @Override
            public void onFailure(Call<ApiCommonDetailListResponse> call, Throwable t) {
                mBinding.progressBarCircular.setVisibility(View.GONE);
                mBinding.constraintLayoutDataNotFound.setVisibility(View.VISIBLE);
                isLoadingDataInProgress = false;
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
     * Go video details
     * @param datum datum
     */
    public void goToVideoDetails(Datum datum) {
        if (datum.getLink() == null) {
            CheckVideoTypeUtil.checkVideoType(datum);
        }
        VideoDetailsActivity.runActivity(SearchMoviesActivity.this, datum);
        TransactionHelper.TransactionLeftToRight(SearchMoviesActivity.this);
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

    @Override
    public void LoadImage(ImageView imageView, ProgressBar progressBar, String imageLink) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("mylink",imageLink);
                RequestOptions requestOptions = new RequestOptions();
                requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(16)).error(R.drawable.default_img);
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
                        }).apply(requestOptions)
                        .into(imageView);
            }
        });
    }
}
