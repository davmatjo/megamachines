package com.battlezone.megamachines.networking;

public class Protocol {
    // Define the states of the server
    enum State{LOBBY, IN_GAME}

    // Define constant server event types for Client to Server packets -> on byte 0
    static final byte JOIN_LOBBY = 0;
    static final byte START_GAME = 1;
    static final byte KEY_EVENT =  2;

    // Define constant key event types for Client to Server packets -> on byte 1
    static final byte KEY_PRESSED =  0;
    static final byte KEY_RELEASED = 1;

    // Define constant for types of packets for Server to Client -> on byte 0
    static final byte GAME_STATE =  0;
    static final byte TRACK_TYPE =  1;
    static final byte PLAYER_INFO = 2;
}
