package com.battlezone.megamachines.networking;

import com.battlezone.megamachines.networking.client.Client;
import com.battlezone.megamachines.networking.server.Server;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;

public class NetworkingTest {

    private Client client;
    private Server server;
    private byte roomNumber = 12;
    private InetAddress address;
    private Thread serverThread;

    @Before
    public void setUp() throws IOException {
        address = InetAddress.getByName("127.0.0.1");
    }

    @Test(expected = ConnectException.class)
    public void testException() throws IOException {
        new Client(address, (byte) 0);
    }

    @Test
    public void launchClient() throws IOException {
        serverThread = new Thread(()-> {
            Server.main(new String[0]);
            this.server = Server.server;
        });
        serverThread.start();

//        client = new Client(address, roomNumber);
//        client.keyPressRelease(null);
//        client.setTrack(null);
//        client.setRoomNumber((byte) 0);
//        client.run();
//        client.close();
    }

    @After
    public void close() {

    }
}
