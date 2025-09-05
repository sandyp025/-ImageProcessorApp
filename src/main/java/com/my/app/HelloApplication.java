package com.my.app;

import com.my.app.filters.ImageFilter;
import com.my.app.filters.impl.GreyScaleFilter;
import com.my.app.image.DrawMultipleImagesOnCanvas;
import com.my.app.io.ImageFileIO;
import com.my.app.io.ImageOperations;
import com.my.app.processor.ImageProcessor;
import javafx.application.Application;
import javafx.stage.Stage;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Scanner;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException, URISyntaxException {
        URI resourceURI = getClass().getResource("/quino-al-mBQIfKlvowM-unsplash.jpg").toURI();
//        URI resourceURI = getClass().getResource("/painting-mountain-lake-with-mountain-background.jpg").toURI();
        Path path = Paths.get(resourceURI);
        String originalFileName = path.getFileName().toString();


        if (path == null || !path.toFile().exists()) {
            System.err.println("No image found at the given path");
            return;
        }

        ImageOperations imageIO = new ImageFileIO();
        Optional<BufferedImage> imageOpt = imageIO.readImage(path.toString());

        if (imageOpt.isEmpty()) {
            System.err.println("Error reading image");
            return;
        }

        System.out.println("Image Loaded");

        BufferedImage image = imageOpt.get();
        DrawMultipleImagesOnCanvas drawMultipleImagesOnCanvas = DrawMultipleImagesOnCanvas.getInstance();
        drawMultipleImagesOnCanvas.initialize(stage, image.getWidth(), image.getHeight());

        ImageProcessor processor = new ImageProcessor();
        ImageFilter imageFilter = new GreyScaleFilter();

        // Run input & processing on a separate thread
        new Thread(() -> {
            Scanner sc = new Scanner(System.in);
            boolean loop = true;
            while (loop) {
                System.out.println("\n\n----Welcome to Image Processing App----");
                System.out.println("Process image -:");
                System.out.println("1. Asynchronously");
                System.out.println("2. Synchronously");
                System.out.println("3. Exit");
                System.out.print("Enter your choice [1-3]: ");
                int choice = sc.nextInt();
                switch (choice) {
                    case 1 -> {
                        BufferedImage filteredImage = processor.processImage(image, 40, imageFilter, true);
                        imageIO.sendImage(filteredImage, originalFileName);
                    }
                    case 2 -> {
                        BufferedImage filteredImage = processor.processImage(image, 40, imageFilter, false);
                        imageIO.sendImage(filteredImage, originalFileName);
                    }
                    case 3 -> loop = false;
                    default -> System.out.println("Wrong Input, please make sure give integer [1-3]");
                }
            }
            System.out.println("\nThank you for using the App.");
            System.out.println("See you soon, any feedback appreciated 🥹");
            System.out.println("Made in India ❤️ by Sandip Mandal ✨");
            sc.close();
        }).start();
    }

    public static void main(String[] args) {
        launch();
    }
}