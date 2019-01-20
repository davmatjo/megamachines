package com.battlezone.megamachines.world;

import com.battlezone.megamachines.math.Vector2f;

public class Car extends GameObject {

    private int modelNumber;

    public Car(Vector2f position, float scale, int number) {
        super(position, scale);
        modelNumber = number;
    }

    public int getModelNumber() {
        return modelNumber;
    }
}
