package com.w3engineers.core.util.helper;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class CheckNetworkAvailabilityAndPermission {

    String[] permissions = new String[]{
            Manifest.permission.ACCESS_NETWORK_STATE
    };

    ///Checking network availability
    public boolean checkIfHasNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        boolean b = networkInfo != null && networkInfo.isConnected();
        if (!b)
            Toast.makeText(context, "Please turn on your Wifi or Mobile data .", Toast.LENGTH_LONG).show();
        return b;
    }


    ///Checking permission
    public boolean checkPermissions(Context context) {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(context, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions((Activity) context, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 100);
            return false;
        }
        return true;
    }
}
