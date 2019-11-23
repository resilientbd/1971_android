package com.shadowhite.archieve1971.ui.setting;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.shadowhite.archieve1971.R;
import com.shadowhite.archieve1971.data.local.Enums;
import com.shadowhite.archieve1971.data.local.downloadsetting.DownloadSetting;
import com.shadowhite.archieve1971.data.local.downloadsetting.DownloadSettingResponse;
import com.shadowhite.archieve1971.data.remote.RemoteApiProvider;
import com.shadowhite.archieve1971.data.remote.home.RemoteVideoApiInterface;
import com.shadowhite.archieve1971.databinding.ActivitySettingBinding;
import com.shadowhite.archieve1971.ui.aboutus.AboutUsActivity;
import com.shadowhite.archieve1971.ui.empty.EmptyActivity;
import com.shadowhite.archieve1971.ui.login.LoginActivity;
import com.shadowhite.archieve1971.ui.privacypolicy.PrivacyPolicyActivity;
import com.shadowhite.util.LocaleUtils;
import com.shadowhite.util.helper.ApiToken;
import com.shadowhite.util.helper.AppConstants;
import com.shadowhite.util.helper.CopyDataHelper;
import com.shadowhite.util.helper.MySessionManager;
import com.shadowhite.util.helper.PrefType;
import com.shadowhite.util.helper.PreferenceKey;
import com.shadowhite.util.helper.SharedPref;
import com.w3engineers.ext.strom.application.ui.base.BaseActivity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class
SettingActivity extends BaseActivity implements MySessionManager.CallBackListener {
    private SharedPreferences preferences;
    private ActivitySettingBinding mBinding;
    private boolean isNotFirstOpen;
    private RemoteVideoApiInterface mRemoteVideoApiInterface;
    private int mPosition;


    public static void runActivity(Context context) {
        Intent intent = new Intent(context, SettingActivity.class);
        runCurrentActivity(context, intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void startUI() {
        mBinding = (ActivitySettingBinding) getViewDataBinding();
        setSupportActionBar(mBinding.toolbarHome);
        mRemoteVideoApiInterface = RemoteApiProvider.getInstance().getRemoteHomeVideoApi();
        setClickListener(mBinding.constraintLayoutAboutUsSection, mBinding.constraintLayoutPrivacyPolicySection, mBinding.constraintLayoutLogoutSection, mBinding.switchNotification, mBinding.buttonCopy);
        mBinding.switchNotification.setChecked(SharedPref.readBoolean(PreferenceKey.NOTIFICATION_STATUS));
        checkLoginStateAndChangeText();
        langChangeListener();
        getSettingOfFaceBookHashKeyFromServer();
    }

    /**
     * Get Setting on off FaceBook HashKeyFromServer,
     * DownloadSettingResponse class have the hash setting.
     */
    private void getSettingOfFaceBookHashKeyFromServer(){

        mRemoteVideoApiInterface.downLoadSetting(ApiToken.GET_TOKEN(getBaseContext())).enqueue(new Callback<DownloadSettingResponse>() {
            @Override
            public void onResponse(Call<DownloadSettingResponse> call, Response<DownloadSettingResponse> response) {
                if (response.isSuccessful()){
                    DownloadSettingResponse downloadSettingResponse=response.body();
                    if (downloadSettingResponse.getStatusCode().equals(AppConstants.SUCCESS)){
                        DownloadSetting downloadSettingOnOffData =downloadSettingResponse.getDownloadSetting();
                        if (downloadSettingOnOffData !=null){
                            String hashKeySetting=downloadSettingOnOffData.getSocialLoginCredentialsFaceBook();
                            if (hashKeySetting.equals(AppConstants.FACEBOOK_HASH_KEY_ON)){
                                displayFacebookHashKey(true);
                            }else {
                                displayFacebookHashKey(false);
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<DownloadSettingResponse> call, Throwable t) {

            }
        });
    }

    /**
     * change the language
     */
    public void langChangeListener() {
        Spinner spinner = mBinding.spinnerChangeLanguage;
        String lan = readSharedPref(SharedPref.LAN);
        if (lan.equals("")) {
            saveSharedPref(SharedPref.LAN, Enums.ENGLISH);
            LocaleUtils.initialize(this,LocaleUtils.ENGLISH);
            updateViews();
        } else {
            if (lan.equals(Enums.CHINESE)) {
                spinner.setSelection(1);
                LocaleUtils.initialize(this,LocaleUtils.CHINESE);
                updateViews();
            }else {
                LocaleUtils.initialize(this,LocaleUtils.ENGLISH);
                updateViews();
            }
        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isNotFirstOpen) {
                    if (position == 0) {
                        LocaleUtils.initialize(SettingActivity.this,LocaleUtils.ENGLISH);
                        saveSharedPref(SharedPref.LAN, Enums.ENGLISH);
                        Toast.makeText(SettingActivity.this, getResources().getString(R.string.language_change_notification), Toast.LENGTH_LONG);
                        updateViews();
                    } else if (position == 1) {
                        LocaleUtils.initialize(SettingActivity.this,LocaleUtils.CHINESE);
                        saveSharedPref(SharedPref.LAN, Enums.CHINESE);
                        Toast.makeText(SettingActivity.this, getResources().getString(R.string.language_change_notification), Toast.LENGTH_LONG);
                        updateViews();
                    }
                } else {
                    isNotFirstOpen = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }


    /**
     * purpose save value in shared preference
     *
     * @param key   sharedpreference key
     * @param value sharedpreference value
     */
    public void saveSharedPref(String key, String value) {
        if (preferences == null) {
            preferences = getSharedPreferences("local", Context.MODE_PRIVATE);
        }
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();

    }

    /**
     *
     * @param key desire string value
     * @return value of the preference
     */
    public String readSharedPref(String key) {
        if (preferences == null) {
            preferences = getSharedPreferences("local", Context.MODE_PRIVATE);
        }
        return preferences.getString(key, "");
    }

    /**
     * Check login state and change text
     */
    private void checkLoginStateAndChangeText() {
        if (!SharedPref.read(PrefType.USER_REGID).equals("")) {
            //user is logged in now
            mBinding.textViewLogout.setText(getResources().getString(R.string.setting_sign_out));

        } else {
            mBinding.textViewLogout.setText(getResources().getString(R.string.setting_sign_out));

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.constraint_layout_about_us_section:
                AboutUsActivity.runActivity(this);
                break;
            case R.id.constraint_layout_privacy_policy_section:
                PrivacyPolicyActivity.runActivity(this);
                break;
            case R.id.constraint_layout_logout_section:
                if (!SharedPref.read(PrefType.USER_REGID).equals("")) {
                    //login state
                    alertDialog();
                } else {
                    gotToEmptyActivity();
                }
                break;
            case R.id.switch_notification:
                notificationControl();
                break;
            case R.id.buttonCopy:
                String data = mBinding.editKeyHash.getText().toString().trim();
                if (!data.isEmpty()) {
                    CopyDataHelper.copyText(this, data);
                   Toast.makeText(this, getResources().getString(R.string.data_copy_message), Toast.LENGTH_LONG).show();
                }
                break;

        }
    }

    /**
     * Go to empty activity
     */
    public void gotToEmptyActivity() {
        EmptyActivity.runActivity(this);
    }


    /**
     * Log out current user
     *
     * @param callBackListener call back listener
     */
    private void revokeAccess(MySessionManager.CallBackListener callBackListener) {

        if (SharedPref.readInt(PrefType.AUTH_TYPE) == Enums.GOOGLE_LOGIN) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)

                    .requestEmail()
                    .build();
            GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

            mGoogleSignInClient.revokeAccess()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            new MySessionManager().googleLogOut(getBaseContext(), callBackListener);

                        }
                    });
        } else if (SharedPref.readInt(PrefType.AUTH_TYPE) == Enums.FACEBOOK_LOGIN) {
            LoginManager.getInstance().logOut();
            new MySessionManager().facebookLogOut(getBaseContext(), callBackListener);
        } else if (SharedPref.readInt(PrefType.AUTH_TYPE) == Enums.EMAIL_LGOIN) {
            new MySessionManager().emailLogOut(getBaseContext(), callBackListener);
        }
    }

    @Override
    public void complete(boolean isSuccess) {
        LoginActivity.runActivity(SettingActivity.this);
        finish();
        overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right);

    }

    /**
     * Notification control
     */
    void notificationControl() {
        if (SharedPref.readBoolean(PreferenceKey.NOTIFICATION_STATUS)) {
            SharedPref.write(PreferenceKey.NOTIFICATION_STATUS, false);
        } else {
            SharedPref.write(PreferenceKey.NOTIFICATION_STATUS, true);
        }
    }

    /***
     * Alert Dialog
     */
    private void alertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Confirm");
        builder.setMessage("Are you want to Logout?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                try {
                    revokeAccess(SettingActivity.this);
                } catch (Exception e) {

                }
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }


    /**
     * Display hash key
     * @param value true or false
     */
    private void displayFacebookHashKey(boolean value) {
        if (value) {
            mBinding.constraintLayoutConfigurationSection.setVisibility(View.VISIBLE);
            printHashKey(this);
        }



    }

    /**
     * Facebook hash key print
     * @param pContext context
     */
    public void printHashKey(Context pContext) {
        try {
            PackageInfo info = pContext.getPackageManager().getPackageInfo(pContext.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.d("keyhash", "printHashKey() Hash Key: " + hashKey);
                Log.d("keyhash", "printHashKey() Sha1 Key1: " + md.toString());

                mBinding.editKeyHash.setText("" + hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.d("keyhash", "printHashKey()", e);
        } catch (Exception e) {
            Log.d("keyhash", "printHashKey()", e);
        }
    }

    /**
     * Update view by text
     */
    private void updateViews(){
        mBinding.textViewPushNotification.setText(getResources().getString(R.string.setting_push_notification));
        mBinding.textViewAboutUs.setText(getResources().getString(R.string.setting_text_about_us));
        mBinding.textViewPrivacyPolicy.setText(getResources().getString(R.string.setting_privacy_policy));
        mBinding.textViewLogout.setText(getResources().getString(R.string.setting_sign_out));
        mBinding.toolbarHome.setTitle(getResources().getString(R.string.Setting_text));

    }


}
