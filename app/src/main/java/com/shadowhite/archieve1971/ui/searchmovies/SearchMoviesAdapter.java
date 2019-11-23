package com.shadowhite.archieve1971.ui.searchmovies;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.shadowhite.archieve1971.R;
import com.shadowhite.archieve1971.data.local.commondatalistresponse.Datum;
import com.shadowhite.archieve1971.databinding.ItemSearchMoviesBinding;
import com.shadowhite.archieve1971.ui.home.UICommunicatorInterface;
import com.shadowhite.util.CheckVideoTypeUtil;
import com.shadowhite.util.NetworkURL;
import com.shadowhite.util.adapterUtil.ProjectBaseAdapter;
import com.shadowhite.util.helper.ResoulationConverter;
import com.shadowhite.util.helper.TimeConverter;


public class SearchMoviesAdapter extends ProjectBaseAdapter<Datum> {
    private UICommunicatorInterface uiCommunicator;
    private Context context;

    public SearchMoviesAdapter(UICommunicatorInterface uiCommunicator, Context context) {
        this.uiCommunicator = uiCommunicator;
        this.context = context;
    }

    @Override
    public boolean isEqual(Datum left, Datum right) {
        return false;
    }

    @Override
    public BaseAdapterViewHolder<Datum> newViewHolder(ViewGroup parent, int viewType) {
        return new SearchViewHolder(inflate(parent, R.layout.item_search_movies));
    }


    private class SearchViewHolder extends BaseAdapterViewHolder<Datum> {

        private ItemSearchMoviesBinding mBinding;

        SearchViewHolder(ViewDataBinding viewDataBinding) {
            super(viewDataBinding);
            mBinding = (ItemSearchMoviesBinding) viewDataBinding;
        }

        @Override
        public void bind(Datum item) {
            mBinding.textViewVideoTitle.setText("" + item.getTitle());
            mBinding.textViewCatName.setText("" + item.getCategory());
            mBinding.textViewViewsCount.setText("" + item.getViewCount());
            mBinding.textViewUploadTime.setText("" + item.getCreated());


            mBinding.textViewDuration.setText(TimeConverter.ConvertSecondsToHourMinute(Integer.parseInt(item.getDuration())));

            CheckVideoTypeUtil.checkVideoType(item);

            String imageUrl = NetworkURL.imageEndPointURL + item.getImageName();
            ViewGroup.LayoutParams params = mBinding.roundImageViewVideo.getLayoutParams();
            Integer[] reqHeight = ResoulationConverter.ConvertResoulationHeight(context, item.getImageResolution(), (float) params.width);
            LoadImage(mBinding.roundImageViewVideo, mBinding.progressBarCircular, imageUrl, reqHeight);
            mBinding.progressBarCircular.setVisibility(View.GONE);

        }

        /**
         * Load image
         *
         * @param imageView   imageView
         * @param progressBar progressBar
         * @param imageLink   imageLink
         * @param heightWidth heightWidth
         */
        public void LoadImage(ImageView imageView, ProgressBar progressBar, String imageLink, Integer[] heightWidth) {

            try {
                RequestOptions requestOptions = new RequestOptions();

                requestOptions = requestOptions.transforms(new FitCenter(), new RoundedCorners(16)).override(heightWidth[0], heightWidth[1]).error(R.drawable.default_img); // resizes the image to these dimensions (in pixel)
                Glide.with(context)
                        .load(imageLink)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                progressBar.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                progressBar.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .apply(requestOptions)
                        .into(imageView);
            } catch (Exception e) {
                progressBar.setVisibility(View.GONE);
            }
        }


    }
}
