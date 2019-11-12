package com.w3engineers.core.videon.ui.forgotpassword;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.w3engineers.core.util.helper.ApiToken;
import com.w3engineers.core.util.helper.AppConstants;
import com.w3engineers.core.util.helper.ProgressbarHandler;
import com.w3engineers.core.videon.R;
import com.w3engineers.core.videon.data.local.authentication.AuthenticationModel;
import com.w3engineers.core.videon.data.remote.RemoteApiProvider;
import com.w3engineers.core.videon.data.remote.home.RemoteVideoApiInterface;
import com.w3engineers.core.videon.databinding.ActivityForgotPasswordBinding;
import com.w3engineers.core.videon.ui.inputpin.InputPinActivity;
import com.w3engineers.ext.strom.application.ui.base.BaseActivity;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ForgotPasswordActivity extends BaseActivity {
    private RemoteVideoApiInterface mRemoteVideoApiInterface;
    private ActivityForgotPasswordBinding mBinding;

    public static void runActivity(Context context) {
        Intent intent = new Intent(context, ForgotPasswordActivity.class);
        runCurrentActivity(context, intent);

    }
    @Override
    protected int getLayoutId() {
        return R.layout.activity_forgot_password;
    }

    @Override
    protected void startUI() {
        mBinding = (ActivityForgotPasswordBinding) getViewDataBinding();
        setClickListener(mBinding.btnResetPassword);
        mRemoteVideoApiInterface = RemoteApiProvider.getInstance().getRemoteHomeVideoApi();
        setSupportActionBar(mBinding.toolbarForgotPassword);
        emailValidation();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_reset_password:
                String email = mBinding.baseEditTextEmail.getText().toString().trim();
                if (!email.isEmpty()) {
                    if (!isEmailValid(email)) {
                        mBinding.baseEditTextEmail.setError(getResources().getString(R.string.sign_up_invalid_email));
                    } else {
                        sendEmailToServer(email);
                    }
                } else {
                    mBinding.baseEditTextEmail.setError(getResources().getString(R.string.email_can_not_empty));
                }
                break;


        }
    }

    /**
     * send email to server
     *
     * @param email
     */
    private void sendEmailToServer(String email) {


        ProgressbarHandler.ShowLoadingProgress(ForgotPasswordActivity.this);
        mRemoteVideoApiInterface.validateToken(ApiToken.GET_TOKEN(getBaseContext()),""+ email).enqueue(new Callback<AuthenticationModel>() {
            @Override
            public void onResponse(Call<AuthenticationModel> call, Response<AuthenticationModel> response) {
                AuthenticationModel serverResponse = response.body();
                if (serverResponse.getStatusCode() == 200) {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put(AppConstants.EMAIL, email);
                    hashMap.put(AppConstants.PREV_ACTIVITY, "" + AppConstants.FORGOT_PASSWORD);
                    InputPinActivity.runActivity(ForgotPasswordActivity.this, hashMap);
                    finish();
                    ProgressbarHandler.DismissProgress(ForgotPasswordActivity.this);

                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "" + serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    ProgressbarHandler.DismissProgress(ForgotPasswordActivity.this);
                }
            }

            @Override
            public void onFailure(Call<AuthenticationModel> call, Throwable t) {
                ProgressbarHandler.DismissProgress(ForgotPasswordActivity.this);
            }
        });
    }


    /*
      Added email validation
     */
    private void emailValidation() {


        mBinding.baseEditTextEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() <= AppConstants.zero) {
                    mBinding.baseEditTextEmail.setError(getResources().getString(R.string.email_can_not_empty));
                } else if (!isEmailValid(s.toString())) {
                    mBinding.baseEditTextEmail.setError(getResources().getString(R.string.sign_up_invalid_email));
                } else {
                    mBinding.baseEditTextEmail.setError(null);
                }
            }
        });

    }


    /**
     * method is used for checking valid email id format.
     *
     * @param email
     * @return boolean true for valid false for invalid
     */
    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
