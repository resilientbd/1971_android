package com.shadowhite.util.helper;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.shadowhite.archieve1971.R;
import com.shadowhite.archieve1971.data.local.apimodels.AdResponse;
import com.shadowhite.archieve1971.data.remote.RemoteApiProvider;
import com.shadowhite.archieve1971.data.remote.home.RemoteVideoApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AdHelper {
    static RemoteVideoApiInterface remoteVideoApiInterface;

    public static RemoteVideoApiInterface loadAdUnitsFromServer() {
        remoteVideoApiInterface = RemoteApiProvider.getInstance().getRemoteHomeVideoApi();
        return remoteVideoApiInterface;
    }

    public static void loadBannerAd(Context context, LinearLayout linearLayout) {

        remoteVideoApiInterface = loadAdUnitsFromServer();
        remoteVideoApiInterface.getAdUnits(ApiToken.GET_TOKEN(context)).enqueue(new Callback<AdResponse>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onResponse(Call<AdResponse> call, Response<AdResponse> response) {
                if (response.isSuccessful()) {


                    AdResponse adResponse = response.body();

                    ApplicationInfo ai = null;
                    try {
                        ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    ai.metaData.putString("com.google.android.gms.ads.APPLICATION_ID",adResponse.getData().getBannerId());


                    LinearLayout layout = linearLayout;
                    layout.setOrientation(LinearLayout.VERTICAL);

                    // Create a banner ad
                    AdView mAdView = new AdView(context);
                    mAdView.setAdSize(AdSize.SMART_BANNER);

                    // Create an ad request.
                    AdRequest.Builder adRequestBuilder = new AdRequest.Builder();

                    // Optionally populate the ad request builder.
                    //adRequestBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);

                    // Add the AdView to the view hierarchy.
                    layout.addView(mAdView);



                    mAdView.setAdUnitId(adResponse.getData().getBannerUnitId());
                    // Start loading the ad.
                    mAdView.setBackgroundColor(context.getResources().getColor(R.color.white));
                    if(adResponse.getData().getBannerStatus().equals("1")) {
                        mAdView.loadAd(adRequestBuilder.build());
                    }
                }
            }

            @Override
            public void onFailure(Call<AdResponse> call, Throwable t) {
            }
        });

    }

    public void interstitialsAd(Context context, AdCloseListener adCloseListener) {
        remoteVideoApiInterface = loadAdUnitsFromServer();
        remoteVideoApiInterface.getAdUnits(ApiToken.GET_TOKEN(context)).enqueue(new Callback<AdResponse>() {
            @Override
            public void onResponse(Call<AdResponse> call, Response<AdResponse> response) {
                if (response.isSuccessful()) {
                    AdResponse adResponse = response.body();

                    ApplicationInfo ai = null;
                    try {
                        ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    ai.metaData.putString("com.google.android.gms.ads.APPLICATION_ID",adResponse.getData().getInterstitialId());


                    MobileAds.initialize(context,
                            adResponse.getData().getInterstitialUnitId());


                    InterstitialAd mInterstitialAd = new InterstitialAd(context);
                    //      mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
                    mInterstitialAd.setAdUnitId(adResponse.getData().getInterstitialUnitId());
                    if (adResponse.getData().getInterstitialStatus().equals("1")) {
                        mInterstitialAd.loadAd(new AdRequest.Builder().build());
                        mInterstitialAd.setAdListener(new AdListener() {
                            @Override
                            public void onAdLoaded() {
                                // Code to be executed when an ad finishes loading.
                                mInterstitialAd.show();
                            }

                            @Override
                            public void onAdFailedToLoad(int errorCode) {
                                // Code to be executed when an ad request fails.
                            }

                            @Override
                            public void onAdOpened() {
                                // Code to be executed when the ad is displayed.
                            }

                            @Override
                            public void onAdLeftApplication() {
                                // Code to be executed when the user has left the app.
                            }

                            @Override
                            public void onAdClosed() {
                                // Code to be executed when when the interstitial ad is closed.
                                adCloseListener.onAdClosed();
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<AdResponse> call, Throwable t) {

            }
        });

    }

    public interface AdCloseListener {
        void onAdClosed();
    }
}
