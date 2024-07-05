package xyz.xremote.models.player;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerInfo {
    String contentId;
    String episodeId;
    float position;
    long progress;
    long length;
    int volume;
    boolean mute;
    boolean playing;
}
