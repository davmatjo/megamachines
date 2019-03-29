package com.battlezone.megamachines.world;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.events.game.*;
import com.battlezone.megamachines.events.ui.ErrorEvent;
import com.battlezone.megamachines.messaging.EventListener;
import com.battlezone.megamachines.messaging.MessageBus;
import com.battlezone.megamachines.networking.client.Client;
import com.battlezone.megamachines.networking.secure.Protocol;
import com.battlezone.megamachines.renderer.Window;
import com.battlezone.megamachines.renderer.theme.Theme;
import com.battlezone.megamachines.renderer.theme.ThemeHandler;
import com.battlezone.megamachines.renderer.ui.Colour;
import com.battlezone.megamachines.renderer.ui.menu.BaseMenu;
import com.battlezone.megamachines.renderer.ui.menu.LobbyScene;
import com.battlezone.megamachines.renderer.ui.menu.MenuBackground;
import com.battlezone.megamachines.util.Triple;
import com.battlezone.megamachines.world.track.Track;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL30.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL30.glClear;

public class Lobby {

    private final Queue<byte[]> playerUpdates = new ConcurrentLinkedQueue<>();
    private final Queue<byte[]> trackUpdates = new ConcurrentLinkedQueue<>();
    private final Queue<byte[]> portUpdates = new ConcurrentLinkedQueue<>();
    private final LobbyScene lobby;
    private final BaseMenu lobbyMenu;
    private final Client client;
    private final long gameWindow;
    private boolean isHost = false;
    private int playerNumber;
    private boolean running = true;
    private BaseWorld world;

    private List<RWDCar> players;
    private int port = 0;
    private byte laps;

    /*
     * Main constructor for Lobby on client side.
     *
     * @param InetAddress    Address of the server to connect to
     * @param byte           Room number to connect to
     * */
    public Lobby(InetAddress serverAddress, byte roomNumber) throws IOException {
        MessageBus.register(this);

        this.client = new Client(serverAddress, roomNumber);

        this.gameWindow = Window.getWindow().getGameWindow();
        this.lobbyMenu = new BaseMenu();
        this.lobby = new LobbyScene(lobbyMenu, Colour.WHITE, Colour.BLUE, new MenuBackground(), this::startGame, () -> running = false);
        this.lobbyMenu.navigationPush(this.lobby);

        run();
    }

    /*
     * Method to run the Lobby Thread on client side.
     * */
    private void run() {
        while (!glfwWindowShouldClose(gameWindow) && running) {
            glfwPollEvents();

            if (!playerUpdates.isEmpty()) {
                showPlayerUpdates(playerUpdates.poll());
            }

            if (!portUpdates.isEmpty()) {
                byte[] portUpdate = portUpdates.poll();
                port = Protocol.DEFAULT_PORT + portUpdate[1];
                client.setRoomNumber(portUpdate[1]);
            }

            if (!trackUpdates.isEmpty()) {
                byte[] eventTrack = trackUpdates.poll();
                byte[] eventManager = trackUpdates.poll();
                startWithTrack(eventTrack, eventManager);
            }

            glClear(GL_COLOR_BUFFER_BIT);
            lobbyMenu.render();

            glfwSwapBuffers(gameWindow);
        }
        client.close();
    }

    /*
     * Method to start game.
     *
     * @param Triple<Track, Theme, Integer>  Triple with the track, theme and lap counter */
    private void startGame(Triple<Track, Theme, Integer> options) {
        client.setTrack(options.getFirst());
        ThemeHandler.setTheme(options.getSecond());
        client.startGame(options.getThird());
        lobbyMenu.hide();
    }

    /*
     * Method to start with track that was just sent.
     *
     * @param byte[] Array of track updates
     * @param bte[]  Manager updates given
     * */
    private void startWithTrack(byte[] trackUpdates, byte[] managerUpdates) {
        if (players == null || port == 0) {
            System.err.println("Received track before players or port. Fatal");
            System.exit(-1);
        } else {
            MessageBus.fire(new GameStateEvent(GameStateEvent.GameState.PLAYING));
            world = new MultiplayerWorld(players, Track.fromByteArray(trackUpdates, 1), playerNumber, 0, managerUpdates, laps);
            synchronized (client) {
                client.notify();
            }
            lobbyMenu.popToRoot();
            lobbyMenu.hide();
            boolean realQuit = world.start(false);
            if (!realQuit) {
                lobby.showLeaderboard(world.cars);
                lobbyMenu.show();
            } else {
                running = false;
            }
        }
    }

    /*
     * Method to show player updated.
     *
     * @param byte[] List of player updates to be shown
     * */
    private void showPlayerUpdates(byte[] playerUpdates) {
        players = RWDCar.fromByteArray(playerUpdates, 1);
        if (!isHost && playerNumber == 0) {
            isHost = true;
            lobby.setupHost();
        }

        lobby.setPlayerModels(players);
    }

    /*
     * Method that listens for updates on player information.
     *
     * @param PlayerUpdateEvent  Event caught when player information was sent
     * */
    @EventListener
    public void updatePlayers(PlayerUpdateEvent event) {
        System.out.println("Player update received");
        System.out.println(Arrays.toString(event.getData()));
        playerUpdates.add(event.getData());
        playerNumber = event.getPlayerNumber();
    }

    /*
     * Method that listens for updates on track.
     *
     * @param TrackUpdateEvent  Event caught when track sent
     * */
    @EventListener
    public void updateTrack(TrackUpdateEvent event) {
        System.out.println("Track and powerup manager update received");
        trackUpdates.add(event.getTrackData());
        trackUpdates.add(event.getManagerData());
        this.laps = event.getLapCounter();
    }

    /*
     * Method that listens for updates on ports.
     *
     * @param PortUpdateEvent  Event caught when ports sent
     * */
    @EventListener
    public void updatePort(PortUpdateEvent event) {
        System.out.println("Port update received");
        portUpdates.add(event.getData());
    }

    /*
     * Method that listens for updates on fail.
     *
     * @param FailRoomEvent  Event caught when failed
     * */
    @EventListener
    public void updateFail(FailRoomEvent event) throws InterruptedException {
        System.out.println("FAIL ROOM");
        this.running = false;
        Thread.sleep(100);
        System.out.println("Fail event");
        MessageBus.fire(new ErrorEvent("ROOM ERROR", "FAILED TO JOIN ROOM", 2));
    }
}
