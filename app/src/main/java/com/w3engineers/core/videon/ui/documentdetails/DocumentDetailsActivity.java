package com.w3engineers.core.videon.ui.documentdetails;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
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
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnRenderListener;
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
import com.w3engineers.core.videon.databinding.ActivityDocViewerBinding;
import com.w3engineers.core.videon.databinding.ActivityEmptyBinding;
import com.w3engineers.core.videon.ui.login.LoginActivity;
import com.w3engineers.core.videon.ui.myprofile.MyProfileActivity;
import com.w3engineers.core.videon.ui.searchmovies.SearchMoviesActivity;
import com.w3engineers.core.videon.ui.setting.SettingActivity;
import com.w3engineers.ext.strom.application.ui.base.BaseActivity;
import com.w3engineers.ext.strom.util.helper.Toaster;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;


public class DocumentDetailsActivity extends BaseActivity  {
    private String url="http://glazeitsolutions.com/admin/public/uploads/p6f8qlaer7k4ckgk.pdf";

    private ActivityDocViewerBinding mBinding;

    public static void runActivity(Context context) {
        Intent intent = new Intent(context, DocumentDetailsActivity.class);
        runCurrentActivity(context, intent);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_doc_viewer;
    }

    @Override
    protected void startUI() {

        mBinding=(ActivityDocViewerBinding)getViewDataBinding();

            Uri uri=Uri.parse(url);

           // mBinding.pdfview.fromSource(url).load();
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    InputStream input = new URL(url).openStream();
                    mBinding.pdfview.fromStream(input).onRender(new OnRenderListener() {
                        @Override
                        public void onInitiallyRendered(int nbPages, float pageWidth, float pageHeight) {
                            mBinding.pdfview.fitToWidth();
                        }
                    }).load();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();

    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_login:
                LoginActivity.runActivity(DocumentDetailsActivity.this);
                break;
        }
    }







}
