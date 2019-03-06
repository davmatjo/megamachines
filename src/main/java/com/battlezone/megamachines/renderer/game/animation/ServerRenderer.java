package com.battlezone.megamachines.renderer.game.animation;

import com.battlezone.megamachines.renderer.Drawable;
import com.battlezone.megamachines.renderer.game.Renderer;

public class ServerRenderer extends Renderer {
    /**
     * Create a new empty renderer
     */
    public ServerRenderer() {
        super(null);
    }

    @Override
    public void addDrawable(Drawable drawable) {
        // Nothing
    }

    @Override
    public void removeDrawable(Drawable drawable) {
        // NOTHING
    }

    @Override
    public void populateRenderables() {
        // NO
    }

    @Override
    public void render(double interval) {
        // Nothing
    }
}