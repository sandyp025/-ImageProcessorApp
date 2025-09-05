package com.my.app.image;

import javafx.animation.AnimationTimer;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.Stage;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class DrawMultipleImagesOnCanvas {
    private static final DrawMultipleImagesOnCanvas instance = new DrawMultipleImagesOnCanvas();

    private BlockingQueue<ImageData> queue = new LinkedBlockingQueue<>();

    private Canvas canvas;
    private GraphicsContext gc;
    private Stage primaryStage;

    public void initialize(Stage primaryStage, int width, int height) {
        this.primaryStage = primaryStage;
        this.canvas = new Canvas(width, height);
        this.gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, width, height);

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                if(!queue.isEmpty()) {
                    drawNextImage();
                }
            }
        }.start();

        Group root = new Group();
        root.getChildren().add(canvas);

        primaryStage.setScene(new Scene(root, width, height));

        primaryStage.sizeToScene();
        primaryStage.show();
    }

    private void drawNextImage() {
        ImageData image = queue.poll();
        if (image != null) {
                gc.drawImage(SwingFXUtils.toFXImage(image.getImage(),null),
                        image.getI(), image.getJ(), image.getX(), image.getY());
//                System.out.println("Drawing using Thread: "+ Thread.currentThread().getName());
//                System.out.printf("Drawing image at i: %d, j: %d%n", image.getI(), image.getJ());
        }

    }

    public void addImageToQueue(ImageData image) {
        try {
            queue.put(image);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Interrupted while adding image to queue");
        }
    }

    public static DrawMultipleImagesOnCanvas getInstance() {
        return instance;
    }
}
