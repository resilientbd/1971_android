package com.w3engineers.core.util;

import com.w3engineers.core.videon.data.local.Enums;
import com.w3engineers.core.videon.data.local.commondatalistresponse.Datum;

public class CheckVideoTypeUtil {

    /**
     * Check video type
     * @param datum Datum object
     */
    public static void  checkVideoType(Datum datum){
        if (datum !=null){
            switch (datum.getType()) {
                case Enums.VIMEO_VIDEO:
                    if (datum.getVimeo().contains("vimeo.com/")) {
                        String[] vidIdVimeo = datum.getVimeo().split("vimeo.com/");
                        datum.setLink(vidIdVimeo[1]);
                    } else {
                        datum.setLink(datum.getVimeo());
                    }
                    break;
                case Enums.OTHER_VIDEO:
                    datum.setLink(NetworkURL.videosEndPointURL + datum.getUploadedVideo());
                    break;
                case Enums.LINK_VIDEO:
                    datum.setLink(datum.getVideoLink());
                    break;
                case Enums.YOUTUBE_VIDEO:
                    if (datum.getYoutube().contains("=")) {
                        String[] vidIdYoutube = datum.getYoutube().split("=");
                        datum.setLink("" + vidIdYoutube[1]);
                    } else {
                        datum.setLink("" + datum.getYoutube());
                    }
                    break;
            }
        }

    }
}
