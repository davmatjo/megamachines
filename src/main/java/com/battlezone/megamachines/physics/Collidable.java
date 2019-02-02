package com.battlezone.megamachines.physics;

import com.battlezone.megamachines.util.Pair;

import java.util.List;

/**
 * All collidable objects must implement this interface
 */
public interface Collidable {
    /**
     * Returns a list of all hitboxes
     * @return The list of hitboxes
     */
    public List<List<Pair<Double,Double>>> getCornersOfAllHitBoxes();

    /**
     * This function gets called when the object has collided
     */
    public void collided();
}
