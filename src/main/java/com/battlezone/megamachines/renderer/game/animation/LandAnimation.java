package com.battlezone.megamachines.renderer.game.animation;

import com.battlezone.megamachines.math.MathUtils;
import com.battlezone.megamachines.world.GameObject;

public class LandAnimation extends Animation {
    private static final double DURATION = 0.6;
    private static final double DURATION_STAGE_1 = DURATION / 2;
    private static final double DURATION_STAGE_2 = DURATION / 4;
    private static final double DURATION_STAGE_3 = DURATION / 4;
    private static final double TIME_STAGE_2 = DURATION_STAGE_1;
    private static final double TIME_STAGE_3 = DURATION_STAGE_1 + DURATION_STAGE_2;
    private static final float INTERMEDIATE_SCALE = 1.5f;
    private static final float STARTING_SCALE = 3f;
    private final float scalePerSecDown1;
    private final float scalePerSecDown2;
    private final float scalePerSecUp;
    private final GameObject obj;
    private float initialScale;

    public LandAnimation(GameObject object) {
        super(DURATION);
        obj = object;
        initialScale = object.getScale();
        scalePerSecDown1 = MathUtils.lerpVelocity(STARTING_SCALE, initialScale, (float) (1 / DURATION_STAGE_1));
        scalePerSecUp = MathUtils.lerpVelocity(initialScale, INTERMEDIATE_SCALE, (float) (1 / DURATION_STAGE_2));
        scalePerSecDown2 = MathUtils.lerpVelocity(initialScale, INTERMEDIATE_SCALE, (float) (1 / DURATION_STAGE_3));
    }

    @Override
    void update(double interval) {
        if (elapsed < TIME_STAGE_2) {
            obj.setScale(STARTING_SCALE + (float) (elapsed * scalePerSecDown1));
        } else if (elapsed < TIME_STAGE_3) {
            obj.setScale(obj.getScale() + MathUtils.lerpVelocity(obj.getScale(), INTERMEDIATE_SCALE, 0.1f));
        } else {
            obj.setScale(obj.getScale() + MathUtils.lerpVelocity(obj.getScale(), initialScale, 0.1f));
        }
    }

    @Override
    void finish() {
        obj.setScale(initialScale);
    }
}
