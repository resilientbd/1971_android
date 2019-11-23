package com.shadowhite.util.helper;

import android.content.Context;

import com.facebook.login.LoginManager;

public class MySessionManager {
    public MySessionManager() {

    }

    /**
     * @param context          context of the activity
     * @param callBackListener for complete facebook logout
     */
    public static void facebookLogOut(Context context, CallBackListener callBackListener) {
        LoginManager.getInstance().logOut();
        clearSession(context);
        callBackListener.complete(PrefType.STATUS_SUCCESS);

    }

    /**
     * @param context   context of the activity
     * @param mCallBack for complete google logout
     */
    public void googleLogOut(Context context, CallBackListener mCallBack) {

        clearSession(context);
        mCallBack.complete(true);
    }

    /**
     * @param context          context of the activity
     * @param callBackListener for email logout
     */
    public static void emailLogOut(Context context, CallBackListener callBackListener) {
        SharedPref.init(context);
        SharedPref.clear();
        callBackListener.complete(PrefType.STATUS_SUCCESS);
    }

    /**
     * notify the activity is log out done
     */
    public interface CallBackListener {
        void complete(boolean isSuccess);
    }

    /**
     * clear the entire session after logout
     *
     * @param context context of the activity
     */


    public static void clearSession(Context context) {
        SharedPref.init(context);
        SharedPref.clear();
    }
}
