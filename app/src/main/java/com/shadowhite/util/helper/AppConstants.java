package com.shadowhite.util.helper;

import android.os.Environment;

import java.io.File;

public interface AppConstants {

    // Directory
    String DIRECTORY_EXTERNAL_STORAGE =
            Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
    String DIRECTORY_ROOT = DIRECTORY_EXTERNAL_STORAGE + "/VideOn";
    String DIRECTORY_IMAGE = DIRECTORY_EXTERNAL_STORAGE + DIRECTORY_ROOT + "/Image/";

    // Prefix
    String PREFIX_IMAGE = "VIDEON_IMG_";

    // Postfix
    String POSTFIX_IMAGE = ".jpg";
    // Common
    String APP_COMMON_DATE_FORMAT = "MMM dd, yyyy";


    int progress = 60;


    int zero = 0;
    int one = 1;
    int four = 4;
    int five = 5;
    int six=6;

    String PREV_ACTIVITY = "previous_activity";
    String EMAIL = "email";
    int SIGN_UP_ACTIVITY = 1;
    int FORGOT_PASSWORD = 2;
    String TOKEN = "token";

    String space=" ";
    Integer SUCCESS=200;
    String message="Success";
    String DOWNLOAD_ON="1";

    String FACEBOOK_HASH_KEY_ON="1";
    String DOWN_LOAD_FROM_LINK_ON="1";

    String CATEGORY="category";
}
