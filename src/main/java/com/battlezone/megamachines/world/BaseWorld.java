package com.battlezone.megamachines.world;

import com.battlezone.megamachines.ai.Driver;
import com.battlezone.megamachines.ai.TrackRoute;
import com.battlezone.megamachines.entities.Cars.DordConcentrate;
import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.input.GameInput;
import com.battlezone.megamachines.input.Gamepad;
import com.battlezone.megamachines.math.Vector3f;
import com.battlezone.megamachines.networking.Server;
import com.battlezone.megamachines.renderer.Texture;
import com.battlezone.megamachines.renderer.Window;
import com.battlezone.megamachines.renderer.game.Background;
import com.battlezone.megamachines.renderer.game.Camera;
import com.battlezone.megamachines.renderer.game.Renderer;
import com.battlezone.megamachines.renderer.game.TrackSet;
import com.battlezone.megamachines.renderer.ui.Box;
import com.battlezone.megamachines.renderer.ui.Colour;
import com.battlezone.megamachines.renderer.ui.Minimap;
import com.battlezone.megamachines.renderer.ui.Scene;
import com.battlezone.megamachines.util.AssetManager;
import com.battlezone.megamachines.world.track.Track;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

public abstract class BaseWorld {

    public static final double TARGET_FPS = 60.0;
    private static final double FRAME_LENGTH = 1000000000 / TARGET_FPS;
    private static final float CAM_WIDTH = 25f;
    private static final float CAM_HEIGHT = 25f;
    final List<RWDCar> cars;
    private final List<Driver> AIs;
    private final Track track;
    private final Renderer renderer;
    private final Scene hud;
    private final Camera camera;
    private final RWDCar target;
    private final Background background;
    private final long window;
    private final GameInput input;
    private final List<Texture> positionTextures = new ArrayList<>() {{
        for (int i = 0; i < Server.MAX_PLAYERS; i++) {
            add(AssetManager.loadTexture("/ui/positions/" + i + ".png"));
        }
    }};
    private final Box positionIndicator;
    private final Gamepad gamepad;
    private byte previousPosition = -1;
    private boolean running = true;

    public BaseWorld(List<RWDCar> cars, Track track, int playerNumber, int aiCount) {

        Random r = new Random();
        this.AIs = new ArrayList<>() {{
            TrackRoute route = new TrackRoute(track);
            for (int i = 0; i < aiCount; i++) {

                RWDCar ai = new DordConcentrate(
                        track.getStartPiece().getX() + 2 + i * 2,
                        track.getStartPiece().getY(),
                        ScaleController.RWDCAR_SCALE,
                        1 + r.nextInt(2),
                        new Vector3f(r.nextFloat(), r.nextFloat(), r.nextFloat()), 0, 1);
                cars.add(ai);
                add(new Driver(route, ai));
            }
        }};

        this.cars = cars;
        this.track = track;
        this.camera = new Camera(Window.getWindow().getAspectRatio() * CAM_WIDTH, CAM_HEIGHT);
        Window.getWindow().setResizeCamera(camera, CAM_WIDTH, CAM_HEIGHT);
        this.renderer = new Renderer(camera);

        this.background = new Background();
        this.renderer.addRenderable(background);

        TrackSet trackSet = new TrackSet();
        trackSet.setTrack(track);

        cars.forEach(this.renderer::addRenderable);
        this.renderer.addRenderable(trackSet);

        this.target = cars.get(playerNumber);

        this.window = Window.getWindow().getGameWindow();

        this.input = new GameInput();
        glfwSetKeyCallback(window, input);

        this.hud = new Scene();
        hud.addElement(new Minimap(track, cars));
        hud.show();

        this.positionIndicator = new Box(0.5f, 0.5f, -0.5f, -0.5f, Colour.WHITE);
        hud.addElement(positionIndicator);

        this.gamepad = new Gamepad();

    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void start() {

        double previousTime = System.nanoTime();
        double frametime = 0;
        int frames = 0;

        try {
            Thread.sleep(15);
        } catch (InterruptedException ignored) {
        }

        while (!glfwWindowShouldClose(window) && running) {
            glfwPollEvents();

            double currentTime = System.nanoTime();
            double interval = currentTime - previousTime;
            frametime += interval;
            frames += 1;
            previousTime = currentTime;

            glClear(GL_COLOR_BUFFER_BIT);

            background.setX(target.getXf() / 10f);
            background.setY(target.getYf() / 10f);
            camera.setPosition(target.getXf(), target.getYf(), 0);

            for (int i = 0; i < AIs.size(); i++) {
                AIs.get(i).update();
            }

            gamepad.update();

            preRender(interval);

            renderer.render();
            hud.render();

            if (target.getPosition() != previousPosition) {
                previousPosition = target.getPosition();
                positionIndicator.setTexture(positionTextures.get(target.getPosition()));
            }

            if (frametime >= 1000000000) {
                frametime = 0;
                System.out.println("FPS: " + frames);
                frames = 0;
            }

            glfwSwapBuffers(window);

            while (System.nanoTime() - previousTime < FRAME_LENGTH) {
                try {
                    Thread.sleep(0);
                } catch (InterruptedException ignored) {
                }
            }
        }
        hud.hide();
    }

    abstract void preRender(double interval);

}
