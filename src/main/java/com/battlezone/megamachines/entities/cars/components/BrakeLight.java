package com.battlezone.megamachines.entities.cars.components;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.math.Matrix4f;
import com.battlezone.megamachines.math.Vector4f;
import com.battlezone.megamachines.renderer.Drawable;
import com.battlezone.megamachines.renderer.Model;
import com.battlezone.megamachines.renderer.Shader;
import com.battlezone.megamachines.renderer.Texture;
import com.battlezone.megamachines.renderer.ui.Colour;

import static org.lwjgl.opengl.GL11.*;

public class BrakeLight implements Drawable {

    private static final float SCALE_REDUCTION_MULTIPLIER = 0.9f;
    private final RWDCar target;
    private final Matrix4f tempMatrix = new Matrix4f();
    private final int indexCount = Model.SQUARE.getIndices().length;

    public BrakeLight(RWDCar target) {
        this.target = target;
    }

    @Override
    public void draw() {

        Vector4f colour = Colour.RED;
        if (target.getGearbox().isOnReverse()) {
            colour = Colour.WHITE;
        }

        if (target.getBrakeAmount() > 0) {
            Texture.BLANK.bind();

            getShader().setMatrix4f("rotation", Matrix4f.rotationZ((float) target.getAngle(), tempMatrix));
            getShader().setVector4f("spriteColour", colour);
            getShader().setMatrix4f("size", Matrix4f.scale(target.getScale() * 0.08f, tempMatrix));
            getShader().setInt("sampler", 0);


            double x = target.getX() - (target.getScale() * SCALE_REDUCTION_MULTIPLIER) * Math.cos(Math.toRadians(target.getRotation())) + (target.getScale() / 5f) * Math.cos(Math.toRadians(target.getRotation() + 90));
            double y = target.getY() - (target.getScale() * SCALE_REDUCTION_MULTIPLIER) * Math.sin(Math.toRadians(target.getRotation())) + (target.getScale() / 5f) * Math.sin(Math.toRadians(target.getRotation() + 90));
            getShader().setMatrix4f("position", Matrix4f.translate(Matrix4f.IDENTITY, (float) x, (float) y, 0f, tempMatrix));
            glDrawElements(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, 0);

            x = target.getX() - (target.getScale() * SCALE_REDUCTION_MULTIPLIER) * Math.cos(Math.toRadians(target.getRotation())) - (target.getScale() / 5f) * Math.cos(Math.toRadians(target.getRotation() + 90));
            y = target.getY() - (target.getScale() * SCALE_REDUCTION_MULTIPLIER) * Math.sin(Math.toRadians(target.getRotation())) - (target.getScale() / 5f) * Math.sin(Math.toRadians(target.getRotation() + 90));
            getShader().setMatrix4f("position", Matrix4f.translate(Matrix4f.IDENTITY, (float) x, (float) y, 0f, tempMatrix));
            glDrawElements(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, 0);
        }
    }

    @Override
    public Model getModel() {
        return Model.SQUARE;
    }

    @Override
    public Shader getShader() {
        return Shader.CAR;
    }

    @Override
    public int getDepth() {
        return target.getDepth();
    }
}
