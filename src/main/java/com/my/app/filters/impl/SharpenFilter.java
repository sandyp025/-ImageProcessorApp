package com.my.app.filters.impl;

import com.my.app.filters.ImageFilter;
import java.awt.*;
import java.awt.image.BufferedImage;

public class SharpenFilter implements ImageFilter {
    
    @Override
    public BufferedImage apply(BufferedImage originalImage) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        
        BufferedImage sharpenedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        // Sharpen kernel
        float[][] sharpenKernel = {{0, -1, 0}, {-1, 5, -1}, {0, -1, 0}};
        
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                float sumR = 0, sumG = 0, sumB = 0;
                
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        int rgb = originalImage.getRGB(x + i, y + j);
                        int r = (rgb >> 16) & 0xFF;
                        int g = (rgb >> 8) & 0xFF;
                        int b = rgb & 0xFF;
                        
                        float weight = sharpenKernel[i + 1][j + 1];
                        sumR += r * weight;
                        sumG += g * weight;
                        sumB += b * weight;
                    }
                }
                
                int newR = Math.min(255, Math.max(0, (int) sumR));
                int newG = Math.min(255, Math.max(0, (int) sumG));
                int newB = Math.min(255, Math.max(0, (int) sumB));
                
                Color sharpenColor = new Color(newR, newG, newB);
                sharpenedImage.setRGB(x, y, sharpenColor.getRGB());
            }
        }
        
        return sharpenedImage;
    }
}