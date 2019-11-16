package com.w3engineers.core.videon.ui.audiodetails;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.w3engineers.core.videon.R;
import com.w3engineers.core.videon.databinding.ActivityAudioBinding;
import com.w3engineers.core.videon.databinding.ActivityAudioDetailsBinding;
import com.w3engineers.core.videon.databinding.ActivityDocViewerBinding;
import com.w3engineers.core.videon.ui.login.LoginActivity;
import com.w3engineers.ext.strom.application.ui.base.BaseActivity;


public class AudioDetailsActivity extends BaseActivity {


    private ActivityAudioDetailsBinding mBinding;

    public static void runActivity(Context context) {
        Intent intent = new Intent(context, AudioDetailsActivity.class);
        runCurrentActivity(context, intent);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_audio_details;
    }

    @Override
    protected void startUI() {
        mBinding=(ActivityAudioDetailsBinding)getViewDataBinding();

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_login:
                LoginActivity.runActivity(AudioDetailsActivity.this);
                break;
        }
    }







}
