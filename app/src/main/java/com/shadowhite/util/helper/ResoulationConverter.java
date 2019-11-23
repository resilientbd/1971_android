package com.shadowhite.util.helper;

import android.content.Context;

public class ResoulationConverter {
    public static Integer[] ConvertResoulation(String res) {
        Integer[] fl = {0, 0};
        return fl;
    }

    public static Integer[] ConvertResoulationHeight(Context context, String res, Float dpWidth) {
        String[] split_string = new String[2];
        try {
            split_string = res.split(":");
        } catch (Exception e) {
            split_string[0] = "50";
            split_string[1] = "50";
        }
        float width = Float.parseFloat(split_string[0]);
        float height = Float.parseFloat(split_string[1]);
        float reqHeight = (dpWidth * dpFromPx(context, height)) / dpFromPx(context, width);
        Integer[] reqResoulation = {Math.round(dpWidth), Math.round(reqHeight)};

        return reqResoulation;
    }

    public static Integer[] ConvertResoulationWidth(Context context, String res, Float dpHeight) {
        String[] split_string = res.split(":");
        float width = Float.parseFloat(split_string[0]);
        float height = Float.parseFloat(split_string[1]);
        float reqWidth = (dpHeight * dpFromPx(context, width)) / dpFromPx(context, height);
        Integer[] reqResoulation = {Math.round(dpHeight), Math.round(reqWidth)};
        return reqResoulation;
    }

    public static float dpFromPx(final Context context, final float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }
}
