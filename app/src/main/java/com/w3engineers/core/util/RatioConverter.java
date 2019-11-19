package com.w3engineers.core.util;

public class RatioConverter {
    public static int GetRequiredHeight(float imageWidth,float imageHeight,float deviceWidth)
    {
        return (int) (imageWidth/(deviceWidth*imageHeight));
    }
}
