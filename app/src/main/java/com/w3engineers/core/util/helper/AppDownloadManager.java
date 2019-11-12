package com.w3engineers.core.util.helper;
/*
DownloadManager manager = (android.app.DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
@Param DownloadManager manager  should inititate with above method from activity to call download method.
 */

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;

import com.w3engineers.core.videon.ui.videodetails.VideoDetailsActivity;

public class AppDownloadManager {

    public static void download(String downloadUrl, String title, String filename, Context context) {

        Uri uri = Uri.parse(downloadUrl);


            android.app.DownloadManager.Request request = new android.app.DownloadManager.Request(uri);
            request.setTitle(title);

            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir("Videon", filename);


            try {
                DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                manager.enqueue(request);

                VideoDetailsActivity activity = (VideoDetailsActivity) context;
                activity.downloadClick();
            } catch (Exception e) {
              //  activity.downloadError();
            }
        }



    }

