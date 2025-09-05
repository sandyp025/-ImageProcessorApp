package com.my.app.io;

import java.awt.image.BufferedImage;
import java.util.Optional;

public interface ImageOperations {
    <T> Optional<BufferedImage> readImage(T source);

    void sendImage(BufferedImage image, String name);
}
