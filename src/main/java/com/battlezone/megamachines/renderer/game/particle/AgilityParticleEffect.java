package com.battlezone.megamachines.renderer.game.particle;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.math.Matrix4f;
import com.battlezone.megamachines.math.Vector4f;
import com.battlezone.megamachines.renderer.Model;
import com.battlezone.megamachines.renderer.Shader;
import com.battlezone.megamachines.renderer.Texture;
import com.battlezone.megamachines.util.AssetManager;

import java.util.List;
import java.util.Random;

public class AgilityParticleEffect extends ParticleEffect {


    private static final Model model = Model.SQUARE;
    private static final Texture SMOKE = AssetManager.loadTexture("/effects/smoke.png");
    private final RWDCar toFollow;
    private int elasped = 0;
    private final Random r = new Random();
    private final Vector4f tempVec = new Vector4f(1, 1, 1, 1);
    private final Matrix4f tempMat = new Matrix4f();
    private final List<Particle> particles = List.of(
            new Particle(r.nextInt(10) + 20, 0.1f),
            new Particle(r.nextInt(10) + 20, 0.1f),
            new Particle(r.nextInt(10) + 20, 0.1f),
            new Particle(r.nextInt(10) + 20, 0.1f),
            new Particle(r.nextInt(10) + 20, 0.1f),
            new Particle(r.nextInt(10) + 20, 0.1f),
            new Particle(r.nextInt(10) + 20, 0.1f),
            new Particle(r.nextInt(10) + 20, 0.1f),
            new Particle(r.nextInt(10) + 20, 0.1f),
            new Particle(r.nextInt(10) + 20, 0.1f),
            new Particle(r.nextInt(10) + 20, 0.1f),
            new Particle(r.nextInt(10) + 20, 0.1f),
            new Particle(r.nextInt(10) + 20, 0.1f),
            new Particle(r.nextInt(10) + 20, 0.1f),
            new Particle(r.nextInt(10) + 20, 0.1f)
    );

    public AgilityParticleEffect(RWDCar toFollow) {
        this.toFollow = toFollow;
    }

    @Override
    public void update() {
        elasped++;
        if (toFollow.isAgilityActive > 0 && elasped % (int) Math.max(2, 50/(toFollow.getSpeed())) == 0) {
            for (int i = 0; i < particles.size(); i++) {
                if (!particles.get(i).isAlive()) {
                    particles.get(i).reset(toFollow.getXf(), toFollow.getYf(), 0.05f);
                    break;
                }
            }
        }
    }

    @Override
    public void draw() {
        if (toFollow.isAgilityActive > 0) {
            Texture.BLANK.bind();
            for (int i = 0; i < particles.size(); i++) {
                tempVec.set(r.nextFloat() / 2 + 0.5f, r.nextFloat() / 2 + 0.5f, r.nextFloat() / 2 + 0.5f, 1);
//            Shader.CAR.setMatrix4f("rotation", Matrix4f.rotationZ(75f, tempMat));
                Shader.CAR.setVector4f("spriteColour", tempVec);
                particles.get(i).draw();
            }
        }
    }

    @Override
    public int getDepth() {
        return 1;
    }

    @Override
    public Model getModel() {
        return model;
    }

    @Override
    public Shader getShader() {
        return Shader.CAR;
    }
}