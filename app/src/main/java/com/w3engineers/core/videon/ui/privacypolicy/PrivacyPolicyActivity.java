package com.w3engineers.core.videon.ui.privacypolicy;

import android.content.Context;
import android.content.Intent;
import android.support.v4.text.HtmlCompat;
import android.view.MenuItem;

import com.w3engineers.core.videon.R;
import com.w3engineers.core.videon.databinding.ActivityPrivacyPolicyBinding;
import com.w3engineers.ext.strom.application.ui.base.BaseActivity;



public class PrivacyPolicyActivity extends BaseActivity {

    private ActivityPrivacyPolicyBinding mBinding;

    public static void runActivity(Context context) {
        Intent intent = new Intent(context, PrivacyPolicyActivity.class);
        runCurrentActivity(context, intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_privacy_policy;
    }

    @Override
    protected void startUI() {
       mBinding=(ActivityPrivacyPolicyBinding) getViewDataBinding();
       setSupportActionBar(mBinding.toolbarPrivacy);
       mBinding.privacyWebview.loadUrl("file:///android_asset/privacy_policy_app.html");
    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        finish();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                finish();
                break;

        }
        return true;
    }

}
