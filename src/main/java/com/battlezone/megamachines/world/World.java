package com.battlezone.megamachines.world;

import com.battlezone.megamachines.ai.Driver;
import com.battlezone.megamachines.ai.TrackRoute;
import com.battlezone.megamachines.entities.Cars.DordConcentrate;
import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.events.game.GameUpdateEvent;
import com.battlezone.megamachines.input.GameInput;
import com.battlezone.megamachines.math.Vector3f;
import com.battlezone.megamachines.messaging.EventListener;
import com.battlezone.megamachines.messaging.MessageBus;
import com.battlezone.megamachines.physics.PhysicsEngine;
import com.battlezone.megamachines.renderer.Window;
import com.battlezone.megamachines.renderer.game.Background;
import com.battlezone.megamachines.renderer.game.Camera;
import com.battlezone.megamachines.renderer.game.Renderer;
import com.battlezone.megamachines.renderer.game.TrackSet;
import com.battlezone.megamachines.renderer.ui.Colour;
import com.battlezone.megamachines.renderer.ui.Minimap;
import com.battlezone.megamachines.renderer.ui.Scene;
import com.battlezone.megamachines.world.track.Track;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class World {

    private static final float CAM_WIDTH = 25f;
    private static final float CAM_HEIGHT = 25f;
    private final List<RWDCar> cars;
    private final List<Driver> AIs;
    private final Track track;
    private final Renderer renderer;
    private final Scene hud;
    private final Camera camera;
    private final RWDCar target;
    private final Background background;
    private final Queue<GameUpdateEvent> gameUpdates;
    private final long window;
    private final GameInput input;
//    private final Race race;
    private boolean running = true;

    public World(List<RWDCar> cars, Track track, int playerNumber, int aiCount) {
        MessageBus.register(this);

        Random r = new Random();
        this.AIs = new ArrayList<>() {{
            TrackRoute route = new TrackRoute(track);
            for (int i=0; i<aiCount; i++) {

                RWDCar ai = new DordConcentrate(
                        track.getStartPiece().getX() + 2 + i*2,
                        track.getStartPiece().getY(),
                        ScaleController.RWDCAR_SCALE,
                        1 + r.nextInt(2),
                        new Vector3f(r.nextFloat(), r.nextFloat(), r.nextFloat()));
                cars.add(ai);
                add(new Driver(route, ai));
            }
        }};

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

//        this.race = new Race(track, 3, cars);

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

            glClear(GL_COLOR_BUFFER_BIT);

            background.setX(target.getXf() / 10f);
            background.setY(target.getYf() / 10f);
            camera.setPosition(target.getXf(), target.getYf(), 0);

            for (int i=0; i<AIs.size(); i++) {
                AIs.get(i).update();
            }

            PhysicsEngine.crank(interval / 1000000);
//            race.update();

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
