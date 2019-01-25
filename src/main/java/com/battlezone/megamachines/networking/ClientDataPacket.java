package com.battlezone.megamachines.networking;

import java.util.ArrayList;

public class ClientDataPacket {
    // Definition of data needed to be sent TODO: add more data
    private ArrayList<Integer> keyPresses;
    private int timestampValue;


    // Constructor
    public ClientDataPacket() {
        keyPresses = new ArrayList<>();
        timestampValue = 0;
    }

    // Set timestampValue method in case of bad sync
    public void setTimestampValue(int timestampValue) {
        this.timestampValue = timestampValue;
    }

    // Get timestampValue method
    public int getTimestampValue() {
        return this.timestampValue;
    }

    // Set method for keyPresses
    public void setKeyPresses(ArrayList<Integer> keyPressesInt) {
        keyPresses = keyPressesInt;
    }

    // Get method for keyPresses
    public ArrayList<Integer> getKeyPresses() {
        return this.keyPresses;
    }

    // Add key to keyPresses
    public boolean addKeyPress(Integer keyPress) {
        if (!keyPresses.contains(keyPress)) {
            keyPresses.add(keyPress);
            return true;
        }
        return false;
    }

    // Remove key from keyPresses
    public boolean removeKeyPress(Integer keyPress) {
        return keyPresses.remove(keyPress);
    }

    // Conversion method from data to string
    public String toString() {
        // Convert each variable to string
        String keyPressString = "k:";
        for (int i = 0; i < keyPresses.size(); i++) {
            keyPressString += keyPresses.get(i);
            if ( i + 1 != keyPresses.size() )
                keyPressString += ",";
        }
        keyPressString += ";";
        String timestampString = "t:" + this.timestampValue + ";";
        // Convert rest of data to string TODO: add more data to be sent

        // Add all strings together
        String UserDataUDPString = timestampString + keyPressString + ".";

        // Return string
        return UserDataUDPString;
    }

    // Update timestamp
    public void updateTimestamp() {
        timestampValue += 1;
    }

    // Conversion from string to ClientDataPacket
    public static ClientDataPacket fromString(String msg) {
        ClientDataPacket tmp = new ClientDataPacket();
        String[] values = msg.split(";");

        // For each element, get data
        for ( int i = 0; i < values.length; i++ ) {
            if ( values[i].length() < 1 ) {
                break;
            }
            if ( values[i].charAt(0) == '.' ) {
                break;
            }
            if ( values[i].charAt(1) == ':' ) {
                String newString = values[i].substring(2);

                if ( 'k' == values[i].charAt(0) ) {
                    String[] tmpAL = newString.split(",");
                    ArrayList<Integer> tmpALI = new ArrayList<>();
                    for ( int j = 0; j < tmpAL.length; j++ )
                        if (tmpAL[j].length() >= 1)
                            tmpALI.add(Integer.parseInt(tmpAL[j]));
                    tmp.setKeyPresses(tmpALI);

                } else if ( 't' == values[i].charAt(0) ) {
                    if ( values[i].length() < 3 ) {
                        try {
                            throw new Exception("Wrong formatting of ClientDataPacket string conversion: missing timestamp.");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return tmp;
                    }
                    tmp.setTimestampValue(Integer.parseInt(newString));

                } else {
                    try {
                        throw new Exception("Wrong formatting of ClientDataPacket string conversion: first letter is " + values[i].charAt(0) + " which is unknown." + msg);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return tmp;
                }
            } else {
                try {
                    throw new Exception("Wrong formatting of ClientDataPacket string conversion: second letter is not character ':'.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return tmp;
            }
        }

        // Finally return the data
        return tmp;
    }
}
