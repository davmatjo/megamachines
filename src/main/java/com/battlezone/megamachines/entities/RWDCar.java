package com.battlezone.megamachines.entities;

import com.battlezone.megamachines.Main;
import com.battlezone.megamachines.entities.abstractCarComponents.*;
import com.battlezone.megamachines.input.KeyCode;
import com.battlezone.megamachines.math.Matrix4f;
import com.battlezone.megamachines.math.Vector3f;
import com.battlezone.megamachines.messaging.MessageBus;
import com.battlezone.megamachines.renderer.game.Model;
import com.battlezone.megamachines.renderer.game.Shader;
import com.battlezone.megamachines.renderer.game.Texture;
import com.battlezone.megamachines.util.AssetManager;

import static org.lwjgl.opengl.GL11.*;

/**
 * This is a Rear Wheel Drive car
 */
public class RWDCar extends PhysicalEntity {
    /**
     * The amount of weight the car has on the front wheels when stationary
     */
    private double weightOnFront;

    private static final Shader SHADER = AssetManager.loadShader("/shaders/car");

    private final int modelNumber;

    private Matrix4f tempMatrix = new Matrix4f();

    private final Vector3f colour;

    private final Texture texture;

    /**
     * A number between 0 and 1 which expresses how much the accelerator pedal was pressed
     */
    public double accelerationAmount = 0;

    /**
     * A number between 0 and 1 which expresses how much the brake pedal was pressed
     */
    public double brakeAmount = 0;

    /**
     * The amount the steering wheel is turned to the left
     */
    public double turnAmount;

    /**
     * The amount of weight the car has on the back wheels when stationary
     */
    private double weightOnBack;

    /**
     * The car's body
     */
    protected CarBody carBody;
    /**
     * The car's back differential
     */
    protected Differential backDifferential;
    /**
     * The car's drive shaft
     */
    protected DriveShaft driveShaft;
    /**
     * The car's engine
     */
    protected Engine engine;
    /**
     * The car's gearbox
     */
    protected Gearbox gearbox;
    /**
     * The car's springs
     */
    protected Springs springs;
    /**
     * The car's front left wheel
     */
    protected Wheel flWheel;
    /**
     * The car's front right wheel
     */
    protected Wheel frWheel;
    /**
     * The car's back left wheel
     */
    protected Wheel blWheel;
    /**
     * The car's back right wheel
     */
    protected Wheel brWheel;

    /**
     * Returns the car's weight
     *
     * @return The car's weight
     */
    public double getWeight() {
        return carBody.getWeight();
    }

    /**
     * Returns the engine of the car
     *
     * @return The engine of the car
     */
    public Engine getEngine() {
        return this.engine;
    }

    /**
     * Returns the gearbox of the car
     *
     * @return The gearbox of the car
     */
    public Gearbox getGearbox() {
        return this.gearbox;
    }

    public RWDCar(double x, double y, float scale, int modelNumber, Vector3f colour) {
        super(x, y, scale, Model.generateCar());
        MessageBus.register(this);
        this.modelNumber = modelNumber;
        this.texture = AssetManager.loadTexture("/cars/car" + modelNumber + ".png");
        this.colour = colour;
    }

    /**
     * TODO: Get weight on a per wheel basis
     */
    public double getLoadOnWheel() {
        return (this.carBody.getWeight() + this.engine.getWeight()) / 4;
    }

    /**
     * This method should be called once per com.battlezone.megamachines.physics step
     */
    public void physicsStep() {
        accelerationAmount = Main.gameInput.isPressed(KeyCode.W) ? 1.0 : 0;
        brakeAmount = Main.gameInput.isPressed(KeyCode.S) ? 1.0 : 0;

        turnAmount = Main.gameInput.isPressed(KeyCode.A) ? 1.0 : 0;
        turnAmount = Main.gameInput.isPressed(KeyCode.D) ? (turnAmount - 1.0) : turnAmount;

        this.setAngle(this.getAngle() + turnAmount);

        this.engine.pushTorque(accelerationAmount);

        flWheel.brake(brakeAmount);
        frWheel.brake(brakeAmount);
        blWheel.brake(brakeAmount);
        brWheel.brake(brakeAmount);

        flWheel.physicsStep();
        frWheel.physicsStep();
        blWheel.physicsStep();
        brWheel.physicsStep();

        if (brakeAmount == 0) {
            this.engine.adjustRPM();
        }
    }

    public int getModelNumber() {
        return modelNumber;
    }

//    @EventListener
//    public void setDriverPress(KeyPressEvent event) {
//        if (event.getKeyCode() == KeyCode.W) {
//            //TODO: make this a linear movement
//            accelerationAmount = 1;
//        }
//        if (event.getKeyCode() == KeyCode.S) {
//            brakeAmount = 1000;
//        }
//    }
//
//    @EventListener
//    public void setDriverRelease(KeyReleaseEvent event) {
//        if (event.getKeyCode() == KeyCode.W) {
//            //TODO: make this a linear movement
//            accelerationAmount = 0;
//        }
//        if (event.getKeyCode() == KeyCode.S) {
//            brakeAmount = 0;
//        }
//    }

    @Override
    public void draw() {
        getShader().setMatrix4f("rotation", Matrix4f.rotationZ((float) getAngle(), tempMatrix));
        getShader().setVector3f("spriteColour", colour);
        getShader().setMatrix4f("size", Matrix4f.scale(getScale(), tempMatrix));
        getShader().setInt("sampler", 0);
        texture.bind();
        getShader().setMatrix4f("position", Matrix4f.translate(Matrix4f.IDENTITY, getXf(), getYf(), 0f, tempMatrix));
        glDrawElements(GL_TRIANGLES, getIndexCount(), GL_UNSIGNED_INT, 0);
    }

    @Override
    public Shader getShader() {
        return SHADER;
    }
}
