package com.w3engineers.core.videon.ui.templete;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.w3engineers.core.videon.R;
import com.w3engineers.core.videon.databinding.ActivityDocViewerBinding;
import com.w3engineers.core.videon.ui.login.LoginActivity;
import com.w3engineers.ext.strom.application.ui.base.BaseActivity;


public class TempleteActivity extends BaseActivity {


    private ActivityDocViewerBinding mBinding;

    public static void runActivity(Context context) {
        Intent intent = new Intent(context, TempleteActivity.class);
        runCurrentActivity(context, intent);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_doc_viewer;
    }

    @Override
    protected void startUI() {
         mBinding=(ActivityDocViewerBinding)getViewDataBinding();

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_login:
                LoginActivity.runActivity(TempleteActivity.this);
                break;
        }
    }







}
