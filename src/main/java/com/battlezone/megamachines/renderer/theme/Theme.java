package com.battlezone.megamachines.renderer.theme;

import com.battlezone.megamachines.math.Vector4f;
import com.battlezone.megamachines.renderer.ui.Colour;

public enum Theme {
    DEFAULT, ICE, SPACE;

    public String toString() {
        switch (this) {
            case ICE:
                return "/ice";
            case SPACE:
                return "/space";
            default:
                return "/default";
        }
    }

    public String getName() {
        switch (this) {
            case ICE:
                return "Ice";
            case SPACE:
                return "Space";
            default:
                return "Default";
        }
    }

    public byte toByte() {
        return (byte) this.ordinal();
    }

    public Vector4f uiFontColour() {
        switch (this) {
            case ICE:
                return Colour.BLACK;
            default:
                return Colour.WHITE;
        }
    }
}