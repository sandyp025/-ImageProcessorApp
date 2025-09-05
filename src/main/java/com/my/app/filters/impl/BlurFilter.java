package com.my.app.filters.impl;

import com.my.app.filters.ImageFilter;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

public class BlurFilter implements ImageFilter {
    private final int intensity;
    
    public BlurFilter() {
        this.intensity = 3;
    }
    
    public BlurFilter(int intensity) {
        this.intensity = Math.max(1, Math.min(intensity, 9));
    }
    
    @Override
    public BufferedImage apply(BufferedImage originalImage) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        
        BufferedImage blurredImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        float[] matrix = createGaussianKernel(intensity);
        Kernel kernel = new Kernel(intensity, intensity, matrix);
        ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        
        op.filter(originalImage, blurredImage);
        return blurredImage;
    }
    
    private float[] createGaussianKernel(int size) {
        float[] kernel = new float[size * size];
        float sum = 0.0f;
        int center = size / 2;
        float sigma = size / 3.0f;
        
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int x = i - center;
                int y = j - center;
                kernel[i * size + j] = (float) Math.exp(-(x * x + y * y) / (2 * sigma * sigma));
                sum += kernel[i * size + j];
            }
        }
        
        for (int i = 0; i < kernel.length; i++) {
            kernel[i] /= sum;
        }
        
        return kernel;
    }
}