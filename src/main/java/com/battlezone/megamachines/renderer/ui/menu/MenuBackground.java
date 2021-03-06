package com.battlezone.megamachines.renderer.ui.menu;

import com.battlezone.megamachines.math.Matrix4f;
import com.battlezone.megamachines.renderer.SubTexture;
import com.battlezone.megamachines.renderer.Texture;
import com.battlezone.megamachines.renderer.ui.Colour;
import com.battlezone.megamachines.renderer.ui.elements.Box;
import com.battlezone.megamachines.util.AssetManager;

import java.util.Random;

/**
 * A background the menus
 */
public class MenuBackground extends Box {

    private static final Texture background = AssetManager.loadTexture("/ui/background/background_" + new Random().nextInt(3) + ".png");
    private static final Matrix4f backgroundPosition = Matrix4f.IDENTITY.clone();

    /**
     * Creates a menu background ready to be drawn on the screen
     */
    public MenuBackground() {
        super(4f, 2f, -2f, -1f, Colour.WHITE, new SubTexture(backgroundPosition));
    }

    /**
     * Draws the menu background, translating it each fram
     */
    @Override
    public void render() {
        background.bind();
        Matrix4f.translate(backgroundPosition, 0.001f, 0, 0, backgroundPosition);
        super.render();
    }
}
