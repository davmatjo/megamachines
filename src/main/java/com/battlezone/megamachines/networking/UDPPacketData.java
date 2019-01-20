package com.battlezone.megamachines.networking;

public class UDPPacketData {
    // Definition of data needed to be sent TODO: add more data
    private String keyPresses;
    private int timestampValue;


    // Constructor
    public UDPPacketData() {
        keyPresses = "";
        timestampValue = -1;
    }

    // Set method for keyPresses
    public void setKeyPresses(String keyPresses) {
        this.keyPresses = keyPresses;

        // Update timestampValue when new keys are pressed
        timestampValue += 1;
    }

    // Set timestampValue method in case of bad sync
    public void setTimestampValue(int timestampValue) {
        this.timestampValue = timestampValue;
    }

    // Conversion method from data to string
    public String toString() {
        // Convert each variable to string
        String keyPressString = "keypresses:" + this.keyPresses + ";";
        String timestampString = "t:" + this.timestampValue + ";";
        // Convert rest of data to string TODO: add more data to be sent

        // Add all strings together
        String UserDataUDPString = timestampString + keyPressString + ".";

        // Return string
        return UserDataUDPString;
    }
}
