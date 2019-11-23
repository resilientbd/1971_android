package com.shadowhite.util.helper;

import android.content.Context;

import com.shadowhite.archieve1971.R;


public class ApiToken {
    public static String GET_TOKEN(Context context)
    {
        return context.getResources().getString(R.string.api_token_value);
    }
}
