package com.w3engineers.core.videon.ui.signup;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.w3engineers.core.util.helper.ActivityTracker;
import com.w3engineers.core.util.helper.ApiToken;
import com.w3engineers.core.util.helper.AppConstants;
import com.w3engineers.core.util.helper.ProgressbarHandler;
import com.w3engineers.core.videon.R;
import com.w3engineers.core.videon.data.local.Enums;
import com.w3engineers.core.videon.data.local.authentication.AuthenticationModel;
import com.w3engineers.core.videon.data.remote.RemoteApiProvider;
import com.w3engineers.core.videon.data.remote.home.RemoteVideoApiInterface;
import com.w3engineers.core.videon.databinding.ActivitySignupBinding;
import com.w3engineers.core.videon.ui.inputpin.InputPinActivity;
import com.w3engineers.core.videon.ui.login.LoginActivity;
import com.w3engineers.core.videon.ui.privacypolicy.PrivacyPolicyActivity;
import com.w3engineers.ext.strom.application.ui.base.BaseActivity;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SignUpActivity extends BaseActivity {
    private final String TAG = this.getClass().getName();
    private ActivitySignupBinding mBinding;
    private RemoteVideoApiInterface mRemoteLoginLogoutApiInterface;

    public static void runActivity(Context context, int activityTracker) {
        Intent intent = new Intent(context, SignUpActivity.class);
        intent.putExtra("preActivity", activityTracker);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_signup;
    }

    @Override
    protected void startUI() {


        mBinding = (ActivitySignupBinding) getViewDataBinding();
        mBinding.textSignInMessage.setText(Html.fromHtml(getResources().getString(R.string.already_have_account_sign_up)));
        setClickListener(mBinding.textSignInMessage);
        setSupportActionBar(mBinding.toolbarHome);

        setClickListener(mBinding.btnSignUp);
        if (getIntent().getIntExtra(ActivityTracker.PRE_ACTIVITY, ActivityTracker.ACTIVITY_DEFAULT) != ActivityTracker.ACTIVITY_DEFAULT) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mRemoteLoginLogoutApiInterface = RemoteApiProvider.getInstance().getRemoteHomeVideoApi();
        validation();
        checkboxFunctionality();
    }

    /**
     * Checkbox functionality
     */
    private void checkboxFunctionality() {
        mBinding.agreementTextMessage.setText(Html.fromHtml(getResources().getString(R.string.sign_up_agree_to_videon)));
        mBinding.policyLink.setText(Html.fromHtml(getResources().getString(R.string.sign_up_privacy_policy)));
        mBinding.policyLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrivacyPolicyActivity.runActivity(SignUpActivity.this);
                overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
            }
        });
    }

    /**
     * Sign up validation and hit to server for sign up
     *
     * @param type            type
     * @param socialId        social id
     * @param username        user name
     * @param email           email
     * @param password        password
     * @param confirmPassword confirmPassword
     */
    private void signUp(int type, String socialId, String username, String email, String password, String confirmPassword) {
        if (!username.isEmpty() || !email.isEmpty() || !password.isEmpty() || !confirmPassword.isEmpty()) {

            if (!mBinding.aggrementCheckbox.isChecked()){
                Toast.makeText(SignUpActivity.this,getResources().getString(R.string.sign_up_accept_privacy_policy) , Toast.LENGTH_LONG).show();
            }
            else if(checkAllCharactersAreSpace(username)){
                mBinding.editName.setError(getResources().getString(R.string.white_space_not_allowed));
            }
            else if (!isEmailValid(email)) {
                mBinding.editEmail.setError(getResources().getString(R.string.sign_up_invalid_email));
            } else if (password.contains(AppConstants.space) || confirmPassword.contains(AppConstants.space)) {
               Toast.makeText(SignUpActivity.this,getResources().getString(R.string.white_space_not_allowed), Toast.LENGTH_LONG).show();
            } else if (password.length() >= AppConstants.one && password.length() <= AppConstants.five) {
                mBinding.editPassword.setError(getResources().getString(R.string.give_min_six_character));
            } else if (password.equals(confirmPassword)) {
                ProgressbarHandler.ShowLoadingProgress(SignUpActivity.this);
                mRemoteLoginLogoutApiInterface.preformRegistrationRequest(type, socialId, username, email, password, ApiToken.GET_TOKEN(getBaseContext())).enqueue(new Callback<AuthenticationModel>() {
                    @Override
                    public void onResponse(Call<AuthenticationModel> call, Response<AuthenticationModel> response) {
                        if (response.isSuccessful()) {
                            AuthenticationModel models = response.body();

                            if (models.getStatusCode()==200) {
                                // clearAll();
                                HashMap<String, String> hashMap = new HashMap<>();
                                hashMap.put(AppConstants.EMAIL, mBinding.editEmail.getText().toString().trim());
                                hashMap.put(AppConstants.PREV_ACTIVITY, "" + AppConstants.SIGN_UP_ACTIVITY);
                                InputPinActivity.runActivity(SignUpActivity.this, hashMap);

                                ProgressbarHandler.DismissProgress(SignUpActivity.this);
                                Toast.makeText(SignUpActivity.this,""+models.getMessage(), Toast.LENGTH_LONG).show();
                            } else {

                                ProgressbarHandler.DismissProgress(SignUpActivity.this);
                                Toast.makeText(SignUpActivity.this,models.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            ProgressbarHandler.DismissProgress(SignUpActivity.this);
                            Toast.makeText(SignUpActivity.this,"Request Failed. Error code:"+response.code(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<AuthenticationModel> call, Throwable t) {
                        Toast.makeText(SignUpActivity.this,""+t.getMessage(), Toast.LENGTH_LONG).show();
                        ProgressbarHandler.DismissProgress(SignUpActivity.this);
                    }
                });

            } else {
                Toast.makeText(SignUpActivity.this,getResources().getString(R.string.password_isnot_mattched), Toast.LENGTH_LONG).show();
            }

        } else {
            mBinding.editName.setError(getResources().getString(R.string.name_cannot_be_empty));
            mBinding.editEmail.setError(getResources().getString(R.string.email_can_not_empty));
            mBinding.editPassword.setError(getResources().getString(R.string.password_can_not_empty));
            mBinding.editConfirmPassword.setError(getResources().getString(R.string.confirm_can_not_empty));
            if (!mBinding.aggrementCheckbox.isChecked()) {
                Toast.makeText(SignUpActivity.this,getResources().getString(R.string.sign_up_accept_privacy_policy), Toast.LENGTH_LONG).show();
            }

        }
    }

    /**
     * Check all characters are white space
     * @param text text
     * @return
     */
    private static boolean checkAllCharactersAreSpace(String text){
        int count =0;
        for (int i = 0; i < text.length() ; i++) {
            if (Character.isWhitespace(text.charAt(i))) {
                count++;
            }
        }

        return text.length() == count;
    }


    /**
     * Sign up form validation
     */
    private void validation() {
        mBinding.editName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() <= AppConstants.zero) {
                    mBinding.editName.setError(getResources().getString(R.string.name_cannot_be_empty));
                    mBinding.btnSignUp.setClickable(false);
                } else {
                    mBinding.editName.setError(null);
                    mBinding.btnSignUp.setClickable(true);
                }
            }
        });


        mBinding.editEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() <= AppConstants.zero) {
                    mBinding.editEmail.setError(getResources().getString(R.string.email_can_not_empty));
                    mBinding.btnSignUp.setClickable(false);
                } else if (!isEmailValid(s.toString())) {
                    mBinding.editEmail.setError(getResources().getString(R.string.sign_up_invalid_email));
                    mBinding.btnSignUp.setClickable(false);
                } else {
                    mBinding.editEmail.setError(null);
                    mBinding.btnSignUp.setClickable(true);
                }
            }
        });


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
                    mBinding.btnSignUp.setClickable(false);
                } else if (s.toString().contains(AppConstants.space)) {
                    mBinding.editPassword.setError(getResources().getString(R.string.space_not_allowed));
                    mBinding.btnSignUp.setClickable(false);
                } else if (s.toString().length() >= AppConstants.one && s.toString().length() <= AppConstants.five) {
                    mBinding.editPassword.setError(getResources().getString(R.string.give_min_six_character));
                    mBinding.btnSignUp.setClickable(false);
                } else {
                    mBinding.editPassword.setError(null);
                    mBinding.btnSignUp.setClickable(true);
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
                    mBinding.btnSignUp.setClickable(false);
                } else {
                    mBinding.editConfirmPassword.setError(null);
                    mBinding.btnSignUp.setClickable(true);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                int preActivity = getIntent().getIntExtra(ActivityTracker.PRE_ACTIVITY, 0);
                if (preActivity == ActivityTracker.ACTIVITY_NO_LOGIN) {
                    overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                } else if (preActivity == ActivityTracker.ACTIVITY_LOGIN) {
                  //  LoginActivity.runActivity(SignUpActivity.this);
                    overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                }
                finish();
                break;

        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.text_sign_in_message:
                LoginActivity.runActivity(SignUpActivity.this);
               // finish();
                break;
            case R.id.btn_sign_up:

                signUp(Enums.EMAIL_LGOIN, "", mBinding.editName.getText().toString().trim(),
                        mBinding.editEmail.getText().toString().trim(), mBinding.editPassword.getText().toString().trim(),
                        mBinding.editConfirmPassword.getText().toString().trim());
                break;
        }
    }


    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        super.onBackPressed();
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
