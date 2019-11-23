package com.shadowhite.archieve1971.ui.audiodetails;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.shadowhite.archieve1971.R;
import com.shadowhite.archieve1971.data.local.audio.Datum;
import com.shadowhite.archieve1971.databinding.ActivityAudioDetailsBinding;
import com.shadowhite.util.NetworkURL;
import com.w3engineers.ext.strom.application.ui.base.BaseActivity;

import java.io.IOException;


public class AudioDetailsActivity extends BaseActivity implements MediaPlayer.OnCompletionListener,MediaPlayer.OnPreparedListener,MediaPlayer.OnBufferingUpdateListener,MediaPlayer.OnErrorListener{


    private ActivityAudioDetailsBinding mBinding;
    private MediaPlayer mPlayer;
    private int timer=100000;
    private String audioUrl="http://glazeitsolutions.com/admin/public/uploads/f9sf2xzzr9k40o4w.mp3";
    private int currentposition=0;

    public static void runActivity(Context context) {
        Intent intent = new Intent(context, AudioDetailsActivity.class);
        runCurrentActivity(context, intent);
    }
    public static void runActivity(Context context, Datum datum) {
        Intent intent = new Intent(context, AudioDetailsActivity.class);
        Gson gson=new Gson();
        intent.putExtra("audio",gson.toJson(datum));
        runCurrentActivity(context, intent);
    }

    private void mediastop(){
        if(mPlayer!=null)
        {
            try{
                mPlayer.stop();
                mPlayer.release();
                mBinding.progressBar2.setVisibility(View.GONE);
                mBinding.seekBar.setProgress(0);
                mBinding.totaltime.setText(getDurationString(mPlayer.getDuration()/1000));
                mBinding.currenttime.setText("00:00:00");

            }catch (Exception e)
            {
                stopProgress();
                Log.d("datacheck",""+e.getMessage());
            }
        }
    }
    private void mediaplay()
    {
        if(mPlayer!=null)
        {
            try{

                mPlayer.start();
                mBinding.pauseMedia.setImageResource(R.drawable.ic_pause_black);
                mBinding.progressBar2.setVisibility(View.GONE);


            }catch (Exception e)
            {
                stopProgress();
                Log.d("datacheck","error:"+e.getMessage());
            }
        }
    }
    @Override
    protected int getLayoutId() {
        return R.layout.activity_audio_details;
    }

    @Override
    protected void startUI() {
        mBinding=(ActivityAudioDetailsBinding)getViewDataBinding();
        setClickListener();
        mPlayer=new MediaPlayer();
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnErrorListener(this);
        mPlayer.setOnPreparedListener(this);
        mPlayer.setOnBufferingUpdateListener(this);

//

        try{
            Gson gson=new Gson();
            String string=getIntent().getStringExtra("audio");
            Datum audioItem=gson.fromJson(string,Datum.class);
            audioUrl= NetworkURL.videosEndPointURL+audioItem.getAudioUrl();
            mBinding.title.setText(audioItem.getAudioTitle());
            Glide.with(AudioDetailsActivity.this).load(NetworkURL.videosEndPointURL+audioItem.getAudioImg()).into(mBinding.imageView5);

        }catch (Exception e)
        {

        }
       setClickListener(mBinding.backwardMedia,mBinding.forwardMedia,mBinding.pauseMedia);
        try {
            mPlayer.setDataSource(audioUrl);
            mPlayer.prepare();
        } catch (IOException e) {
            stopProgress();
            e.printStackTrace();
        }


        mediaplay();
        mBinding.seekBar.setProgress(0);
        mBinding.totaltime.setText(""+getDurationString(mPlayer.getDuration()/1000));
        mBinding.currenttime.setText("00:00:00");
    }


    public void startProgress(){
        mBinding.progressBar2.setVisibility(View.VISIBLE);;
    }
    public void stopProgress()
    {
        mBinding.progressBar2.setProgress(View.GONE);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.backward_media:
                backward();
                break;
            case R.id.forward_media:
                forward();
                break;
            case R.id.pause_media:
               pause_play();
                break;
            case R.id.forward:

                break;
          //  case R.id.backword:
                //back

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mediastop();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mediastop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediastop();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mediastop();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
      // mBinding.seekBar.setProgress(percent);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mediastop();
        return false;
    }

    private void pause_play()
    {
        if(mPlayer!=null)
        {
            if(mPlayer.isPlaying())
            {

               mPlayer.pause();
               mBinding.pauseMedia.setImageResource(R.drawable.ic_play);
            }
            else {
                mediaplay();
            }
        }
    }
    private void forward()
    {
        if(mPlayer!=null)
        {
            int currentposition=mPlayer.getCurrentPosition()+timer;
            mPlayer.seekTo(currentposition);
          //  mBinding.seekBar.setProgress();
        }
    }
    private void backward()
    {
        if(mPlayer!=null)
        {
            int currentposition=mPlayer.getCurrentPosition()-timer;
            mPlayer.seekTo(currentposition);
           // mBinding.seekBar.setProgress(currentposition-timer);
        }
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


}
