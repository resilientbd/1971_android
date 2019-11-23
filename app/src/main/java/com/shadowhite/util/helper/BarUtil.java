package com.shadowhite.util.helper;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.shadowhite.archieve1971.R;


public class BarUtil {
    private BarUtil() {
    }

    public static void setStatusBarColor(AppCompatActivity context, int colorId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = context.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getColor(context, colorId));
        }
    }

    public static void setActionBarColor(AppCompatActivity activity, int colorId) {
        if (activity != null && colorId > 0) {
            ActionBar actionBar = activity.getSupportActionBar();
            if (actionBar != null) {
                actionBar.setBackgroundDrawable(new ColorDrawable(activity.getResources().getColor(colorId)));
            }
        }
    }

    public static int getColor(Context context, int colorId) {
        return ContextCompat.getColor(context, colorId);
    }

    public static int getToolbarHeight(Context context) {

        final TypedArray styledAttributes = context.getTheme().
                obtainStyledAttributes(new int[]{R.attr.actionBarSize});

        int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        return toolbarHeight;
    }

    public static void showToolbar(Activity activity, int toolbarId) {
        View view = ViewUtils.viewById(activity, toolbarId);
        if (Toolbar.class.isInstance(view)) {
            showToolbar((Toolbar) view);
        }
    }

    public static void showToolbar(Toolbar toolbar) {
        if (toolbar != null) {
            toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
        }
    }

    public static void hideToolbar(Activity activity, int toolbarId) {
        View view = ViewUtils.viewById(activity, toolbarId);
        if (Toolbar.class.isInstance(view)) {
            hideToolbar((Toolbar) view);
        }
    }

    public static void hideToolbar(Toolbar toolbar) {
        if (toolbar != null) {
            toolbar.animate().translationY(-toolbar.getHeight()).setInterpolator(new AccelerateInterpolator(2));
        }
    }
}
