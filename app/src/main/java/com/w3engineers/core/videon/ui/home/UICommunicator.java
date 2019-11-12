package com.w3engineers.core.videon.ui.home;

import android.widget.ImageView;
import android.widget.ProgressBar;


public interface UICommunicator {
     void LoadImage(ImageView imageView, ProgressBar progressBar, String imageLink, Integer[] heightWidth);

}
