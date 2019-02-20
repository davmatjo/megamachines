package com.battlezone.megamachines.networking;

public class Protocol {

    // Define constant server event types for Client to Server packets -> on byte 0
    public static final byte JOIN_LOBBY = 0;
    public static final byte START_GAME = 1;
    public static final byte KEY_EVENT =  2;
    public static final byte EXIT_LOBBY = 3;
    public static final byte EXIT_GAME =  4;

    // Define constant key event types for Client to Server packets -> on byte 1
    public static final byte KEY_PRESSED =  0;
    public static final byte KEY_RELEASED = 1;

    // Define constant for types of packets for Server to Client -> on byte 0
    public static final byte GAME_STATE =  0;
    public static final byte TRACK_TYPE =  1;
    public static final byte PLAYER_INFO = 2;
    public static final byte UDP_DATA =    3;
    public static final byte FAIL_CREATE = 4;
    public static final byte GAME_COUNTDOWN = 5;
    public static final byte END_RACE = 6;
    public static final byte END_GAME = 7;

    // Define default port for game rooms
    public static final int DEFAULT_PORT = 7200;
}
