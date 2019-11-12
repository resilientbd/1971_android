package com.w3engineers.core.videon.ui.editprofile;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.w3engineers.core.util.helper.AdHelper;
import com.w3engineers.core.util.helper.ApiToken;
import com.w3engineers.core.util.helper.AppConstants;
import com.w3engineers.core.util.helper.PrefType;
import com.w3engineers.core.util.helper.SharedPref;
import com.w3engineers.core.util.helper.TransactionHelper;
import com.w3engineers.core.videon.R;
import com.w3engineers.core.videon.data.local.authentication.AuthenticationModel;
import com.w3engineers.core.videon.data.local.authentication.Data;
import com.w3engineers.core.videon.data.remote.RemoteApiProvider;
import com.w3engineers.core.videon.data.remote.myprofileedit.RemoteMyProfileEditRequestApi;
import com.w3engineers.core.videon.databinding.ActivityEditProfileBinding;
import com.w3engineers.ext.strom.application.ui.base.BaseActivity;
import com.w3engineers.ext.strom.util.helper.Toaster;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class EditProfileActivity extends BaseActivity {
    private ActivityEditProfileBinding mBinding;
    RemoteMyProfileEditRequestApi mRemoteMyProfileEditRequestApi;
    String userId;


    public static void runActivity(Context context) {
        Intent intent = new Intent(context, EditProfileActivity.class);
        runCurrentActivity(context, intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit_profile;
    }

    @Override
    protected void startUI() {
        mBinding = (ActivityEditProfileBinding) getViewDataBinding();
        mRemoteMyProfileEditRequestApi = RemoteApiProvider.getInstance().getRemoteMyProfileApi();
        AdHelper.loadBannerAd(this, (LinearLayout) mBinding.buttonLayout);
        setSupportActionBar(mBinding.toolbarHome);
        setClickListener(mBinding.buttonUpdate);
        setPersonalInformation();
        userId = SharedPref.read(PrefType.USER_REGID);
        validation();
    }


    /**
     * validation
     */
    private void validation() {
        mBinding.baseEditTextUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //Regex pattern for matching only alphabets and white spaces:

                if (s.length() <= AppConstants.zero) {
                    mBinding.baseEditTextUsername.setError(getResources().getString(R.string.name_cannot_be_empty));
                    mBinding.buttonUpdate.setClickable(false);
                } else {
                    mBinding.buttonUpdate.setClickable(true);
                    mBinding.baseEditTextUsername.setError(null);
                }
            }
        });


        mBinding.baseEditTextChangePassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() <= AppConstants.zero) {
                    mBinding.baseEditTextChangePassword.setError(getResources().getString(R.string.password_can_not_empty));
                    mBinding.buttonUpdate.setClickable(false);
                } else if (s.toString().contains(AppConstants.space)) {
                    mBinding.baseEditTextChangePassword.setError(getResources().getString(R.string.white_space_not_allowed));
                    mBinding.buttonUpdate.setClickable(false);
                } else if (s.toString().length() >= AppConstants.one && s.toString().length() <= AppConstants.five) {
                    mBinding.baseEditTextChangePassword.setError(getResources().getString(R.string.give_min_six_character));
                    mBinding.buttonUpdate.setClickable(false);
                } else {
                    mBinding.baseEditTextChangePassword.setError(null);
                    mBinding.buttonUpdate.setClickable(true);
                }
            }
        });


        mBinding.baseEditTextOldPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() <= AppConstants.zero) {
                    mBinding.baseEditTextOldPassword.setError(getResources().getString(R.string.confirm_can_not_empty));
                    mBinding.buttonUpdate.setClickable(false);
                } else {
                    mBinding.baseEditTextOldPassword.setError(null);
                    mBinding.buttonUpdate.setClickable(true);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_update:
                updateProfileInfo();
                break;
        }
    }

    /**
     * show personal information for edit
     */
    private void setPersonalInformation() {
        String userName = SharedPref.read(PrefType.AUTH_NAME);
        mBinding.baseEditTextUsername.setText(userName);
        mBinding.baseEditTextUsername.setSelection(userName.length());
    }

    /**
     * Update profile info
     */
    private void updateProfileInfo() {
        String userName = mBinding.baseEditTextUsername.getText().toString().trim();
        String oldPassword = mBinding.baseEditTextOldPassword.getText().toString().trim();
        String newPassword = mBinding.baseEditTextChangePassword.getText().toString().trim();

        //all are empty
        if (userName.isEmpty() && newPassword.isEmpty() && oldPassword.isEmpty()) {
            Toaster.showLong(getResources().getString(R.string.edit_profile_did_not_update_any_things));
        }else if (!userName.isEmpty() && newPassword.isEmpty() && oldPassword.isEmpty()){
            if (checkAllCharactersAreSpace(userName)) {
                mBinding.baseEditTextUsername.setError(getResources().getString(R.string.white_space_not_allowed));
            } else {
                //hit to server
                hitToServerForUpdateUserName(userId, userName);
            }

        }
        else if (!newPassword.isEmpty() && !oldPassword.isEmpty() && !userName.isEmpty()) {

            if (checkAllCharactersAreSpace(userName)) {
                mBinding.baseEditTextUsername.setError(getResources().getString(R.string.white_space_not_allowed));
            } else if (newPassword.contains(AppConstants.space) || oldPassword.contains(AppConstants.space)) {
                Toaster.showLong(getResources().getString(R.string.white_space_not_allowed));
            } else if (newPassword.length() >= AppConstants.one && newPassword.length() <= AppConstants.five) {
                mBinding.baseEditTextChangePassword.setError(getResources().getString(R.string.give_min_six_character));
            } else {
                //hit to server
                hitToServer(Integer.parseInt(userId), userName, oldPassword, newPassword);
            }

        } else {
            Toaster.showLong(getResources().getString(R.string.please_update_all_fields));
        }
    }

    /**
     * Hit to server
     *
     * @param userId      userId
     * @param userName    user name
     */
    private void hitToServer(int userId, String userName, String oldPassword, String newPassword) {
        mRemoteMyProfileEditRequestApi.preformMyProfileEditRequest(userId, userName, oldPassword, newPassword, ApiToken.GET_TOKEN(getBaseContext()))
                .enqueue(new Callback<AuthenticationModel>() {
                    @Override
                    public void onResponse(Call<AuthenticationModel> call, Response<AuthenticationModel> response) {
                        if (response.isSuccessful()) {
                            AuthenticationModel authenticationModel = response.body();
                            Data myProfileModel = authenticationModel.getData();
                            updateInformation(myProfileModel);
                            Toast.makeText(EditProfileActivity.this, "" + response.body().getMessage(), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(EditProfileActivity.this, "" + response.body().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<AuthenticationModel> call, Throwable t) {
                        Toaster.showLong(getResources().getString(R.string.edit_profile_update_failed));
                    }
                });
    }

    /**
     * Hit to server
     *
     * @param userId      userId
     * @param userName    user name
     */
    private void hitToServerForUpdateUserName(String userId, String userName) {
        mRemoteMyProfileEditRequestApi.preformMyProfileEditForUserNameRequest(ApiToken.GET_TOKEN(getBaseContext()),userId, userName)
                .enqueue(new Callback<AuthenticationModel>() {
                    @Override
                    public void onResponse(Call<AuthenticationModel> call, Response<AuthenticationModel> response) {
                        if (response.isSuccessful()) {
                            AuthenticationModel authenticationModel = response.body();
                            Data myProfileModel = authenticationModel.getData();

                            updateInformation(myProfileModel);

                            Toast.makeText(EditProfileActivity.this, "" + response.body().getMessage(), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(EditProfileActivity.this, "" + response.body().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<AuthenticationModel> call, Throwable t) {
                        Toaster.showLong(getResources().getString(R.string.edit_profile_update_failed));
                    }
                });
    }

    /**
     * update profile
     *
     * @param myProfileModel MyProfileModel
     */
    private void updateInformation(Data myProfileModel) {
        if (myProfileModel.getUsername() !=null){
            SharedPref.write(PrefType.AUTH_NAME, myProfileModel.getUsername());
        }
        TransactionHelper.TransactionRightToLeft(EditProfileActivity.this);
        finish();

    }


    /**
     * Check all characters are white space
     *
     * @param text username
     * @return true or false
     */
    private static boolean checkAllCharactersAreSpace(String text) {
        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            if (Character.isWhitespace(text.charAt(i))) {
                count++;
            }
        }

        return text.length() == count;
    }


}
