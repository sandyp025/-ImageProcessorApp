package com.my.app.filters.impl;

import com.my.app.filters.ImageFilter;
import java.awt.*;
import java.awt.image.BufferedImage;

public class VintageFilter implements ImageFilter {
    
    @Override
    public BufferedImage apply(BufferedImage originalImage) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        
        BufferedImage vintageImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = originalImage.getRGB(x, y);
                
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                
                // Apply vintage color grading
                int newR = (int) Math.min(255, r * 1.2 + 20);
                int newG = (int) Math.min(255, g * 1.1 + 10);
                int newB = (int) Math.min(255, b * 0.8 - 10);
                
                // Add vignette effect
                double centerX = width / 2.0;
                double centerY = height / 2.0;
                double maxDistance = Math.sqrt(centerX * centerX + centerY * centerY);
                double distance = Math.sqrt((x - centerX) * (x - centerX) + (y - centerY) * (y - centerY));
                double vignette = 1.0 - (distance / maxDistance) * 0.4;
                
                newR = (int) Math.min(255, Math.max(0, newR * vignette));
                newG = (int) Math.min(255, Math.max(0, newG * vignette));
                newB = (int) Math.min(255, Math.max(0, newB * vignette));
                
                Color vintageColor = new Color(newR, newG, newB);
                vintageImage.setRGB(x, y, vintageColor.getRGB());
            }
        }
        
        return vintageImage;
    }
}