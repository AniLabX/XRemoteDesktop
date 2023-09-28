package com.crazyxacker.apps.xremote.models.remote;

import com.crazyxacker.apps.xremote.remote.XRemoteResponse;
import com.crazyxacker.apps.xremote.remote.XRemoteServer;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class XRemoteResponseData {
    private int apiVersion;
    private XRemoteResponse response;
    private volatile XRemotePlayInfo playInfo;

    public XRemoteResponseData(XRemoteResponse response) {
        this.apiVersion = XRemoteServer.API_VERSION;
        this.response = response;
    }
}
