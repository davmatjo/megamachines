package com.battlezone.megamachines.events;

/**
 * A pooled Event
 */
public interface Pooled {

    /**
     * Return this event to the pool
     */
    void delete();
}
