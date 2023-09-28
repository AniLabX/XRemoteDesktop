package com.crazyxacker.apps.xremote.models.remote;

import lombok.Data;

import java.util.List;

@Data
public class XRemotePlayInfo {
    private volatile String title;
    private volatile String subtitle;
    private volatile int window;
    private volatile int resizeMode;
    private volatile int volumeLevel = -1;
    private volatile long position;
    private volatile long seekByMillis;
    private volatile long duration;
    private volatile String quality;
    private volatile List<String> qualities;
    private volatile float playbackSpeed;
    private volatile boolean isPlaying;
    private volatile boolean isMuted;
    private volatile boolean isSubsAvailable;
    private volatile boolean isSubsEnabled;
}
