package com.battlezone.megamachines.renderer.game.animation;

import java.util.ArrayList;
import java.util.List;

public interface Animatable {

    default void animate(double interval) {
        for (int i = 0; i < getAnimations().size(); i++) {
            getAnimations().get(i).tryUpdate(interval);
        }
    }

    default boolean playAnimation(Class type) {
        for (int i = 0; i < getAnimations().size(); i++) {
            if (getAnimations().get(i).getClass().equals(type)) {
                getAnimations().get(i).play();
                return true;
            }
        }
        return false;
    }

    List<Animation> getAnimations();

}
