package com.w3engineers.core.videon.ui.myprofile;


import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.w3engineers.core.util.helper.PrefType;
import com.w3engineers.core.util.helper.SharedPref;
import com.w3engineers.core.videon.R;
import com.w3engineers.core.videon.data.local.Enums;
import com.w3engineers.core.videon.databinding.ActivityMyProfileBinding;
import com.w3engineers.core.videon.ui.editprofile.EditProfileActivity;
import com.w3engineers.core.videon.ui.favourite.FavouriteFragment;
import com.w3engineers.core.videon.ui.playlist.PlayListFragment;
import com.w3engineers.ext.strom.application.ui.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class MyProfileActivity extends BaseActivity implements TabLayout.OnTabSelectedListener {
    private ActivityMyProfileBinding mBinding;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_profile;
    }

    public static void runActivity(Context context) {
        Intent intent = new Intent(context, MyProfileActivity.class);
        runCurrentActivity(context, intent);
    }

    @Override
    protected int getMenuId() {
        return R.menu.menu_my_profile;
    }


    @Override
    protected void startUI() {
        mBinding = (ActivityMyProfileBinding) getViewDataBinding();

        setSupportActionBar(mBinding.toolbarHome);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setupViewPager(mBinding.viewpagerMyProfile);
        mBinding.tabsMyProfile.setupWithViewPager(mBinding.viewpagerMyProfile);

        mBinding.tabsMyProfile.addOnTabSelectedListener(this);
        setClickListener(mBinding.circularImageViewUser);

        setPersonalInformation();

    }

    /**
     * set profile information
     */
    private void setPersonalInformation() {
        String userName = SharedPref.read(PrefType.AUTH_NAME);
        String email = SharedPref.read(PrefType.USER_EMAIL);
        mBinding.textViewUserName.setText(userName);
        mBinding.textViewEmail.setText(email);
    }

    /**
     * Set up view pager
     *
     * @param viewPager ViewPager
     */
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FavouriteFragment(),getResources().getString(R.string.my_profile_fragment_favourite));
        adapter.addFragment(new PlayListFragment(), getResources().getString(R.string.my_profile_fragment_playlist));
        viewPager.setAdapter(adapter);

    }


    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        int position = tab.getPosition();
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }



    /**
     * View pager adapter class for favourite and playlist fragment
     */
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        SharedPref.init(getBaseContext());
        if(SharedPref.readInt(PrefType.AUTH_TYPE)== Enums.EMAIL_LGOIN) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_my_profile, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_edit_profile:
                EditProfileActivity.runActivity(this);
                //finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.circular_image_view_user:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setPersonalInformation();
    }
}
