package com.shadowhite.archieve1971.ui.aboutus;

import android.content.Context;
import android.content.Intent;
import android.support.v4.text.HtmlCompat;

import com.shadowhite.archieve1971.R;
import com.shadowhite.archieve1971.databinding.ActivityAboutUsBinding;
import com.w3engineers.ext.strom.application.ui.base.BaseActivity;


public class AboutUsActivity extends BaseActivity {

    private ActivityAboutUsBinding mBinding;

    /*
    * Run activity about us
    * */
    public static void runActivity(Context context) {
        Intent intent = new Intent(context, AboutUsActivity.class);
        runCurrentActivity(context, intent);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_about_us;
    }
    @Override
    public void onBackPressed() {
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        finish();
    }

    @Override
    protected void startUI() {
        mBinding=(ActivityAboutUsBinding)getViewDataBinding();
        setSupportActionBar(mBinding.toolbarHome);
        String string=getResources().getString(R.string.text_about);
        mBinding.aboutUs.setText(HtmlCompat.fromHtml(string, 0));

    }
}
