package com.w3engineers.core.videon.ui.contactus;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.w3engineers.core.videon.R;
import com.w3engineers.core.videon.databinding.ActivityAboutUsBinding;
import com.w3engineers.core.videon.databinding.ActivityContactUsBinding;
import com.w3engineers.core.videon.ui.aboutus.AboutUsActivity;
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
