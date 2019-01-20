package com.battlezone.megamachines.renderer;

import com.battlezone.megamachines.math.Vector3f;
import com.battlezone.megamachines.math.Matrix4f;
import com.battlezone.megamachines.util.AssetManager;
import com.battlezone.megamachines.world.Car;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL30.*;

public class CarRenderer extends Renderer {

    private static final List<Texture> carTextures = new ArrayList<Texture>() {{
        add(AssetManager.loadTexture("/cars/car1.png"));
        add(AssetManager.loadTexture("/cars/car2.png"));
        add(AssetManager.loadTexture("/cars/car3.png"));
    }};
    private List<Car> cars;
    private float angle = 0.707f;
    private Camera camera;

    public CarRenderer(Model model, List<Car> cars, Camera camera) {
        super(model, AssetManager.loadShader("/shaders/car"));
        this.cars = cars;
        this.camera = camera;
    }

    @Override
    public void draw() {
        getShader().use();
        getShader().setMatrix4f("projection", camera.getProjection());
        for (Car car : cars) {
            getShader().setMatrix4f("rotation", Matrix4f.rotateZ(angle));
            getShader().setVector3f("spriteColour", new Vector3f(1f, 0f, 0f));
            getShader().setMatrix4f("size", Matrix4f.scale(car.getScale()));
            getShader().setInt("sampler", 0);
            carTextures.get(car.getModelNumber()).bind();
            getShader().setMatrix4f("position", new Matrix4f().translate(car.getPosition(), 0f));
            glDrawElements(GL_TRIANGLES, getIndexCount(), GL_UNSIGNED_INT, 0);
        }
    }
}
