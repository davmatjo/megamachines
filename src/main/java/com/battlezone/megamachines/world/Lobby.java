package com.battlezone.megamachines.world;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.events.game.FailRoomEvent;
import com.battlezone.megamachines.events.game.PlayerUpdateEvent;
import com.battlezone.megamachines.events.game.PortUpdateEvent;
import com.battlezone.megamachines.events.game.TrackUpdateEvent;
import com.battlezone.megamachines.events.ui.ErrorEvent;
import com.battlezone.megamachines.input.Cursor;
import com.battlezone.megamachines.math.Vector4f;
import com.battlezone.megamachines.messaging.EventListener;
import com.battlezone.megamachines.messaging.MessageBus;
import com.battlezone.megamachines.networking.Client;
import com.battlezone.megamachines.networking.Protocol;
import com.battlezone.megamachines.networking.Server;
import com.battlezone.megamachines.renderer.Window;
import com.battlezone.megamachines.renderer.ui.Box;
import com.battlezone.megamachines.renderer.ui.Button;
import com.battlezone.megamachines.renderer.ui.Colour;
import com.battlezone.megamachines.renderer.ui.Scene;
import com.battlezone.megamachines.util.AssetManager;
import com.battlezone.megamachines.world.track.Track;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL30.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL30.glClear;

public class Lobby {

    private static final float PLAYER_AVATAR_WIDTH = 0.4f;
    private static final float PLAYER_AVATER_HEIGHT = 0.2f;
    private static final float PLAYER_AVATAR_POSITION_OFFSET = 0.5f;
    private static final float PLAYER_AVATAR_X = -1f;
    private static final float PLAYER_AVATAR_Y_TOP = 0.5f;
    private static final float PLAYER_AVATAR_Y_BOTTOM = 0f;
    private static final float PADDING = 0.1f;

    private static final float BUTTON_ROW_HEIGHT = 0.3f;
    private static final float BUTTON_WIDTH = 1f;
    private static final float BUTTON_ROW_Y = -0.5f;
    private static final float CENTRAL_BUTTON_X = -BUTTON_WIDTH / 2;
    private static final float RIGHT_BUTTON_X = PADDING / 2;
    private static final float LEFT_BUTTON_X = -BUTTON_WIDTH - PADDING / 2;

    private final Queue<byte[]> playerUpdates = new ConcurrentLinkedQueue<>();
    private final Queue<byte[]> trackUpdates = new ConcurrentLinkedQueue<>();
    private final Queue<byte[]> portUpdates = new ConcurrentLinkedQueue<>();
    private final Scene lobby;
    private final Client client;
    private final Cursor cursor;
    private boolean isHost = false;
    private int playerNumber;
    private final long gameWindow;
    private boolean running = true;

    public Lobby(InetAddress serverAddress, byte roomNumber) throws IOException {
        MessageBus.register(this);

        this.gameWindow = Window.getWindow().getGameWindow();
        this.lobby = new Scene();

        this.cursor = Cursor.getCursor();

        this.client = new Client(serverAddress, roomNumber);
        run();
    }

    private void run() {
        List<RWDCar> players = null;
        List<Box> models = new ArrayList<>();
        int port = 0;

        Button quit = new Button(BUTTON_WIDTH, BUTTON_ROW_HEIGHT, CENTRAL_BUTTON_X, BUTTON_ROW_Y, Colour.WHITE, Colour.BLUE, "QUIT", PADDING);
        quit.setAction(() -> running = false);
        lobby.addElement(quit);

        while (!glfwWindowShouldClose(gameWindow) && running) {
            glfwPollEvents();

            byte[] playerUpdates = this.playerUpdates.poll();
            if (playerUpdates != null) {
                players = RWDCar.fromByteArray(playerUpdates, 1);
                if (!isHost && playerNumber == 0) {
                    isHost = true;

                    Button start = new Button(BUTTON_WIDTH, BUTTON_ROW_HEIGHT, RIGHT_BUTTON_X, BUTTON_ROW_Y, Colour.WHITE, Colour.BLUE, "START", PADDING);
                    start.setAction(client::startGame);

                    Button repositionedQuit = new Button(BUTTON_WIDTH, BUTTON_ROW_HEIGHT, LEFT_BUTTON_X, BUTTON_ROW_Y, Colour.WHITE, Colour.BLUE, "QUIT", PADDING);
                    repositionedQuit.setAction(() -> running = false);
                    lobby.removeElement(quit);
                    quit.hide();
                    quit.delete();
                    quit = null;
                    lobby.addElement(start);
                    lobby.addElement(repositionedQuit);

                }
                models.forEach(lobby::removeElement);
                models.forEach(Box::delete);
                models.clear();
                for (int i = 0; i < players.size(); i++) {
                    models.add(
                            new Box(
                                    PLAYER_AVATAR_WIDTH,
                                    PLAYER_AVATER_HEIGHT,
                                    PLAYER_AVATAR_X + (i % (int) Math.ceil((Server.MAX_PLAYERS / 2.0))) * PLAYER_AVATAR_POSITION_OFFSET,
                                    i > Math.ceil(Server.MAX_PLAYERS / 2.0) ? PLAYER_AVATAR_Y_BOTTOM : PLAYER_AVATAR_Y_TOP,
                                    new Vector4f(players.get(i).getColour(), 1f),
                                    AssetManager.loadTexture("/cars/car" + players.get(i).getModelNumber() + ".png")));
                }
                models.forEach(lobby::addElement);
            }

            byte[] portUpdates = this.portUpdates.poll();
            if (portUpdates != null) {
                port = Protocol.DEFAULT_PORT + portUpdates[1];
                client.setRoomNumber(portUpdates[1]);
            }

            byte[] trackUpdates = this.trackUpdates.poll();
            if (trackUpdates != null) {
                if (players == null || port == 0) {
                    System.err.println("Received track before players or port. Fatal");
                    System.exit(-1);
                } else {
                    BaseWorld world = new MultiplayerWorld(players, Track.fromByteArray(trackUpdates, 1), playerNumber, 0);
                    world.start();
                }
            }

            glClear(GL_COLOR_BUFFER_BIT);
            lobby.render();

            glfwSwapBuffers(gameWindow);
        }
        client.close();
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
        System.out.println("Track update received");
        trackUpdates.add(event.getData());
    }

    @EventListener
    public void updatePort(PortUpdateEvent event) {
        System.out.println("Port update received");
        portUpdates.add(event.getData());
    }

    @EventListener
    public void updateFail(FailRoomEvent event) throws InterruptedException {
        this.running = false;
        Thread.sleep(100);
        System.out.println("Fail event");
        MessageBus.fire(new ErrorEvent("ROOM ERROR", "FAILED TO JOIN ROOM", 2));
    }
}
