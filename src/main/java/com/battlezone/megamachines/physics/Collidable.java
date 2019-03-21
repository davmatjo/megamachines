package com.battlezone.megamachines.physics;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.entities.powerups.PowerupSpace;
import com.battlezone.megamachines.entities.powerups.types.physical.BombDrop;
import com.battlezone.megamachines.entities.powerups.types.physical.OilSpillOnGround;
import com.battlezone.megamachines.math.Vector2f;
import com.battlezone.megamachines.math.Vector3d;
import com.battlezone.megamachines.sound.SoundEngine;
import com.battlezone.megamachines.util.AssetManager;
import com.battlezone.megamachines.util.Pair;

import java.util.List;

/**
 * All collidable objects must implement this interface
 */
public interface Collidable {
    /**
     * Returns a list of all hitboxes
     *
     * @return The list of hitboxes
     */
    List<List<Pair<Double, Double>>> getCornersOfAllHitBoxes();

    /**
     * Returns the body's velocity to the point of impact
     * The point of impact and the objects are always on the game world, so we assume
     * it's enough to get their velocity
     * First the speed, then the angle in degrees
     */
    Pair<Double, Double> getVelocity();

    /**
     * 1 means perfectly elastic, 0 means plastic
     * You should probably return something close to 1 (> 0.7)
     * This gets multiplied by the other's object coefficient
     *
     * @return The coefficient of restitution
     */
    double getCoefficientOfRestitution();

    /**
     * Gets the mass of the object
     *
     * @return The mass of the object
     */
    double getMass();

