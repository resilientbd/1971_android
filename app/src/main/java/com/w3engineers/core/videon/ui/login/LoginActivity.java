package com.w3engineers.core.videon.ui.login;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.w3engineers.core.util.helper.ActivityTracker;
import com.w3engineers.core.util.helper.ApiToken;
import com.w3engineers.core.util.helper.AppConstants;
import com.w3engineers.core.util.helper.PrefType;
import com.w3engineers.core.util.helper.ProgressbarHandler;
import com.w3engineers.core.util.helper.SharedPref;
import com.w3engineers.core.videon.R;
import com.w3engineers.core.videon.data.local.Enums;
import com.w3engineers.core.videon.data.local.authentication.AuthenticationModel;
import com.w3engineers.core.videon.data.remote.RemoteApiProvider;
import com.w3engineers.core.videon.data.remote.home.RemoteVideoApiInterface;
import com.w3engineers.core.videon.databinding.ActivityLoginBinding;
import com.w3engineers.core.videon.ui.forgotpassword.ForgotPasswordActivity;
import com.w3engineers.core.videon.ui.home.HomeActivity;
import com.w3engineers.core.videon.ui.signup.SignUpActivity;
import com.w3engineers.ext.strom.application.ui.base.BaseActivity;

import org.json.JSONException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends BaseActivity {
    private static final int RC_SIGN_IN = 111;
    private final String TAG = this.getClass().getName();
    GoogleSignInClient mGoogleSignInClient;
    private ActivityLoginBinding mBinding;
    private CallbackManager mCallbackManager;

    private LoginButton facebookLoginButton;

    private GoogleSignInOptions gso;
    private RemoteVideoApiInterface mRemoteLoginLogoutApiInterface;


    public static void runActivity(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        runCurrentActivity(context, intent);
    }

    public static void printHashKey(Context pContext) {
        try {
            PackageInfo info = pContext.getPackageManager().getPackageInfo(pContext.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.d("keyhash", "printHashKey() Hash Key: " + hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.d("keyhash", "printHashKey()", e);
        } catch (Exception e) {
            Log.d("keyhash", "printHashKey()", e);
        }
    }

    public static void runActivity(Context context, int activityTracker) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(ActivityTracker.PRE_ACTIVITY, activityTracker);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_Launcher);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void startUI() {
        FirebaseApp.initializeApp(getApplication().getBaseContext());


        FirebaseMessaging.getInstance().setAutoInitEnabled(true);

        mBinding = (ActivityLoginBinding) getViewDataBinding();
        mBinding.textSignUpMessage.setText(Html.fromHtml(getResources().getString(R.string.have_any_account)));
        mBinding.forgotPassword.setText(Html.fromHtml("<font color=#F95201>"+getResources().getString(R.string.forgot_password)+"</font>"));
        setSupportActionBar(mBinding.toolbarHome);
        //Load Saved data if exists
        retriveSavedCredentials();
        mRemoteLoginLogoutApiInterface = RemoteApiProvider.getInstance().getRemoteHomeVideoApi();
        setClickListener(mBinding.textSignUpMessage, mBinding.btnFacebookLogin, mBinding.btnGmailLogin, mBinding.btnSignIn, mBinding.forgotPassword);
        //initial google sign in
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)

                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        //print yout current hash key
        printHashKey(this);
        // Initialize Facebook Login button
        boolean b= Boolean.parseBoolean(SharedPref.read(LoginActivity.this,SharedPref.IS_REMEMBERED));
        mBinding.checkBox.setChecked(b);
        mCallbackManager = CallbackManager.Factory.create();
        facebookLoginButton = mBinding.coreFacebookLoginBtn;
        facebookLoginButton.setReadPermissions(Arrays.asList("public_profile", "email"));
        facebookLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                User currentuser = new User();
                if (AccessToken.getCurrentAccessToken() != null) {
                    GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                            (jsonObject, response) -> {
                                if (jsonObject != null) {

                                    currentuser.setPassword(null);
                                    currentuser.setType(User.TYPE_FACEBOOK);

                                    if (jsonObject.has(JsonKey.ID)) {
                                        try {
                                            currentuser.setSocialNetworkId(jsonObject.getString(JsonKey.ID));

                                            if (jsonObject.has(JsonKey.NAME)) {
                                                try {
                                                    currentuser.setName(jsonObject.getString(JsonKey.NAME));
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            if (jsonObject.has(JsonKey.EMAIL)) {
                                                try {
                                                    currentuser.setEmail(jsonObject.getString(JsonKey.EMAIL));
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                }
                                mRemoteLoginLogoutApiInterface.preformRegistrationRequest(Enums.FACEBOOK_LOGIN, currentuser.getSocialNetworkId(), ""+currentuser.getName(), ""+currentuser.getEmail(), "", ApiToken.GET_TOKEN(getBaseContext())).enqueue(new Callback<AuthenticationModel>() {
                                    @Override
                                    public void onResponse(Call<AuthenticationModel> call, Response<AuthenticationModel> response) {
                                        AuthenticationModel authenticationModel = response.body();
                                        goToNextActivity(currentuser.getName(), ""+currentuser.getEmail(), "" + authenticationModel.getData().getId(), Enums.FACEBOOK_LOGIN);
                                    }
                                    @Override
                                    public void onFailure(Call<AuthenticationModel> call, Throwable t) {

                                    }
                                });
                            });

                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,name,link,email,picture");
                    request.setParameters(parameters);
                    request.executeAsync();
                }
            }

            @Override
            public void onCancel() {
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                // ...
            }
        });
        checkFacebookSignIn();
        checkGoogleSignIn();
        validation();

    }


    /**
     * Form validation
     */
    private void validation() {
        mBinding.editTextEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() <= AppConstants.zero) {
                    mBinding.editTextEmail.setError(getResources().getString(R.string.email_can_not_empty));
                    mBinding.btnSignIn.setClickable(false);
                } else if (!isEmailValid(s.toString())) {
                    mBinding.editTextEmail.setError(getResources().getString(R.string.sign_up_invalid_email));
                    mBinding.btnSignIn.setClickable(false);
                } else {
                    mBinding.editTextEmail.setError(null);
                    mBinding.btnSignIn.setClickable(true);
                }
            }
        });


        mBinding.editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() <= AppConstants.zero) {
                    mBinding.editTextPassword.setError(getResources().getString(R.string.password_can_not_empty));
                    mBinding.btnSignIn.setClickable(false);
                } else if (s.toString().contains(AppConstants.space)) {
                    mBinding.editTextPassword.setError(getResources().getString(R.string.white_space_not_allowed));
                    mBinding.btnSignIn.setClickable(false);
                } else if (s.toString().length() >= AppConstants.one && s.toString().length() <= AppConstants.five) {
                    mBinding.editTextPassword.setError(getResources().getString(R.string.give_min_six_character));
                    mBinding.btnSignIn.setClickable(false);
                } else {
                    mBinding.editTextPassword.setError(null);
                    mBinding.btnSignIn.setClickable(true);
                }
            }
        });

    }


    /**
     * Check facebook sign in
     */
    public void checkFacebookSignIn() {
        if (!mBinding.coreFacebookLoginBtn.getText().toString().toLowerCase().equals("log out")) {
            mBinding.btnFacebookLogin.setText(getResources().getString(R.string.login_with_facebook_button_main));
        } else {
            mBinding.btnFacebookLogin.setText(getResources().getString(R.string.facebook_sign_out));
        }
    }

    /**
     * Check google sign in
     */
    public void checkGoogleSignIn() {
        if (SharedPref.readInt(PrefType.AUTH_TYPE)!=0){
            if (SharedPref.readInt(PrefType.AUTH_TYPE) == Enums.GOOGLE_LOGIN)
                mBinding.btnGmailLogin.setText("Sign Out");
        }



    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                break;

        }
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);


        if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                final String id = account.getId();
                String password = "";
                String email = account.getEmail();
                Log.d("gtest",""+account.getDisplayName());
                mRemoteLoginLogoutApiInterface.preformRegistrationRequest(Enums.GOOGLE_LOGIN, id, account.getDisplayName(), email, password, ApiToken.GET_TOKEN(getBaseContext())).enqueue(new Callback<AuthenticationModel>() {
                    @Override
                    public void onResponse(Call<AuthenticationModel> call, Response<AuthenticationModel> response) {
                        AuthenticationModel authenticationModel = response.body();
                        goToNextActivity(account.getDisplayName(), email, "" + authenticationModel.getData().getId(), Enums.GOOGLE_LOGIN);
                    }

                    @Override
                    public void onFailure(Call<AuthenticationModel> call, Throwable t) {

                    }
                });

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }


    /**
     * Get current activity
     *
     * @return activity tracker
     */
    private int getCurrentActivity() {
        return ActivityTracker.ACTIVITY_LOGIN;
    }

    /**
     * Sign in with email and password
     *
     * @param email    email
     * @param password password
     */
    private void mainSignIn(String email, String password) {
        if (!email.isEmpty() || !password.isEmpty()) {
            if (!isEmailValid(email)) {
                mBinding.editTextEmail.setError(getResources().getString(R.string.sign_up_invalid_email));
            } else if (password.contains(AppConstants.space)) {
                mBinding.editTextPassword.setError(getResources().getString(R.string.space_not_allowed));
            } else if (password.length() >= AppConstants.one && password.length() <= AppConstants.five) {
                mBinding.editTextPassword.setError(getResources().getString(R.string.give_min_six_character));
            } else {
                if (mBinding.checkBox.isChecked()) {
                    saveCredentials(mBinding.editTextEmail.getText().toString().trim(), mBinding.editTextPassword.getText().toString().trim());
                }
                ProgressbarHandler.ShowLoadingProgress(LoginActivity.this);
                Log.d("checklogin", "loginstring" + "password:");
                mRemoteLoginLogoutApiInterface.preformLoginRequest(email, password, ApiToken.GET_TOKEN(getBaseContext())).enqueue(new Callback<AuthenticationModel>() {
                    @Override
                    public void onResponse(Call<AuthenticationModel> call, Response<AuthenticationModel> response) {
                        if (response.isSuccessful()) {
                            AuthenticationModel models = response.body();
                            if (models.getStatusCode()==200) {
                                Log.d("checklogin", "" + models.toString());
                                Toast.makeText(getBaseContext(),models.getMessage(), Toast.LENGTH_LONG).show();
                                goToNextActivity(models.getData().getUsername(), email, "" + models.getData().getId(), Enums.EMAIL_LGOIN);
//                                Snackbar.make(getWindow().getDecorView(), models.getMessage(), Snackbar.LENGTH_LONG).show();

                            } else {
                               // Toaster.showLong(""+models.getMessage());
                                Toast.makeText(getBaseContext(),models.getMessage(), Toast.LENGTH_LONG).show();
                                ProgressbarHandler.DismissProgress(LoginActivity.this);

                            }
                        } else {
                            ProgressbarHandler.DismissProgress(LoginActivity.this);
                        }
                    }

                    @Override
                    public void onFailure(Call<AuthenticationModel> call, Throwable t) {
                        ProgressbarHandler.DismissProgress(LoginActivity.this);
                    }
                });
            }

        } else {
            mBinding.editTextEmail.setError(getResources().getString(R.string.email_can_not_empty));
            mBinding.editTextPassword.setError(getResources().getString(R.string.password_can_not_empty));
        }

    }

    //Retrive saved data from shared preference
    private void retriveSavedCredentials() {
        mBinding.editTextEmail.setText(SharedPref.read(getBaseContext(), SharedPref.USER_ID));
        mBinding.editTextPassword.setText(SharedPref.read(getBaseContext(), SharedPref.PASSWORD));
    }

    //Save data to shared preference
    private void saveCredentials(String userid, String password) {
        SharedPref.write(getBaseContext(), SharedPref.USER_ID, userid);
        SharedPref.write(getBaseContext(), SharedPref.PASSWORD, password);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.text_sign_up_message:
                SignUpActivity.runActivity(LoginActivity.this, getCurrentActivity());
                overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right);
               // finish();
                break;
            case R.id.btn_facebook_login:
                if (!mBinding.coreFacebookLoginBtn.getText().toString().toLowerCase().equals("log out"))
                    facebookLoginButton.performClick();
                else {

                    LoginManager.getInstance().logOut();
                    mBinding.btnFacebookLogin.setText(getResources().getString(R.string.facebook_login_text));
                    SharedPref.write(PrefType.AUTH_TYPE, PrefType.FACEBOOK_AUTH);
                    SharedPref.write(PrefType.AUTH_STATUS, PrefType.STATUS_LOGOUT);

                }
                break;
            case R.id.btn_sign_in:

                mainSignIn(mBinding.editTextEmail.getText().toString().trim(), mBinding.editTextPassword.getText().toString().trim());

                break;
            case R.id.btn_gmail_login:
                // Configure Google Sign In
                if (SharedPref.readBoolean(PrefType.AUTH_STATUS) == PrefType.STATUS_SUCCESS && (SharedPref.read(PrefType.AUTH_TYPE).equals(PrefType.GOOGLE_AUTH))) {

                    revokeAccess();
                    LoginManager.getInstance().logOut();
                    SharedPref.clear();
                    mBinding.btnGmailLogin.setText(getResources().getString(R.string.google_login_text));
                } else {

                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                }
                break;
            case R.id.forgotPassword:
                ForgotPasswordActivity.runActivity(this);
                break;

        }
    }

    /*
     * Check login or not
     * */
    private void revokeAccess() {
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
    }

    /**
     * Go to next activity
     *
     * @param name     name
     * @param email    email
     * @param reg_id   reg_id
     * @param authType authType
     */
    private void goToNextActivity(String name, String email, String reg_id, int authType) {
        SharedPref.init(this);

        SharedPref.write(PrefType.AUTH_TYPE, authType);
        SharedPref.write(PrefType.AUTH_NAME, name);
        SharedPref.write(LoginActivity.this,SharedPref.IS_REMEMBERED,""+mBinding.checkBox.isChecked());
        SharedPref.write(PrefType.AUTH_STATUS, PrefType.STATUS_SUCCESS);
        SharedPref.write(PrefType.USER_REGID, reg_id);
        SharedPref.write(PrefType.USER_EMAIL, email);
        Intent intent = new Intent(this, HomeActivity.class);
        //delete all top activities
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.enter, R.anim.exit);
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
