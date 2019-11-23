package com.shadowhite.util;

import com.shadowhite.archieve1971.data.remote.RemoteApiProvider;

/**
 * Network call urls for server hit
 */
public interface NetworkURL {
    String imageEndPointURL= RemoteApiProvider.getBaseUrl()+"public/uploads/thumb/";
    String mostRecentVideosEndPointUrl="frontend/web/index.php/videonapi/recent/";
    String videosEndPointURL=RemoteApiProvider.getBaseUrl()+"public/uploads/";
    String searchEndPointURL="frontend/web/index.php/searchapi/search/";
    String featuredEndPointURL="public/api/video/featured.php";
    String mostPopularEndPointURL="public/api/video/popular.php";
    String mostRecentEndPointURL="public/api/video/recent.php";
    String categoryEndPointURL="public/api/video/by-category.php";
    String channelListEndPointURL="public/api/livetv/all.php";
}
