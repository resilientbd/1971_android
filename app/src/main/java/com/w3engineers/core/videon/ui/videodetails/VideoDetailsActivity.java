package com.w3engineers.core.videon.ui.videodetails;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.SeekBar;
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
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.gson.Gson;
import com.w3engineers.core.util.CheckVideoTypeUtil;
import com.w3engineers.core.util.DeveloperKey;
import com.w3engineers.core.util.helper.AdHelper;
import com.w3engineers.core.util.helper.ApiToken;
import com.w3engineers.core.util.helper.AppConstants;
import com.w3engineers.core.util.helper.AppDownloadManager;
import com.w3engineers.core.util.helper.BarUtil;
import com.w3engineers.core.util.helper.CmdDownload;
import com.w3engineers.core.util.helper.DialogHelper;
import com.w3engineers.core.util.helper.PermissionUtil;
import com.w3engineers.core.util.helper.PrefType;
import com.w3engineers.core.util.helper.ProgressbarHandler;
import com.w3engineers.core.util.helper.SharedPref;
import com.w3engineers.core.util.helper.TransactionHelper;
import com.w3engineers.core.videon.R;
import com.w3engineers.core.videon.data.local.Enums;
import com.w3engineers.core.videon.data.local.authentication.AuthenticationModel;
import com.w3engineers.core.videon.data.local.commondatalistresponse.ApiCommonDetailListResponse;
import com.w3engineers.core.videon.data.local.commondatalistresponse.Datum;
import com.w3engineers.core.videon.data.local.commondatalistresponse.VideoDetailsByIdResponseModel;
import com.w3engineers.core.videon.data.local.downloadsetting.DownloadSetting;
import com.w3engineers.core.videon.data.local.downloadsetting.DownloadSettingResponse;
import com.w3engineers.core.videon.data.local.userstatus.Data;
import com.w3engineers.core.videon.data.local.userstatus.UserStatus;
import com.w3engineers.core.videon.data.remote.RemoteApiProvider;
import com.w3engineers.core.videon.data.remote.home.RemoteVideoApiInterface;
import com.w3engineers.core.videon.databinding.ActivityVideoDetailsBinding;
import com.w3engineers.core.videon.ui.adapter.CommonDataAdapter;
import com.w3engineers.core.videon.ui.empty.EmptyActivity;
import com.w3engineers.core.videon.ui.fullscreenactivity.FullScreeActivity;
import com.w3engineers.core.videon.ui.home.MyMediaController;
import com.w3engineers.core.videon.ui.home.UICommunicator;
import com.w3engineers.ext.strom.application.ui.base.BaseActivity;
import com.w3engineers.ext.strom.application.ui.base.ItemClickListener;
import com.w3engineers.ext.strom.util.helper.Toaster;

import java.util.ArrayList;
import java.util.List;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.breedrapps.vimeoextractor.OnVimeoExtractionListener;
import uk.breedrapps.vimeoextractor.VimeoExtractor;
import uk.breedrapps.vimeoextractor.VimeoVideo;


