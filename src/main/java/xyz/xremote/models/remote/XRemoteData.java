package xyz.xremote.models.remote;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import xyz.xremote.remote.XRemoteClientType;
import xyz.xremote.remote.XRemoteCommand;
import xyz.xremote.remote.XRemotePlayCommand;

@Data
public class XRemoteData {
    public static final int XREMOTE_API_VERSION = 10;

    // XRemote data
    @SerializedName("API_VERSION")
    private int apiVersion;
    @SerializedName("CLIENT_NAME")
    private String clientName;
    @SerializedName("CLIENT_APP_NAME")
    private String clientAppName;
    @SerializedName("CLIENT_APP_VERSION")
    private String clientAppVersion;
    @SerializedName("CLIENT_TYPE")
    private XRemoteClientType clientType = XRemoteClientType.UNKNOWN;
    @SerializedName("COMMAND")
    private XRemoteCommand command;

    // Play data
    @SerializedName("PLAY_COMMAND")
    private XRemotePlayCommand playCommand = XRemotePlayCommand.NONE;
    @SerializedName("PLAY_INFO")
    private XRemotePlayInfo playInfo;
    @SerializedName("PLAY_DATA")
    private XRemotePlayData playData;

    public XRemoteData() {
        this.apiVersion = XREMOTE_API_VERSION;
    }

    public XRemoteData(XRemoteCommand command) {
        this.command = command;
        this.apiVersion = XREMOTE_API_VERSION;
    }
}