package com.shadowhite.archieve1971.ui.home;

import android.widget.ImageView;
import android.widget.ProgressBar;


public interface UICommunicator {
     void LoadImage(ImageView imageView, ProgressBar progressBar, String imageLink, Integer[] heightWidth);

}
