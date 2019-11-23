package com.shadowhite.util.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.shadowhite.archieve1971.R;

import java.io.File;
import java.util.concurrent.ExecutionException;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;


public class Glider {
    private static Context thisContext;


    //Init Glider class with context
    public static void init(Context context) {
        thisContext = context;
    }

    public static void show(Object location, ImageView imageView) {
        try {
            if (location != null && imageView != null && thisContext != null) {
                Glide.with(thisContext)
                        .load(location)
                        .apply(new RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true))
                        .into(imageView);
            }
        } catch (Exception e) {
            // Do nothing
            Log.d("GlideException", "error: " + e.getMessage());
        }
    }

    /*
     * Loads image into ImageView and provides callback if the resource is ready or not
     * */
    public static void show(Object location, ImageView imageView,
                            RequestListener<Drawable> listener) {
        try {
            if (location != null && imageView != null
                    && thisContext != null && listener != null) {
                Glide.with(thisContext)
                        .load(location)
                        .apply(new RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true))
                        .listener(listener)
                        .into(imageView);
            }
        } catch (Exception ignored) {
            // Do nothing
        }
    }


    /*
     * Loads image into ImageView and provides callback if the resource is ready or not
     * */
    public static void showWithCaching(Object location, ImageView imageView,
                                       RequestListener<Drawable> listener) {
        try {
            if (location != null && imageView != null
                    && thisContext != null) {
                Glide.with(thisContext)
                        .load(location)
                        .apply(new RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .skipMemoryCache(false))
                        .listener(listener)
                        .into(imageView);
            }
        } catch (Exception ignored) {
            // Do nothing
        }
    }

    public static void showGallery(String location, ImageView imageView) {
        try {
            if (location != null && !location.isEmpty() && imageView != null && thisContext != null) {
                Glide.with(thisContext)
                        .load(location)
                        .apply(new RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .skipMemoryCache(false))
                        .into(imageView);
            }
        } catch (Exception ignored) {
            // Do nothing
        }
    }

    public static void showCircular(final ImageView imageView, Object location) {
        if (location != null && imageView != null && thisContext != null) {

            Glide.with(thisContext)
                    .asBitmap()
                    .load(location)
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .dontAnimate())
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource,
                                                    @Nullable Transition<? super Bitmap> transition) {
                            imageView.setImageBitmap(resource);
                        }
                    });
        }
    }

    public static void loadUserAvatarWithoutAnimation(String path, final ImageView imageView) {
        if (imageView == null) return;
        Glide.with(thisContext.getApplicationContext())
                .asBitmap()
                .load(path)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .dontAnimate())
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        imageView.setImageBitmap(resource);
                    }
                });
    }

    public static void showCircularWithClearCache(ImageView imageView, Object location) {
        if (location != null && imageView != null && thisContext != null) {

            Glide.with(thisContext)
                    .load(location)
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .skipMemoryCache(true))
                    .into(imageView);
        }
    }

    public static void showWithPlaceholder(ImageView imageView, String location) {
        if (location != null && imageView != null && thisContext != null) {
            Glide.with(thisContext)
                    .load(location)
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .placeholder(R.drawable.user))
                    .into(imageView);
        }
    }


    /**
     * Load user notification user
     *
     * @param path
     * @param imageView
     */
    public static void loadUserNotificationAvatar(String path, ImageView imageView) {
        try {
            if (imageView == null || path.isEmpty()) return;
        }
       catch (Exception e)
       {
           return;
       }

        Glide.with(thisContext)
                .load(path)
                .apply(new RequestOptions()
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .error(R.drawable.user)
                        .centerCrop())
                .into(imageView);
    }


    /**
     * Load user notification user
     *
     * @param path
     * @param imageView
     */
    public static void loadPostImage(String path, ImageView imageView) {
        if (imageView == null || path.isEmpty()) return;

        Glide.with(thisContext)
                .load(path)
                .apply(new RequestOptions()
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .centerCrop())
                .into(imageView);
    }

    public static void loadUserAvatarInMessagePage(String path, ImageView imageView) {
        if (imageView == null) return;
        File file = new File(path);

        Glide.with(thisContext.getApplicationContext())
                .load(file)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .error(R.drawable.user)
                        .centerCrop())
                .into(imageView);
    }

    public static void loadMenuImage(final Context context, String imageUri, final MenuItem menuItem) {

        Glide.with(context)
                .asBitmap()
                .load(imageUri)
                .apply(new RequestOptions()
                        .skipMemoryCache(false)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .centerCrop()
                        .dontAnimate())
                .into(new SimpleTarget<Bitmap>(100, 100) {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource,
                                                @Nullable Transition<? super Bitmap> transition) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory
                                        .create(context.getResources(), resource);

                        circularBitmapDrawable.setCircular(true);

                        if (menuItem != null)
                            menuItem.setIcon(circularBitmapDrawable);
                    }
                });
    }

    public static Bitmap getBitmap(Uri imageUri, int width, int height) {
        try {
            return Glide.with(thisContext)
                    .asBitmap()
                    .load(imageUri)
                    .apply(new RequestOptions()
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE))
                    .submit(width, height)
                    .get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void clearCache(Context context) {
        Glide.get(context).clearDiskCache();
    }

    public static void loadImage(String url, ImageView imageView) {
        Context context = imageView.getContext();
        ColorDrawable cd = new ColorDrawable(ContextCompat.getColor(context, R.color.accent));
        Glide.with(context)
                .load(url)
                .apply(new RequestOptions()
                        .placeholder(cd)
                        .centerCrop())
                .transition(withCrossFade())
                .into(imageView);
    }

    public static void loadProfileIcon(String url, ImageView imageView) {
        Context context = imageView.getContext();
        Glide.with(context)
                .load(url)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.user)
                        .dontAnimate()
                        .fitCenter())
                .into(imageView);
    }

}