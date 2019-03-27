package com.battlezone.megamachines.renderer.game.particle;

import com.battlezone.megamachines.math.Matrix4f;
import com.battlezone.megamachines.renderer.Model;
import com.battlezone.megamachines.renderer.Shader;

import java.util.Random;

import static org.lwjgl.opengl.GL11.*;

public class Particle {

    private float x;
    private float y;
    private float scale;
    private int elapsed;
    private double magnitude;
    private final int lifetime;
    private static final Matrix4f TEMP = new Matrix4f();
    private static final int indexCount = Model.SQUARE.getIndices().length;
    private static final Random r = new Random();
    private final float maxSize;
    private final Shader shader;

    public Particle(int lifetime, float maxSize, Shader shader) {
        this.lifetime = lifetime;
        this.elapsed = Integer.MAX_VALUE;
        this.maxSize = maxSize;
        this.shader = shader;
    }

    public void draw() {
        if (elapsed < lifetime) {
            scale = Math.max(maxSize, (float) (-Math.pow((0.1f * elapsed - 1), 2) + Math.min(magnitude / 10, 0.1f)));
            elapsed++;
            shader.setMatrix4f("position", Matrix4f.translate(Matrix4f.IDENTITY, x, y, 0f, TEMP));
            shader.setMatrix4f("size", Matrix4f.scale(scale, TEMP));
            glDrawElements(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, 0);
        }
    }

    public void reset(float x, float y, float magnitude) {
        this.x = x + r.nextFloat() * 0.8f - 0.5f;
        this.y = y + r.nextFloat() * 0.8f - 0.5f;
        this.magnitude = magnitude;
        this.elapsed = 0;
    }

    public boolean isAlive() {
        return elapsed < lifetime;
    }
}
