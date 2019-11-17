package com.w3engineers.core.videon.ui.imagedetails;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.w3engineers.core.videon.R;
import com.w3engineers.core.videon.databinding.ActivityDocViewerBinding;
import com.w3engineers.core.videon.databinding.ActivityImageRndTestBinding;
import com.w3engineers.core.videon.databinding.ActivityVideosBinding;
import com.w3engineers.core.videon.ui.login.LoginActivity;
import com.w3engineers.ext.strom.application.ui.base.BaseActivity;

import java.util.Arrays;
import java.util.List;


public class ImageDetailsActivity extends BaseActivity {

    private static final String TAG ="imagernd" ;
    private SlidingUpPanelLayout mLayout;
    private ActivityImageRndTestBinding mBinding;

    public static void runActivity(Context context) {
        Intent intent = new Intent(context, ImageDetailsActivity.class);
        runCurrentActivity(context, intent);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_image_rnd_test;
    }

    @Override
    protected void startUI() {
         mBinding=(ActivityImageRndTestBinding)getViewDataBinding();


      //  mLayout =  mBinding.slidingLayout;
//        mLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
//            @Override
//            public void onPanelSlide(View panel, float slideOffset) {
//                Log.i(TAG, "onPanelSlide, offset " + slideOffset);
//            }
//
//            @Override
//            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
//                Log.i(TAG, "onPanelStateChanged " + newState);
//            }
//        });

//        mLayout.setFadeOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
//            }
//        });
//
//        mBinding.upbtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_login:
                LoginActivity.runActivity(ImageDetailsActivity.this);
                break;
        }
    }


    @Override
    public void onBackPressed() {
//        if (mLayout != null &&
//                (mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
//            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
//        } else {
            super.onBackPressed();
//        }
    }




}
