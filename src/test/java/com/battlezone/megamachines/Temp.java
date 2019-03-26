package com.battlezone.megamachines;

import com.battlezone.megamachines.renderer.Window;
import com.battlezone.megamachines.renderer.game.*;
import com.battlezone.megamachines.renderer.theme.Theme;
import com.battlezone.megamachines.renderer.theme.ThemeHandler;
import com.battlezone.megamachines.util.AssetManager;
import com.battlezone.megamachines.world.track.Track;
import com.battlezone.megamachines.world.track.TrackType;
import com.battlezone.megamachines.world.track.generator.TrackFromGridGenerator;
import org.junit.Test;
import org.lwjgl.BufferUtils;

import static com.battlezone.megamachines.world.track.TrackType.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Temp {

    @Test
    public void run() {
        AssetManager.setIsHeadless(false);
        Window window = Window.getWindow();

        ThemeHandler.setTheme(Theme.SPACE);

        Track track = new TrackFromGridGenerator(new TrackType[][] {
                {null, DOWN_RIGHT, DOWN, LEFT_DOWN, null},
                {null, RIGHT, null, LEFT, null},
                {DOWN_RIGHT, RIGHT_DOWN, null, LEFT, null},
                {RIGHT, null, null, DOWN_LEFT, LEFT_DOWN},
                {RIGHT_UP, UP, UP_RIGHT, null, LEFT},
                {null, null, RIGHT, null, LEFT},
                {null, DOWN_RIGHT, RIGHT_DOWN, null, LEFT},
                {DOWN_RIGHT, RIGHT_DOWN, null, LEFT_UP, UP_LEFT},
                {RIGHT, null, LEFT_UP, UP_LEFT, null},
                {RIGHT_UP, UP, UP_LEFT, null, null}
        }).generateTrack();

        var set = new TrackSet();
        set.setTrack(track);


        var camera = new Camera(112, 63);
        camera.setPosition(45, 20, 0);
        Renderer renderer = new Renderer(camera);

        Background background = new Background();
        renderer.addDrawable(background);
        TrackShadow shadow = new TrackShadow(camera);
        shadow.setTrack(track);

        renderer.addDrawable(shadow);

        renderer.addDrawable(set);

        renderer.render(0);
        glfwSwapBuffers(window.getGameWindow());
        var actual = BufferUtils.createByteBuffer(4 * 1920 * 1080);
        glReadBuffer(GL_FRONT);
        glReadPixels(0, 0, 1920, 1080, GL_RGBA, GL_UNSIGNED_BYTE, actual);
        var image = AssetManager.imageFromBytes(actual, 1920, 1080);
        AssetManager.saveImage(image, "trackSpace.bmp");

//        while (!glfwWindowShouldClose(window.getGameWindow())) {
//            renderer.render(0);
//            glfwSwapBuffers(window.getGameWindow());
//        }
    }
}
