package com.battlezone.megamachines.world;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.events.game.GameUpdateEvent;
import com.battlezone.megamachines.input.GameInput;
import com.battlezone.megamachines.messaging.EventListener;
import com.battlezone.megamachines.messaging.MessageBus;
import com.battlezone.megamachines.physics.PhysicsEngine;
import com.battlezone.megamachines.renderer.Window;
import com.battlezone.megamachines.renderer.game.Background;
import com.battlezone.megamachines.renderer.game.Camera;
import com.battlezone.megamachines.renderer.game.Renderer;
import com.battlezone.megamachines.renderer.game.TrackSet;
import com.battlezone.megamachines.renderer.ui.Minimap;
import com.battlezone.megamachines.renderer.ui.Scene;
import com.battlezone.megamachines.world.track.Track;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class World {

    private static final float CAM_WIDTH = 25f;
    private static final float CAM_HEIGHT = 25f;
    private final List<RWDCar> cars;
    private final Track track;
    private final Renderer renderer;
    private final Scene hud;
    private final Camera camera;
    private final RWDCar target;
    private final Background background;
    private final Queue<GameUpdateEvent> gameUpdates;
    private final long window;
    private final GameInput input;
    private boolean running = true;

    public World(List<RWDCar> cars, Track track, int playerNumber) {
        MessageBus.register(this);

        this.cars = cars;
        this.track = track;
        this.camera = new Camera(Window.getWindow().getAspectRatio() * CAM_WIDTH, CAM_HEIGHT);
        this.renderer = new Renderer(camera);

        this.background = new Background();
        this.renderer.addRenderable(background);

        TrackSet trackSet = new TrackSet();
        trackSet.setTrack(track);

        cars.forEach(this.renderer::addRenderable);
        cars.forEach(PhysicsEngine::addCar);
        this.renderer.addRenderable(trackSet);

        this.target = cars.get(playerNumber);

        this.gameUpdates = new ConcurrentLinkedQueue<>();

        this.window = Window.getWindow().getGameWindow();

        this.input = new GameInput();
        glfwSetKeyCallback(window, input);

        this.hud = new Scene();
        hud.addElement(new Minimap(track, cars));

    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void start() {

        double previousTime = System.nanoTime();
        double frametime = 0;
        int frames = 0;

        while (!glfwWindowShouldClose(window) && running) {
            glfwPollEvents();

            double currentTime = System.nanoTime();
            double interval = currentTime - previousTime;
            frametime += interval;
            frames += 1;
            previousTime = currentTime;

            while (gameUpdates.peek() != null) {
                update(gameUpdates.poll());
            }

            glClearColor(0.0f, .6f, 0.0f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);

            background.setX(target.getXf() / 10f);
            background.setY(target.getYf() / 10f);
            camera.setPosition(target.getXf(), target.getYf(), 0);

            PhysicsEngine.crank();

            renderer.render();
            hud.render();

            if (frametime >= 1000000000) {
                frametime = 0;
                System.out.println("FPS: " + frames);
                frames = 0;
            }

            glfwSwapBuffers(window);
        }
    }

    private void update(GameUpdateEvent update) {
        ByteBuffer buffer = update.getBuffer();
        byte playerCount = buffer.get(1);

        int playerNumber = 0;
        for (int i = 2; i < playerCount * 32; i += 32) {
            RWDCar player = cars.get(playerNumber);

            player.setX(buffer.getDouble(i));
            player.setY(buffer.getDouble(i + 8));
            player.setAngle(buffer.getDouble(i + 16));
            player.setSpeed(buffer.getDouble(i + 24));

            playerNumber++;
        }

        GameUpdateEvent.delete(update);
    }

    @EventListener
    public void receiveGameUpdates(GameUpdateEvent gameUpdate) {
        gameUpdates.add(gameUpdate);
    }


}
