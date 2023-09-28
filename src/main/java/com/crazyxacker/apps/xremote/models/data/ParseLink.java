package com.crazyxacker.apps.xremote.models.data;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@Data
@NoArgsConstructor
public class ParseLink implements Serializable {
    private String originalLink;
    private String downloadLink;
    private Subtitles subtitles;
    private Map<String, String> headersForVideo;

    public ParseLink(String originalLink, String downloadLink, Subtitles subtitles, Map<String, String> headersForVideo) {
        this.originalLink = originalLink;
        this.downloadLink = downloadLink;
        this.subtitles = subtitles;
        this.headersForVideo = headersForVideo;
    }
}
