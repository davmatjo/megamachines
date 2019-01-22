package com.battlezone.megamachines.renderer.game;

import com.battlezone.megamachines.math.Vector3f;
import com.battlezone.megamachines.math.Matrix4f;
import com.battlezone.megamachines.util.AssetManager;
import entities.RWDCar;

import java.util.List;
import java.util.Objects;

import static org.lwjgl.opengl.GL30.*;

public class CarSet extends AbstractRenderable {

    private static final List<Texture> CAR_TEXTURES = List.of(
            Objects.requireNonNull(AssetManager.loadTexture("/cars/car1.png")),
            Objects.requireNonNull(AssetManager.loadTexture("/cars/car2.png")),
            Objects.requireNonNull(AssetManager.loadTexture("/cars/car3.png"))
    );
    private List<RWDCar> cars;
    private Camera camera;

    public CarSet(Model model, List<RWDCar> cars, Camera camera) {
        super(model, AssetManager.loadShader("/shaders/car"));
        this.cars = cars;
        this.camera = camera;
    }

    @Override
    public void draw() {
//        getShader().use();
//        getShader().setMatrix4f("projection", camera.getProjection());
        for (RWDCar car : cars) {
            getShader().setMatrix4f("rotation", Matrix4f.rotateZ((float)car.getAngle()));
            getShader().setVector3f("spriteColour", new Vector3f(1f, 0f, 0f));
            getShader().setMatrix4f("size", Matrix4f.scale(car.getScale()));
            getShader().setInt("sampler", 0);
            CAR_TEXTURES.get(car.getModelNumber()).bind();
            getShader().setMatrix4f("position", new Matrix4f().translate(car.getXf(), car.getYf(), 0f));
            glDrawElements(GL_TRIANGLES, getIndexCount(), GL_UNSIGNED_INT, 0);
        }
    }
}