public class VideoDetailsActivity extends BaseActivity implements ItemClickListener<Datum>, YouTubePlayer.OnInitializedListener, YouTubePlayer.PlaybackEventListener,
        YouTubePlayer.PlayerStateChangeListener, MediaPlayer.OnCompletionListener, CmdDownload, UICommunicator, MediaControllerInteractor, MyMediaController.ChangeText, AdHelper.AdCloseListener, MyMediaController.MediaButtonControllListerner {
    /**
     * Fields
     */
    private Datum model;
    private ActivityVideoDetailsBinding mBinding;
    private YouTubePlayer youTubePlayer;
    YouTubePlayerFragment youTubePlayerFragment;
    private CommonDataAdapter mSuggestionVideosAdapter;
    private LinearLayout layout;
    private VimeoVideo vimeoVideo;
    Bundle savedInstanceState;
    private RemoteVideoApiInterface mRemoteVideoApiInterface;
    private Handler mHandler = null;
    private UserStatus checkResponse;
    private String userid;
    private SparseArray<YtFile> ytFileList;
    private boolean isLive; //flag for check is video live
    private boolean isDownloadAvailable = true;
    private UserStatus mUserStatus;
    private MediaPlayer mediaPlayer;
    private Handler handler;
    SimpleExoPlayer player;
    ImageView exoMaxMin;
    long currentPosition = 0;
    int currentYoutubePosition = 0;
    private boolean isJustRetrieved = true;

    public static void runActivity(Context context) {
        Intent intent = new Intent(context, VideoDetailsActivity.class);
        runCurrentActivity(context, intent);
    }


    public static void runActivity(Context context, Datum model) {
        Gson gson = new Gson();
        String dataModel = gson.toJson(model);
        try {
            context.startActivity(new Intent(context, VideoDetailsActivity.class).putExtra("dataModel", dataModel));
        } catch (Exception e) {

        }

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_video_details;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.savedInstanceState = savedInstanceState;
        if (this.savedInstanceState != null) {
            if (this.savedInstanceState.getBoolean(PrefType.ORIENTATION_MODE) == PrefType.LANDSCAPE) {
                enableFullScreen();
            } else {
                disableFullScreen();
            }
        } else {

        }
        super.onCreate(this.savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        try {
            super.onRestoreInstanceState(this.savedInstanceState);
        } catch (Exception e) {

        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)


    @Override
    protected void startUI() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mBinding = (ActivityVideoDetailsBinding) getViewDataBinding();
        BarUtil.setStatusBarColor(this, R.color.black);

        mBinding.youtubeSeekbar.setOnSeekBarChangeListener(mVideoSeekBarChangeListener);
        mHandler = new Handler();
        layout = mBinding.buttonLayout;
        //check login
        userid = SharedPref.read(PrefType.USER_REGID);
        mRemoteVideoApiInterface = RemoteApiProvider.getInstance().getRemoteHomeVideoApi();
        handler = new Handler();
        Gson gson = new Gson();
        model = gson.fromJson(getIntent().getStringExtra("dataModel"), Datum.class);
        Log.d("checklog", model.toString());
        mBinding.textViewVideoTitle.setText(model.getTitle());
        if (model.getDuration() !=null){
            mBinding.textViewTime.setText((getDurationString(Integer.parseInt(model.getDuration()))));
        }
        //live tv details page
        if (model.getViewCount() == null) {
            liveDetailsPage();
        }
        mainProgressBar = mBinding.progressBar;
        if (!isLive) {
            playNotLiveVideos();
        } else //action if link is live
        {
            goToDetailsPageFromLiveTv();
        }
        initSuggestionAdapter();
        setClickListener(mBinding.overlayItem, mBinding.constraintLayer, mBinding.exoPlayerView, mBinding.maxMin, mBinding.forward, mBinding.backword,
                mBinding.playPause, mBinding.imageViewDownload, mBinding.imageViewPlayList, mBinding.imageViewShare,
                mBinding.imageViewFav, mBinding.textViewRateThis, mBinding.imageViewVideoDetailsBack, mBinding.constraintLayer, mBinding.contentPanel);

        loadData();
        getVideoDetailsByVideoId();
        initialte_video_details_contents();
        getCurrentStatus();
        if (savedInstanceState != null) {
            updateOrieantationData();
            initialte_video_details_contents();
        }
        imageTouchListener();
        ratingTextClickEffect();
        getDownloadSettingFromServer(model.getType());
    }


    private void getVideoDetailsByVideoId(){
        mRemoteVideoApiInterface.getVideoDetailsByID(ApiToken.GET_TOKEN(getBaseContext()),model.getId()).enqueue(new Callback<VideoDetailsByIdResponseModel>() {
            @Override
            public void onResponse(Call<VideoDetailsByIdResponseModel> call, Response<VideoDetailsByIdResponseModel> response) {
                if (response.isSuccessful()){
                    if (response.body()!=null){
                        if (!isLive) {
                            VideoDetailsByIdResponseModel videoDetailsByIdResponseModel=response.body();
                            mBinding.textViewDescription.setText(videoDetailsByIdResponseModel.getData().getDescription());

                        }

                    }
                }
            }

            @Override
            public void onFailure(Call<VideoDetailsByIdResponseModel> call, Throwable t) {

            }
        });
    }

    /**
     * Live details page off some views
     */
    private void liveDetailsPage() {
        mBinding.constraintLayoutTimeViewsSeriesSection.setVisibility(View.GONE);
        mBinding.textViewDescription.setVisibility(View.GONE);
        mBinding.ratingBar.setVisibility(View.GONE);
        mBinding.textViewRatingNumber.setVisibility(View.GONE);
        mBinding.constraintLayoutRatingNumberSection.setVisibility(View.GONE);
        mBinding.textViewVideoSuggestionTitle.setVisibility(View.VISIBLE);
        mBinding.constraintLayoutSharePlaylistFavRateSection.setVisibility(View.GONE);
        mBinding.viewSeparator1.setVisibility(View.GONE);
        mBinding.viewSeparator2.setVisibility(View.GONE);
        mBinding.viewSeparator3.setVisibility(View.GONE);
        isLive = true;
        mBinding.textViewVideoSuggestionTitle.setText(getResources().getString(R.string.details_suggested_tv));
        mBinding.youtubeSeekbar.setVisibility(View.GONE);
        mBinding.forward.setVisibility(View.GONE);
        mBinding.backword.setVisibility(View.GONE);
        mBinding.textCurrentTime.setVisibility(View.GONE);
        ViewGroup.LayoutParams params = mBinding.youtubeController.getLayoutParams();
        params.height = getResources().getDimensionPixelSize(R.dimen.height_50);
    }

    /**
     * play videos which are not live
     */
    private void playNotLiveVideos() {
        addViewCount();
        if (model.getType() == Enums.VIMEO_VIDEO) {
            loadVimoVideo(model.getLink());
            mBinding.youtubeProgress.setVisibility(View.GONE);
        } else if (model.getType() == Enums.LINK_VIDEO) {
            initializeExoPlayerAndPlayVideo(model.getLink());
            mBinding.youtubeProgress.setVisibility(View.GONE);

        } else if (model.getType() == Enums.OTHER_VIDEO) {
            mBinding.youtubeController.setVisibility(View.GONE);
            initializeExoPlayerAndPlayVideo(model.getLink());
            mBinding.youtubeProgress.setVisibility(View.GONE);
        } else {
            playFromYoutubeLink();
        }
    }

    private boolean isPreparing;

    /**
     * initialize exo player for vimeo ,upload and link videoes
     *
     * @param videoLink videoLink
     */
    private void initializeExoPlayerAndPlayVideo(String videoLink) {
        mBinding.exoPlayerView.setVisibility(View.VISIBLE);
        mBinding.simplePlayer.setVisibility(View.GONE);

        // Create a default TrackSelector
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        if (player == null) {
            player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mBinding.exoPlayerView.setPlayer(player);


                exoPlayerVideoPlayingPosition();

                clickListenerOfFullScreen();
                player.setPlayWhenReady(true);

                DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(VideoDetailsActivity.this, "videon");
                DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
                MediaSource mediaSource = new ExtractorMediaSource(Uri.parse(videoLink),
                        dataSourceFactory, extractorsFactory, null, null);

                player.prepare(mediaSource);
                player.addListener(new Player.EventListener() {
                    @Override
                    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

                    }

                    @Override
                    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

                    }

                    @Override
                    public void onLoadingChanged(boolean isLoading) {

                    }

                    @Override
                    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                        if (!isPreparing) {
                            // this is accurate
                            isPreparing = true;
                            Log.d("explayer", "prepared");
                            if (currentPosition != 0) {
                                player.seekTo(currentPosition);
                            }

                            player.setPlayWhenReady(true);

                        }
                    }

                    @Override
                    public void onRepeatModeChanged(int repeatMode) {

                    }

                    @Override
                    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

                    }

                    @Override
                    public void onPlayerError(ExoPlaybackException error) {

                    }

                    @Override
                    public void onPositionDiscontinuity(int reason) {

                    }

                    @Override
                    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

                    }

                    @Override
                    public void onSeekProcessed() {

                    }
                });

            }
        });

    }

    /**
     * Exo player video current position
     */
    private void exoPlayerVideoPlayingPosition() {
        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getLong(PrefType.MED_POS);
            currentYoutubePosition = savedInstanceState.getInt(PrefType.MED_POS);
        }
    }

    /**
     * Click listener of full screen
     */
    private void clickListenerOfFullScreen() {
        ImageView exoMaxMin = mBinding.exoPlayerView.findViewById(R.id.max_min);
        exoMaxMin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPosition = player.getCurrentPosition();

                maximize();
            }
        });

    }

    /**
     * init suggestion adapter
     */
    private void initSuggestionAdapter() {
        mSuggestionVideosAdapter = new CommonDataAdapter(this);
        mBinding.baseRecyclerViewSuggestionVideos.setAdapter(mSuggestionVideosAdapter);
        mBinding.baseRecyclerViewSuggestionVideos.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,
                        false));
        mSuggestionVideosAdapter.setItemClickListener(new ItemClickListener<Datum>() {
            @Override
            public void onItemClick(View view, Datum item) {
                if (isLive) {
                    if (item.getType() == 2) {
                        item.setLink(item.getLiveLinkm3u8());

                    }
                    else if(item.getType() == 1){
                        if (item.getLink().contains("=")) {
                            String[] vidIdYoutube = item.getLink().split("=");
                            item.setLink("" + vidIdYoutube[1]);
                        } else {
                            item.setLink("" + item.getLink());
                        }
                    }
                    else if (item.getType() == 3) {
                        item.setLink(convertRtmpTom3u8(item.getLiveRtmp()));
                        item.setType(2);
                        item.setLiveLinkm3u8(convertRtmpTom3u8(item.getLiveRtmp()));
                    }
                    goToDetailsPageFromLiveTv();
                    VideoDetailsActivity.runActivity(VideoDetailsActivity.this, item);
                    finish();
                } else {
                    goToVideoDetails(item);
                }

            }

        });
    }

    /**
     * Convert to rtmp to m3u8
     *
     * @param rtml rtml
     * @return url
     */
    private String convertRtmpTom3u8(String rtml) {
        String convertedUrl = "";
        String str = "";
        for (int i = 4; i < rtml.length(); i++) {
            str = str + rtml.charAt(i);
        }
        str = "http" + str + "/playlist.m3u8";
        Log.d("rtmlcheck", str);
        return str;
    }

    /**
     * Go to details page from live tv
     */
    private void goToDetailsPageFromLiveTv() {
        if (model.getLink() != null) {
            switch (model.getType()) {
                case Enums.YOUTUBE_VIDEO:
                    playFromYoutubeLink();
                    mBinding.liveLayerIndecator.setVisibility(View.VISIBLE);
                    break;
                case Enums.live_link_m3u8:
                    ///live
                    mBinding.simplePlayer.setVisibility(View.VISIBLE);
                    loadRtmpVideo(model.getLink());
                    mBinding.youtubeProgress.setVisibility(View.GONE);
                    break;
                case Enums.live_rtmp:
                    mBinding.liveLayerIndecator.setVisibility(View.VISIBLE);
                    mBinding.simplePlayer.setVisibility(View.VISIBLE);
                    loadRtmpVideo(model.getLink());
                    mBinding.youtubeProgress.setVisibility(View.GONE);
                    break;
            }
        }
    }

    /**
     * action for playing video
     */
    public void playFromYoutubeLink() {
        //for youtube video playing
        mBinding.exoPlayerView.setVisibility(View.GONE);
        youTubePlayerFragment = (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.video_view_single_video);

        youTubePlayerFragment.initialize(DeveloperKey.api_key, this);
        this.model = model;
        youTubePlayerFragment.getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller_hide_visible();
            }
        });


    }

    /**
     * go to video details
     *
     * @param datum datum
     */
    public void goToVideoDetails(Datum datum) {
        if (datum.getLink() == null) {
            CheckVideoTypeUtil.checkVideoType(datum);

        }
        VideoDetailsActivity.runActivity(this, datum);
        finish();
    }

    /**
     * Update
     */
    private void updateOrieantationData() {
        if (model.getType() == Enums.VIMEO_VIDEO) {
            loadVimoVideo(model.getLink());
        }
        if (savedInstanceState.getBoolean(PrefType.ORIENTATION_MODE) == PrefType.LANDSCAPE) {
            mBinding.part2.setVisibility(View.GONE);
        } else {
            //AdHelper.loadBannerAd(mBinding.adView);
            AdHelper.loadBannerAd(this, (LinearLayout) mBinding.buttonLayout);
            if (model.getType() == Enums.YOUTUBE_VIDEO) {
                //  mBinding.moviePoster.setVisibility(View.VISIBLE);
            }
            mBinding.part2.setVisibility(View.VISIBLE);

        }

    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        if (player != null) {
            player.setPlayWhenReady(true);
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (player != null) {
            player.stop();
            player.release();
        }
        super.onDestroy();
    }

    /**
     * Load other videos
     *
     * @param videoLink video link to play
     */
    private void loadOtherVideo(String videoLink) {
        if (mBinding.exoPlayerView.getVisibility() == View.GONE) {
            mBinding.exoPlayerView.setVisibility(View.VISIBLE);
        }

        initializeExoPlayerAndPlayVideo(videoLink);

    }


    /**
     * Load Rtmp video with exo-player
     *
     * @param videolink videolink
     */
    private void loadRtmpVideo(String videolink) {

        mBinding.simplePlayer.setVisibility(View.VISIBLE);
        mBinding.liveLayerIndecator.setVisibility(View.VISIBLE);

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        //Create the player
        if (player == null) {
            player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
            PlayerView playerView = mBinding.simplePlayer;
            playerView.setPlayer(player);
            exoMaxMin = playerView.findViewById(R.id.max_min);
            exoMaxMin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    player.stop();
                  //  player.release();
                    Intent intent = new Intent(VideoDetailsActivity.this, FullScreeActivity.class);
                    intent.putExtra("url", model.getLink());
                    startActivityForResult(intent, 121);
                  //  maximize();

                    // .createMediaSource(Uri.parse("rtmp://203.81.81.230/livepkgr/HTT"));
                    player.addListener(new Player.EventListener() {
                        @Override
                        public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

                        }

                        @Override
                        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

                        }

                        @Override
                        public void onLoadingChanged(boolean isLoading) {
                            Log.d("loadingstate", "loading: " + isLoading);
                        }

                        @Override
                        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                            Log.d("loadingstate", "ready: " + playWhenReady + " playbackState:" + playbackState);
                            if (playbackState == 1) {
                                //  "http://185.152.66.57/OSJOVENSTITAS/video.m3u8?token=TohQHf9RV2"
                                loadRtmpVideo(model.getLink());
                            }
                        }

                        @Override
                        public void onRepeatModeChanged(int repeatMode) {

                        }

                        @Override
                        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

                        }

                        @Override
                        public void onPlayerError(ExoPlaybackException error) {

                        }

                        @Override
                        public void onPositionDiscontinuity(int reason) {

                        }

                        @Override
                        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

                        }

                        @Override
                        public void onSeekProcessed() {

                        }
                    });
                    model.setLink(videolink);
                }
            });
            DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this, "user-agent");
            Uri uri = Uri.parse(model.getLink());

            // This is the MediaSource representing the media to be played.
            MediaSource mediaSource = new HlsMediaSource(uri, dataSourceFactory, handler, null);
            // Prepare the player with the source.
            player.prepare(mediaSource);
            //auto start playing
            player.setPlayWhenReady(true);
            ProgressbarHandler.DismissProgress(this);
        }
        else {
           // player.setPlayWhenReady(true);
//            if(!isLivePlayTwice) {
//                player.stop();
//                player.release();
//                loadRtmpVideo(model.getLink());
//                isLivePlayTwice=true;
//            }
        }








    }
