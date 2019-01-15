package messaging;

import java.util.Random;

public class RandomCoordinates implements Runnable {

    @Override
    public void run() {
        Random r = new Random();

        while (true) {
            try {
                Thread.sleep(300);
                MessageBus.fire(Double.toString(r.nextDouble()));
                MessageBus.fire(new Coordinate(r.nextInt(100), r.nextInt(100), 0));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
