package com.battlezone.megamachines.renderer.game.particle;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.renderer.Model;
import com.battlezone.megamachines.renderer.Shader;
import com.battlezone.megamachines.renderer.Texture;
import com.battlezone.megamachines.util.AssetManager;

import java.util.List;

public class DriftParticleEffect implements ParticleEffect {

    private static final Model model = Model.SQUARE;
    private static final Texture SMOKE = AssetManager.loadTexture("/effects/smoke.png");
    private final RWDCar toFollow;
    private final List<Particle> particles = List.of(
            new Particle(20, 0.05f, Shader.ENTITY),
            new Particle(20, 0.05f, Shader.ENTITY),
            new Particle(20, 0.05f, Shader.ENTITY),
            new Particle(20, 0.05f, Shader.ENTITY),
            new Particle(20, 0.05f, Shader.ENTITY),
            new Particle(20, 0.05f, Shader.ENTITY),
            new Particle(20, 0.05f, Shader.ENTITY),
            new Particle(20, 0.05f, Shader.ENTITY),
            new Particle(20, 0.05f, Shader.ENTITY),
            new Particle(20, 0.05f, Shader.ENTITY)
    );
    private int elasped = 0;

    /**
     * Creates a new DriftParticleEffect for a car
     *
     * @param toFollow Car for the effect to run on
     */
    public DriftParticleEffect(RWDCar toFollow) {
        this.toFollow = toFollow;
    }

    /**
     * Checks if a drift particle should be spawned and finds the oldest particle if this is the case
     */
    @Override
    public void update() {
        elasped++;
        if (toFollow.getCurrentlyPlaying() == 0
                && elasped % 2 == 0
                && ((toFollow.getLateralSpeed() > 0.8 || toFollow.getLateralSpeed() < -0.8))) {
            for (int i = 0; i < particles.size(); i++) {
                if (!particles.get(i).isAlive()) {
                    particles.get(i).reset(toFollow.getXf(), toFollow.getYf(), (float) toFollow.getLateralSpeed());
                    break;
                }
            }
        }
    }

    /**
     * Draws all active particles
     */
    @Override
    public void draw() {
        SMOKE.bind();
        for (int i = 0; i < particles.size(); i++) {
            particles.get(i).draw();
        }
    }

    @Override
    public int getDepth() {
        return 0;
    }

    @Override
    public Model getModel() {
        return model;
    }

    @Override
    public Shader getShader() {
        return Shader.ENTITY;
    }
}
