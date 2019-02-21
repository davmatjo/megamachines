package com.battlezone.megamachines.world;

import com.battlezone.megamachines.entities.Cars.AffordThoroughbred;
import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.events.game.GameEndEvent;
import com.battlezone.megamachines.events.game.GameStateEvent;
import com.battlezone.megamachines.events.keys.KeyEvent;
import com.battlezone.megamachines.events.ui.WindowResizeEvent;
import com.battlezone.megamachines.input.GameInput;
import com.battlezone.megamachines.input.Gamepad;
import com.battlezone.megamachines.input.KeyCode;
import com.battlezone.megamachines.math.MathUtils;
import com.battlezone.megamachines.math.Vector3f;
import com.battlezone.megamachines.messaging.EventListener;
import com.battlezone.megamachines.messaging.MessageBus;
import com.battlezone.megamachines.physics.PhysicsEngine;
import com.battlezone.megamachines.renderer.Window;
import com.battlezone.megamachines.renderer.game.*;
import com.battlezone.megamachines.renderer.ui.*;
import com.battlezone.megamachines.util.StringUtil;
import com.battlezone.megamachines.world.track.Track;

import java.util.List;
import java.util.Random;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

public abstract class BaseWorld {

    public static final double TARGET_FPS = 60.0;
    private static final double FRAME_TIME = 1.0 / 60.0;
    private static final double FRAME_LENGTH = 1000000000 / TARGET_FPS;
    private static final float CAM_WIDTH = 25f;
    private static final float CAM_HEIGHT = 25f;
    public static final float PADDING = 0.05f;
    final List<RWDCar> cars;
    private final Track track;
    private FinishLine finishPiece;
    private final Renderer renderer;
    private final Scene hud;
    private final Camera camera;
    private final RWDCar target;
    private final Background background;
    private final long window;
    private final GameInput input;

    private final Label positionIndicator;
    private final Label lapIndicator;
    private final Label speedIndicator;
    private final Label lapTimeLabel;
    private final Minimap minimap;

    private final Gamepad gamepad;
    private byte previousPosition = -1;
    private byte previousLap = 1;
    private int previousSpeed = 0;
    private double previousAbsoluteSpeed = 0.0;
    private long lapStartTime;
    private boolean showingLapTime = false;
    private boolean running = true;
    private final PhysicsEngine physicsEngine;

    private GameStateEvent.GameState gameState;
    private PauseMenu pauseMenu;


    public BaseWorld(List<RWDCar> cars, Track track, int playerNumber, int aiCount) {
        MessageBus.register(this);

        Random r = new Random();
        for (int i = 0; i < aiCount; i++) {

            RWDCar ai = new AffordThoroughbred(
                    track.getFinishPiece().getX() + 2 + i * 2,
                    track.getFinishPiece().getY(),
                    ScaleController.RWDCAR_SCALE,
                    1 + r.nextInt(2),
                    new Vector3f(r.nextFloat(), r.nextFloat(), r.nextFloat()), 0, 1);
            cars.add(ai);

        }

        this.cars = cars;
        this.track = track;
        this.camera = new Camera(Window.getWindow().getAspectRatio() * CAM_WIDTH, CAM_HEIGHT);
        this.renderer = new Renderer(camera);

        this.background = new Background();
        this.renderer.addRenderable(background);

        TrackSet trackSet = new TrackSet();
        trackSet.setTrack(track);

        this.finishPiece = new FinishLine(track.getFinishPiece());
        this.renderer.addRenderable(finishPiece);

        cars.forEach(this.renderer::addRenderable);
        this.renderer.addRenderable(trackSet);

        this.target = cars.get(playerNumber);

        this.window = Window.getWindow().getGameWindow();

        this.input = GameInput.getGameInput();
        glfwSetKeyCallback(window, input);

        this.hud = new Scene();

        this.minimap = new Minimap(track, cars);
        hud.addElement(minimap);

        this.hud.show();

        this.positionIndicator = new Label("", 0.1f, Window.getWindow().getLeft() + PADDING, Window.getWindow().getBottom() + PADDING, Colour.WHITE);
        hud.addElement(positionIndicator);

        this.lapIndicator = new Label("Lap:1", 0.1f, Window.getWindow().getLeft() + PADDING, Window.getWindow().getTop() - 0.1f - PADDING, Colour.WHITE);
        hud.addElement(lapIndicator);

        this.speedIndicator = new Label("000mph", 0.1f, 0, 0, Colour.WHITE);
        speedIndicator.setPos(Window.getWindow().getRight() - speedIndicator.getWidth() - PADDING, Window.getWindow().getBottom() + PADDING);
        hud.addElement(speedIndicator);

        this.lapTimeLabel = new Label("Lap Time: 00:00", 0.18f, 0, 0, Colour.WHITE);
        lapTimeLabel.setPos((Window.getWindow().getLeft() + Window.getWindow().getRight() - lapTimeLabel.getWidth()) / 2, Window.getWindow().getTop() - lapIndicator.getHeight() - PADDING * 8);
        //hud.addElement(lapTimeLabel);

        this.gamepad = new Gamepad();

        this.pauseMenu = new PauseMenu(canPause(), this::togglePause, this::quitGame);

        this.physicsEngine = new PhysicsEngine();
        cars.forEach(physicsEngine::addCar);

        this.lapStartTime = System.currentTimeMillis();

        Window.getWindow().setResizeCamera(camera, CAM_WIDTH, CAM_HEIGHT);
    }

