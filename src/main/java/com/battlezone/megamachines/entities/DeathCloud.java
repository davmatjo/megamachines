package com.battlezone.megamachines.entities;

import com.battlezone.megamachines.math.Matrix4f;
import com.battlezone.megamachines.math.Vector2d;
import com.battlezone.megamachines.renderer.AnimatedTexture;
import com.battlezone.megamachines.renderer.Drawable;
import com.battlezone.megamachines.renderer.Model;
import com.battlezone.megamachines.renderer.Shader;
import com.battlezone.megamachines.util.AssetManager;
import com.battlezone.megamachines.util.Pair;

import static org.lwjgl.opengl.GL11.*;

public class DeathCloud extends PhysicalEntity implements Drawable {

    private final Model model = Model.SQUARE;

    private final int indexCount = model.getIndices().length;

    private final Matrix4f tempMatrix = new Matrix4f();
    private final AnimatedTexture cloudTexture = AssetManager.loadAnimation("/effects/cloud_", 8, 16, false);
    private double length = getScale();
    private double width = getScale();

    public DeathCloud() {
        super(0, 0, 1.125f);
        cloudTexture.setFrame(7);
    }

    public void play(double x, double y) {
        setX(x);
        setY(y);
        cloudTexture.setFrame(0);
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
    public void draw() {
        cloudTexture.bind();
        getShader().setMatrix4f("size", Matrix4f.scale(getScale(), tempMatrix));
        getShader().setInt("sampler", 0);
        getShader().setMatrix4f("position", Matrix4f.translate(Matrix4f.IDENTITY, getXf(), getYf(), 0f, tempMatrix));
        glDrawElements(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, 0);
    }

    @Override
    public int getDepth() {
        return -1;
    }

    @Override
    public Model getModel() {
        return Model.SQUARE;
    }

    @Override
    public boolean isEnlargedByPowerup() {
        return false;
    }

    @Override
    public Vector2d getVelocity() {
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
    public Vector2d getVectorFromCenterOfMass(double xp, double yp, Vector2d position) {
        return null;
    }

    @Override
    public Vector2d getCenterOfMassPosition() {
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
    public void correctCollision(Vector2d velocityDifference, double l) {

    }

    @Override
    public double getRotation() {
        return 0;
    }
}
