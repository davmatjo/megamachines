package com.battlezone.megamachines.renderer.game.particle;

import com.battlezone.megamachines.renderer.Drawable;
import com.battlezone.megamachines.renderer.Model;
import com.battlezone.megamachines.renderer.Shader;

public abstract class ParticleEffect implements Drawable {
    public abstract void update();

    @Override
    public abstract void draw();

    @Override
    public abstract int getDepth();

    @Override
    public abstract Model getModel();

    @Override
    public abstract Shader getShader();
}
