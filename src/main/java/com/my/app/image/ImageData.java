package com.my.app.image;

import java.awt.image.BufferedImage;

public class ImageData {
    private BufferedImage image;
    private int i;
    private int j;
    private int X;

    public ImageData(BufferedImage image, int i, int j, int x, int y) {
        this.image = image;
        this.i = i;
        this.j = j;
        X = x;
        Y = y;
    }

    public ImageData(){}

    private int Y;

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public int getJ() {
        return j;
    }

    public void setJ(int j) {
        this.j = j;
    }

    public int getX() {
        return X;
    }

    public void setX(int x) {
        X = x;
    }

    public int getY() {
        return Y;
    }

    public void setY(int y) {
        Y = y;
    }
}
