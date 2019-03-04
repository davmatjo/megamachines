package com.battlezone.megamachines.renderer;

import com.battlezone.megamachines.math.MathUtils;

import java.util.List;

/**
 * Implementation of a texture that allows for animation
 */
public class AnimatedTexture implements Texture {

    /**
     * StaticTextures to loop through
     */
    private final List<Texture> textures;

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
     * Whether the animation loops
     */
    private final boolean loop;

    /**
     * Creates an animated texture from a list of textures and a speed in frames per second
     *
     * @param textures List of static textures to animate using
     * @param speed    speed in frames per second
     */
    public AnimatedTexture(List<Texture> textures, int speed) {
        this.textures = textures;
        this.speed = (long) ((1.0 / speed) * 1000000000);
        lastMeasuredTime = System.nanoTime();
        this.loop = true;
    }

    /**
     * Creates an animated texture from a list of textures and a speed in frames per second
     *
     * @param textures List of static textures to animate using
     * @param speed    speed in frames per second
     * @param loop     Whether the animation loops or not
     */
    public AnimatedTexture(List<Texture> textures, int speed, boolean loop) {
        this.textures = textures;
        this.speed = (long) ((1.0 / speed) * 1000000000);
        lastMeasuredTime = System.nanoTime();
        this.loop = loop;
    }

    /**
     * Set the current frame of the animated texture.
     *
     * @param frame the frame to set the animated texture to.
     */
    public void setFrame(int frame) {
        currentFrame = frame;
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
            if (loop)
                currentFrame = MathUtils.wrap(currentFrame + 1, 0, textures.size());
            else
                currentFrame = MathUtils.clamp(currentFrame + 1, 0, textures.size() - 1);
        }

        textures.get(currentFrame).bind();
    }
}
