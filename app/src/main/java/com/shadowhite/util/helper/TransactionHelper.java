package com.shadowhite.util.helper;

import android.content.Context;

import com.shadowhite.archieve1971.R;
import com.w3engineers.ext.strom.application.ui.base.BaseActivity;

/**
 * @method TransactionRightToLeft manage right to left page transaction
 * @method TransactionLeftToRight manage left to right page transaction
 */
public class TransactionHelper {
    public static void TransactionRightToLeft(Context context)
    {
        BaseActivity activity= (BaseActivity) context;
        activity.overridePendingTransition(R.anim.right_to_left,R.anim.left_to_right);
    }
    public static void TransactionLeftToRight(Context context)
    {
        BaseActivity activity= (BaseActivity) context;
        activity.overridePendingTransition(R.anim.left_to_right,R.anim.right_to_left);
    }
}
