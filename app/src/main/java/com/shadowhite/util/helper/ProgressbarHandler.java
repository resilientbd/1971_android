package com.shadowhite.util.helper;

import android.app.ProgressDialog;
import android.content.Context;

public class ProgressbarHandler {
    static ProgressDialog progressDialog;

    /**
     * Show dailog progress loading
     * @param context context
     */
    public static void ShowLoadingProgress(Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    /**
     * Dismiss progress of loading
     * @param context context
     */
    public static void DismissProgress(Context context)
    {
        if(progressDialog==null)
        {
            progressDialog=new ProgressDialog(context);
        }
        if(progressDialog.isShowing())
        progressDialog.dismiss();
    }
}
