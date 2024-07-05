package xyz.xremote.models.remote;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import xyz.xremote.models.data.LinkItem;
import xyz.xremote.models.data.ParseLink;
import xyz.xremote.utils.Utils;

@Data
public class XRemotePlayData {
    public static String DEFAULT_TRANSLATION_NAME = "UnknownTranslation";

    @SerializedName("SERVICE_ID")
    private long serviceId;
    @SerializedName("CONTENT_ID")
    private String contentId;
    @SerializedName("CONTENT_TITLE")
    private String contentTitle;
    @SerializedName("CONTENT_SUBTITLE")
    private String contentSubtitle;
    @SerializedName("SEASON")
    private String seasonPart;
    @SerializedName("TRANSLATION")
    private String translationPart;
    @SerializedName("EPISODE")
    private String episodePart;
    @SerializedName("LINK_ITEM")
    private LinkItem videoUrl;
    @SerializedName("PARSE_LINK")
    private ParseLink parseLink;

    @SerializedName("RESPECT_TRANSLATION_NAME")
    private Boolean dontIgnoreTranslationName;

    @SerializedName("POSITION")
    private Long position;
    @SerializedName("WINDOW")
    private Integer window;

    public StringBuilder buildEpisodeTitle() {
        StringBuilder titleBuilder = new StringBuilder();
        if (Utils.isNotEmpty(getSeasonPart()) && !Utils.equalsIgnoreCase(getSeasonPart(), getTranslationPart())) {
            titleBuilder.append(getSeasonPart());
            titleBuilder.append(" > ");
        }

        if (Utils.isNotEmpty(getTranslationPart()) && !Utils.equalsIgnoreCase(getTranslationPart(), DEFAULT_TRANSLATION_NAME)) {
            titleBuilder.append(getTranslationPart());
            titleBuilder.append(" > ");
        }
        titleBuilder.append(getEpisodePart());
        return titleBuilder;
    }

    public String getEpisodeId() {
        return Utils.md5Hex(getSeasonPart() + getTranslationPart() + getEpisodePart());
    }

    public Boolean getDontIgnoreTranslationName() {
        return dontIgnoreTranslationName != null && dontIgnoreTranslationName;
    }

    public Long getPosition() {
        return position != null ? position : 0;
    }

    public Integer getWindow() {
        return window != null ? window : 0;
    }
}
