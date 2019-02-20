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
        setCurrentlyPlaying(getCurrentlyPlaying() | Animation.ANIM_TO_INDEX.get(type));
        for (int i = 0; i < getAnimations().size(); i++) {
            if (getAnimations().get(i).getClass().equals(type)) {
                getAnimations().get(i).play(() -> setCurrentlyPlaying(getCurrentlyPlaying() & ~Animation.ANIM_TO_INDEX.get(type)));
                return true;
            }
        }
        return false;
    }

    default boolean playAnimation(Class type, Runnable onFinished) {
        setCurrentlyPlaying(getCurrentlyPlaying() | Animation.ANIM_TO_INDEX.get(type));
        for (int i = 0; i < getAnimations().size(); i++) {
            if (getAnimations().get(i).getClass().equals(type)) {
                getAnimations().get(i).play(() -> {
                    onFinished.run();
                    setCurrentlyPlaying(getCurrentlyPlaying() & ~Animation.ANIM_TO_INDEX.get(type));
                    System.out.println(getCurrentlyPlaying() & ~Animation.ANIM_TO_INDEX.get(type));
                });
                return true;
            }
        }
        return false;
    }

    List<Animation> getAnimations();

    int getCurrentlyPlaying();

    void setCurrentlyPlaying(int currentlyPlaying);

}
