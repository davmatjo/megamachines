package com.battlezone.megamachines.renderer.game;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.renderer.Drawable;
import com.battlezone.megamachines.renderer.Model;
import com.battlezone.megamachines.renderer.Shader;
import com.battlezone.megamachines.renderer.Texture;
import com.battlezone.megamachines.world.GameObject;

import java.util.List;

public class ParticleEffect implements Drawable {

    private static final Model model = Model.generateSquare();
    private final RWDCar toFollow;
    private int elasped = 0;
    private final List<Particle> particles = List.of(
            new Particle(20),
            new Particle(20),
            new Particle(20),
            new Particle(20),
            new Particle(20),
            new Particle(20),
            new Particle(20),
            new Particle(20),
            new Particle(20),
            new Particle(20)
    );

    public ParticleEffect(RWDCar toFollow) {
        this.toFollow = toFollow;
    }

    public void continueEffect() {
        elasped++;
        if ((toFollow.getLateralSpeed() > 0.5 || toFollow.getLateralSpeed() <-0.5) && elasped % 2 == 0) {
            for (int i = 0; i < particles.size(); i++) {
                if (!particles.get(i).isAlive()) {
                    particles.get(i).reset(toFollow.getXf(), toFollow.getYf(), (float) toFollow.getLateralSpeed());
                    break;
                }
            }
        }
    }

    @Override
    public void draw() {
        Texture.BLANK.bind();
        for (int i=0; i<particles.size(); i++) {
            particles.get(i).draw();
        }
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
