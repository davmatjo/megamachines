package com.battlezone.megamachines.renderer.game.particle;

import com.battlezone.megamachines.renderer.Drawable;
import com.battlezone.megamachines.renderer.Model;
import com.battlezone.megamachines.renderer.Shader;

public interface ParticleEffect extends Drawable {
    void update();

    @Override
    void draw();

    @Override
    int getDepth();

    @Override
    Model getModel();

    @Override
    Shader getShader();
}
