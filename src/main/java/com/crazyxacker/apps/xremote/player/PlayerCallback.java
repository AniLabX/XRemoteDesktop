package com.crazyxacker.apps.xremote.player;

import com.crazyxacker.apps.xremote.models.player.PlayerInfo;

public interface PlayerCallback {
    void onProgressUpdate(PlayerInfo playerInfo);
    void onPlayerClose(PlayerInfo playerInfo);
}
