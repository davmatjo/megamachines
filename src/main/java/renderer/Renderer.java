package renderer;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import messaging.RandomCoordinates;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Renderer extends Application {

    private static final String CAR_1 = Renderer.class.getResource("/car1.png").toString();
    private static int SCREEN_WIDTH;
    private static int SCREEN_HEIGHT;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        final Image car1Original = new Image(CAR_1);
        final Image car = new Image(CAR_1, 200, 200, true, false);
        final double HEIGHT = car.getHeight() * 4;
        final double WIDTH = car.getWidth() * 4;

        primaryStage.setFullScreen(true);
        primaryStage.setResizable(false);
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);

        Group root = new Group();
        Scene primaryScene = new Scene(root);
        primaryStage.setScene(primaryScene);

        Canvas canvas = new Canvas(400, 200);
        root.getChildren().add(canvas);

        GraphicsContext gc = canvas.getGraphicsContext2D();

        ThreadPoolExecutor threads = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);

        Image car2 = rotateImage(car, 40);

        gc.drawImage(car2, 100, 100);

//        threads.execute(new messaging.Renderer(gc));
//        threads.execute(new RandomCoordinates());

        primaryStage.show();
    }

    private Image rotateImage(Image image, double degrees) {
        ImageView iv = new ImageView(image);
        iv.setRotate(degrees);
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.RED);
        return iv.snapshot(params, null);
    }
}