    @EventListener
    public void onKey(KeyEvent keyEvent) {
        if (keyEvent.getPressed() && keyEvent.getKeyCode() == KeyCode.ESCAPE) {
            this.togglePause();
        }
    }

    @EventListener
    public void onResize(WindowResizeEvent event) {
        positionIndicator.setPos(Window.getWindow().getLeft() + PADDING, Window.getWindow().getBottom() + PADDING);
        lapIndicator.setPos(Window.getWindow().getLeft() + PADDING, Window.getWindow().getTop() - lapIndicator.getHeight() - PADDING);
        speedIndicator.setPos(Window.getWindow().getRight() - speedIndicator.getWidth() - PADDING, Window.getWindow().getBottom() + PADDING);
        minimap.setPos(Window.getWindow().getRight() - Minimap.MAP_WIDTH - BaseWorld.PADDING, Window.getWindow().getTop() - Minimap.MAP_HEIGHT - BaseWorld.PADDING);
        lapTimeLabel.setPos((Window.getWindow().getLeft() + Window.getWindow().getRight() - lapTimeLabel.getWidth()) / 2, Window.getWindow().getTop() - lapIndicator.getHeight() - PADDING);
    }

    private void togglePause() {
        System.out.println("togglePause");
        if (gameState == GameStateEvent.GameState.PAUSED) {
            gameState = GameStateEvent.GameState.PLAYING;
        } else {
            gameState = GameStateEvent.GameState.PAUSED;
        }
        MessageBus.fire(new GameStateEvent(gameState));
    }

    private void quitGame() {
        running = false;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void start() {

        double previousTime = System.nanoTime();
        double frametime = 0;
        int frames = 0;

        camera.setPosition(target.getXf(),
                target.getYf(), 0);
        camera.setTarget(target);

        preLoop();

        while (!glfwWindowShouldClose(window) && running) {

            physicsEngine.crank(FRAME_TIME);

            glfwPollEvents();

            double currentTime = System.nanoTime();
            double interval = currentTime - previousTime;
            frametime += interval;
            frames += 1;
            previousTime = currentTime;

            background.setX(target.getXf() / 10f);
            background.setY(target.getYf() / 10f);

            camera.update();

            gamepad.update();

            preRender(interval);

            glClear(GL_COLOR_BUFFER_BIT);
            renderer.render(FRAME_TIME);
            hud.render();

            double speed = MathUtils.msToMph(target.getSpeed());
            int speedRounded = (int) Math.round(speed);
            if (speedRounded != previousSpeed && Math.abs(speed - previousAbsoluteSpeed) > 1) {
                speedIndicator.setText(speedRounded + "mph");
                previousSpeed = speedRounded;
                previousAbsoluteSpeed = speed;
            }


            if (target.getPosition() != previousPosition) {
                previousPosition = target.getPosition();
                positionIndicator.setText(Race.positions[target.getPosition()]);
            }

            if (target.getLap() > previousLap) {
                previousLap = target.getLap();
                lapIndicator.setText("Lap:" + previousLap);
                long lapTimeMillis = System.currentTimeMillis() - lapStartTime;
                long lapTimeSecs = lapTimeMillis / 1000;
                long secs = lapTimeSecs % 60;
                long mins = (lapTimeSecs - secs) / 60;
                lapTimeLabel.setText("LAP TIME " + StringUtil.pad(String.valueOf(mins), 2, '0') + ":" + StringUtil.pad(String.valueOf(secs), 2, '0'));
                lapStartTime = System.currentTimeMillis();
                showingLapTime = true;
            }

            if (showingLapTime && System.currentTimeMillis() - lapStartTime > 3500) {
                showingLapTime = false;
            }

            if (showingLapTime)
                lapTimeLabel.render();

            if (gameState == GameStateEvent.GameState.PAUSED) {
                System.out.println("menuing");
                pauseMenu.render();
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

    @EventListener
    public void gameEnd(GameEndEvent e) {
        running = false;
    }

    abstract boolean canPause();

    abstract void preRender(double interval);

    abstract void preLoop();
}
