package messaging;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.Stage;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Group root = new Group();
        Scene primaryScene = new Scene(root);
        primaryStage.setScene(primaryScene);

        Canvas canvas = new Canvas(400, 200);
        root.getChildren().add(canvas);

        GraphicsContext gc = canvas.getGraphicsContext2D();

        ThreadPoolExecutor threads = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);

        threads.execute(new Renderer(gc));
        threads.execute(new RandomCoordinates());

        primaryStage.show();
    }
}
