package entities.Cars;

import com.battlezone.megamachines.messaging.EventListener;
import entities.CarComponents.*;
import entities.RWDCar;
import entities.abstractCarComponents.Wheel;

public class DordConcentrate extends RWDCar {

    public DordConcentrate() {
        flWheel = new RegularWheel(this);
        frWheel = new RegularWheel(this);
        blWheel = new RegularWheel(this);
        brWheel = new RegularWheel(this);

        backDifferential = new RearDifferential(blWheel, brWheel);

        driveShaft = new RWDDriveShaft(backDifferential);

        gearbox = new AutomaticSixSpeedGearbox(driveShaft);

        engine = new SmallTurboEngine(gearbox);

        carBody = new RegularChasis();
    }
}
