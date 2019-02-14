package com.battlezone.megamachines.world.track;

import com.battlezone.megamachines.physics.Collidable;
import com.battlezone.megamachines.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class TrackEdges implements Collidable {

    private static final float HITBOX_WIDTH = 0.05f;
    private static final Pair<Double, Double> velocity = new Pair<>(0.0, 0.0);
    private final List<List<Pair<Double, Double>>> hitbox;
    private final Pair<Double, Double> position;

    public TrackEdges(TrackPiece piece) {
        this.position = new Pair<>(piece.getX(), piece.getY());

        switch (piece.getType()) {
            case DOWN:
            case UP:
                // Left and right
                hitbox = List.of(

                        List.of(new Pair<>(piece.getX() - (piece.getScale() / 2) + HITBOX_WIDTH, piece.getY() + (piece.getScale() / 2)),
                                new Pair<>(piece.getX() - (piece.getScale() / 2), piece.getY() + (piece.getScale() / 2)),
                                new Pair<>(piece.getX() - (piece.getScale() / 2), piece.getY() - (piece.getScale() / 2)),
                                new Pair<>(piece.getX() - (piece.getScale() / 2) + HITBOX_WIDTH, piece.getY() - (piece.getScale() / 2))),

                        List.of(new Pair<>(piece.getX() + (piece.getScale() / 2) - HITBOX_WIDTH, piece.getY() + (piece.getScale() / 2)),
                                new Pair<>(piece.getX() + (piece.getScale() / 2), piece.getY() + (piece.getScale() / 2)),
                                new Pair<>(piece.getX() + (piece.getScale() / 2), piece.getY() - (piece.getScale() / 2)),
                                new Pair<>(piece.getX() + (piece.getScale() / 2) - HITBOX_WIDTH, piece.getY() - (piece.getScale() / 2))));
                break;
            case LEFT:
            case RIGHT:
                hitbox = new ArrayList<>();
                break;
            case RIGHT_UP:
            case DOWN_LEFT:
                hitbox = new ArrayList<>();
                break;
            case LEFT_UP:
            case DOWN_RIGHT:
                hitbox = new ArrayList<>();
                break;
            case UP_RIGHT:
            case LEFT_DOWN:
                hitbox = new ArrayList<>();
                break;
            case UP_LEFT:
            case RIGHT_DOWN:
                hitbox = new ArrayList<>();
                break;
            default:
                throw new RuntimeException("Cases missed");
        }
    }

    @Override
    public List<List<Pair<Double, Double>>> getCornersOfAllHitBoxes() {
        return hitbox;
    }

    @Override
    public Pair<Double, Double> getVelocity() {
        return velocity;
    }

    @Override
    public double getCoefficientOfRestitution() {
        return 0.9;
    }

    @Override
    public double getMass() {
        return 1000000.0;
    }

    @Override
    public Pair<Double, Double> getCenterOfMassPosition() {
        return position;
    }

    @Override
    public Pair<Double, Double> getPositionDelta() {
        return new Pair<Double, Double>(0.0, 0.0);
    }

    @Override
    public double getRotationalInertia() {
        return 1000000.0;
    }

    @Override
    public void applyVelocityDelta(Pair<Double, Double> impactResult) {
        return;
    }

    @Override
    public void applyAngularVelocityDelta(double delta) {
        return;
    }

    @Override
    public void correctCollision(Pair<Double, Double> velocityDifference) {
    }

    @Override
    public double getRotation() {
        return 0;
    }

    @Override
    public double getXVelocity() {
        return 0;
    }

    @Override
    public double getYVelocity() {
        return 0;
    }
}
