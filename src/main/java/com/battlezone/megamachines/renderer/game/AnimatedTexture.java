package com.battlezone.megamachines.renderer.game;

import java.util.List;

public class AnimatedTexture implements Texture {

    private final List<StaticTexture> textures;
    private final long speed;
    private long lastMeasuredTime;
    private long lastFrameTime = 0;
    private int currentFrame = 0;


    public AnimatedTexture(List<StaticTexture> textures, int speed) {
        this.textures = textures;
        this.speed = (long) ((1.0 / speed) * 1000000000);
        lastMeasuredTime = System.nanoTime();
    }

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
