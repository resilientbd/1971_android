package com.w3engineers.core.videon.ui.welcome;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;

import com.w3engineers.core.util.helper.AppConstants;
import com.w3engineers.core.videon.R;
import com.w3engineers.core.videon.databinding.ActivityWelcomeBinding;
import com.w3engineers.core.videon.ui.login.LoginActivity;
import com.w3engineers.ext.strom.application.ui.base.BaseActivity;


public class WelcomeActivity extends BaseActivity {
    private ActivityWelcomeBinding mBinding;

    public static void runActivity(Context context, int prevActivity) {
        Intent intent = new Intent(context, WelcomeActivity.class);
        intent.putExtra(AppConstants.PREV_ACTIVITY, prevActivity);
        runCurrentActivity(context, intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_welcome;
    }

    @Override
    protected void startUI() {
        mBinding = (ActivityWelcomeBinding) getViewDataBinding();
        int prevActivity = getIntent().getIntExtra(AppConstants.PREV_ACTIVITY, 0);

        if (prevActivity == AppConstants.SIGN_UP_ACTIVITY) {

        } else if (prevActivity == AppConstants.FORGOT_PASSWORD) {
            mBinding.welcomeText.setText(getBaseContext().getResources().getString(R.string.welcome_text_reset_password));
        }
        new MyTimer(2000, 500, prevActivity).start();
    }

    private class MyTimer extends CountDownTimer {
        int prevActivity;

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public MyTimer(long millisInFuture, long countDownInterval, int prevActivity) {

            super(millisInFuture, countDownInterval);
            this.prevActivity = prevActivity;
        }

        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            if (prevActivity == AppConstants.SIGN_UP_ACTIVITY) {
                LoginActivity.runActivity(WelcomeActivity.this);
                finish();
            } else if (prevActivity == AppConstants.FORGOT_PASSWORD)
            {
                LoginActivity.runActivity(WelcomeActivity.this);
                finish();
            }
        }
    }
}
