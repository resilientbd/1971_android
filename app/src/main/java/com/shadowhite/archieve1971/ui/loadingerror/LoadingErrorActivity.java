package com.shadowhite.archieve1971.ui.loadingerror;


import com.shadowhite.archieve1971.R;
import com.shadowhite.archieve1971.databinding.ActivityLoadingErrorBinding;
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
