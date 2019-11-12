package com.w3engineers.core.videon.ui.downloadmanager;

import android.Manifest;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Toast;

import com.w3engineers.core.util.helper.AppDownloadManager;
import com.w3engineers.core.util.helper.DialogHelper;
import com.w3engineers.core.util.helper.PermissionUtil;
import com.w3engineers.core.util.helper.TimeUtil;
import com.w3engineers.core.videon.R;
import com.w3engineers.core.videon.data.local.Enums;
import com.w3engineers.core.videon.data.local.adaptermodel.ItemVideoDownload;
import com.w3engineers.core.videon.databinding.ActivityDownloadManagerBinding;
import com.w3engineers.core.videon.ui.adapter.DownloadVideoAdapter;
import com.w3engineers.ext.strom.application.ui.base.BaseActivity;
import com.w3engineers.ext.strom.application.ui.widget.BaseRecyclerView;

import java.util.ArrayList;
import java.util.List;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;
import uk.breedrapps.vimeoextractor.OnVimeoExtractionListener;
import uk.breedrapps.vimeoextractor.VimeoExtractor;
import uk.breedrapps.vimeoextractor.VimeoVideo;

public class DownloadManagerActivity extends BaseActivity implements DownloadVideoAdapter.OnDownloadClick, DialogHelper.InputDialogListener {
    private List<ItemVideoDownload> itemVideoDownloads;
    private DownloadVideoAdapter mAdapter;
    private DialogHelper dialogHelper;
    ActivityDownloadManagerBinding mBinding;

    /**
     * @return layoute
     */
    @Override
    protected int getLayoutId() {
        return R.layout.activity_download_manager;
    }

    /**
     * start ui
     */
    @Override
    protected void startUI() {
        mBinding = (ActivityDownloadManagerBinding) getViewDataBinding();
        itemVideoDownloads = new ArrayList<>();
        setClickListener(mBinding.searchLinkButton);
        initBuilderRecycleView();
    }

    /**
     * initiate recycleview
     */
    public void initBuilderRecycleView() {

        mAdapter = new DownloadVideoAdapter();
        BaseRecyclerView mRecycleView = mBinding.downloadableOptionsRecycleView;
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mRecycleView.setAdapter(mAdapter);
        mAdapter.setDownloadClick(this);


    }

