package com.shadowhite.util;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class RatioConverter {
    public static int GetRequiredHeight(float deviceWidth,float imageHeight,float imageWidth)
    {
        float x=deviceWidth*imageHeight;
        float y= x/imageWidth;
        return (int) (y);
    }
    public static Bitmap GetResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }
}
