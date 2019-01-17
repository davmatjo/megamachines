package entities;

import entities.abstractCarComponents.*;

public class RWDCar extends PhysicalEntity {
    public RWDCar(CarBody carBody, Differential backDifferential, DriveShaft driveShaft, Engine engine,
                  Gearbox gearbox, Springs springs, Wheel flWheel, Wheel frWheel, Wheel blWheel, Wheel brWheel) {
        this.addComponent(carBody);
        this.addComponent(backDifferential);
        this.addComponent(driveShaft);
        this.addComponent(engine);
        this.addComponent(gearbox);
        this.addComponent(springs);
        this.addComponent(flWheel);
        this.addComponent(frWheel);
        this.addComponent(blWheel);
        this.addComponent(brWheel);
    }
}
