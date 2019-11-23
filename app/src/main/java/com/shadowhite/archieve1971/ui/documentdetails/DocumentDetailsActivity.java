package com.shadowhite.archieve1971.ui.documentdetails;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;

import com.github.barteksc.pdfviewer.listener.OnRenderListener;
import com.google.gson.Gson;
import com.shadowhite.archieve1971.R;
import com.shadowhite.archieve1971.data.local.document.Datum;
import com.shadowhite.archieve1971.databinding.ActivityDocViewerScrollingBinding;
import com.shadowhite.archieve1971.ui.login.LoginActivity;
import com.shadowhite.util.NetworkURL;
import com.w3engineers.ext.strom.application.ui.base.BaseActivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


public class DocumentDetailsActivity extends BaseActivity  {
    private String url="http://192.168.63.108/1971admin/public/uploads/ddzczkz45tcs8gcs.pdf";

    private ActivityDocViewerScrollingBinding mBinding;

    public static void runActivity(Context context) {
        Intent intent = new Intent(context, DocumentDetailsActivity.class);
        runCurrentActivity(context, intent);
    }
    public static void runActivity(Context context, Datum datum) {
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
