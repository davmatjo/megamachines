package com.battlezone.megamachines.entities.powerups;

import com.battlezone.megamachines.entities.PhysicalEntity;
import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.math.Matrix4f;
import com.battlezone.megamachines.math.Vector2d;
import com.battlezone.megamachines.physics.Collidable;
import com.battlezone.megamachines.renderer.Drawable;
import com.battlezone.megamachines.renderer.Model;
import com.battlezone.megamachines.renderer.Shader;
import com.battlezone.megamachines.renderer.Texture;
import com.battlezone.megamachines.util.Pair;

import static org.lwjgl.opengl.GL30.*;

public class PowerupSpace extends PhysicalEntity implements Collidable, Drawable {

    /**
     * Number of refreshes this space is dead for before it respawns
     */
    private static final int DEATH_TICKS = 300;

    /**
     * The model of this
     */
    private final Model model = Model.SQUARE;

    /**
     * Number of indices for rendering
     */
    private final int indexCount = model.getIndices().length;

    /**
     * A temporary matrix for calculations
     */
    private final Matrix4f tempMatrix = new Matrix4f();
    /**
     * The powerup manager
     */
    private final PowerupManager manager;
    /**
     * The length
     */
    private double length = getScale();
    /**
     * The width
     */
    private double width = getScale();
    /**
     * Whether this space is alive (not broken)
     */
    private boolean alive = true;
    /**
     * Number of frames since this box was broken
     */
    private int timeSinceDeath = 0;
    /**
     * The powerup stored in this box currently
     */
    private Powerup storedPowerup;
    /**
     * The texture used to render this box
     */
    private Texture currentTexture = Powerup.CRATE;

    /**
     * Creates a new powerup box
     *
     * @param x       x coordinate of the space
     * @param y       y coordinate of the space
     * @param manager the PowerupManager for this powerup
     * @param initial The initial powerup stored in this box
     * @see PowerupManager
     */
    PowerupSpace(double x, double y, PowerupManager manager, Powerup initial) {
        super(x, y, 1f);
        this.manager = manager;
        this.storedPowerup = initial;
    }

    /**
     * @return The currently stored texture for this box
     */
    public Texture getTexture() {
        return currentTexture;
    }

    /**
     * Gets a car to pickup the powerup, called after a collision occurs
     *
     * @param pickup The car that picked up this powerup
     */
    private void pickup(RWDCar pickup) {
        currentTexture = Powerup.BROKEN_CRATE;
        if (storedPowerup != null) {
            if (pickup.getCurrentPowerup() == null) {
                storedPowerup.pickup(pickup);
                pickup.setCurrentPowerup(storedPowerup);
                manager.pickedUp(storedPowerup);
                storedPowerup = null;
            }
        } else {
            System.err.println("Picked up null powerup");
        }
    }

    /**
     * Notify this powerup space of a collision with a car
     *
     * @param xp not used
     * @param yp not used
     * @param c2 the car this has collided with
     * @param n  not used
     * @param l  not used
     */
    @Override
    public void collided(double xp, double yp, Collidable c2, Vector2d n, double l) {
        if (c2 instanceof RWDCar) {
            if (alive && ((RWDCar) c2).getCurrentlyPlaying() == 0) {
                alive = false;
                pickup((RWDCar) c2);
            }
        }
    }

    /**
     * Draws this PowerupSpace into the world
     */
    @Override
    public void draw() {
        getTexture().bind();
        getShader().setMatrix4f("size", Matrix4f.scale(getScale(), tempMatrix));
        getShader().setInt("sampler", 0);
        getShader().setMatrix4f("position", Matrix4f.translate(Matrix4f.IDENTITY, getXf(), getYf(), 0f, tempMatrix));
        glDrawElements(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, 0);
    }

    /**
     * Update this PowerupSpace every frame
     */
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

    /**
     * @return The depth that this space will be rendered at
     */
    @Override
    public int getDepth() {
        return 0;
    }

    /**
     * @return The length of the box
     */
    @Override
    public double getLength() {
        return length;
    }

    /**
     * @return The width of the box
     */
    @Override
    public double getWidth() {
        return width;
    }

    /**
     * @return The model used to draw the box
     * @see Model
     */
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
