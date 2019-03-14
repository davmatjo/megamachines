package com.battlezone.megamachines.networking;

import com.battlezone.megamachines.events.keys.KeyEvent;
import com.battlezone.megamachines.networking.client.Client;
import com.battlezone.megamachines.networking.server.Server;
import com.battlezone.megamachines.networking.server.ServerCleaner;
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
        client = new Client(address, (byte) 0);
    }

    @Test
    public void testCleaner() throws InterruptedException {
        ServerCleaner cleaner = new ServerCleaner();
        (new Thread(cleaner)).start();
        cleaner.close();
    }

    @Test
    public void testClient() throws IOException, InterruptedException {
        serverThread = new Thread(()-> {
            try {
                this.server = new Server();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            this.server.main(new String[0]);
        });
        serverThread.start();

        Thread.sleep(100);

        client = new Client(address, roomNumber);

        client.setRoomNumber((byte) 10);
        client.setTrack(null);
        client.keyPressRelease(new KeyEvent(0, true));
        client.startGame();

        client.close();
    }

    @After
    public void close() throws InterruptedException {
        if ( server != null )
            server.setRunning(false);
        if ( client != null )
            client.close();
    }
}
