package xyz.xremote.models.remote;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
public class XRemotePlayInfo {
    @SerializedName("TITLE")
    private volatile String title;
    @SerializedName("SUBTITLE")
    private volatile String subtitle;
    @SerializedName("WINDOW")
    private volatile int window;
    @SerializedName("RESIZE_MODE")
    private volatile int resizeMode = -1;
    @SerializedName("VOLUME_LEVEL")
    private volatile int volumeLevel = -1;
    @SerializedName("POSITION")
    private volatile long position;
    @SerializedName("SEEK_BY_MILLIS")
    private volatile long seekByMillis;
    @SerializedName("DURATION")
    private volatile long duration;
    @SerializedName("QUALITY")
    private volatile String quality;
    @SerializedName("QUALITIES")
    private volatile List<String> qualities;
    @SerializedName("PLAYBACK_SPEED")
    private volatile float playbackSpeed;
    @SerializedName("IS_PLAYING")
    private volatile boolean isPlaying;
    @SerializedName("IS_MUTED")
    private volatile boolean isMuted;
    @SerializedName("IS_SUBTITLES_AVAILABLE")
    private volatile boolean isSubsAvailable;
    @SerializedName("IS_SUBTITLES_ENABLED")
    private volatile boolean isSubsEnabled;
}
