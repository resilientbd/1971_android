package com.w3engineers.core.videon.ui.fullscreenactivity;

import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
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
import com.w3engineers.core.util.helper.ProgressbarHandler;
import com.w3engineers.core.videon.R;
import com.w3engineers.core.videon.databinding.ActivityFullscreenBinding;
import com.w3engineers.ext.strom.application.ui.base.BaseActivity;

public class FullScreeActivity extends BaseActivity {
    ActivityFullscreenBinding mBinding;
    private Handler handler;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_fullscreen;
    }

    @Override
    protected void startUI() {
        mBinding= (ActivityFullscreenBinding) getViewDataBinding();
        String url=getIntent().getStringExtra("url");
        ProgressbarHandler.ShowLoadingProgress(this);
        loadRtmpVideo(url);

        exoMaxMin=mBinding.simplePlayer.findViewById(R.id.max_min);
        exoMaxMin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.stop();
                player.release();
                finish();
            }
        });
    }

    SimpleExoPlayer player;
    ImageView exoMaxMin;

    /**
     * Load rtmp video
     * @param videolink videolink
     */
    private void loadRtmpVideo(String videolink)
    {

        mBinding.simplePlayer.setVisibility(View.VISIBLE);


        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        //Create the player

        player = ExoPlayerFactory.newSimpleInstance(this,trackSelector);
        PlayerView playerView = mBinding.simplePlayer;
        playerView.setPlayer(player);

        exoMaxMin=playerView.findViewById(R.id.max_min);
        exoMaxMin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.stop();
                player.release();
                finish();
            }
        });

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
                Log.d("loadingstate","loading: "+isLoading);
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                Log.d("loadingstate","ready: "+playWhenReady+" playbackState:"+playbackState);
                if(playbackState==1)
                {
                    //  "http://185.152.66.57/OSJOVENSTITAS/video.m3u8?token=TohQHf9RV2"
                    loadRtmpVideo(videolink);
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

//        play(model.getLink());
        DefaultDataSourceFactory dataSourceFactory= new DefaultDataSourceFactory(this, "user-agent");
        Uri uri = Uri.parse(videolink);

        // This is the MediaSource representing the media to be played.
        handler=new Handler();
        MediaSource mediaSource = new HlsMediaSource(uri, dataSourceFactory, handler, null);
// Prepare the player with the source.
        player.prepare(mediaSource);
//auto start playing
        player.setPlayWhenReady(true);
        ProgressbarHandler.DismissProgress(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        player.stop();
        player.release();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        player.stop();
        player.release();
    }
}
