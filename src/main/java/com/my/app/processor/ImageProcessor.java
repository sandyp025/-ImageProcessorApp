package com.my.app.processor;

import com.my.app.filters.ImageFilter;
import com.my.app.image.DrawMultipleImagesOnCanvas;
import com.my.app.image.ImageData;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ImageProcessor {
    private ExecutorService executorService;
    private DrawMultipleImagesOnCanvas drawFunc;

    public ImageProcessor(){
        this.executorService = Executors.newFixedThreadPool(100);
        this.drawFunc = DrawMultipleImagesOnCanvas.getInstance();
    }

    public BufferedImage processImage(BufferedImage image, int num, ImageFilter filter, boolean async) {
        if (async) {
            return processImageAsync(image, num, filter);
        } else {
            return processImageSync(image, filter);
        }
    }

    private BufferedImage processImageSync(BufferedImage image, ImageFilter filter) {
        long startTime = System.currentTimeMillis();
        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage filteredImage = filter.apply(image);
        ImageData imageData = new ImageData(filteredImage, 0, 0, width, height);

        drawFunc.addImageToQueue(imageData);
        System.out.println("Image Processed synchronously & also added to queue");

        long endTime = System.currentTimeMillis();
        System.out.println("Total processing time: " + (endTime - startTime) + " ms");

        return filteredImage;

    }

    private BufferedImage processImageAsync(BufferedImage image, int num, ImageFilter filter) {
        long startTime = System.currentTimeMillis();

        int width = image.getWidth();
        int height = image.getHeight();

        if(width%num != 0 || height%num !=0 ) {
            System.err.println("Require a num, which completely divides the image, given num: "+num);
            return null;
        }

        int horizontalImages = width / num;
        int verticalImages = height / num;

        List<CompletableFuture<ImageData>> futures = new ArrayList<>();

        for (int i=0; i<horizontalImages; i++) {
            for (int j=0; j<verticalImages; j++) {
                BufferedImage subImage = image.getSubimage(i*num, j*num, num, num);
                int finalI = i;
                int finalJ = j;
                CompletableFuture<ImageData> future = CompletableFuture.supplyAsync( () -> {
                    BufferedImage filteredImage = filter.apply(subImage);
                    return new ImageData(filteredImage, finalI * num, finalJ * num, num, num);
                }, executorService);
                futures.add(future);
            }
        }

        futures.forEach(future -> {
            future.thenAccept(drawFunc::addImageToQueue)
                    .exceptionally(ex -> {
                        System.err.println("Error processing image: " + ex.getMessage());
                        return null;
                    });
        });

        // announce when all are done
        CompletableFuture<Void> allDone = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allDone.thenRun(() -> {
            long endTime = System.currentTimeMillis();
            System.out.println("All photos done asynchronously!");
            System.out.println("Total processing time: " + (endTime - startTime) + " ms");
        });
        allDone.join();

        // Combine results
        BufferedImage finalImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = finalImage.createGraphics();

        for (CompletableFuture<ImageData> future : futures) {
            try {
                ImageData imageData = future.get();
                g.drawImage(imageData.getImage(), imageData.getI(), imageData.getJ(), null);
            } catch (InterruptedException | ExecutionException e) {
                System.err.println("Failed to get processed tile: " + e.getMessage());
            }
        }
        g.dispose();

        return finalImage;
    }
}
