package com.my.app.filters.impl;

import com.my.app.filters.ImageFilter;
import java.awt.*;
import java.awt.image.BufferedImage;

public class EmbossFilter implements ImageFilter {
    
    @Override
    public BufferedImage apply(BufferedImage originalImage) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        
        BufferedImage embossedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        // Emboss kernel
        int[][] embossKernel = {{-2, -1, 0}, {-1, 1, 1}, {0, 1, 2}};
        
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                int sumR = 0, sumG = 0, sumB = 0;
                
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        int rgb = originalImage.getRGB(x + i, y + j);
                        int r = (rgb >> 16) & 0xFF;
                        int g = (rgb >> 8) & 0xFF;
                        int b = rgb & 0xFF;
                        
                        int weight = embossKernel[i + 1][j + 1];
                        sumR += r * weight;
                        sumG += g * weight;
                        sumB += b * weight;
                    }
                }
                
                // Add gray offset and clamp values
                int newR = Math.min(255, Math.max(0, sumR + 128));
                int newG = Math.min(255, Math.max(0, sumG + 128));
                int newB = Math.min(255, Math.max(0, sumB + 128));
                
                Color embossColor = new Color(newR, newG, newB);
                embossedImage.setRGB(x, y, embossColor.getRGB());
            }
        }
        
        return embossedImage;
    }
}