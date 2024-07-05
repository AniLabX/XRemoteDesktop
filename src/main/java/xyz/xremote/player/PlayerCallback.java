package xyz.xremote.player;

import xyz.xremote.models.player.PlayerInfo;

public interface PlayerCallback {
    void onProgressUpdate(PlayerInfo playerInfo);
    void onPlayerClose(PlayerInfo playerInfo);
}
