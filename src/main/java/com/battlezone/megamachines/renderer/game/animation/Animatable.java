package com.battlezone.megamachines.renderer.game.animation;

import java.util.List;

/**
 * This handles the animations for any class that needs to be animatable
 */
public interface Animatable {

    /**
     * Runs the animations of anything that is animatable
     *
     * @param interval Time since last call in seconds
     */
    default void animate(double interval) {
        for (int i = 0; i < getAnimations().size(); i++) {
            getAnimations().get(i).tryUpdate(interval);
        }
    }

    /**
     * Plays an animation of a given type
     *
     * @param type The animation to play
     * @return Whether the animation was successfully played
     */
    default boolean playAnimation(Class type) {
        if ((getCurrentlyPlaying() & Animation.ANIM_TO_INDEX.get(type)) != Animation.ANIM_TO_INDEX.get(type)) {
            setCurrentlyPlaying(getCurrentlyPlaying() | Animation.ANIM_TO_INDEX.get(type));
            for (int i = 0; i < getAnimations().size(); i++) {
                if (getAnimations().get(i).getClass().equals(type)) {
                    getAnimations().get(i).play(() -> setCurrentlyPlaying(getCurrentlyPlaying() & ~Animation.ANIM_TO_INDEX.get(type)));
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Plays an animation of a given type, then runs a runnable when the animation is finished
     *
     * @param type       The type of the animation to play
     * @param onFinished Code to run when the animation finishes
     * @return Whether the animation successfully played
     */
    default boolean playAnimation(Class type, Runnable onFinished) {
        setCurrentlyPlaying(getCurrentlyPlaying() | Animation.ANIM_TO_INDEX.get(type));
        for (int i = 0; i < getAnimations().size(); i++) {
            if (getAnimations().get(i).getClass().equals(type)) {
                getAnimations().get(i).play(() -> {
                    onFinished.run();
                    setCurrentlyPlaying(getCurrentlyPlaying() & ~Animation.ANIM_TO_INDEX.get(type));
                });
                return true;
            }
        }
        return false;
    }

    /**
     * @return All animations this class is able to play
     */
    List<Animation> getAnimations();

    /**
     * @return The ID of the animation currently playing
     */
    int getCurrentlyPlaying();

    /**
     * Set the animation that is currently playing
     *
     * @param currentlyPlaying The animation that is currently playing
     */
    void setCurrentlyPlaying(int currentlyPlaying);

}
