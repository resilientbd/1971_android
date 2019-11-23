package com.shadowhite.archieve1971.ui.favourite;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
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
import com.shadowhite.archieve1971.databinding.FragmentFavouriteBinding;
import com.shadowhite.archieve1971.ui.home.UICommunicator;
import com.shadowhite.archieve1971.ui.videodetails.VideoDetailsActivity;
import com.shadowhite.util.CheckVideoTypeUtil;
import com.shadowhite.util.helper.ApiToken;
import com.shadowhite.util.helper.AppConstants;
import com.shadowhite.util.helper.CheckNetworkAvailabilityAndPermission;
import com.shadowhite.util.helper.PrefType;
import com.shadowhite.util.helper.SharedPref;
import com.w3engineers.ext.strom.application.ui.base.BaseFragment;
import com.w3engineers.ext.strom.application.ui.base.ItemClickListener;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FavouriteFragment extends BaseFragment implements UICommunicator {
    RemoteVideoApiInterface mRemoteVideoApiInterface;
    private FragmentFavouriteBinding mBinding;
    //TODO
    private FavouriteAdapter mAdapter;
    private boolean isLoadingDataInProgress = false;
    private int pageNumberToLoadData = 1;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_favourite;
    }

    @Override
    protected void startUI() {
        mBinding = (FragmentFavouriteBinding) getViewDataBinding();
        mAdapter = new FavouriteAdapter(getContext(), this);
        mBinding.baseRecyclerViewFavourite.setAdapter(mAdapter);
        mBinding.baseRecyclerViewFavourite.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRemoteVideoApiInterface = RemoteApiProvider.getInstance().getRemoteHomeVideoApi();

        // loadDataSeeAllVideosAdapter(""+0);
        scrollListenerAndHitServerForVideos();
        retrieveVideosFromServer();
    }


    /**
     * Hit server for videos
     */
    private void scrollListenerAndHitServerForVideos() {

        mBinding.baseRecyclerViewFavourite.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    boolean canScrollDownMore = recyclerView.canScrollVertically(1);
                    if (!canScrollDownMore)
                        if (!isLoadingDataInProgress)///Checking if retrofit is busy to load data or not
                            retrieveVideosFromServer();//Now recycler view is in bottom . So loading more data .
                }
            }

        });
    }


    /**
     * retrieve all videos from server
     * paging added here
     */
    public void retrieveVideosFromServer() {

        if (!new CheckNetworkAvailabilityAndPermission().checkIfHasNetwork(getActivity())) {

            return;
        }

        isLoadingDataInProgress = true;
        Call<ApiCommonDetailListResponse> seeAllVideosList = mRemoteVideoApiInterface.getAllFavVideosByUser(ApiToken.GET_TOKEN(getActivity().getBaseContext()), "" + getPageNumberToLoadData(), "" + SharedPref.read(PrefType.USER_REGID));

        seeAllVideosList.enqueue(new Callback<ApiCommonDetailListResponse>() {
            @Override
            public void onResponse(Call<ApiCommonDetailListResponse> call, Response<ApiCommonDetailListResponse> response) {
                isLoadingDataInProgress = false;
                if (response.isSuccessful()) {
                    ApiCommonDetailListResponse apiCommonDetailListResponse = response.body();//it is a list
                    if (apiCommonDetailListResponse.getStatusCode().equals(AppConstants.SUCCESS)) {
                        List<Datum> dataList = apiCommonDetailListResponse.getData();

                        if (apiCommonDetailListResponse.getData() == null && mAdapter.getItems().size() != 0) {
                            // Toaster.showLong("You've reached last page ");
                            return;
                        }

                        int rvSize = mAdapter.getItemCount();

                        setPageNumberToLoadData(getPageNumberToLoadData() + 1);
                        if (dataList != null) {
                            mAdapter.addAllItemToPosition(dataList, rvSize);
                        }
                        mAdapter.setItemClickListener(new ItemClickListener<Datum>() {
                            @Override
                            public void onItemClick(View view, Datum item) {

                                goToVideoDetails(item);
                            }
                        });
                    }

                }
            }

            @Override
            public void onFailure(Call<ApiCommonDetailListResponse> call, Throwable t) {
                isLoadingDataInProgress = false;
            }
        });

    }


    /**
     * Go video details
     *
     * @param datum datum
     */
    public void goToVideoDetails(Datum datum) {
        if (datum.getLink() == null) {
            CheckVideoTypeUtil.checkVideoType(datum);
        }
        VideoDetailsActivity.runActivity(getActivity(), datum);
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


    @Override
    public void LoadImage(ImageView imageView, ProgressBar progressBar, String imageLink, Integer[] heightWidth) {
        try {



            RequestOptions requestOptions = new RequestOptions();

            requestOptions = requestOptions.transforms(new FitCenter(), new RoundedCorners(16)).override(heightWidth[0], heightWidth[1]); // resizes the image to these dimensions (in pixel)


            Glide.with(getActivity().getBaseContext())
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
        } catch (Exception e) {
            progressBar.setVisibility(View.GONE);
        }
    }

}

