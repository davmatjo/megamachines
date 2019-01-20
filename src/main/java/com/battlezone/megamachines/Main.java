package com.battlezone.megamachines;

import com.battlezone.megamachines.input.GameInput;
import com.battlezone.megamachines.input.KeyCode;
import com.battlezone.megamachines.math.Vector2f;
import com.battlezone.megamachines.renderer.*;
import com.battlezone.megamachines.util.AssetManager;
import com.battlezone.megamachines.world.Car;
import com.battlezone.megamachines.world.Track;
import com.battlezone.megamachines.world.TrackType;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL30.*;

public class Main {

    private Main() {
        // Attempt to initialise GLFW
        if (!glfwInit()) {
            System.err.println("GLFW Failed to initialise");
            System.exit(-1);
        }

        GLFWVidMode mode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        int width = 1920;
        int height = 1080;
        // Create window
        long gameWindow = glfwCreateWindow(width, height, "MegaMachines", 0, 0);

//        glfwSwapInterval(1);

        // Initialise openGL states
        glfwShowWindow(gameWindow);
        glfwMakeContextCurrent(gameWindow);
        GL.createCapabilities(false);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        Camera camera = new Camera((int) (1000 * ((float) width / (float) height)), 1000);
//        Shader shader = AssetManager.loadShader("/shaders/entity");
//        Shader shader2 = AssetManager.loadShader("/shaders/entity");
        Model square = Model.generateSquare();
        TrackRenderer trackRenderer = new TrackRenderer(Model.generateSquare(), camera);
        trackRenderer.setTrack(Track.generateMap(10, 10, 400));

        List<Car> cars = new ArrayList<Car>() {{
            add(new Car(new Vector2f(0, 100), 50, 0));
            add(new Car(new Vector2f(0, 0), 50, 1));
            add(new Car(new Vector2f(0, -100), 50, 2));
        }};
        CarRenderer carRenderer = new CarRenderer(Model.generateCar(), cars, camera);


        camera.setPosition(200, 0, 0);

        GameInput gameInput = new GameInput();
        glfwSetKeyCallback(gameWindow, gameInput);

        double previousTime = System.nanoTime();
        double frametime = 0;
        int frames = 0;

        int i = 0;
        int j = 0;
        int thing = 0;
        // Game loop for now
        while (!glfwWindowShouldClose(gameWindow)) {
            glfwPollEvents();

            double currentTime = System.nanoTime();
            double interval = currentTime - previousTime;
            frametime += interval;
            frames += 1;

            previousTime = currentTime;

            if (gameInput.isPressed(KeyCode.D))
                i += 10;
            if (gameInput.isPressed(KeyCode.A))
                i -= 10;
            if (gameInput.isPressed(KeyCode.W))
                j += 10;
            if (gameInput.isPressed(KeyCode.S))
                j -= 10;


            glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);

            cars.get(0).translate(1, 0);
            camera.setPosition(cars.get(0).getPosition().x, cars.get(0).getPosition().y, 0);
            trackRenderer.render();
            carRenderer.render();

            glfwSwapBuffers(gameWindow);

            if (frametime >= 1000000000) {
                frametime = 0;
                System.out.println("FPS: " + frames);
                frames = 0;
            }
        }
    }

    public static void main(String[] args) {
//        SharedLibraryLoader.load();
        new Main();

    }
}
