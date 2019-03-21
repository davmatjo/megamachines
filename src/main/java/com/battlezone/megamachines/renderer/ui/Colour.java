package com.battlezone.megamachines.renderer.ui;

import com.battlezone.megamachines.math.Vector3f;
import com.battlezone.megamachines.math.Vector4f;

public class Colour {

    public static final Vector4f RED = new Vector4f(0.8f, 0.1f, 0.1f, 1f);
    public static final Vector4f GREEN = new Vector4f(0.1f, 0.8f, 0.1f, 1f);
    public static final Vector4f BLUE = new Vector4f(0.3f, 0.3f, 0.9f, 1f);
    public static final Vector4f WHITE = new Vector4f(1, 1, 1, 1);
    public static final Vector4f BLACK = new Vector4f(0f, 0f, 0f, 1);
    public static final Vector4f GREY = new Vector4f(0.3f, 0.3f, 0.3f, 1);

    public static final Vector3f WHITE_3 = new Vector3f(WHITE.x, WHITE.y, WHITE.z);
    public static final Vector3f GREEN_3 = new Vector3f(GREEN.x, GREEN.y, GREEN.z);
    public static final Vector3f BLACK_3 = new Vector3f(BLACK.x, BLACK.y, BLACK.z);
}
