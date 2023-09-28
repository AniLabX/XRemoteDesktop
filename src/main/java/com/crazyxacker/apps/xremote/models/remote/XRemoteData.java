package com.crazyxacker.apps.xremote.models.remote;

import com.crazyxacker.apps.xremote.models.data.LinkItem;
import com.crazyxacker.apps.xremote.models.data.ParseLink;
import com.crazyxacker.apps.xremote.remote.XRemoteCommand;
import com.crazyxacker.apps.xremote.remote.XRemotePlayCommand;
import com.crazyxacker.apps.xremote.remote.XRemoteServer;
import com.crazyxacker.apps.xremote.utils.Utils;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class XRemoteData {
    public static String DEFAULT_TRANSLATION_NAME = "UnknownTranslation";

    private int apiVersion;
    private XRemoteCommand command;
    private XRemotePlayInfo playInfo;
    private XRemotePlayCommand playCommand = XRemotePlayCommand.NONE;

    private String contentId;
    private String contentTitle;
    private String contentSubtitle;
    private String seasonPart;
    private String translationPart;
    private String episodePart;
    private LinkItem videoUrl;
    private ParseLink parseLink;

    public XRemoteData(XRemoteCommand command) {
        this.command = command;
        this.apiVersion = XRemoteServer.API_VERSION;
    }

    public StringBuilder buildEpisodeTitle() {
        StringBuilder titleBuilder = new StringBuilder();
        if (Utils.isNotEmpty(seasonPart) && !Utils.equalsIgnoreCase(seasonPart, translationPart)) {
            titleBuilder.append(seasonPart);
            titleBuilder.append(" > ");
        }

        if (Utils.isNotEmpty(translationPart) && !Utils.equalsIgnoreCase(translationPart, DEFAULT_TRANSLATION_NAME)) {
            titleBuilder.append(translationPart);
            titleBuilder.append(" > ");
        }
        titleBuilder.append(episodePart);
        return titleBuilder;
    }

    public String getEpisodeId() {
        return Utils.md5Hex(getSeasonPart() + getTranslationPart() + getEpisodePart());
    }
}