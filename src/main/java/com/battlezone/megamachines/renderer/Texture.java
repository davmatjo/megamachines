package com.battlezone.megamachines.renderer;

import com.battlezone.megamachines.util.AssetManager;

public interface Texture {

    Texture BLANK = AssetManager.loadTexture("/ui/blank.png");
    Texture CIRCLE = AssetManager.loadTexture("/ui/circle.png");

    void bind();
}
