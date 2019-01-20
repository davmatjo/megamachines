package com.battlezone.megamachines.world;

import org.joml.Vector2f;

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
