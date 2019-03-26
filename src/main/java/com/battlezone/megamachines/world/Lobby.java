package com.battlezone.megamachines.world;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.events.game.*;
import com.battlezone.megamachines.events.ui.ErrorEvent;
import com.battlezone.megamachines.messaging.EventListener;
import com.battlezone.megamachines.messaging.MessageBus;
import com.battlezone.megamachines.networking.Protocol;
import com.battlezone.megamachines.networking.client.Client;
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
    private boolean isHost = false;
    private int playerNumber;
    private final long gameWindow;
    private boolean running = true;

    private List<RWDCar> players;
    private int port = 0;

    public Lobby(InetAddress serverAddress, byte roomNumber) throws IOException {
        MessageBus.register(this);

        this.client = new Client(serverAddress, roomNumber);

        this.gameWindow = Window.getWindow().getGameWindow();
        this.lobbyMenu = new BaseMenu();
        this.lobby = new LobbyScene(lobbyMenu, Colour.WHITE, Colour.BLUE, new MenuBackground(), this::startGame, () -> running = false);
        this.lobbyMenu.navigationPush(this.lobby);

        run();
    }

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

    private void startGame(Triple<Track, Theme, Integer> options) {
        client.setTrack(options.getFirst());
        ThemeHandler.setTheme(options.getSecond());
        client.startGame();
        lobbyMenu.hide();
    }

    private void startWithTrack(byte[] trackUpdates, byte[] managerUpdates) {
        if (players == null || port == 0) {
            System.err.println("Received track before players or port. Fatal");
            System.exit(-1);
        } else {
            MessageBus.fire(new GameStateEvent(GameStateEvent.GameState.PLAYING));
            BaseWorld world = new MultiplayerWorld(players, Track.fromByteArray(trackUpdates, 1), playerNumber, 0, managerUpdates);
            synchronized (client) {
                client.notify();
            }
            lobbyMenu.popToRoot();
            lobbyMenu.hide();
            boolean realQuit = world.start();
            if (!realQuit) {
                lobby.showLeaderboard(world.cars);
                lobbyMenu.show();
            } else {
                running = false;
            }
        }
    }

    private void showPlayerUpdates(byte[] playerUpdates) {
        players = RWDCar.fromByteArray(playerUpdates, 1);
        if (!isHost && playerNumber == 0) {
            isHost = true;
            lobby.setupHost();
        }

        lobby.setPlayerModels(players);
    }

    @EventListener
    public void updatePlayers(PlayerUpdateEvent event) {
        System.out.println("Player update received");
        System.out.println(Arrays.toString(event.getData()));
        playerUpdates.add(event.getData());
        playerNumber = event.getPlayerNumber();
    }

    @EventListener
    public void updateTrack(TrackUpdateEvent event) {
        System.out.println("Track and powerup manager update received");
        trackUpdates.add(event.getTrackData());
        trackUpdates.add(event.getManagerData());
    }

    @EventListener
    public void updatePort(PortUpdateEvent event) {
        System.out.println("Port update received");
        portUpdates.add(event.getData());
    }

    @EventListener
    public void updateFail(FailRoomEvent event) throws InterruptedException {
        System.out.println("FAIL ROOM");
        this.running = false;
        Thread.sleep(100);
        System.out.println("Fail event");
        MessageBus.fire(new ErrorEvent("ROOM ERROR", "FAILED TO JOIN ROOM", 2));
    }
}
