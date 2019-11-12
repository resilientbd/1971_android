package com.w3engineers.core.videon.ui.loadingerror;


import com.w3engineers.core.videon.R;
import com.w3engineers.core.videon.databinding.ActivityLoadingErrorBinding;
import com.w3engineers.ext.strom.application.ui.base.BaseActivity;

public class LoadingErrorActivity extends BaseActivity {
    private ActivityLoadingErrorBinding mBinding;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_loading_error;
    }

    @Override
    protected void startUI() {
        mBinding=(ActivityLoadingErrorBinding)getViewDataBinding();
    }
}