    /**
     * @param view button view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.searchLinkButton:
                String url = mBinding.idInputLink.getText().toString().trim();
                if (!url.isEmpty()) {
                    int linkType = getLinkType(url);
                    String videoId = getVideoId(url, linkType);
                    String solidLink = prepareAbsoulateLink(videoId, linkType);
                    Log.d("linkgen", "" + solidLink);
                    switch ((linkType)) {
                        case Enums.YOUTUBE_VIDEO:
                            processDisplayYoutubeLink(solidLink);
                            break;
                        case Enums.VIMEO_VIDEO:
                            processDisplayVimeoLink(solidLink);
                            break;
                        default:
                            processDisplayDefaultLink(url);
                            break;
                    }
                }
                break;
            default:
                break;
        }

    }

    private void processDisplayDefaultLink(String solidLink) {
        mBinding.selectViewTitle.setVisibility(View.GONE);

        List<ItemVideoDownload> mDownloads = new ArrayList<>();

        ItemVideoDownload itemVideoDownload = new ItemVideoDownload();
        itemVideoDownload.setTitle("Resoulation Unknown");
        itemVideoDownload.setUrl(solidLink);
        itemVideoDownload.setFormat("mp4");
        mDownloads.add(itemVideoDownload);
        setVideoDownloadableItems(mDownloads);

    }

    /**
     * process to extract and display download vimeo video items
     *
     * @param solidLink vimeo id
     */
    private void processDisplayVimeoLink(String solidLink) {

        VimeoExtractor.getInstance().fetchVideoWithIdentifier(solidLink, null, new OnVimeoExtractionListener() {
            @Override
            public void onSuccess(VimeoVideo video) {
                VimeoVideo vimeoVideo = video;
                List<ItemVideoDownload> mDownloads = new ArrayList<>();
                for (String key : vimeoVideo.getStreams().keySet()) {
                    ItemVideoDownload itemVideoDownload = new ItemVideoDownload();
                    itemVideoDownload.setTitle(key);
                    itemVideoDownload.setUrl(vimeoVideo.getStreams().get(key));
                    itemVideoDownload.setFormat("mp4");
                    mDownloads.add(itemVideoDownload);

                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setVideoDownloadableItems(mDownloads, vimeoVideo.getTitle(), vimeoVideo.getDuration());
                    }
                });

            }

            @Override
            public void onFailure(Throwable throwable) {

            }
        });


    }

    private void setVideoDownloadableItems(List<ItemVideoDownload> mDownloads, String title, long duration) {
        mBinding.selectViewTitle.setVisibility(View.VISIBLE);
        mBinding.headingLayer.setVisibility(View.VISIBLE);
        mAdapter.clear();
        mBinding.titleText.setText(title);
        mBinding.durationText.setText("" + TimeUtil.convertDuration(TimeUtil.secondsToMillis(duration)));
        mAdapter.addItems(mDownloads);
    }

    /**
     * @param link copied link input
     * @return type source
     */
    public int getLinkType(String link) {
        if (link.toLowerCase().contains("vimeo.com")) {
            return Enums.VIMEO_VIDEO;
        } else if (link.toLowerCase().contains("youtube.com") || link.toLowerCase().contains("youtu.be")) {
            return Enums.YOUTUBE_VIDEO;
        } else {
            return Enums.OTHER_VIDEO;
        }
    }

    /**
     * @param link copied link
     * @param type link source type
     * @return for youtube and vimo -> id
     * for other video -> unchange the link
     * and return the same input
     */
    public String getVideoId(String link, int type) {

        String linkid = "";
        if (!link.equals("")) {
            switch (type) {
                case Enums.VIMEO_VIDEO:

                    try {
                        String[] vidIdVimeo = link.split("vimeo.com/");
                        linkid = vidIdVimeo[1];
                    } catch (ArrayIndexOutOfBoundsException e) {
                        Toast.makeText(this, getResources().getString(R.string.see_all_invalid_link), Toast.LENGTH_LONG).show();
                        break;
                    }

                    break;
                case Enums.OTHER_VIDEO:
                    link = link;
                    break;
                default:
                    try {
                        String[] vidIdYoutube;
                        if (link.contains("youtu.be")) {
                            vidIdYoutube = link.split("youtu.be/");
                            linkid = "" + vidIdYoutube[1];
                        } else {
                            vidIdYoutube = link.split("=");
                            linkid = "" + vidIdYoutube[1];
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        Toast.makeText(this,getResources().getString(R.string.see_all_invalid_link), Toast.LENGTH_LONG).show();
                        break;
                    }

                    break;
            }
        }
        return linkid;
    }

    /**
     * @param linkid id for youtube and vimeo; link for other video
     * @param type   source type (youtube/vimeo/other)
     * @return pre-process download link / input link for downloadable link
     */
    public String prepareAbsoulateLink(String linkid, int type) {
        String sharableLink = "";
        if (type == Enums.VIMEO_VIDEO) {
            sharableLink = linkid;
        } else if (type == Enums.OTHER_VIDEO) {
            sharableLink = linkid;
        } else {
            sharableLink = "https://youtu.be/" + linkid;


        }
        return sharableLink;
    }

    /**
     * Get youtube view download
     *
     * @param youtubeLink youtube link
     *                    start process to display available downloadable
     *                    video with sizes in recycle view.
     */
    private void processDisplayYoutubeLink(String youtubeLink) {
        new YouTubeExtractor(this) {

            @Override
            public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {


                if (ytFiles == null) {
                    // Something went wrong we got no urls. Always check this.
                    finish();
                    return;
                }
                String sharelink = "";

                // ytFile represents one file with its url and meta data
                List<ItemVideoDownload> itemVideoDownloadList = new ArrayList<>();
                for (int i = 0, itag; i < ytFiles.size(); i++) {
                    itag = ytFiles.keyAt(i);
                    // ytFile represents one file with its url and meta data
                    YtFile ytFile = ytFiles.get(itag);

                    // Just add videos in a decent format => height -1 = audio
                    if (ytFile.getFormat().getHeight() == -1 || ytFile.getFormat().getHeight() >= 360) {
                        //   addButtonToMainLayout(vMeta.getTitle(), ytFile);

                        Log.d("downloadfile", "Download url:" + ytFile);

                        ItemVideoDownload itemVideoDownload = new ItemVideoDownload();
                        sharelink = sharelink + ytFile.getFormat().getHeight() + ": " + ytFile.getUrl() + "'\n";
                        if (ytFile.getFormat().getHeight() == -1) {
                            itemVideoDownload.setTitle("Audio File");
                        } else {
                            Log.d("audiocodec", "" + ytFile.getFormat().getAudioBitrate());
                            itemVideoDownload.setTitle("" + ytFile.getFormat().getHeight() + "p");
                        }

                        itemVideoDownload.setFormat(ytFile.getFormat().getExt());
                        itemVideoDownload.setUrl(ytFile.getUrl());
                        itemVideoDownloadList.add(itemVideoDownload);

                    }
                    // strings.add(makeButtonText(vMeta.getTitle(),ytFile));
                }
                setVideoDownloadableItems(itemVideoDownloadList);
                Log.d("linkgen", "generated: " + sharelink);
                //callDialogHelper(strings);
            }
        }.extract(youtubeLink, true, false);
        return;
    }

    /**
     * @param itemVideoDownloadList display recyclable items
     */
    private void setVideoDownloadableItems(List<ItemVideoDownload> itemVideoDownloadList) {
        mAdapter.clear();
        mBinding.headingLayer.setVisibility(View.GONE);
        mAdapter.addItems(itemVideoDownloadList);
    }

    /**
     * @param item item download button click
     */
    @Override
    public void onItemDownloadClick(ItemVideoDownload item) {
        if (dialogHelper == null) {
            dialogHelper = new DialogHelper();
        }
        dialogHelper.showInputDialog(this, this, item);
    }

    /**
     * @param filename name of the file to save
     * @param url      downloadable url
     */
    @Override
    public void onSave(String filename, String url) {
        if(checkStoragePermission()==true) {
            AppDownloadManager.download(url, filename, "videon", DownloadManagerActivity.this);
        }
    }

    /**
     * check permission
     */
    public boolean checkStoragePermission() {
        boolean b = PermissionUtil.init(this).request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return b;
    }

    /**
     * callback for dialog event cancelled
     */
    @Override
    public void onCancel() {
        Log.d("abc", "cancelled");
    }
}

