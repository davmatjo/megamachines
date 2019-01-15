package messaging;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Renderer implements Runnable {

    private GraphicsContext gc;
    private BlockingQueue<Coordinate> frames = new LinkedBlockingQueue<>();

    public Renderer(GraphicsContext gc) {
        this.gc = gc;
        MessageBus.register(this);
    }

    @Override
    public void run() {
        try {
            while (true) {
                Coordinate pos = frames.take();
                gc.setFill(Color.WHITE);
                gc.fillRect(0, 0, 200, 400);

                gc.setFill(Color.BLACK);
                gc.fillOval(pos.getX(), pos.getY(), 80, 80);

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Listen
    public void addFrame(Coordinate frame) {
        frames.add(frame);
    }

    @Listen
    public void event(String message) {
        System.out.println(message);
    }
}
