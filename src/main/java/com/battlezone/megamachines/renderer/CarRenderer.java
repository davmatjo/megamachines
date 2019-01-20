package com.battlezone.megamachines.renderer;

import com.battlezone.megamachines.math.Matrix4f;
import com.battlezone.megamachines.util.AssetManager;
import com.battlezone.megamachines.world.Car;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class CarRenderer extends Renderer {

    private List<Car> cars;
    private static final List<Texture> carTextures = new ArrayList<Texture>() {{
        add(AssetManager.loadTexture("/cars/car1.png"));
        add(AssetManager.loadTexture("/cars/car2.png"));
        add(AssetManager.loadTexture("/cars/car3.png"));
    }};

    public CarRenderer(Model model, Shader shader, List<Car> cars) {
        super(model, shader);
        this.cars = cars;

    }

    @Override
    public void draw() {
        for (Car car : cars) {
            getShader().setMatrix4f("size", Matrix4f.scale(car.getScale()));
            getShader().setInt("sampler", 0);
            carTextures.get(car.getModelNumber()).bind();
            getShader().setMatrix4f("position", new Matrix4f().translate(car.getPosition(), 0f));
            glDrawElements(GL_TRIANGLES, getIndexCount(), GL_UNSIGNED_INT, 0);
        }
    }
}
