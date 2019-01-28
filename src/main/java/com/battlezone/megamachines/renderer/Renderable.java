package com.battlezone.megamachines.renderer;

import com.battlezone.megamachines.renderer.game.Shader;


/**
 * An object is renderable if it binds its own buffers and a call to render will draw it on the screen
 */
public interface Renderable {

    void render();
    Shader getShader();
}
