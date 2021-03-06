package com.battlezone.megamachines.entities.powerups.types.physical;

import com.battlezone.megamachines.entities.PhysicalEntity;
import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.entities.powerups.Powerup;
import com.battlezone.megamachines.math.Matrix4f;
import com.battlezone.megamachines.math.Vector2d;
import com.battlezone.megamachines.physics.Collidable;
import com.battlezone.megamachines.physics.PhysicsEngine;
import com.battlezone.megamachines.renderer.Drawable;
import com.battlezone.megamachines.renderer.Model;
import com.battlezone.megamachines.renderer.Shader;
import com.battlezone.megamachines.renderer.game.Renderer;
import com.battlezone.megamachines.util.Pair;

import static org.lwjgl.opengl.GL11.*;

public class OilSpillOnGround extends PhysicalEntity implements Drawable, Collidable {
    private static final Model MODEL = Model.SQUARE;
    private static final int INDEX_COUNT = MODEL.getIndices().length;
    private static final float SCALE = 1.00f;
    private final Matrix4f tempMatrix = new Matrix4f();
    private final Vector2d velocity = new Vector2d(0.0, 0.0);
    private final Vector2d position = new Vector2d(0.0, 0.0);


    public OilSpillOnGround(double x, double y, PhysicsEngine pe, Renderer r) {
        super(x, y, SCALE);
        position.x = x;
        position.y = y;

        pe.addCollidable(this);
        r.addDrawable(this);
    }

    @Override
    public void draw() {
        Powerup.OIL_SPILL.bind();
        getShader().setMatrix4f("size", Matrix4f.scale(getScale(), tempMatrix));
        getShader().setInt("sampler", 0);
        getShader().setMatrix4f("position", Matrix4f.translate(Matrix4f.IDENTITY, getXf(), getYf(), 0f, tempMatrix));
        glDrawElements(GL_TRIANGLES, INDEX_COUNT, GL_UNSIGNED_INT, 0);
    }

    @Override
    public Model getModel() {
        return MODEL;
    }

    @Override
    public Shader getShader() {
        return Shader.ENTITY;
    }

    @Override
    public int getDepth() {
        return 0;
    }

    @Override
    public double getLength() {
        return SCALE;
    }

    @Override
    public double getWidth() {
        return SCALE;
    }

    @Override
    public Vector2d getVelocity() {
        return velocity;
    }

    @Override
    public double getCoefficientOfRestitution() {
        return 1.0;
    }

    @Override
    public double getMass() {
        return 10000;
    }

    @Override
    public Vector2d getCenterOfMassPosition() {
        return position;
    }

    @Override
    public double getRotationalInertia() {
        return this.getMass() / 2;
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
        return getAngle();
    }

    @Override
    public boolean isEnlargedByPowerup() {
        return false;
    }

    @Override
    public void collided(double xp, double yp, Collidable c2, Vector2d n, double l) {
        if (c2 instanceof RWDCar) {
            ((RWDCar) c2).getFlWheel().isOnOil();
            ((RWDCar) c2).getFrWheel().isOnOil();
            ((RWDCar) c2).getBlWheel().isOnOil();
            ((RWDCar) c2).getBrWheel().isOnOil();
        }
    }
}
