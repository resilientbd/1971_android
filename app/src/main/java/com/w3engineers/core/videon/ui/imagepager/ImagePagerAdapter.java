package com.w3engineers.core.videon.ui.imagepager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.w3engineers.core.videon.data.local.images.Datum;

import java.util.List;
import java.util.Map;

public class ImagePagerAdapter extends FragmentPagerAdapter {
    List<Datum> data;
    public ImagePagerAdapter(FragmentManager fm,List<Datum> data) {
        super(fm);
        this.data=data;
    }

    @Override
    public Fragment getItem(int i) {
        ImagePagerFragment fragment=new ImagePagerFragment();
        Map map= (Map) data.get(i);
        Datum datum=new Datum();
        datum.setImgUrl(""+map.get("img_url"));
        datum.setImgTitle(""+map.get("img_title"));
        datum.setImgDesc(""+map.get("img_desc"));
//        String value=""+map.get("img_url");
        fragment.setImagelist(datum);
        return fragment;
    }

    @Override
    public int getCount() {
        return data.size();
    }
}
