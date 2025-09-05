package com.my.app.io;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class ImageFileIO implements ImageOperations{
    @Override
    public <T> Optional<BufferedImage> readImage(T source) {
        if (source instanceof String path) {
            try {
                File imageFile = new File(path);
                return Optional.ofNullable(ImageIO.read(imageFile));
            } catch (IOException e) {
                System.err.println("Unable to read image from path: " + path);
                e.printStackTrace();
            }
        } else {
            System.err.println("ReadImage must be given a path String, type passed: " + source.getClass().getName());
        }
        return Optional.empty();
    }

    @Override
    public void sendImage(BufferedImage image, String name) {
        String outputDir = "output/filtered_"+name+".png";
        saveImage(image, outputDir);
    }

    public void saveImage(BufferedImage image, String relativePath) {
        File outputFile = new File(relativePath);

        File parentDir = outputFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try {
            ImageIO.write(image, "png", outputFile);
            System.out.println("Image saved at: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Failed to save image: " + e.getMessage());
        }
    }
}
