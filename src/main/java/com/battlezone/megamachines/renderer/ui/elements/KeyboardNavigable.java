package com.battlezone.megamachines.renderer.ui.elements;

public interface KeyboardNavigable {
    void focusChanged(boolean active);

    void setManaged(boolean managed);

    void runAction();

    float getTopY();

    float getBottomY();

    float getLeftX();

    float getRightX();
}
