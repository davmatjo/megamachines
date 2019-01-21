package entities.Cars;

import com.battlezone.megamachines.messaging.EventListener;
import entities.CarComponents.*;
import entities.RWDCar;
import entities.abstractCarComponents.Wheel;

public class DordConcentrate extends RWDCar {

    public DordConcentrate() {
        Wheel flWheel = new RegularWheel(this);
        Wheel frWheel = new RegularWheel(this);
        Wheel blWheel = new RegularWheel(this);
        Wheel brWheel = new RegularWheel(this);

        RearDifferential differential = new RearDifferential(blWheel, brWheel);

        RWDDriveShaft driveShaft = new RWDDriveShaft(differential);

        AutomaticSixSpeedGearbox gearbox = new AutomaticSixSpeedGearbox(driveShaft);

        SmallTurboEngine engine = new SmallTurboEngine(gearbox);

        RegularChasis chasis = new RegularChasis();
    }
}
