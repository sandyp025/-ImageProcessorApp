package com.my.app.filters.impl;

import com.my.app.filters.ImageFilter;
import java.awt.*;
import java.awt.image.BufferedImage;

public class EdgeDetectionFilter implements ImageFilter {
    
    @Override
    public BufferedImage apply(BufferedImage originalImage) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        
        BufferedImage edgeImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        // Sobel operators
        int[][] sobelX = {{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};
        int[][] sobelY = {{-1, -2, -1}, {0, 0, 0}, {1, 2, 1}};
        
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                int gx = 0, gy = 0;
                
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        int rgb = originalImage.getRGB(x + i, y + j);
                        int gray = getGrayScale(rgb);
                        
                        gx += gray * sobelX[i + 1][j + 1];
                        gy += gray * sobelY[i + 1][j + 1];
                    }
                }
                
                int magnitude = (int) Math.sqrt(gx * gx + gy * gy);
                magnitude = Math.min(255, Math.max(0, magnitude));
                
                Color edgeColor = new Color(magnitude, magnitude, magnitude);
                edgeImage.setRGB(x, y, edgeColor.getRGB());
            }
        }
        
        return edgeImage;
    }
    
    private int getGrayScale(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        return (int) (0.2126 * r + 0.7152 * g + 0.0722 * b);
    }
}