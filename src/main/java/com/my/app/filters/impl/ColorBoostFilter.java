package com.my.app.filters.impl;

import com.my.app.filters.ImageFilter;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ColorBoostFilter implements ImageFilter {
    private final double saturationBoost;
    
    public ColorBoostFilter() {
        this.saturationBoost = 1.5;
    }
    
    public ColorBoostFilter(double saturationBoost) {
        this.saturationBoost = Math.max(0.1, Math.min(saturationBoost, 3.0));
    }
    
    @Override
    public BufferedImage apply(BufferedImage originalImage) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        
        BufferedImage boostedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = originalImage.getRGB(x, y);
                
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                
                // Convert RGB to HSV
                float[] hsv = Color.RGBtoHSB(r, g, b, null);
                
                // Boost saturation
                hsv[1] = Math.min(1.0f, (float) (hsv[1] * saturationBoost));
                
                // Convert back to RGB
                Color boostedColor = Color.getHSBColor(hsv[0], hsv[1], hsv[2]);
                boostedImage.setRGB(x, y, boostedColor.getRGB());
            }
        }
        
        return boostedImage;
    }
}