boolean isLivePlayTwice;

    /**
     * Load Vimeo videos
     *
     * @param vid_id vimeo video id
     */
    private void loadVimoVideo(String vid_id) {
        if (mBinding.exoPlayerView.getVisibility() == View.GONE) {
            mBinding.exoPlayerView.setVisibility(View.VISIBLE);
        }
        VimeoExtractor.getInstance().fetchVideoWithIdentifier(vid_id, null, new OnVimeoExtractionListener() {
            @Override
            public void onSuccess(VimeoVideo video) {
                vimeoVideo = video;
                String hdStream = vimeoVideo.getStreams().get("360p");
                vimeoVideo = video;
                System.out.println("VIMEO VIDEO STREAM" + hdStream);
                if (hdStream != null) {
                    loadOtherVideo(hdStream);
                }
            }

            @Override
            public void onFailure(Throwable throwable) {

            }
        });
    }

    /**
     * convert duration
     *
     * @param milliseconds milliseconds
     * @return time
     */
    public String convertDuration(long milliseconds) {
        int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
        int hours = (int) ((milliseconds / (1000 * 60 * 60)) % 24);
        String time = hours + "h " + minutes + "m ";
        return time;
    }

    /**
     * Load data from server for suggestion videos
     */
    private void loadData() {

        if (!isLive) {
            mRemoteVideoApiInterface.getSimilarVideos(model.getId(), ApiToken.GET_TOKEN(getBaseContext())).enqueue(new retrofit2.Callback<ApiCommonDetailListResponse>() {
                @Override
                public void onResponse(Call<ApiCommonDetailListResponse> call, Response<ApiCommonDetailListResponse> response) {
                    if (response.isSuccessful()) {
                        ApiCommonDetailListResponse suggesions = response.body();
                        if (suggesions.getStatusCode() == 200) {
                            if (suggesions.getData() != null) {
                                if (mSuggestionVideosAdapter != null) {
                                    mSuggestionVideosAdapter.addItems(suggesions.getData());
                                }

                            } else {
                                mBinding.textViewVideoSuggestionTitle.setVisibility(View.GONE);
                            }
                        } else {
                            {
                                mBinding.textViewVideoSuggestionTitle.setVisibility(View.GONE);
                            }
                        }
                    }

                }

                @Override
                public void onFailure(Call<ApiCommonDetailListResponse> call, Throwable t) {
                    mBinding.textViewVideoSuggestionTitle.setVisibility(View.GONE);
                }
            });
        } else {
            mRemoteVideoApiInterface.getSimilarLiveVideos(model.getId(), ApiToken.GET_TOKEN(getBaseContext())).enqueue(new retrofit2.Callback<ApiCommonDetailListResponse>() {
                @Override
                public void onResponse(Call<ApiCommonDetailListResponse> call, Response<ApiCommonDetailListResponse> response) {
                    if (response.isSuccessful()) {
                        ApiCommonDetailListResponse suggesions = response.body();
                        if (suggesions.getStatusCode() == 200) {
                            if (suggesions.getData() != null && mSuggestionVideosAdapter != null) {
                                mSuggestionVideosAdapter.addItems(suggesions.getData());
                            } else {
                                mBinding.textViewVideoSuggestionTitle.setVisibility(View.GONE);
                            }

                        } else {
                            {
                                mBinding.textViewVideoSuggestionTitle.setVisibility(View.GONE);
                            }
                        }

                    }
                }

                @Override
                public void onFailure(Call<ApiCommonDetailListResponse> call, Throwable t) {

                    mBinding.textViewVideoSuggestionTitle.setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    public void onItemClick(View view, Datum item) {

        //Toaster.showLong("Hello world"+ item.getVideoId());
        runActivity(this, item);
        TransactionHelper.TransactionLeftToRight(VideoDetailsActivity.this);
        finish();
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        this.youTubePlayer = youTubePlayer;

        this.youTubePlayer.loadVideo(model.getLink());
        this.youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);
        this.youTubePlayer.setPlaybackEventListener(this);
        this.youTubePlayer.setPlayerStateChangeListener(this);
        displayCurrentTime();
//        youTubePlayer.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
//        youTubePlayer.setFullscreen(true);

    }


    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Log.d("failurecoz", youTubeInitializationResult.toString());
        Log.d("failurecoz", "Link" + youtubeLink);

    }

    /**
     * hide visible of youtube controller
     */
    private void controller_hide_visible() {
        if (model.getType() == Enums.YOUTUBE_VIDEO) {
            if (mBinding.constraintLayer.getVisibility() == View.GONE) {
                mBinding.constraintLayer.setVisibility(View.VISIBLE);
            } else {
                mBinding.constraintLayer.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Dialog for to do login
     */
    public void checkLogin() {
        EmptyActivity.runActivity(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.content_panel:
                controller_hide_visible();
                break;
            case R.id.constraint_layer:
                controller_hide_visible();
                break;
            case R.id.overlay_item:
                controller_hide_visible();
                break;
            case R.id.max_min:
                maximize();
                break;
            case R.id.forward:
                firstforward(view);
                break;
            case R.id.backword:
                backward(view);
                break;
            case R.id.play_pause:
                play_pause();

                break;
            case R.id.text_view_rate_this:
                if (!userid.equals("")) {
                    if (checkResponse.getData().getReview() == 0) {
                        showRatingDialog();
                    } else {
                        Toaster.showLong(getResources().getString(R.string.video_details_already_rated_this_video));
                    }
                } else {
                    checkLogin();
                }
                break;
            case R.id.image_view_fav:
                if (!userid.equals("")) {
                    favSelection();
                } else {
                    checkLogin();
                }
                break;
            case R.id.image_view_share:
                sharefunctionalities();
                break;
            case R.id.image_view_play_list:
                if (!userid.equals("")) {
                    addToPlayList();
                } else {
                    checkLogin();
                }
                break;
            case R.id.image_view_download:


                break;
            case R.id.image_view_video_details_back:
                if (savedInstanceState != null) {
                    if (savedInstanceState.getBoolean(PrefType.ORIENTATION_MODE) == PrefType.LANDSCAPE) {
                        maximize();
                    } else {
                        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                        finish();
                    }
                } else {
                    overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                    finish();
                }
                break;
        }
    }

    /**
     * Check download availability
     */
    private void checkDownloadAvailability() {
        if (isDownloadAvailable) {
            if (checkPermission()) {
                downloadClickFunctionality();
                new AdHelper().interstitialsAd(getBaseContext(), this);
            }
        } else {
            mBinding.imageViewDownload.setColorFilter(getResources().getColor(R.color.color_toolbar_home_title));
            mBinding.imageViewDownload.setBackgroundResource(R.drawable.drawable_circular_shape_yellow_color);
        }
    }

    /**
     * Download the video
     */
    public void downloadClickFunctionality() {

        if (model.getType() == Enums.VIMEO_VIDEO) {
            List<String> strings = new ArrayList<>();
            for (String key : vimeoVideo.getStreams().keySet()) {
                strings.add(key + " DASH");

            }
            callDialogHelper(strings);


        } else if ((model.getType() == Enums.OTHER_VIDEO) || (model.getType() == Enums.LINK_VIDEO)) {
            String[] splistSlash = model.getLink().split("/");
            String[] extentionSplit = splistSlash[splistSlash.length - 1].split("\\.");
            String extention = extentionSplit[1];
            AppDownloadManager.download(model.getLink(), model.getTitle() + "." + extention, model.getTitle() + "." + extention, VideoDetailsActivity.this);
            Toaster.showLong(getResources().getString(R.string.video_details_video_downloading));
        } else {
            youtubeLink = "https://youtu.be/" + model.getLink();

            getYoutubeDownloadUrl(youtubeLink);
        }
    }

    /**
     * Rate this video
     *
     * @param rate        rate
     * @param review_text review
     */
    private void rateThisVideo(String rate, String review_text) {

        mRemoteVideoApiInterface.setRating(SharedPref.read(PrefType.USER_REGID), "" + model.getId(), rate, review_text, ApiToken.GET_TOKEN(getBaseContext())).enqueue(new retrofit2.Callback<AuthenticationModel>() {
            @Override
            public void onResponse(Call<AuthenticationModel> call, Response<AuthenticationModel> response) {
                AuthenticationModel m = response.body();
                String feedback;
                if (response.isSuccessful()) {

                    feedback = m.getMessage();

                    getCurrentStatus();
                } else feedback = "Connection Error!";
                Toast.makeText(VideoDetailsActivity.this, feedback, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<AuthenticationModel> call, Throwable t) {
                // Toaster.showLong(t.getMessage());
            }
        });

    }

    /**
     * Get download setting on off from server
     *
     * @param videoType type
     */
    private void getDownloadSettingFromServer(int videoType) {
        mRemoteVideoApiInterface.downLoadSetting(ApiToken.GET_TOKEN(getBaseContext())).enqueue(new Callback<DownloadSettingResponse>() {
            @Override
            public void onResponse(Call<DownloadSettingResponse> call, Response<DownloadSettingResponse> response) {
                if (response.isSuccessful()) {
                    DownloadSettingResponse downloadSettingResponse = response.body();
                    if (downloadSettingResponse.getStatusCode().equals(AppConstants.SUCCESS)) {
                        DownloadSetting downloadSettingOnOffData = downloadSettingResponse.getDownloadSetting();
                        if (downloadSettingOnOffData != null) {
                            setDownLoadSettingOnOff(downloadSettingOnOffData, videoType);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<DownloadSettingResponse> call, Throwable t) {

            }
        });
    }

    /**
     * hide and show button of download
     *
     * @param downloadSettingOnOffData downloadSettingData
     * @param videoType                there are different type of videos get specific video
     */
    private void setDownLoadSettingOnOff(DownloadSetting downloadSettingOnOffData, int videoType) {
        switch (videoType) {
            case Enums.YOUTUBE_VIDEO:
                if (downloadSettingOnOffData.getDownloadYoutube().equals(AppConstants.DOWNLOAD_ON)) {
                    mBinding.imageViewDownload.setVisibility(View.VISIBLE);
                } else {
                    mBinding.imageViewDownload.setVisibility(View.GONE);
                }
                break;
            case Enums.VIMEO_VIDEO:
                if (downloadSettingOnOffData.getDownloadVimeo().equals(AppConstants.DOWNLOAD_ON)) {
                    mBinding.imageViewDownload.setVisibility(View.VISIBLE);
                } else {
                    mBinding.imageViewDownload.setVisibility(View.GONE);
                }
                break;
            case Enums.OTHER_VIDEO:
                if (downloadSettingOnOffData.getDownloadUploadedVideo().equals(AppConstants.DOWNLOAD_ON)) {
                    mBinding.imageViewDownload.setVisibility(View.VISIBLE);
                } else {
                    mBinding.imageViewDownload.setVisibility(View.GONE);
                }
                break;
            case Enums.LINK_VIDEO:
                if (downloadSettingOnOffData.getDownloadLinkedVideo().equals(AppConstants.DOWNLOAD_ON)) {
                    mBinding.imageViewDownload.setVisibility(View.VISIBLE);
                } else {
                    mBinding.imageViewDownload.setVisibility(View.GONE);
                }
                break;

        }
    }


    /**
     * Add to favourite
     */
    private void favSelection() {
        if (checkResponse.getData().getFavourite() == 0) {
            mRemoteVideoApiInterface.addFavourite(SharedPref.read(PrefType.USER_REGID), "" + model.getId(), ApiToken.GET_TOKEN(getBaseContext())).enqueue(new retrofit2.Callback<AuthenticationModel>() {
                @Override
                public void onResponse(Call<AuthenticationModel> call, Response<AuthenticationModel> response) {
                    AuthenticationModel m = response.body();
                    String feedback;
                    if (response.isSuccessful()) {
                        feedback = response.message();
                        Snackbar.make(getWindow().getDecorView(), getResources().getString(R.string.video_details_marked_as_fav), Snackbar.LENGTH_LONG).setAction(null, null).show();
                    } else feedback = response.message();

                    //Toaster.showLong(feedback);
                    getCurrentStatus();
                }

                @Override
                public void onFailure(Call<AuthenticationModel> call, Throwable t) {
                    //     Toaster.showLong(t.getMessage());
                }
            });
        } else {
            AlertDialog builder = new AlertDialog.Builder(VideoDetailsActivity.this).setMessage(getResources().getString(R.string.remove_from_fav)).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mRemoteVideoApiInterface.removeFavourite(SharedPref.read(PrefType.USER_REGID), "" + model.getId(), ApiToken.GET_TOKEN(getBaseContext())).enqueue(new Callback<AuthenticationModel>() {
                        @Override
                        public void onResponse(Call<AuthenticationModel> call, Response<AuthenticationModel> response) {
                            AuthenticationModel m = response.body();
                            String feedback;
                            if (response.isSuccessful()) {
                                feedback = response.message();

                                getCurrentStatus();
                            } else feedback = response.message();

                            //   Toaster.showLong(feedback);

                        }

                        @Override
                        public void onFailure(Call<AuthenticationModel> call, Throwable t) {
                            //   Toaster.showLong(t.getMessage());

                        }
                    });
                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //  Toaster.showLong("Action cancelled!");
                    //  can
                    dialog.dismiss();

                }
            }).show();

        }
    }


    /**
     * Add to play list
     */
    private void addToPlayList() {
        if (checkResponse.getData().getPlaylist() == 0) {
            mRemoteVideoApiInterface.addToPlayList(SharedPref.read(PrefType.USER_REGID), "" + model.getId(), ApiToken.GET_TOKEN(getBaseContext())).enqueue(new retrofit2.Callback<AuthenticationModel>() {
                @Override
                public void onResponse(Call<AuthenticationModel> call, Response<AuthenticationModel> response) {
                    AuthenticationModel m = response.body();
                    String feedback;
                    if (response.isSuccessful()) {
                        feedback = response.message();
                        getCurrentStatus();
                        Snackbar.make(getWindow().getDecorView(), getResources().getString(R.string.video_details_added_to_playlist), Snackbar.LENGTH_LONG).setAction(null, null).show();
                    } else feedback = response.message();

                    //  Toaster.showLong(feedback);
                }

                @Override
                public void onFailure(Call<AuthenticationModel> call, Throwable t) {
                    // Toaster.showLong(t.getMessage());
                }
            });
        } else {
            AlertDialog builder = new AlertDialog.Builder(VideoDetailsActivity.this).setMessage(getResources().getString(R.string.remove_from_fav)).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mRemoteVideoApiInterface.removeFromPlayList(SharedPref.read(PrefType.USER_REGID), "" + model.getId(), ApiToken.GET_TOKEN(getBaseContext())).enqueue(new retrofit2.Callback<AuthenticationModel>() {
                        @Override
                        public void onResponse(Call<AuthenticationModel> call, Response<AuthenticationModel> response) {
                            AuthenticationModel m = response.body();
                            String feedback;
                            if (response.isSuccessful()) {
                                if (m.getStatusCode() == 200) {
                                    feedback = response.message();
                                    getCurrentStatus();
                                } else feedback = response.message();
                                Toaster.showLong(feedback);
                            } else {
                                Toast.makeText(VideoDetailsActivity.this, "Error Code: " + response.code(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<AuthenticationModel> call, Throwable t) {
                            //  Toaster.showLong(t.getMessage());
                        }
                    });
                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //  Toaster.showLong("Action cancelled!");
                    //  can
                    dialog.dismiss();

                }
            }).show();

        }
    }

    /**
     * Add view count
     */
    public void addViewCount() {
        mRemoteVideoApiInterface.incrementViewCount(model.getId(),
                ApiToken.GET_TOKEN(getBaseContext())).enqueue(new retrofit2.Callback<ViewCountResponse>() {
            @Override
            public void onResponse(Call<ViewCountResponse> call, Response<ViewCountResponse> response) {
                if (response.isSuccessful()) {
                    ViewCountResponse viewCountResponse = response.body();
                    mBinding.textViewViewsCount.setText("" + viewCountResponse.getData().getViewCount());
                }
            }

            @Override
            public void onFailure(Call<ViewCountResponse> call, Throwable t) {
                Toaster.showLong(t.getMessage());
            }
        });
    }

    /**
     * Call dialog helper
     *
     * @param strings list of string
     */
    public void callDialogHelper(List<String> strings) {
        new DialogHelper().showDialog(this, this, R.layout.activity_alert_dialog, strings);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                break;
        }
        return true;
    }

    /**
     * Enable full screen
     */
    public void enableFullScreen() {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


    }

    /**
     * Disable full screen
     */
    public void disableFullScreen() {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
    }

    /**
     * Maximize orientation
     * Check the state is potrait or landscape
     */
    public void maximize() {

        try {
            if ((this.savedInstanceState.getBoolean(PrefType.ORIENTATION_MODE) == PrefType.POTRAIT) || (this.savedInstanceState == null)) {
                if (youTubePlayer != null) {

                    Log.d("checkcurrenttime", "" + currentYoutubePosition);
                    savedInstanceState.putInt(PrefType.MED_POS, currentYoutubePosition);
                }


                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);


            } else {
                if (youTubePlayer != null) {

                    Log.d("checkcurrenttime", "" + currentYoutubePosition);
                    savedInstanceState.putInt(PrefType.MED_POS, currentYoutubePosition);
                }

                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


            }
        } catch (Exception e) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

    }

    String youtubeLink;

    private ProgressBar mainProgressBar;

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {

        super.onSaveInstanceState(savedInstanceState);
        if (this.savedInstanceState == null) {
            savedInstanceState.putBoolean(PrefType.ORIENTATION_MODE, PrefType.LANDSCAPE);
            if (player != null) {
                savedInstanceState.putLong(PrefType.MED_POS, currentPosition);
            } else if (youTubePlayer != null) {
                savedInstanceState.putInt(PrefType.MED_POS, currentYoutubePosition);
            }
        } else if (this.savedInstanceState.getBoolean(PrefType.ORIENTATION_MODE) == PrefType.LANDSCAPE) {
            savedInstanceState.putBoolean(PrefType.ORIENTATION_MODE, PrefType.POTRAIT);
            if (player != null) {
                savedInstanceState.putLong(PrefType.MED_POS, currentPosition);
            } else if (youTubePlayer != null) {
                savedInstanceState.putInt(PrefType.MED_POS, currentYoutubePosition);
            }

        } else {
            savedInstanceState.putBoolean(PrefType.ORIENTATION_MODE, PrefType.LANDSCAPE);
            if (player != null) {
                savedInstanceState.putLong(PrefType.MED_POS, currentPosition);
            } else if (youTubePlayer != null) {
                savedInstanceState.putInt(PrefType.MED_POS, currentYoutubePosition);
            }
        }


    }

    /**
     * Get youtube view download
     *
     * @param youtubeLink youtube link
     */
    private void getSharableDownloadLinkUrl(String youtubeLink) {
        new YouTubeExtractor(this) {

            @Override
            public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {

                ytFileList = ytFiles;
                mainProgressBar.setVisibility(View.GONE);
                if (ytFiles == null) {
                    // Something went wrong we got no urls. Always check this.
                    finish();
                    return;
                }
                String sharelink = "";

                // ytFile represents one file with its url and meta data

                for (int i = 0, itag; i < ytFiles.size(); i++) {
                    itag = ytFiles.keyAt(i);
                    // ytFile represents one file with its url and meta data
                    YtFile ytFile = ytFiles.get(itag);

                    // Just add videos in a decent format => height -1 = audio
                    if (ytFile.getFormat().getHeight() == -1 || ytFile.getFormat().getHeight() >= 360) {
                        //   addButtonToMainLayout(vMeta.getTitle(), ytFile);
                        Log.d("downloadfile", "Download url:" + ytFile);
                        sharelink = sharelink + ytFile.getFormat().getHeight() + ": " + ytFile.getUrl() + "'\n";
                    }
                    // values.add(makeButtonText(vMeta.getTitle(),ytFile));
                }
                shareyoutubelinks(sharelink);
                //callDialogHelper(values);
            }
        }.extract(youtubeLink, true, false);
    }

    /**
     * Share youtube links
     *
     * @param sharelink share link
     */
    public void shareyoutubelinks(String sharelink) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        String applink = " http://play.google.com/store/apps/details?id=" + getBaseContext().getPackageName();
        sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_message) + applink + "'\nVideo download link: " + sharelink);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    /**
     * Get youtube view download
     *
     * @param youtubeLink youtube link
     */
    private void getYoutubeDownloadUrl(String youtubeLink) {
        new YouTubeExtractor(this) {

            @Override
            public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {

                ytFileList = ytFiles;
                mainProgressBar.setVisibility(View.GONE);
                if (ytFiles == null) {
                    // Something went wrong we got no urls. Always check this.
                    finish();
                    return;
                }
                List<String> strings = new ArrayList<>();
                // Iterate over itags

                for (int i = 0, itag; i < ytFiles.size(); i++) {
                    itag = ytFiles.keyAt(i);
                    // ytFile represents one file with its url and meta data
                    YtFile ytFile = ytFiles.get(itag);

                    // Just add videos in a decent format => height -1 = audio
                    if (ytFile.getFormat().getHeight() == -1 || ytFile.getFormat().getHeight() >= 360) {
                        //   addButtonToMainLayout(vMeta.getTitle(), ytFile);
                        Log.d("downloadfile", "Download url:" + ytFile);
                        strings.add(makeButtonText(vMeta.getTitle(), ytFile));
                    }
                    // values.add(makeButtonText(vMeta.getTitle(),ytFile));
                }
                callDialogHelper(strings);
            }
        }.extract(youtubeLink, true, false);
    }


    /**
     * Set button text
     *
     * @param videoTitle video ttle
     * @param ytfile     youtube file
     * @return text
     */
    private String makeButtonText(final String videoTitle, final YtFile ytfile) {
        String btnText = (ytfile.getFormat().getHeight() == -1) ? "Audio " +
                ytfile.getFormat().getAudioBitrate() + " kbit/s" :
                ytfile.getFormat().getHeight() + "p";
        btnText += (ytfile.getFormat().isDashContainer()) ? " dash" : "";
        return btnText;
    }


    /**
     * first forward
     *
     * @param view view
     */
    public void firstforward(View view) {
        youTubePlayer.seekToMillis(youTubePlayer.getCurrentTimeMillis() + 15000);
    }

    /**
     * Back ward
     *
     * @param view view
     */
    public void backward(View view) {
        youTubePlayer.seekToMillis(youTubePlayer.getCurrentTimeMillis() - 15000);
    }

    /**
     * Play pause video
     */
    public void play_pause() {
        if (youTubePlayer.isPlaying()) {
            youTubePlayer.pause();
        } else {
            mBinding.playPause.setImageResource(R.drawable.ic_play);
            youTubePlayer.play();
        }
    }


    /**
     * @param checkAllResponse current status of isFav,isRate,isPlayListed,download
     */
    public void setIconColor(UserStatus checkAllResponse) {
        if (checkAllResponse.getData().getReview() == 0) {
            mBinding.ratingIcon.setColorFilter(getResources().getColor(R.color.color_small_text));
            mBinding.textViewRateThis.setBackgroundResource(R.drawable.drawable_rate_this_border_inactive);
            mBinding.textViewRateThis.setTextColor(getResources().getColor(R.color.color_home_category_title));
        } else {
            mBinding.textViewRateThis.setBackgroundResource(R.drawable.drawable_rate_this_border_active);
            mBinding.textViewRateThis.setTextColor(getResources().getColor(R.color.color_home_see_all));
            mBinding.ratingIcon.setColorFilter(getResources().getColor(R.color.color_home_see_all));
        }

        if (checkAllResponse.getData().getFavourite() != 0) {
            mBinding.imageViewFav.setColorFilter(getResources().getColor(R.color.color_home_see_all));
            mBinding.imageViewFav.setBackgroundResource(R.drawable.drawable_circular_shape_yellow_color);
        } else {
            mBinding.imageViewFav.clearColorFilter();
            mBinding.imageViewFav.setBackgroundResource(R.drawable.drawable_circular_shape);
        }
        if (checkAllResponse.getData().getPlaylist() != 0) {
            mBinding.imageViewPlayList.setColorFilter(getResources().getColor(R.color.color_home_see_all));
            mBinding.imageViewPlayList.setBackgroundResource(R.drawable.drawable_circular_shape_yellow_color);
        } else {
            mBinding.imageViewPlayList.clearColorFilter();
            mBinding.imageViewPlayList.setBackgroundResource(R.drawable.drawable_circular_shape);
        }
    }


    /**
     * Set rate this icon color
     *
     * @param checkAllResponse checkAllResponse
     */
    private void setRateIconColor(UserStatus checkAllResponse) {
        if (checkAllResponse.getData().getReview() == 0) {
            mBinding.ratingIcon.setColorFilter(getResources().getColor(R.color.color_small_text));
            mBinding.textViewRateThis.setBackgroundResource(R.drawable.drawable_rate_this_border_inactive);
            mBinding.textViewRateThis.setTextColor(getResources().getColor(R.color.color_home_category_title));
        } else {
            mBinding.textViewRateThis.setBackgroundResource(R.drawable.drawable_rate_this_border_active);
            mBinding.textViewRateThis.setTextColor(getResources().getColor(R.color.color_home_see_all));
            mBinding.ratingIcon.setColorFilter(getResources().getColor(R.color.color_home_see_all));
        }
    }


    /**
     * Set favourite icon color
     *
     * @param checkAllResponse checkAllResponse
     */
    private void setFavouriteIconColor(UserStatus checkAllResponse) {
        if (checkAllResponse.getData().getFavourite() == 1) {
            mBinding.imageViewFav.setColorFilter(getResources().getColor(R.color.color_home_see_all));
            mBinding.imageViewFav.setBackgroundResource(R.drawable.drawable_circular_shape_yellow_color);
        } else {
            mBinding.imageViewFav.clearColorFilter();
            mBinding.imageViewFav.setBackgroundResource(R.drawable.drawable_circular_shape);
        }
    }


    /**
     * set play list color
     *
     * @param checkAllResponse checkAllResponse
     */
    private void setPlayListIcon(UserStatus checkAllResponse) {
        if (checkAllResponse.getData().getPlaylist() == 1) {
            mBinding.imageViewPlayList.setColorFilter(getResources().getColor(R.color.color_home_see_all));
            mBinding.imageViewPlayList.setBackgroundResource(R.drawable.drawable_circular_shape_yellow_color);
        } else {
            mBinding.imageViewPlayList.clearColorFilter();
            mBinding.imageViewPlayList.setBackgroundResource(R.drawable.drawable_circular_shape);
        }
    }


    /**
     * Get current status  of rating ,favourite,playlist
     */

    public void getCurrentStatus() {

        mRemoteVideoApiInterface.getAllStatus("" + SharedPref.read(PrefType.USER_REGID), "" + model.getId(), ApiToken.GET_TOKEN(getBaseContext())).enqueue(new retrofit2.Callback<UserStatus>() {
            @Override
            public void onResponse(Call<UserStatus> call, Response<UserStatus> response) {
                String userid = "" + SharedPref.read(PrefType.USER_REGID);
                String videoId = "" + model.getId();
                String token = ApiToken.GET_TOKEN(getBaseContext());
                UserStatus checkAllResponse = response.body();
                checkResponse = checkAllResponse;

                Data userStatus = checkAllResponse.getData();
                if (userStatus.getTotalUsers() > 0) {
                    mBinding.textViewRatingNumber.setText("" + userStatus.getAvgRating() + "(" + userStatus.getTotalUsers() + ")");
                } else {
                    mBinding.textViewRatingNumber.setText("0" + "(0)");
                }
                mUserStatus = checkAllResponse;
                setIconColor(mUserStatus);

            }

            @Override
            public void onFailure(Call<UserStatus> call, Throwable t) {

            }
        });
    }

    /**
     * init video details contents
     */
    public void initialte_video_details_contents() {
        if (!isLive) {
            mBinding.textViewVideoTitle.setText(model.getTitle());
            mBinding.textViewSeriesName.setText(model.getCategory());
         //   mBinding.textViewViewsCount.setText("" + model.getViewCount());
           // mBinding.textViewDescription.setText(model.getDescription());
            mBinding.textViewTime.setText((getDurationString(Integer.parseInt(model.getDuration()))));
            mBinding.textViewSeriesName.setText(model.getCategory());
        }
        mBinding.textViewViewsUploadTime.setText("" + model.getCreated() + " day ago");
    }

    /**
     * Get duration
     * @param seconds seconds
     * @return string of hours and min
     */
    private String getDurationString(int seconds) {

        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        seconds = seconds % 60;

        return twoDigitString(hours) + ":" + twoDigitString(minutes) + ":" + twoDigitString(seconds);
    }

    private String twoDigitString(int number) {

        if (number == 0) {
            return "00";
        }

        if (number / 10 == 0) {
            return "0" + number;
        }

        return String.valueOf(number);
    }


    @Override
    public void onPlaying() {
        mHandler.postDelayed(runnable, 100);
        displayCurrentTime();
        mBinding.playPause.setImageResource(R.drawable.ic_pause);
        mBinding.constraintLayer.setBackgroundColor(getResources().getColor(R.color.transparent));
        mBinding.youtubeProgress.setVisibility(View.GONE);
        Log.d("checkprogress","progreess count");
    }

    @Override
    public void onPaused() {

        mBinding.playPause.setImageResource(R.drawable.ic_play);
        mHandler.removeCallbacks(runnable);
    }

    @Override
    public void onStopped() {

        mBinding.playPause.setImageResource(R.drawable.ic_play);
        mHandler.removeCallbacks(runnable);
    }

    @Override
    public void onBuffering(boolean b) {

    }

    @Override
    public void onSeekTo(int i) {
        mHandler.postDelayed(runnable, 100);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }


    @Override
    public void executeAction(String selectedButton) {
        String[] value = selectedButton.split(" ");
        String ytLink = value[0];


        if (model.getType() == Enums.VIMEO_VIDEO) {

            AppDownloadManager.download(vimeoVideo.getStreams().get(ytLink.toLowerCase()), vimeoVideo.getTitle(), vimeoVideo.getTitle() + ".mp4", VideoDetailsActivity.this);

        } else if (model.getType() == Enums.OTHER_VIDEO) {
            AppDownloadManager.download(model.getLink(), vimeoVideo.getTitle(), "videon", VideoDetailsActivity.this);
        } else {
            ytLink = "https://youtu.be/" + model.getLink();
            youtubeLink = ytLink;

            YtFile ytFile = null;

            for (int i = 0, itag; i < ytFileList.size(); i++) {
                itag = ytFileList.keyAt(i);
                // ytFile represents one file with its url and meta data
                ytFile = ytFileList.get(itag);
                if (ytFile != null)
                    break;

            }
            String filename;
            if (model.getTitle().length() > 55) {
                filename = model.getTitle().substring(0, 55) + "." + ytFile.getFormat().getExt();
            } else {
                filename = model.getTitle() + "." + ytFile.getFormat().getExt();
            }
            filename = filename.replaceAll("[\\\\><\"|*?%:#/]", "");

            AppDownloadManager.download(ytFile.getUrl(), model.getTitle(), filename, VideoDetailsActivity.this);

        }

    }

    /**
     * Show rating dialog
     */
    private void showRatingDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.dialog_review, null);
        final Button buttonSubmit = (Button) alertLayout.findViewById(R.id.button_submit);
        RatingBar ratingBar = alertLayout.findViewById(R.id.rating_bar_video);
        TextView rateText = alertLayout.findViewById(R.id.text_view_rate_text);
        changeTextForRating(ratingBar, rateText);
        EditText ratingText = alertLayout.findViewById(R.id.edit_text_review);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(alertLayout);
        alert.setCancelable(true);

        final AlertDialog dialog = alert.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {

            }
        });

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rateThisVideo("" + ratingBar.getRating(), ratingText.getText().toString());
                Snackbar.make(v, getResources().getString(R.string.rate_this_video_dialog), Snackbar.LENGTH_LONG).setAction(null, null).show();
                dialog.dismiss();

            }


        });


        dialog.show();
    }


    /**
     * Rating count and change text
     *
     * @param textView change text
     */
    private void changeTextForRating(RatingBar ratingBar, TextView textView) {
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                if (rating <= 1.0) {
                    textView.setText(getResources().getString(R.string.rating_dialog_hated_it));
                } else if (rating <= 2.0) {
                    textView.setText(getResources().getString(R.string.rating_dialog_disliked_it));
                } else if (rating <= 3.0) {
                    textView.setText(getResources().getString(R.string.rating_dialog_it_okay));
                } else if (rating <= 4.0) {
                    textView.setText(getResources().getString(R.string.rating_dialog_liked_it));
                } else {
                    textView.setText(getResources().getString(R.string.rating_dialog_love_it));
                }
            }

        });


    }


    @Override
    protected void onStop() {

        super.onStop();

    }

    /**
     * Share app
     */
    public void sharefunctionalities() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        String applink = " http://play.google.com/store/apps/details?id=" + getBaseContext().getPackageName();
        String sharableLink = "";
        if (model.getType() == Enums.VIMEO_VIDEO) {
            sharableLink = vimeoVideo.getStreams().get("360p");
        } else if (model.getType() == Enums.OTHER_VIDEO) {
            sharableLink = model.getLink();
        } else {
            youtubeLink = "https://youtu.be/" + model.getLink();
            sharableLink = youtubeLink;
            getSharableDownloadLinkUrl(youtubeLink);

            return;
        }
        sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_message) + applink + "'\nVideo download link: " + sharableLink);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }


    @Override
    public void changeOrientation() {
        maximize();
    }

    @Override
    public void onLoading() {

    }

    @Override
    public void onLoaded(String s) {
        if (savedInstanceState != null) {
            int retrieved = savedInstanceState.getInt(PrefType.MED_POS);
            Log.d("checkcurrenttime", "retrieved:" + retrieved);
            youTubePlayer.seekRelativeMillis(retrieved);
        }
        mBinding.textViewTime.setText(getDurationString(youTubePlayer.getDurationMillis()));

    }

    @Override
    public void onAdStarted() {

    }

    @Override
    public void onVideoStarted() {
        displayCurrentTime();
        ProgressbarHandler.DismissProgress(VideoDetailsActivity.this);

    }

    @Override
    public void onVideoEnded() {

    }

    @Override
    public void onError(YouTubePlayer.ErrorReason errorReason) {
        ProgressbarHandler.DismissProgress(VideoDetailsActivity.this);
    }


    @Override
    public void changeDetailsActivityText(String text) {

        maximize();

    }

    @Override
    public void onAdClosed() {

    }


    /**
     * Display current time
     */
    private void displayCurrentTime() {
        try {
            if (null == youTubePlayer) return;
            currentYoutubePosition = youTubePlayer.getCurrentTimeMillis();
            String formattedTime = formatTime(youTubePlayer.getDurationMillis() - youTubePlayer.getCurrentTimeMillis());

            long progress = (100 * youTubePlayer.getCurrentTimeMillis()) / youTubePlayer.getDurationMillis();

            mBinding.youtubeSeekbar.setProgress((int) progress);

            mBinding.textCurrentTime.setText(formattedTime);
        } catch (Exception e) {

        }
    }

    /**
     * Time format
     *
     * @param millis millis
     * @return string time format
     */
    private String formatTime(int millis) {
        int seconds = millis / 1000;
        int minutes = seconds / 60;
        int hours = minutes / 60;

        return (hours == 0 ? "00:" : hours + ":") + String.format("%02d:%02d", minutes % 60, seconds % 60);
    }

    SeekBar.OnSeekBarChangeListener mVideoSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//            long lengthPlayed = (youTubePlayer.getDurationMillis() * progress) / 100;
