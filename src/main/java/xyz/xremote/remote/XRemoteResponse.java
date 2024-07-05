package xyz.xremote.remote;

public enum XRemoteResponse {
    UNKNOWN,
    ALIVE,
    COMMAND_SUCCESS,
    COMMAND_FAIL,
    PLAYER_NOT_RUNNING,
    UNKNOWN_COMMAND,
    VERSION_INCOMPATIBLE,
    EMPTY_RESPONSE,
    EXCEPTION
}