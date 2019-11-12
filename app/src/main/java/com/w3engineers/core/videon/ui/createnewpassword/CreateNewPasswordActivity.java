package com.w3engineers.core.videon.ui.createnewpassword;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.w3engineers.core.util.helper.ApiToken;
import com.w3engineers.core.util.helper.AppConstants;
import com.w3engineers.core.util.helper.ProgressbarHandler;
import com.w3engineers.core.videon.R;
import com.w3engineers.core.videon.data.local.authentication.AuthenticationModel;
import com.w3engineers.core.videon.data.remote.RemoteApiProvider;
import com.w3engineers.core.videon.data.remote.home.RemoteVideoApiInterface;
import com.w3engineers.core.videon.databinding.ActivityCreateNewPasswordBinding;
import com.w3engineers.core.videon.ui.welcome.WelcomeActivity;
import com.w3engineers.ext.strom.application.ui.base.BaseActivity;
import com.w3engineers.ext.strom.util.helper.Toaster;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CreateNewPasswordActivity extends BaseActivity {
    private RemoteVideoApiInterface mRemoteVideoApiInterface;
    private ActivityCreateNewPasswordBinding mBinding;

    public static void runActivity(Context context, HashMap<String, String> prevActivity) {
        String email = "";
        int prev_Activity = 0;
        String token="";
        for (Map.Entry<String, String> entry : prevActivity.entrySet()) {
            String key = entry.getKey();
            if (key.equals(AppConstants.EMAIL)) {
                email = entry.getValue();

            } else if (key.equals(AppConstants.PREV_ACTIVITY)) {
                prev_Activity = Integer.parseInt(entry.getValue());
            }
            else if (key.equals(AppConstants.TOKEN)) {
                token =""+entry.getValue();
            }
        }
        Intent intent = new Intent(context, CreateNewPasswordActivity.class);
        intent.putExtra(AppConstants.PREV_ACTIVITY, prev_Activity);
        intent.putExtra(AppConstants.EMAIL, email);
        intent.putExtra(AppConstants.TOKEN, token);
        runCurrentActivity(context, intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_create_new_password;
    }

    @Override
    protected void startUI() {
        mBinding = (ActivityCreateNewPasswordBinding) getViewDataBinding();
        setSupportActionBar(mBinding.toolbar);
        mRemoteVideoApiInterface = RemoteApiProvider.getInstance().getRemoteHomeVideoApi();
        setClickListener(mBinding.btnResetPassword);
        validation();

    }


    /**
     * password validation
     */
    private void validation() {

        mBinding.editPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() <= AppConstants.zero) {
                    mBinding.editPassword.setError(getResources().getString(R.string.password_can_not_empty));
                    mBinding.btnResetPassword.setClickable(false);
                } else if (s.toString().contains(AppConstants.space)) {
                    mBinding.editPassword.setError(getString(R.string.space_not_allowed));
                    mBinding.btnResetPassword.setClickable(false);
                }  else if (s.toString().length() >= AppConstants.one && s.toString().length() <= AppConstants.five ) {
                    mBinding.editPassword.setError(getResources().getString(R.string.give_min_six_character));
                    mBinding.btnResetPassword.setClickable(false);
                } else {
                    mBinding.editPassword.setError(null);
                    mBinding.btnResetPassword.setClickable(true);
                }
            }
        });


        mBinding.editConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() <= AppConstants.zero) {
                    mBinding.editConfirmPassword.setError(getResources().getString(R.string.confirm_can_not_empty));
                    mBinding.btnResetPassword.setClickable(false);
                }
                else if (s.toString().contains(AppConstants.space)) {
                    mBinding.editConfirmPassword.setError(getResources().getString(R.string.space_not_allowed));
                    mBinding.btnResetPassword.setClickable(false);
                }
                else {
                    mBinding.editConfirmPassword.setError(null);
                    mBinding.btnResetPassword.setClickable(true);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_reset_password:
                String email = getIntent().getStringExtra(AppConstants.EMAIL);
                int prev_activity = getIntent().getIntExtra(AppConstants.PREV_ACTIVITY, 0);
                String token = getIntent().getStringExtra(AppConstants.TOKEN);
                createNewPasswordFunctionality(ApiToken.GET_TOKEN(getBaseContext()), email, token,
                        mBinding.editPassword.getText().toString().trim(),
                        mBinding.editConfirmPassword.getText().toString().trim(), prev_activity);
                break;
        }
    }


    /**
     * check validation and hit to server
     *
     * @param api_token       api_token
     * @param email           take email
     * @param token           take token
     * @param new_pass        new pass
     * @param confirmPassword confirmPassword
     * @param prev_activity   pre_activity
     */
    private void createNewPasswordFunctionality(String api_token, String email, String token, String new_pass, String confirmPassword, final int prev_activity) {
        if (!new_pass.isEmpty() || !confirmPassword.isEmpty()) {
            if (new_pass.contains(AppConstants.space) || confirmPassword.contains(AppConstants.space)) {
                mBinding.editPassword.setError(getResources().getString(R.string.space_not_allowed));
            }else if (new_pass.length() >= AppConstants.one && new_pass.length() <= AppConstants.five) {
                mBinding.editPassword.setError(getResources().getString(R.string.give_min_six_character));
            }
            else if (new_pass.equals(confirmPassword)) {
                ProgressbarHandler.ShowLoadingProgress(CreateNewPasswordActivity.this);
                mRemoteVideoApiInterface.validateToken(api_token, email, token, new_pass).enqueue(new Callback<AuthenticationModel>() {
                    @Override
                    public void onResponse(Call<AuthenticationModel> call, Response<AuthenticationModel> response) {
                        if (response.isSuccessful()) {
                            AuthenticationModel mResponse = response.body();
                            if (mResponse.getStatusCode().equals(AppConstants.SUCCESS)){
                                if (mResponse.getData() != null) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Snackbar.make(getWindow().getDecorView(), getBaseContext().getResources().getString(R.string.text_password_reset_msg), Snackbar.LENGTH_LONG).setAction(null, null).show();
                                            if (prev_activity == AppConstants.FORGOT_PASSWORD) {
                                                WelcomeActivity.runActivity(CreateNewPasswordActivity.this, prev_activity);
                                                finish();
                                                ProgressbarHandler.DismissProgress(CreateNewPasswordActivity.this);
                                            }
                                        }
                                    });
                                }
                            }else {
                                ProgressbarHandler.DismissProgress(CreateNewPasswordActivity.this);
                            }
                        } else {
                            ProgressbarHandler.DismissProgress(CreateNewPasswordActivity.this);
                        }

                    }

                    @Override
                    public void onFailure(Call<AuthenticationModel> call, Throwable t) {
                        ProgressbarHandler.DismissProgress(CreateNewPasswordActivity.this);

                    }
                });
            } else {
                Toaster.showLong(getResources().getString(R.string.password_isnot_mattched));
            }

        } else {
            mBinding.editPassword.setError(getResources().getString(R.string.password_can_not_empty));
            mBinding.editConfirmPassword.setError(getResources().getString(R.string.confirm_can_not_empty));
        }


    }
}