    /**
     * Returns the vector from the object's center of mass to the collision point
     *
     * @return The vector from the object's center of mass to the collision point
     */
    default Pair<Double, Double> getVectorFromCenterOfMass(double xp, double yp, Pair<Double, Double> position) {
        double x = position.getFirst();
        double y = position.getSecond();

        double dx = xp - x;
        double dy = yp - y;

        return new Pair<>(2 * Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2)),
                Math.atan2(dy, dx));
    }

    /**
     * Returns this object's center of mass (x, y) position
     *
     * @return The object's center of mass (x, y) position
     */
    Pair<Double, Double> getCenterOfMassPosition();

    /**
     * Gets the object's rotational inertia
     *
     * @return The object's rotational inertia
     */
    double getRotationalInertia();

    /**
     * Tells the collidable object to add a vector to the object's speed vector
     *
     * @param impactResult The resulting vector from the impact
     */
    void applyVelocityDelta(Pair<Double, Double> impactResult);

    /**
     * Applies an angular velocity to the object
     *
     * @param delta The delta to be applied
     */
    void applyAngularVelocityDelta(double delta);

    /**
     * Corrects collision based on velocity difference vector
     *
     * @param velocityDifference The velocity difference vector
     */
    void correctCollision(Pair<Double, Double> velocityDifference, double l);

    /**
     * Returns the object's rotation
     *
     * @return The object's rotation
     */
    double getRotation();

    /**
     * True when the object is currently enlarged by a powerup, false otherwise
     */
    boolean isEnlargedByPowerup();

    /**
     * This function gets called when the object has collided
     */
    default void collided(double xp, double yp, Collidable c2, Pair<Double, Double> n, double l) {
        if (c2 instanceof PowerupSpace) {
            c2.collided(xp, yp, this, n, l);
            return;
        } else if (c2 instanceof OilSpillOnGround) {
            c2.collided(xp, yp, this, n, l);
            return;
        } else if (c2 instanceof RWDCar) {
            final RWDCar car = (RWDCar) c2;
            if (!car.isControlsActive())
                return;
        }

        Pair<Double, Double> vector1FromCenterOfMass = getVectorFromCenterOfMass(xp, yp, this.getCenterOfMassPosition());
        Pair<Double, Double> vector2FromCenterOfMass = c2.getVectorFromCenterOfMass(xp, yp, c2.getCenterOfMassPosition());

        if (!this.isEnlargedByPowerup()) {
            this.correctCollision(vector1FromCenterOfMass, l);
        } else {
            c2.correctCollision(vector2FromCenterOfMass, l);
        }

        if (!c2.isEnlargedByPowerup() || c2 instanceof BombDrop) {
            c2.correctCollision(vector2FromCenterOfMass, l);
        } else {
            this.correctCollision(vector1FromCenterOfMass, l);
        }

        n.setSecond(n.getSecond() % 360);
        n.setSecond(Math.toRadians(n.getSecond()));

        Pair<Double, Double> firstVelocity = this.getVelocity();
        Pair<Double, Double> secondVelocity = c2.getVelocity();
        double firstX = firstVelocity.getFirst() * (Math.cos(Math.toRadians(firstVelocity.getSecond())));
        double secondX = secondVelocity.getFirst() * (Math.cos(Math.toRadians(secondVelocity.getSecond())));
        double firstY = firstVelocity.getFirst() * (Math.sin(Math.toRadians(firstVelocity.getSecond())));
        double secondY = secondVelocity.getFirst() * (Math.sin(Math.toRadians(secondVelocity.getSecond())));
        double x = firstX - secondX;
        double y = firstY - secondY;

        Pair<Double, Double> relativeVelocity = new Pair<Double, Double>
                (Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)),
                        Math.atan2(y, x));
        Vector3d relativeVelocity3D = new Vector3d(relativeVelocity);

        n.setFirst(1.0);
        Pair<Double, Double> unitVector = n;
        Vector3d unitVector3D = new Vector3d(unitVector);

        double restitution = getCoefficientOfRestitution() * c2.getCoefficientOfRestitution();

        double energy;

        double angularEffects1, angularEffects2;

        Pair<Double, Double> v1p = vector1FromCenterOfMass;
        v1p.setSecond(v1p.getSecond() + Math.PI / 2);
        Vector3d v1p3D = new Vector3d(v1p);
        Pair<Double, Double> v2p = vector2FromCenterOfMass;
        v2p.setSecond(v2p.getSecond() + Math.PI / 2);
        Vector3d v2p3D = new Vector3d(v2p);


        angularEffects1 = Math.pow(Vector3d.dotProduct(v1p3D, unitVector3D), 2) / getRotationalInertia();
        angularEffects2 = Math.pow(Vector3d.dotProduct(v2p3D, unitVector3D), 2) / c2.getRotationalInertia();

        energy = -((Vector3d.dotProduct(relativeVelocity3D, unitVector3D) * (1 + restitution)) /
                ((1 / getMass()) + (1 / c2.getMass()) + angularEffects1 + angularEffects2));
        energy = -Math.abs(energy);

        double oldCar1Energy = this.getMass() * Math.pow(this.getVelocity().getFirst(), 2);
        double oldCar2Energy = c2.getMass() * Math.pow(c2.getVelocity().getFirst(), 2);

        if (!isEnlargedByPowerup()) {
            applyVelocityDelta(new Pair<>(energy / getMass(), Math.toDegrees(unitVector.getSecond())));
        }

        if (!c2.isEnlargedByPowerup()) {
            c2.applyVelocityDelta(new Pair<>(-energy / c2.getMass(), Math.toDegrees(unitVector.getSecond())));
        }

        double newCar1Energy = this.getMass() * Math.pow(this.getVelocity().getFirst(), 2);
        double newCar2Energy = c2.getMass() * Math.pow(c2.getVelocity().getFirst(), 2);

        //If this happens, we got the wrong n, so we correct the results
        if (newCar1Energy + newCar2Energy > oldCar1Energy + oldCar2Energy) {
            if (!isEnlargedByPowerup()) {
                applyVelocityDelta(new Pair<>(-energy / getMass(), Math.toDegrees(unitVector.getSecond())));
            }

            if (!c2.isEnlargedByPowerup()) {
                c2.applyVelocityDelta(new Pair<>(energy / c2.getMass(), Math.toDegrees(unitVector.getSecond())));
            }
        }

        if (!isEnlargedByPowerup()) {
            applyAngularVelocityDelta((Vector3d.dotProduct(v1p3D, unitVector3D) * energy / getRotationalInertia()) / 2);
        }

        if (!c2.isEnlargedByPowerup()) {
            c2.applyAngularVelocityDelta((-Vector3d.dotProduct(v2p3D, unitVector3D) * energy / c2.getRotationalInertia()) / 2);
        }

        if (!AssetManager.isHeadless())
            SoundEngine.getSoundEngine().collide((float) (energy / (Math.max(this.getVelocity().getFirst(), c2.getVelocity().getFirst()))), new Vector2f((float) xp, (float) yp));
    }


}
