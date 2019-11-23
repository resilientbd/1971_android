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
import com.google.gson.Gson;
import com.w3engineers.core.util.NetworkURL;
import com.w3engineers.core.util.helper.AppConstants;
import com.w3engineers.core.util.helper.MySessionManager;
import com.w3engineers.core.util.helper.PrefType;
import com.w3engineers.core.util.helper.SharedPref;
import com.w3engineers.core.videon.R;
import com.w3engineers.core.videon.data.local.Enums;
import com.w3engineers.core.videon.data.local.document.Datum;
import com.w3engineers.core.videon.databinding.ActivityDocViewerBinding;
import com.w3engineers.core.videon.databinding.ActivityDocViewerScrollingBinding;
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
    private String url="http://192.168.63.108/1971admin/public/uploads/ddzczkz45tcs8gcs.pdf";

    private ActivityDocViewerScrollingBinding mBinding;

    public static void runActivity(Context context) {
        Intent intent = new Intent(context, DocumentDetailsActivity.class);
        runCurrentActivity(context, intent);
    }
    public static void runActivity(Context context,Datum datum) {
        Intent intent = new Intent(context, DocumentDetailsActivity.class);
        Gson gson=new Gson();

        intent.putExtra("document",gson.toJson(datum));
        runCurrentActivity(context, intent);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_doc_viewer_scrolling;
    }

    @Override
    protected void startUI() {

        mBinding=(ActivityDocViewerScrollingBinding)getViewDataBinding();
        Gson gson=new Gson();
        try {
            String objGson = getIntent().getStringExtra("document");
            Datum doc = gson.fromJson(objGson, Datum.class);
            url = NetworkURL.videosEndPointURL + doc.getDocFileUrl();
            mBinding.doctitle.setText(""+doc.getDocTitle());
            mBinding.subtitle.setText(""+doc.getDocAuthor());
        }catch (Exception e)
        {

        }
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
//    mBinding.icBack.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            onBackPressed();
//        }
//    });
    mBinding.sharebtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            sharecontent();
        }
    });
    }


    void sharecontent()
    {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "Sharable body...";
        String shareSub = "Sharable Subject";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSub);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share using"));
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