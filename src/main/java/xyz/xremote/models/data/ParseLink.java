package xyz.xremote.models.data;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@Data
@NoArgsConstructor
public class ParseLink implements Serializable {
    @SerializedName("ORIGINAL")
    private String originalLink;
    @SerializedName("DIRECT")
    private String downloadLink;
    @SerializedName("SUBTITLES")
    private Subtitles subtitles;
    @SerializedName("FILES")
    private EpisodeFiles episodeFiles;
    @SerializedName("HEADERS_FOR_ORIGINAL")
    private Map<String, String> headersForOriginalLink;
    @SerializedName("HEADERS_FOR_DIRECT")
    private Map<String, String> headersForVideo;

    public ParseLink(String originalLink, String downloadLink, Subtitles subtitles, EpisodeFiles episodeFiles,
                     Map<String, String> headersForOriginalLink, Map<String, String> headersForVideo) {
        this.originalLink = originalLink;
        this.downloadLink = downloadLink;
        this.subtitles = subtitles;
        this.episodeFiles = episodeFiles;
        this.headersForOriginalLink = headersForOriginalLink;
        this.headersForVideo = headersForVideo;
    }
}
