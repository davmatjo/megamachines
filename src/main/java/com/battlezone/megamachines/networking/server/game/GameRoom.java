package com.battlezone.megamachines.networking.server.game;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.events.keys.NetworkKeyEvent;
import com.battlezone.megamachines.input.KeyCode;
import com.battlezone.megamachines.networking.Protocol;
import com.battlezone.megamachines.networking.client.Client;
import com.battlezone.megamachines.networking.server.Server;
import com.battlezone.megamachines.networking.server.lobby.LobbyRoom;
import com.battlezone.megamachines.networking.server.player.Player;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.battlezone.megamachines.networking.Protocol.*;

public class GameRoom implements Runnable {

    // UDP connection
    private DatagramSocket socket;
    private DatagramPacket receive;
    private DatagramPacket send;
    private int PORT;
    private byte i = 0;

    // Player data
    private final ByteBuffer gameStateBuffer;
    private final ByteBuffer gameCountdownBuffer;
    private Map<InetAddress, Player> players;

    // Room variables
    private LobbyRoom lobbyRoom;
    private Game game;

    // Variables
    private boolean running = true;
    private byte[] received;


    public GameRoom(Map<InetAddress, Player> playerAddresses, LobbyRoom lobbyRoom, int roomNumber, int aiCount) throws IOException {
        // Setting variables
        this.gameStateBuffer = ByteBuffer.allocate(Server.SERVER_TO_CLIENT_LENGTH);
        this.gameCountdownBuffer = ByteBuffer.allocate(2);
        this.PORT = Protocol.DEFAULT_PORT + (byte)(roomNumber * 2);
        this.lobbyRoom = lobbyRoom;
        this.players = playerAddresses;

        // Create and initialise game
        game = new Game(new ArrayList<>() {{playerAddresses.values().forEach((x) -> add(x.getCar()));}}, this, aiCount);
        gameInit();

        // Setting server components
        this.received = new byte[Client.CLIENT_TO_SERVER_LENGTH];
        this.socket = new DatagramSocket(this.PORT);
        this.receive = new DatagramPacket(new byte[Client.CLIENT_TO_SERVER_LENGTH], Client.CLIENT_TO_SERVER_LENGTH);
        this.send = new DatagramPacket(new byte[Server.SERVER_TO_CLIENT_LENGTH], Server.SERVER_TO_CLIENT_LENGTH, null, this.PORT+1);
    }

    public boolean getRunning() {
        return this.running;
    }

    public List<RWDCar> getCars() {
        return game.getCars();
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void gameInit() {
        lobbyRoom.sendPortToAll();
        lobbyRoom.sendPlayers(game.getCars());
        lobbyRoom.sendTrack(game.getTrack());
        this.running = true;
    }

    public void sendGameState(List<RWDCar> cars) {
        // Set data to game state
        gameStateBuffer.put(Protocol.GAME_STATE).put((byte) cars.size()).put(i++);
        for (int c = 0; c < cars.size(); c++) {
            RWDCar car = cars.get(c);
            gameStateBuffer
                    .putDouble(car.getX())
                    .putDouble(car.getY())
                    .putDouble(car.getAngle())
                    .putDouble(car.getSpeed())
                    .putDouble(car.getLongitudinalWeightTransfer())
                    .putDouble(car.getAngularSpeed())
                    .putDouble(car.getSpeedAngle())
                    .putDouble(car.getFlWheel().getAngularVelocity())
                    .putDouble(car.getFrWheel().getAngularVelocity())
                    .putDouble(car.getBlWheel().getAngularVelocity())
                    .putDouble(car.getBrWheel().getAngularVelocity())
                    .putDouble(car.getEngine().getRPM())
                    .put(car.getGearbox().getCurrentGear())
                    .put(car.getLap())
                    .put(car.getPosition())
                    .put((byte) car.getCurrentlyPlaying());
        }
        // Send the data to all the players
        for (InetAddress playerAddress : players.keySet())
            sendPacket(playerAddress, gameStateBuffer.array());
        gameStateBuffer.clear();
    }

    public void sendEndRace() {
        // Set data to game state
        byte[] buffer = new byte[1];
        buffer[0] = END_RACE;

        // Send the data to all the players
        for (InetAddress playerAddress : players.keySet())
            sendPacket(playerAddress, buffer);
    }

    public void sendCountDown(int count) {
        gameStateBuffer.put(GAME_COUNTDOWN).put((byte) count);
        for (InetAddress playerAddress : players.keySet()) {
            sendPacket(playerAddress, gameStateBuffer.array());
        }
        gameStateBuffer.clear();
    }

    private void sendPowerup(InetAddress address) {
        byte[] data = ByteBuffer.allocate(3)
                .put(POWERUP_EVENT)
                .put((byte) getCars().indexOf(players.get(receive.getAddress()).getCar()))
                .put((byte) players.get(receive.getAddress()).getCar().getCurrentPowerup().id).array();
        for ( InetAddress player : players.keySet() )
            sendPacket(player, data);
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
        socket.close();
        game.close();
        this.running = false;
    }

    void end(List<RWDCar> finalPositions, List<RWDCar> cars) {
        // Send end race datagram packets a bunch of times
        for ( int i = 0; i < 100; i++ )
            sendEndRace();
        close();
        lobbyRoom.gameEnded(finalPositions, cars);
    }

    public void remove(RWDCar car) {
        game.removePlayer(car);
    }

    @Override
    public void run() {
        (new Thread(game)).start();

        while (running) {
            // Receive the package
            try {
                socket.receive(receive);
            } catch (IOException e) {
                System.out.println("Room " + (PORT - DEFAULT_PORT)/2 + "'s socket stopped receiving UDP packets.");
                return;
            }
            received = receive.getData();
            // Case when packet specifies key info
            if (received[0] == KEY_EVENT) {
                int eventKeyCode = received[2];
                game.keyPress(new NetworkKeyEvent(eventKeyCode, received[1] == KEY_PRESSED, players.get(receive.getAddress()).getCar()));
                if ( received[1] == KEY_PRESSED && eventKeyCode == KeyCode.SPACE)
                    sendPowerup(receive.getAddress());
            }
        }
    }
}
