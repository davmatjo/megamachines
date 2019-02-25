package com.battlezone.megamachines.entities.powerups;

import com.battlezone.megamachines.entities.PhysicalEntity;
import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.math.Matrix4f;
import com.battlezone.megamachines.physics.Collidable;
import com.battlezone.megamachines.renderer.Drawable;
import com.battlezone.megamachines.renderer.Model;
import com.battlezone.megamachines.renderer.Shader;
import com.battlezone.megamachines.renderer.Texture;
import com.battlezone.megamachines.util.Pair;

import static org.lwjgl.opengl.GL30.*;

public abstract class Powerup extends PhysicalEntity implements Collidable, Drawable {
    private final Model model = Model.generateSquare();

    private final int indexCount = model.getIndices().length;

    private final Matrix4f tempMatrix = new Matrix4f();

    private double length = getScale();

    private double width = getScale();

    private boolean alive = true;

    protected Powerup(double x, double y) {
        super(x, y, 1f);
    }

    public abstract Texture getTexture();

    protected abstract void pickup(RWDCar pickup);

    public abstract void activate(RWDCar activated);

    @Override
    public void collided(double xp, double yp, Collidable c2, Pair<Double, Double> n, double l) {
        if (c2 instanceof RWDCar) {
            if (alive) {
                alive = false;
                pickup((RWDCar) c2);
            } else {
                System.err.println("Dead powerup collision");
            }
        } else {
            System.err.println("Non car collided with powerup");
        }
    }

    @Override
    public void draw() {
        getTexture().bind();
        getShader().setMatrix4f("size", Matrix4f.scale(getScale(), tempMatrix));
        getShader().setInt("sampler", 0);
        getShader().setMatrix4f("position", Matrix4f.translate(Matrix4f.IDENTITY, getXf(), getYf(), 0f, tempMatrix));
        glDrawElements(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, 0);
    }

    @Override
    public double getLength() {
        return length;
    }

    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public Model getModel() {
        return Model.generateSquare();
    }

    @Override
    public double getYVelocity() {
        return 0;
    }

    @Override
    public double getXVelocity() {
        return 0;
    }

    @Override
    public Pair<Double, Double> getVelocity() {
        return null;
    }

    @Override
    public Shader getShader() {
        return Shader.ENTITY;
    }

    @Override
    public double getCoefficientOfRestitution() {
        return 0;
    }

    @Override
    public double getMass() {
        return 0;
    }

    @Override
    public Pair<Double, Double> getVectorFromCenterOfMass(double xp, double yp, Pair<Double, Double> position) {
        return null;
    }

    @Override
    public Pair<Double, Double> getCenterOfMassPosition() {
        return null;
    }

    @Override
    public double getRotationalInertia() {
        return 0;
    }

    @Override
    public void applyVelocityDelta(Pair<Double, Double> impactResult) {

    }

    @Override
    public void applyAngularVelocityDelta(double delta) {

    }

    @Override
    public void correctCollision(Pair<Double, Double> velocityDifference, double l) {

    }

    @Override
    public double getRotation() {
        return 0;
    }
}
