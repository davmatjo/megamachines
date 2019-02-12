package com.battlezone.megamachines.networking;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.events.keys.NetworkKeyEvent;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

import static com.battlezone.megamachines.networking.Protocol.KEY_EVENT;
import static com.battlezone.megamachines.networking.Protocol.KEY_PRESSED;

public class GameRoom implements Runnable {

    private boolean running;
    private NewServer server;
    byte[] received;
    private Game game;
    private DatagramSocket socket;
    private DatagramPacket receive;
    private DatagramPacket send;
    private int PORT;
    private final ByteBuffer gameStateBuffer;

    public GameRoom(NewServer server, Map<InetAddress, Player> players, int aiCount, byte room) throws IOException {
        this.running = true;
        this.server = server;
        this.gameStateBuffer = ByteBuffer.allocate(NewServer.MAX_PLAYERS * 32 + 2);
        this.PORT = Protocol.DEFAULT_PORT + room;

        game = new Game(players, this, aiCount);
        server.sendPortToAll(players);
        server.sendPlayers(players, game.getCars());
        server.createAndSendTrack(game, players);

        this.received = new byte[NewServer.CLIENT_TO_SERVER_LENGTH];
        this.socket = new DatagramSocket(this.PORT);
        this.receive = new DatagramPacket(new byte[NewServer.CLIENT_TO_SERVER_LENGTH], NewServer.CLIENT_TO_SERVER_LENGTH);
        this.send = new DatagramPacket(new byte[NewServer.SERVER_TO_CLIENT_LENGTH], NewServer.SERVER_TO_CLIENT_LENGTH, null, this.PORT+1);
    }

    public boolean getRunning() {
        return this.running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public int getPORT() {
        return this.PORT;
    }

    public void sendGameState(Map<InetAddress, Player> players, List<RWDCar> cars) {
        // Set data to game state
        gameStateBuffer.put(Protocol.GAME_STATE).put((byte) cars.size());
        for ( RWDCar car : cars )
            gameStateBuffer.putDouble(car.getX()).putDouble(car.getY()).putDouble(car.getAngle()).putDouble(car.getSpeed());
        byte[] data = gameStateBuffer.array();
        gameStateBuffer.clear();

        // Send the data to all the players
        for (InetAddress playerAddress : players.keySet())
            sendPacket(playerAddress, data);
    }

    private void sendPacket(InetAddress address, byte[] data) {
        try {
            send.setAddress(address);
            send.setData(data);
            socket.send(send);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        this.running = false;
    }

    @Override
    public void run() {
        (new Thread(game)).start();
        while (running) {
            // Receive the package
            try {
                socket.receive(receive);
            } catch (IOException e) {
                e.printStackTrace();
            }
            received = receive.getData();

            // Case when packet specifies key info
            if (received[0] == KEY_EVENT) {
                int eventKeyCode = received[2];
                game.keyPress(new NetworkKeyEvent(eventKeyCode, received[1] == KEY_PRESSED, receive.getAddress()));
            }
        }
    }
}
