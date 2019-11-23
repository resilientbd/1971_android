package com.shadowhite.archieve1971.ui.home;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;

import com.shadowhite.archieve1971.R;
import com.shadowhite.archieve1971.ui.videodetails.MediaControllerInteractor;



public class MyMediaController extends MediaController {

    private Context context;
    private MediaControllerInteractor mediacontrollerInteractor;
    ChangeText mChangeText;
    boolean mIsLive;
    private MediaButtonControllListerner mediaButtonControllListerner;
    public MyMediaController(Context context, ChangeText  changeText,boolean isLive) {
        super(context);
        this.context=context;
        mChangeText=changeText;
        mIsLive=isLive;

    }

    public void setMediaButtonControllListerner(MediaButtonControllListerner mediaButtonControllListerner) {
        this.mediaButtonControllListerner = mediaButtonControllListerner;
    }

    public void setMediacontrollerInteractor(MediaControllerInteractor mediacontrollerInteractor) {
        this.mediacontrollerInteractor = mediacontrollerInteractor;
    }

    @Override
    public void setAnchorView(View view) {
        super.setAnchorView(view);

        Button minMaxButton = new Button(context);
        minMaxButton.setBackground(context.getResources().getDrawable(R.drawable.ic_expand));
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.setMargins(0,context.getResources().getDimensionPixelOffset(R.dimen.margin_16),context.getResources().getDimensionPixelOffset(R.dimen.margin_8),0);
        params.gravity = Gravity.RIGHT;
        params.height=50;
        params.width=50;
       // addView(minMaxButton, params);
        minMaxButton.setLayoutParams(params);

        minMaxButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediacontrollerInteractor!=null) {
                    mediacontrollerInteractor.changeOrientation();
                }

            }
        });
        LinearLayout mLayout;
        if (mIsLive) {
            for (int index = 0; index < getChildCount(); ++index) {
                View nextChild = getChildAt(index);
                FrameLayout.LayoutParams layoutParams=  new  FrameLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);


                LinearLayout horizentalLayout=(LinearLayout) nextChild;
                horizentalLayout.setLayoutParams(layoutParams);
                LayoutInflater inflater=LayoutInflater.from(context);
                View view1=inflater.inflate(R.layout.mediacontroller_custom_layout,null);
                ImageView maxBtn=view1.findViewById(R.id.minmaxbutton);
                ImageView playPause=view1.findViewById(R.id.pausecontroller);
                playPause.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mediaButtonControllListerner!=null)
                        {
                            mediaButtonControllListerner.onPlayPauseClick(v);
                        }
                    }
                });
                maxBtn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mediacontrollerInteractor!=null) {
                            mediacontrollerInteractor.changeOrientation();
                        }

                    }
                });
              //  horizentalLayout.setOrientation(LinearLayout.HORIZONTAL);

                if (nextChild instanceof LinearLayout) {
                    LinearLayout linearLayout = (LinearLayout) nextChild;
                    for (int j = 0; j < linearLayout.getChildCount(); j++) {
                        LinearLayout view2 = (LinearLayout) linearLayout.getChildAt(j);
                        view2.removeAllViews();
                        view2.setBackground(getResources().getDrawable(R.color.tr_black));

                    }

                    //horizentalLayout.addView(minMaxButton);

                }


                    LinearLayout.LayoutParams paramsLiveTwo = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    paramsLiveTwo.height=50;
                    paramsLiveTwo.width=50;
                    paramsLiveTwo.setMargins(0,context.getResources().getDimensionPixelOffset(R.dimen.margin_16),context.getResources().getDimensionPixelOffset(R.dimen.margin_8),0);


                    //horizentalLayout.addView(buttonLive);
                horizentalLayout.addView(view1);
                break;
            }
        }else {
            addView(minMaxButton, params);

        }
    }

    public  interface ChangeText{
       void changeDetailsActivityText(String text);
    }
    public interface MediaButtonControllListerner{
        public void onPlayPauseClick(View view);
    }
}
