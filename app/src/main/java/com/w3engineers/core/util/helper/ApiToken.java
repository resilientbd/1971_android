package com.w3engineers.core.util.helper;

import android.content.Context;

import com.w3engineers.core.videon.R;

public class ApiToken {
    public static String GET_TOKEN(Context context)
    {
        return context.getResources().getString(R.string.api_token_value);
    }
}
