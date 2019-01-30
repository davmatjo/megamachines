package com.battlezone.megamachines.renderer;

import java.util.List;

/**
 * Implementation of a texture that allows for animation
 */
public class AnimatedTexture implements Texture {

    /**
     * StaticTextures to loop through
     */
    private final List<StaticTexture> textures;

    /**
     * Speed in time since last frame of animation
     */
    private final long speed;

    /**
     * Stores the last measured time
     */
    private long lastMeasuredTime;

    /**
     * Time at which the last frame occurred
     */
    private long lastFrameTime = 0;

    /**
     * The current texture number
     */
    private int currentFrame = 0;

    /**
     * Creates an animated texture from a list of textures and a speed in frames per second
     * @param textures List of static textures to animate using
     * @param speed speed in frames per second
     */
    public AnimatedTexture(List<StaticTexture> textures, int speed) {
        this.textures = textures;
        this.speed = (long) ((1.0 / speed) * 1000000000);
        lastMeasuredTime = System.nanoTime();
    }

    /**
     * Binds the correct texture based off animation timings
     */
    @Override
    public void bind() {
        long now = System.nanoTime();
        lastFrameTime += now - lastMeasuredTime;
        lastMeasuredTime = now;

        if (lastFrameTime >= speed) {
            lastFrameTime = 0;
            currentFrame = currentFrame < textures.size() - 1 ? currentFrame + 1 : 0;
        }

        textures.get(currentFrame).bind();
    }
}
