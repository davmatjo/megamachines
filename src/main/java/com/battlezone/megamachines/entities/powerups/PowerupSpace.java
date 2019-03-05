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

public class PowerupSpace extends PhysicalEntity implements Collidable, Drawable {

    private static final int DEATH_TICKS = 300;

    private final Model model = Model.SQUARE;

    private final int indexCount = model.getIndices().length;

    private final Matrix4f tempMatrix = new Matrix4f();

    private double length = getScale();

    private double width = getScale();

    private boolean alive = true;

    private int timeSinceDeath = 0;

    private Powerup storedPowerup;

    private final PowerupManager manager;

    private Texture currentTexture = Powerup.CRATE;

    public PowerupSpace(double x, double y, PowerupManager manager, Powerup initial) {
        super(x, y, 1f);
        this.manager = manager;
        this.storedPowerup = initial;
    }

    public Texture getTexture() {
        return currentTexture;
    }

    private void pickup(RWDCar pickup) {
        if (storedPowerup != null) {
            storedPowerup.pickup(pickup);
            pickup.setCurrentPowerup(storedPowerup);
            manager.pickedUp(storedPowerup);
            currentTexture = Powerup.BROKEN_CRATE;
            storedPowerup = null;
        } else {
            System.err.println("Picked up null powerup");
        }
    }

    @Override
    public void collided(double xp, double yp, Collidable c2, Pair<Double, Double> n, double l) {
        if (c2 instanceof RWDCar) {
            if (alive) {
                alive = false;
                pickup((RWDCar) c2);
            }
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

    public void update() {
        if (!alive) {
            timeSinceDeath++;
            if (timeSinceDeath >= DEATH_TICKS) {
                timeSinceDeath = 0;
                alive = true;
                storedPowerup = manager.getNext();
                currentTexture = Powerup.CRATE;
            }
        }
    }

    @Override
    public int getDepth() {
        return 0;
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
        return Model.SQUARE;
    }

    @Override
    public double getYVelocity() {
        return 0;
    }

    @Override
    public boolean isEnlargedByPowerup() {
        return false;
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
