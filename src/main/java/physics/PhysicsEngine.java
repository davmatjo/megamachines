package physics;
/*
This is The implementation of the game's physics engine.
Here, we compute things like collision control, movement, etc.
 */
public class PhysicsEngine{
    /*
    This variable stores the time at which the last crank was performed
     */
    private static double lastCrank = -1;
    /*
    This method updates the state of the physics engine.
    Preferably, it should be called at least once between each frame.
     */
    public static void crank() {
        if (lastCrank == -1) {
            lastCrank = System.currentTimeMillis();
            return;
        }

        double deltaTime = System.currentTimeMillis() - lastCrank;
        lastCrank = System.currentTimeMillis();


    }
}