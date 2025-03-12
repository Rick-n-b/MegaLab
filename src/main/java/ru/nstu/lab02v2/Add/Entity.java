package ru.nstu.lab02v2.Add;

import javafx.scene.image.Image;
import ru.nstu.lab02v2.Main;

import java.util.Objects;
import java.util.Random;

public abstract class Entity {
    protected double x = 0, y = 0;
    protected double sizeX = 150, sizeY = 0;
    protected static final Random random = new Random();

    protected static Image[] motoImages = {
            new Image(Objects.requireNonNull(Main.class.getResourceAsStream("MotoToad1.png")), 150, 150, true, true),
            new Image(Objects.requireNonNull(Main.class.getResourceAsStream("MotoToad2.png")), 150, 150, true, true)
    };
    protected static Image[] carImages = {
            new Image(Objects.requireNonNull(Main.class.getResourceAsStream("CarToad1.png")), 150, 150, true, true),
            new Image(Objects.requireNonNull(Main.class.getResourceAsStream("CarToad2.png")), 150, 150, true, true)
    };

    public void setSizeY(double sizeY) {
        this.sizeY = sizeY;
    }
    public void setSizeX(double sizeX) {
        this.sizeX = sizeX;
    }
    public void setX(double x) {
        this.x = x;
    }
    public void setY(double y) {
        this.y = y;
    }
    public void setSize(double sizeX, double sizeY){
        this.sizeX = sizeX;
        this.sizeY = sizeY;
    }

    public static Image[] getMotoImages() {
        return motoImages;
    }
    public double getSizeX() {
        return sizeX;
    }
    public double getSizeY() {
        return sizeY;
    }
    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }

}
