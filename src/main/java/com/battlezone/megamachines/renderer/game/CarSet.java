package com.battlezone.megamachines.renderer.game;

import com.battlezone.megamachines.math.Vector3f;
import com.battlezone.megamachines.math.Matrix4f;
import com.battlezone.megamachines.renderer.Model;
import com.battlezone.megamachines.renderer.Shader;
import com.battlezone.megamachines.renderer.Texture;
import com.battlezone.megamachines.util.AssetManager;
import com.battlezone.megamachines.entities.RWDCar;

import java.util.List;
import java.util.Objects;

import static org.lwjgl.opengl.GL30.*;

@Deprecated
public class CarSet extends AbstractRenderable {

//    private static final Shader shader = Shader.CAR;

    private static final List<Texture> CAR_TEXTURES = List.of(
            Objects.requireNonNull(AssetManager.loadTexture("/cars/car1.png")),
            Objects.requireNonNull(AssetManager.loadTexture("/cars/car2.png")),
            Objects.requireNonNull(AssetManager.loadTexture("/cars/car3.png"))
    );
    private List<RWDCar> cars;
    private Camera camera;
    private Matrix4f tempMatrix = new Matrix4f();

    public CarSet(Model model, List<RWDCar> cars, Camera camera) {
        super(model);
        this.cars = cars;
        this.camera = camera;
    }

    @Override
    public void draw() {
//        getShader().use();
//        getShader().setMatrix4f("projection", camera.getProjection());
        for (RWDCar car : cars) {
            getShader().setMatrix4f("rotation", Matrix4f.rotationZ((float) car.getAngle(), tempMatrix));
            getShader().setVector3f("spriteColour", new Vector3f(1f, 0f, 0f));
            getShader().setMatrix4f("size", Matrix4f.scale(car.getScale(), tempMatrix));
            getShader().setInt("sampler", 0);
            CAR_TEXTURES.get(car.getModelNumber()).bind();
            getShader().setMatrix4f("position", Matrix4f.translate(Matrix4f.IDENTITY, car.getXf(), car.getYf(), 0f, tempMatrix));
            glDrawElements(GL_TRIANGLES, getIndexCount(), GL_UNSIGNED_INT, 0);
        }
    }

    @Override
    public Shader getShader() {
//        return shader;
        return null;
    }
}
