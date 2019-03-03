package com.battlezone.megamachines.renderer.game.animation;

import com.battlezone.megamachines.math.MathUtils;
import com.battlezone.megamachines.world.GameObject;

public class FallAnimation extends Animation {

    private static final double DURATION_STAGE_1 = 1.0;
    private static final double DURATION_STAGE_2 = 0.5;
    private static final double DURATION = DURATION_STAGE_1 + DURATION_STAGE_2;
    private static final float TARGET_SCALE = 0.6f;
    private final float scalePerSec;
    private final GameObject obj;
    private float initialScale;

    public FallAnimation(GameObject object) {
        super(DURATION);
        obj = object;
        initialScale = object.getScale();
        scalePerSec = MathUtils.lerpVelocity(initialScale, TARGET_SCALE, (float) (1 / DURATION_STAGE_1));
    }

    @Override
    void update(double interval) {
        if (elapsed < DURATION_STAGE_1) {
            obj.setScale(initialScale + (float) (elapsed * scalePerSec));
        } else {
            obj.setSpeed(0);
            obj.setScale(0);
        }
    }

    @Override
    void finish() {
        obj.setScale(initialScale);
    }
}