package com.battlezone.megamachines.renderer.game.animation;

import java.util.ArrayList;
import java.util.List;

public interface Animatable {

    List<Animation> animations = new ArrayList<>();

    default void animate(double interval) {
        for (int i = 0; i < animations.size(); i++) {
            animations.get(i).tryUpdate(interval);
        }
    }

    default boolean playAnimation(Class type) {
        for (int i = 0; i < animations.size(); i++) {
            if (animations.get(i).getClass().equals(type)) {
                animations.get(i).play();
                return true;
            }
        }
        return false;
    }

    default void addAnimation(Animation anim) {
        animations.add(anim);
    }

}
