package com.battlezone.megamachines.renderer;

import com.battlezone.megamachines.util.AssetManager;
import com.battlezone.megamachines.world.Car;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL30.*;

public class CarRenderer extends Renderer {

    private List<Car> cars;
    private float angle = 0.707f;
    private Camera camera;
    private static final List<Texture> carTextures = new ArrayList<Texture>() {{
        add(AssetManager.loadTexture("/cars/car1.png"));
        add(AssetManager.loadTexture("/cars/car2.png"));
        add(AssetManager.loadTexture("/cars/car3.png"));
    }};

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
            getShader().setMatrix4f("rotation", new Matrix4f().rotate(new Quaternionf(0f, 0f, angle, angle)));
            getShader().setVector3f("spriteColour", new Vector3f(1f, 0f, 0f));
            getShader().setMatrix4f("size", new Matrix4f().scale(car.getScale()));
            getShader().setInt("sampler", 0);
            carTextures.get(car.getModelNumber()).bind();
            getShader().setMatrix4f("position", new Matrix4f().translate(car.getPosition().x, car.getPosition().y, 0f));
            glDrawElements(GL_TRIANGLES, getIndexCount(), GL_UNSIGNED_INT, 0);
        }
    }
}
