package com.shadowhite.archieve1971.ui.contactus;

import android.content.Context;
import android.content.Intent;

import com.shadowhite.archieve1971.R;
import com.shadowhite.archieve1971.databinding.ActivityContactUsBinding;
import com.w3engineers.ext.strom.application.ui.base.BaseActivity;

public class ContactUsActivity extends BaseActivity {
    private ActivityContactUsBinding mBinding;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_contact_us;
    }

    public static void runActivity(Context context) {
        Intent intent = new Intent(context, ContactUsActivity.class);
        runCurrentActivity(context, intent);

    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        finish();
    }
    @Override
    protected void startUI() {
        mBinding=(ActivityContactUsBinding)getViewDataBinding();
        setSupportActionBar(mBinding.toolbarContactUs);

    }
}
