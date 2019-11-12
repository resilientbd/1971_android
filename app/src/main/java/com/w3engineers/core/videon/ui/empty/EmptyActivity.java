package com.w3engineers.core.videon.ui.empty;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.w3engineers.core.util.helper.AppConstants;
import com.w3engineers.core.util.helper.MySessionManager;
import com.w3engineers.core.util.helper.PrefType;
import com.w3engineers.core.util.helper.SharedPref;
import com.w3engineers.core.videon.R;
import com.w3engineers.core.videon.data.local.Enums;
import com.w3engineers.core.videon.databinding.ActivityEmptyBinding;
import com.w3engineers.core.videon.ui.login.LoginActivity;
import com.w3engineers.core.videon.ui.myprofile.MyProfileActivity;
import com.w3engineers.core.videon.ui.searchmovies.SearchMoviesActivity;
import com.w3engineers.core.videon.ui.setting.SettingActivity;
import com.w3engineers.ext.strom.application.ui.base.BaseActivity;
import com.w3engineers.ext.strom.util.helper.Toaster;


public class EmptyActivity extends BaseActivity implements  TextView.OnEditorActionListener,
        MySessionManager.CallBackListener{

    private ActivityEmptyBinding mBinding;

    public static void runActivity(Context context) {
        Intent intent = new Intent(context, EmptyActivity.class);
        runCurrentActivity(context, intent);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_empty;
    }

    @Override
    protected void startUI() {
         mBinding=(ActivityEmptyBinding)getViewDataBinding();
         setSupportActionBar(mBinding.toolbarHome);
         getSupportActionBar().setDisplayShowTitleEnabled(false);
         setClickListener(mBinding.buttonLogin);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
           String searchText=mBinding.editTextSearch.getText().toString().trim();
           if (searchText.length()> AppConstants.zero){
             SearchMoviesActivity.runActivity(this,searchText);
           }
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_login:
                LoginActivity.runActivity(EmptyActivity.this);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // SharedPref.init(getBaseContext());

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        String loginText = "";
        SharedPref.init(getBaseContext());
        if (SharedPref.readBoolean(PrefType.AUTH_STATUS) == PrefType.STATUS_SUCCESS)
            loginText = getResources().getString(R.string.log_out);
        else loginText = getResources().getString(R.string.login);
        menu.findItem(R.id.action_login).setTitle(loginText);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_profile:
                if (!SharedPref.read(PrefType.USER_REGID).equals("")){
                    MyProfileActivity.runActivity(this);
                }else {
                    checkLogin();
                }
                return true;
            case R.id.action_settings:
                if (!SharedPref.read(PrefType.USER_REGID).equals("")){
                    SettingActivity.runActivity(this);
                }else {
                    checkLogin();
                }

                return true;
            case R.id.action_login:
                try {
                    revokeAccess(this);
                } catch (Exception e) {
                    Log.d("mytag", "Error: " + e);
                }
                return true;
            case R.id.action_playlist:
                if (!SharedPref.read(PrefType.USER_REGID).equals("")){
                    MyProfileActivity.runActivity(this);
                }else {
                    checkLogin();
                }
                return true;

            case  android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /*
      Run the empty activity
     */
    public void checkLogin()
    {
        LoginActivity.runActivity(EmptyActivity.this);
    }

    /*
      Check the login or not
    */
    private void revokeAccess(MySessionManager.CallBackListener callBackListener) {

        if (SharedPref.readInt(PrefType.AUTH_TYPE) == Enums.GOOGLE_LOGIN) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)

                    .requestEmail()
                    .build();
            GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
            mGoogleSignInClient.asGoogleApiClient().clearDefaultAccountAndReconnect();
            mGoogleSignInClient.revokeAccess()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // ...
                            new MySessionManager().googleLogOut(getBaseContext(), callBackListener);

                        }
                    });
        } else if (SharedPref.readInt(PrefType.AUTH_TYPE) == Enums.FACEBOOK_LOGIN) {
            LoginManager.getInstance().logOut();
            new MySessionManager().facebookLogOut(getBaseContext(), callBackListener);
        } else if (SharedPref.readInt(PrefType.AUTH_TYPE) == Enums.EMAIL_LGOIN) {
            new MySessionManager().emailLogOut(getBaseContext(), callBackListener);
        } else {
            Log.d("mytag", "" + SharedPref.read(PrefType.AUTH_TYPE));
            LoginActivity.runActivity(this);
            finish();
            overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right);
        }
    }

    @Override
    public void complete(boolean isSuccess) {
        Toaster.showLong("LogOutSuccess");
        LoginActivity.runActivity(this);
        finish();
    }
}
