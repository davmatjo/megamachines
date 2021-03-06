package com.battlezone.megamachines.networking.server.game;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.events.keys.NetworkKeyEvent;
import com.battlezone.megamachines.input.KeyCode;
import com.battlezone.megamachines.networking.secure.Encryption;
import com.battlezone.megamachines.networking.secure.Protocol;
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

import static com.battlezone.megamachines.networking.secure.Protocol.*;

public class GameRoom implements Runnable {

    // Player data
    private final ByteBuffer gameStateBuffer;
    private final ByteBuffer gameCountdownBuffer;

    // UDP connection
    private DatagramSocket socket;
    private DatagramPacket receive;
    private DatagramPacket send;
    private int PORT;
    private byte i = 0;
    private Map<InetAddress, Player> players;

    // Room variables
    private LobbyRoom lobbyRoom;
    private Game game;

    // Variables
    private boolean running = true;
    private byte[] received;


    /*
     * Main constructor of the GameRoom.
     *
     * @param Map<InetAddress, Player>   Map from internet address to each player from the lobby
     * @param LobbyRoom                  The parent of this class which is the lobby room
     * @param int                        The room number
     * @param int                        The AI count
     * */
    public GameRoom(Map<InetAddress, Player> playerAddresses, LobbyRoom lobbyRoom, int roomNumber, int aiCount) throws IOException {
        // Setting variables
        this.gameStateBuffer = ByteBuffer.allocate(Server.SERVER_TO_CLIENT_LENGTH);
        this.gameCountdownBuffer = ByteBuffer.allocate(2);
        this.PORT = Protocol.DEFAULT_PORT + (byte) (roomNumber * 2);
        this.lobbyRoom = lobbyRoom;
        this.players = playerAddresses;

        // Create and initialise game
        game = new Game(new ArrayList<>() {{
            playerAddresses.values().forEach((x) -> add(x.getCar()));
        }}, this, aiCount, lobbyRoom.getTrack(), lobbyRoom.getLapCounter());
        gameInit();

        // Setting server components
        this.received = new byte[16];
        this.socket = new DatagramSocket(this.PORT);
        this.receive = new DatagramPacket(new byte[16], 16);
        this.send = new DatagramPacket(new byte[Server.SERVER_TO_CLIENT_LENGTH], Server.SERVER_TO_CLIENT_LENGTH, null, this.PORT + 1);
    }

    /*
     * Get if GameRoom is running method.
     *
     * @return boolean   True if it is running and false if not
     * */
    public boolean getRunning() {
        return this.running;
    }

    /*
     * Set running method.
     *
     * @param boolean    True if GameRoom is set to run and false if not.
     * */
    public void setRunning(boolean running) {
        this.running = running;
    }

    /*
     * Get list of cars method.
     *
     * @return List<RWDCar>  List of cars to be returned.
     * */
    public List<RWDCar> getCars() {
        return game.getCars();
    }

    /*
     * Method to initialise the game.
     */
    public void gameInit() {
        lobbyRoom.sendPortToAll();
        lobbyRoom.sendPlayers(game.getCars());
        lobbyRoom.sendTrack(game.getTrack());
        lobbyRoom.sendPowerupManager(game.getManager());
        this.running = true;
    }

    /*
     * Method to recycle all players when game has ended to prevent keeping attributes from previous game.
     * */
    public void recyclePlayerCars() {
        for (Player p : players.values())
            p.recycleCar();
    }

    /*
     * Method to send the game state about each car.
     *
     * @param List<RWDCar>   List of cars to be sent
     * */
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
                    .put((byte) car.getCurrentlyPlaying())
                    .put(car.getCurrentPowerup() == null ? 0 : car.getCurrentPowerup().getID());
        }
        // Send the data to all the players
        for (InetAddress playerAddress : players.keySet())
            sendPacket(playerAddress, gameStateBuffer.array());
        gameStateBuffer.clear();
    }

    /*
     * Method to send end race message.
     * */
    public void sendEndRace() {
        // Set data to game state
        byte[] buffer = new byte[1];
        buffer[0] = END_RACE;

        // Send the data to all the players
        for (InetAddress playerAddress : players.keySet())
            sendPacket(playerAddress, buffer);
    }

    /*
     * Method to send count down message.
     *
     * @param int    The count to be sent
     * */
    public void sendCountDown(int count) {
        gameStateBuffer.put(GAME_COUNTDOWN).put((byte) count);
        for (InetAddress playerAddress : players.keySet()) {
            sendPacket(playerAddress, gameStateBuffer.array());
        }
        gameStateBuffer.clear();
    }

    /*
     * Method to send when a player has finished
     *
     * @param int    Player number of the finisher
     * @param byte   The position of the player
     * */
    public void sendPlayerFinish(int playerNumber, byte position) {
        gameStateBuffer.put(PLAYER_FINISH).put((byte) playerNumber).put(position);
        for (InetAddress playerAddress : players.keySet()) {
            sendPacket(playerAddress, gameStateBuffer.array());
        }
        gameStateBuffer.clear();
    }

    /*
     * Method to send when a powerup has been activated.
     *
     * @param RWDCar The car that activated the powerup
     * */
    public void sendPowerup(RWDCar car) {
        byte[] data = ByteBuffer.allocate(3)
                .put(POWERUP_EVENT)
                .put((byte) getCars().indexOf(car))
                .put(car.getCurrentPowerup() == null ? 0 : car.getCurrentPowerup().getID()).array();

        for (InetAddress player : players.keySet())
            sendPacket(player, data);
    }

    /*
     * Send packet method.
     *
     * @param InetAddress    The address to send the packet to
     * @param byte[]         The data to send the data to
     * */
    private void sendPacket(InetAddress address, byte[] data) {
        try {
            send.setAddress(address);
            send.setData(Encryption.encrypt(data));
            socket.send(send);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * Method to close the Thread of GameRoom.
     * */
    public void close() {
        socket.close();
        game.close();
        this.running = false;
    }

    /*
     * Method to send to all players when game has ended.
     *
     * @param List<RWDCar>   The list of final positions of cars
     * @param List<RWDCar>   The list of all the cars
     * */
    void end(List<RWDCar> finalPositions, List<RWDCar> cars) {
        // Send end race datagram packets a bunch of times
        for (int i = 0; i < 10; i++)
            sendEndRace();
        close();
        lobbyRoom.gameEnded(finalPositions, cars);
    }

    /*
     * Method to remove a car.
     *
     * @param RWDCar     Car to be removed
     * */
    public void remove(RWDCar car) {
        game.removePlayer(car);
    }

    /*
     * Method to run the Thread of the GameRoom.
     */
    @Override
    public void run() {
        (new Thread(game)).start();

        while (running) {
            // Receive the package
            try {
                socket.receive(receive);
            } catch (IOException e) {
                System.out.println("Room " + (PORT - DEFAULT_PORT) / 2 + "'s socket stopped receiving UDP packets.");
                return;
            }
            received = receive.getData();

            try {
                received = Encryption.decrypt(received);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Case when packet specifies key info
            if (received[0] == KEY_EVENT) {
                int eventKeyCode = received[2];
                game.keyPress(new NetworkKeyEvent(eventKeyCode, received[1] == KEY_PRESSED, players.get(receive.getAddress()).getCar()));
                if (received[1] == KEY_PRESSED && eventKeyCode == KeyCode.SPACE)
                    sendPowerup(players.get(receive.getAddress()).getCar());
            }
        }
    }
}
