package xyz.xremote.models.remote;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import xyz.xremote.remote.XRemoteResponse;

@Data
public class XRemoteResponseData {
    @SerializedName("API_VERSION")
    private int apiVersion;

    @SerializedName("RESPONSE")
    private XRemoteResponse response;

    // Play data
    @SerializedName("PLAY_NFO")
    private volatile XRemotePlayInfo playInfo;

    public XRemoteResponseData() {
        this.apiVersion = XRemoteData.XREMOTE_API_VERSION;
    }

    public XRemoteResponseData(XRemoteResponse response) {
        this.apiVersion = XRemoteData.XREMOTE_API_VERSION;
        this.response = response;
    }

    public XRemoteResponseData(XRemoteResponse response, XRemotePlayInfo playInfo) {
        this(response);
        this.playInfo = playInfo;
    }
}
