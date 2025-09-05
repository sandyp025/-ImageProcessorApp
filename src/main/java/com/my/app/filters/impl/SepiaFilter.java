package com.my.app.filters.impl;

import com.my.app.filters.ImageFilter;
import java.awt.*;
import java.awt.image.BufferedImage;

public class SepiaFilter implements ImageFilter {
    
    @Override
    public BufferedImage apply(BufferedImage originalImage) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        
        BufferedImage sepiaImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = originalImage.getRGB(x, y);
                
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                
                int newR = (int) Math.min(255, (r * 0.393) + (g * 0.769) + (b * 0.189));
                int newG = (int) Math.min(255, (r * 0.349) + (g * 0.686) + (b * 0.168));
                int newB = (int) Math.min(255, (r * 0.272) + (g * 0.534) + (b * 0.131));
                
                Color sepiaColor = new Color(newR, newG, newB);
                sepiaImage.setRGB(x, y, sepiaColor.getRGB());
            }
        }
        
        return sepiaImage;
    }
}