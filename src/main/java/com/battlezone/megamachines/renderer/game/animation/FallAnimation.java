package com.battlezone.megamachines.renderer.game.animation;

import com.battlezone.megamachines.math.MathUtils;
import com.battlezone.megamachines.world.GameObject;

public class FallAnimation extends Animation {

    private static final double DURATION = 2d;
    private static final float TARGET_SCALE = 0.3f;
    private final float scalePerSec;
    private final GameObject obj;
    private float initialScale;

    public FallAnimation(GameObject object) {
        super(DURATION);
        obj = object;
        initialScale = object.getScale();
        scalePerSec = MathUtils.lerpVelocity(initialScale, TARGET_SCALE, (float) (1 / DURATION));
    }

    @Override
    void update(double interval) {
        obj.setScale(initialScale + (float) (elapsed * scalePerSec));
    }

    @Override
    void finish() {
        obj.setScale(initialScale);
    }
}