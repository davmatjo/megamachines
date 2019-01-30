package com.battlezone.megamachines.renderer;

import com.battlezone.megamachines.util.AssetManager;

public interface Texture {

    Texture BLANK = AssetManager.loadTexture("/ui/blank.png");

    void bind();
}
