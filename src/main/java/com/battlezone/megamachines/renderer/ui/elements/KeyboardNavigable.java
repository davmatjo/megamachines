package com.battlezone.megamachines.renderer.ui.elements;

public interface KeyboardNavigable {
    /**
     * Sets whether or not an element is focused and therefore should respond to clicks etc
     *
     * @param active
     */
    void focusChanged(boolean active);

    /**
     * Sets whether or not an object is managed. If it is, then it should not respond to cursor movement etc itself and the manager will do this
     *
     * @param managed
     */
    void setManaged(boolean managed);

    /**
     * Runs the action of this element
     */
    void runAction();

    /**
     * @return Y coordinate of the top of the element
     */
    float getTopY();

    /**
     * @return Y coordinate of the bottom of the element
     */
    float getBottomY();

    /**
     * @return X coordinate of the left of the element
     */
    float getLeftX();

    /**
     * @return X coordinate of the right of the element
     */
    float getRightX();
}