//            youTubePlayer.seekRelativeMillis((int) lengthPlayed);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            long lengthPlayed = (youTubePlayer.getDurationMillis() * seekBar.getProgress()) / 100;
            youTubePlayer.seekToMillis((int) lengthPlayed);
        }
    };
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            displayCurrentTime();
            mHandler.postDelayed(this, 100);
        }
    };

    @Override
    public void LoadImage(ImageView imageView, ProgressBar progressBar, String imageLink, Integer[] heightWidth) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RequestOptions requestOptions = new RequestOptions();

                if (!isLive) {
                    requestOptions = requestOptions.transforms(new FitCenter(), new RoundedCorners(16)).override(heightWidth[0], heightWidth[1]).error(R.drawable.default_img); // resizes the image to these dimensions (in pixel)
                } else {
                    requestOptions = requestOptions.transforms(new FitCenter(), new RoundedCorners(16)).override(heightWidth[0], heightWidth[1]).error(R.drawable.default_category_img);
                }

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

    public boolean checkPermission() {
        return PermissionUtil.init(this).request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    /**
     * changing backgoud color of download button
     * this is a callback that indicat you have clicked download button
     */
    public void downloadClick() {
        mBinding.imageViewDownload.setColorFilter(getResources().getColor(R.color.color_home_see_all));
        mBinding.imageViewDownload.setBackgroundResource(R.drawable.drawable_circular_shape_yellow_color);
        isDownloadAvailable = false;
        Snackbar.make(getWindow().getDecorView(), "Downloading..", Snackbar.LENGTH_LONG).setAction(null, null).show();
    }

    /**
     * Image touch listener for color changed in the fav ,download, share, playlist
     */
    private void imageTouchListener() {

        //set image touch listener fav
        mBinding.imageViewFav.setOnTouchListener((v, event) -> {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    mBinding.imageViewFav.setColorFilter(getResources().getColor(R.color.color_toolbar_home_title));
                    mBinding.imageViewFav.setBackgroundResource(R.drawable.drawable_circular_shape_yellow_color);
                    mBinding.imageViewFav.invalidate();
                    break;
                }
                case MotionEvent.ACTION_UP:

                case MotionEvent.ACTION_CANCEL: {
                    mBinding.imageViewFav.setColorFilter(getResources().getColor(R.color.color_details_page_icon));
                    mBinding.imageViewFav.setBackgroundResource(R.drawable.drawable_circular_shape);
                    mBinding.imageViewFav.invalidate();
                    setFavouriteIconColor(mUserStatus);
                    break;
                }
            }

            return false;
        });

        //set image touch listener download
        mBinding.imageViewDownload.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    mBinding.imageViewDownload.setColorFilter(getResources().getColor(R.color.color_toolbar_home_title));
                    mBinding.imageViewDownload.setBackgroundResource(R.drawable.drawable_circular_shape_yellow_color);
                    mBinding.imageViewDownload.invalidate();
                    break;
                }
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL: {
                    mBinding.imageViewDownload.setColorFilter(getResources().getColor(R.color.color_details_page_icon));
                    mBinding.imageViewDownload.setBackgroundResource(R.drawable.drawable_circular_shape);
                    mBinding.imageViewDownload.invalidate();
                    checkDownloadAvailability();
                    //downloadClick();
                    break;
                }
            }

            return false;
        });


        //set image touch listener share
        mBinding.imageViewShare.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    mBinding.imageViewShare.setColorFilter(getResources().getColor(R.color.color_toolbar_home_title));
                    mBinding.imageViewShare.setBackgroundResource(R.drawable.drawable_circular_shape_yellow_color);
                    mBinding.imageViewShare.invalidate();
                    break;
                }
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL: {
                    mBinding.imageViewShare.setColorFilter(getResources().getColor(R.color.color_details_page_icon));
                    mBinding.imageViewShare.setBackgroundResource(R.drawable.drawable_circular_shape);
                    mBinding.imageViewShare.invalidate();
                    setIconColor(mUserStatus);
                    break;
                }
            }

            return false;
        });


        //set image touch listener playlist
        mBinding.imageViewPlayList.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    mBinding.imageViewPlayList.setColorFilter(getResources().getColor(R.color.color_toolbar_home_title));
                    mBinding.imageViewPlayList.setBackgroundResource(R.drawable.drawable_circular_shape_yellow_color);
                    mBinding.imageViewPlayList.invalidate();
                    break;
                }
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL: {
                    mBinding.imageViewPlayList.setColorFilter(getResources().getColor(R.color.color_details_page_icon));
                    mBinding.imageViewPlayList.setBackgroundResource(R.drawable.drawable_circular_shape);
                    mBinding.imageViewPlayList.invalidate();
                    setPlayListIcon(mUserStatus);
                    break;
                }
            }

            return false;
        });

    }


    /**
     * Rate this button effect
     */
    private void ratingTextClickEffect() {
        mBinding.textViewRateThis.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        mBinding.textViewRateThis.setBackgroundResource(R.drawable.drawable_rate_this_border_active);
                        mBinding.textViewRateThis.setTextColor(getResources().getColor(R.color.color_home_see_all));
                        mBinding.ratingIcon.setColorFilter(getResources().getColor(R.color.color_home_see_all));
                        mBinding.textViewRateThis.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        mBinding.textViewRateThis.setBackgroundResource(R.drawable.drawable_rate_this_border_inactive);
                        mBinding.textViewRateThis.setTextColor(getResources().getColor(R.color.color_home_category_title));
                        mBinding.ratingIcon.setColorFilter(getResources().getColor(R.color.color_home_category_title));
                        mBinding.textViewRateThis.invalidate();
                        setRateIconColor(mUserStatus);
                        break;
                    }
                }

                return false;
            }
        });


    }

    @Override
    public void onBackPressed() {
        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(PrefType.ORIENTATION_MODE) == PrefType.LANDSCAPE) {
                maximize();
            } else {
                overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                finish();
            }
        } else {
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
            finish();
        }
        try {
            player.stop();
            player.release();
        } catch (Exception e) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 121) {
//            loadRtmpVideo(model.getLink());
//            Log.d("testlink", model.getLink());
            if (player != null) {
                player.stop();
                player.release();
                player=null;
                loadRtmpVideo(model.getLink());
                Log.d("testlink", "Should restart");
            }
        } else {
            Log.d("testlink", "Restored:" + model.getLink());
        }
    }

    @Override
    public void onPlayPauseClick(View view) {
        ImageView v = (ImageView) view;
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                v.setImageResource(R.drawable.ic_play);
                mediaPlayer.pause();
            } else {
                v.setImageResource(R.drawable.ic_pause);
                mediaPlayer.start();
            }

        }

    }




}
