package com.shadowhite.archieve1971.ui.inputpin;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.shadowhite.archieve1971.R;
import com.shadowhite.archieve1971.data.local.authentication.AuthenticationModel;
import com.shadowhite.archieve1971.data.remote.RemoteApiProvider;
import com.shadowhite.archieve1971.data.remote.home.RemoteVideoApiInterface;
import com.shadowhite.archieve1971.databinding.ActivityInputPinBinding;
import com.shadowhite.archieve1971.ui.createnewpassword.CreateNewPasswordActivity;
import com.shadowhite.archieve1971.ui.videodetails.ViewCountResponse;
import com.shadowhite.archieve1971.ui.welcome.WelcomeActivity;
import com.shadowhite.util.helper.ApiToken;
import com.shadowhite.util.helper.AppConstants;
import com.shadowhite.util.helper.ProgressbarHandler;
import com.w3engineers.ext.strom.application.ui.base.BaseActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class InputPinActivity extends BaseActivity {
    private ActivityInputPinBinding mBinding;
    private int prevActivity;
    private String email;
    private RemoteVideoApiInterface mRemoteVideoApiInterface;
    private boolean isActive=false;

    public static void runActivity(Context context, HashMap<String, String> prevActivity) {
        String email="";
        /**
         * @param prev_activity track previous activity
         */
        int prev_Activity=0;
        for(Map.Entry<String, String> entry : prevActivity.entrySet()) {
            String key = entry.getKey();
           if(key.equals(AppConstants.EMAIL))
           {
               email=entry.getValue();

           }
           else if(key.equals(AppConstants.PREV_ACTIVITY))
           {
               prev_Activity= Integer.parseInt(entry.getValue());
           }
        }
        Intent intent = new Intent(context, InputPinActivity.class);
        intent.putExtra(AppConstants.PREV_ACTIVITY,prev_Activity);
        intent.putExtra(AppConstants.EMAIL,email);
        context.startActivity( intent);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_input_pin;
    }

    @Override
    protected void startUI() {
        mBinding=(ActivityInputPinBinding)getViewDataBinding();
        setClickListener(mBinding.btnProceed,mBinding.resendCode);
        prevActivity=getIntent().getIntExtra(AppConstants.PREV_ACTIVITY,0);
        email=getIntent().getStringExtra(AppConstants.EMAIL);
        Log.d("mykey",""+prevActivity);
        Log.d("mykey","Email:"+email);
        setSupportActionBar(mBinding.toolbarForgotPassword);
        mRemoteVideoApiInterface= RemoteApiProvider.getInstance().getRemoteHomeVideoApi();
        if(prevActivity==AppConstants.SIGN_UP_ACTIVITY){
            getSupportActionBar().setTitle(getBaseContext().getResources().getString(R.string.text_password_verification_msg));
        }
        else if(prevActivity==AppConstants.FORGOT_PASSWORD){
            getSupportActionBar().setTitle(getBaseContext().getResources().getString(R.string.text_password_password_recovery_msg));

        }
        buttonActiveInactive();
        timeCountDownStart();

    }



    /**
     * button proceed active and in active
     */
    private void buttonActiveInactive(){

        mBinding.inputToken.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length()< AppConstants.four){
                    mBinding.btnProceed.setEnabled(false);
                    mBinding.btnProceed.setClickable(false);
                    mBinding.btnProceed.setAlpha(.5f);
                }
                else if(s.length()== AppConstants.four){
                    mBinding.btnProceed.setEnabled(true);
                    mBinding.btnProceed.setClickable(true);
                    mBinding.btnProceed.setAlpha(1f);
                }else {
                    mBinding.btnProceed.setEnabled(false);
                    mBinding.btnProceed.setClickable(false);
                    mBinding.btnProceed.setAlpha(.5f);
                }
            }
        });

    }


    /**
     * Resend code
     * @param prevActivity prevActivity
     * @param email email
     */
    private void resendCode(int prevActivity,String email)
    {
        if(prevActivity==AppConstants.SIGN_UP_ACTIVITY)
        {
            mRemoteVideoApiInterface.signUpValidation(ApiToken.GET_TOKEN(getBaseContext()),email).enqueue(new Callback<ViewCountResponse>() {
                @Override
                public void onResponse(Call<ViewCountResponse> call, Response<ViewCountResponse> response) {

                }

                @Override
                public void onFailure(Call<ViewCountResponse> call, Throwable t) {

                }
            });
        }
        else if(prevActivity==AppConstants.FORGOT_PASSWORD)
            {
                mRemoteVideoApiInterface.validateToken(ApiToken.GET_TOKEN(getBaseContext()),email).enqueue(new Callback<AuthenticationModel>() {
                    @Override
                    public void onResponse(Call<AuthenticationModel> call, Response<AuthenticationModel> response) {

                    }

                    @Override
                    public void onFailure(Call<AuthenticationModel> call, Throwable t) {

                    }
                });
            }
    }


    /**
     * Verification code for sign up validation
     * @param prevActivity prevActivity
     */
    private void verificationCode(int prevActivity)
    {
        if(prevActivity==AppConstants.SIGN_UP_ACTIVITY)
        {
            mRemoteVideoApiInterface.signUpValidation(ApiToken.GET_TOKEN(getBaseContext()),email,mBinding.inputToken.getText().toString().trim()).enqueue(new Callback<AuthenticationModel>() {
                @Override
                public void onResponse(Call<AuthenticationModel> call, Response<AuthenticationModel> response) {
                    AuthenticationModel serverResponse=response.body();
                    Log.d("mykey","All res:"+serverResponse.toString());
                    Log.d("mykey","Code:"+mBinding.inputToken.getText().toString().trim());
                    if(serverResponse.getStatusCode()==200&&prevActivity==AppConstants.SIGN_UP_ACTIVITY)
                    {

                        WelcomeActivity.runActivity(InputPinActivity.this,prevActivity);
                        finish();
                        ProgressbarHandler.DismissProgress(InputPinActivity.this);
                        Log.d("mykey","PrefActivity: SignUpActivity");

                    }
                    else  if(serverResponse.getStatusCode()==200&&prevActivity==AppConstants.FORGOT_PASSWORD)
                    {
                        HashMap<String,String> hashMap=new HashMap<>();
                        hashMap.put(AppConstants.EMAIL,email);
                        hashMap.put(AppConstants.PREV_ACTIVITY,""+prevActivity);
                        hashMap.put(AppConstants.TOKEN,mBinding.inputToken.getText().toString().trim());
                        CreateNewPasswordActivity.runActivity(getBaseContext(),hashMap);
                        finish();
                        ProgressbarHandler.DismissProgress(InputPinActivity.this);
                        Log.d("mykey","PrefActivity: InputActivity");

                    }
                    else {
                        Toast.makeText(InputPinActivity.this,"" + serverResponse.getMessage(), Toast.LENGTH_LONG).show();
                        ProgressbarHandler.DismissProgress(InputPinActivity.this);
                    }
                }

                @Override
                public void onFailure(Call<AuthenticationModel> call, Throwable t) {

                }
            });
        }
        else if(prevActivity==AppConstants.FORGOT_PASSWORD)
        {
            mRemoteVideoApiInterface.validateToken(ApiToken.GET_TOKEN(getBaseContext()),email,mBinding.inputToken.getText().toString().trim()).enqueue(new Callback<AuthenticationModel>() {
                @Override
                public void onResponse(Call<AuthenticationModel> call, Response<AuthenticationModel> response) {
                    AuthenticationModel serverResponse=response.body();
                    Log.d("mykey","All res:"+serverResponse.toString());
                    Log.d("mykey","Code:"+mBinding.inputToken.getText().toString().trim());
                    if(serverResponse.getStatusCode()==200&&prevActivity==AppConstants.SIGN_UP_ACTIVITY)
                    {

                        WelcomeActivity.runActivity(InputPinActivity.this,prevActivity);
                        finish();
                        ProgressbarHandler.DismissProgress(InputPinActivity.this);

                    }
                    else  if(serverResponse.getStatusCode()==200&&prevActivity==AppConstants.FORGOT_PASSWORD)
                    {
                        HashMap<String,String> hashMap=new HashMap<>();
                        hashMap.put(AppConstants.EMAIL,email);
                        hashMap.put(AppConstants.PREV_ACTIVITY,""+prevActivity);
                        hashMap.put(AppConstants.TOKEN,mBinding.inputToken.getText().toString().trim());
                        CreateNewPasswordActivity.runActivity(InputPinActivity.this,hashMap);
                        finish();
                        ProgressbarHandler.DismissProgress(InputPinActivity.this);

                    }
                    else {
                        Toast.makeText(InputPinActivity.this,""+serverResponse.getMessage(), Toast.LENGTH_LONG).show();
                        ProgressbarHandler.DismissProgress(InputPinActivity.this);
                    }
                }

                @Override
                public void onFailure(Call<AuthenticationModel> call, Throwable t) {

                }
            });
        }
    }
    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId())
        {
            case R.id.btn_proceed:
                ProgressbarHandler.ShowLoadingProgress(InputPinActivity.this);
                verificationCode(prevActivity);
                break;
            case R.id.resend_code:
                if (isActive){
                    resendCode(prevActivity,email);
                    timeCountDownStart();
                }
                break;

        }
    }


    /**
     * Time count down start
     */
    private void timeCountDownStart(){

        new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                long millis = millisUntilFinished;
                String second = String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));

                mBinding.textTime.setText(""+second);
                mBinding.resendCode.setClickable(false);
                mBinding.resendCode.setAlpha(0.5f);
                isActive=false;


            }

            public void onFinish() {
                mBinding.textTime.setText("");
                mBinding.resendCode.setClickable(true);
                mBinding.resendCode.setAlpha(1f);
                isActive=true;

            }

        }.start();
    }



}
