package com.battlezone.megamachines.networking.server.player;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.entities.cars.AffordThoroughbred;
import com.battlezone.megamachines.math.Vector3f;

public class Player {

    private final PlayerConnection connection;
    private RWDCar car;
    private Vector3f colour;

    /*
    * Main constructor of the player class.
    *
    * @param int                Model number of the car
    * @param Vector3f           Colour of the car
    * @param PlayerConnection   Connection of the player to its Client
    * @param String             Name of the player
    * */
    public Player(int modelNumber, Vector3f colour, PlayerConnection connection, String name) {
        this.connection = connection;
        this.colour = colour;
        this.car = new AffordThoroughbred(0, 0, 1.25f, modelNumber, colour, 0, 1, name);
    }

    /*
    * Get car of the player method.
    *
    * @return RWDCar Car of the player to be returned
    * */
    public RWDCar getCar() {
        return car;
    }

    /*
    * Get player connection method.
    *
    * @return PlayerConnection  The connection of the player to be returned
    * */
    public PlayerConnection getConnection() {
        return connection;
    }

    /*
    * Method to recycle car of player.
    * */
    public void recycleCar() {
        this.car = new AffordThoroughbred(0, 0, 1.25f, car.getModelNumber(), colour, 0, 1, car.getName());
    }
}