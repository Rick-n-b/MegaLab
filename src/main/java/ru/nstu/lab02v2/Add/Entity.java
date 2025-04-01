package ru.nstu.lab02v2.Add;

import java.util.Random;

public abstract class Entity {
    //hashcode equals переопределение
    //class propeties для пятой лабы(плюс искать в тг)
    protected double x = 0, y = 0;
    protected double sizeX = 150, sizeY = 150;
    protected static final Random random = new Random();
    protected int ID = 0;
    protected long birthtime = 0;

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
    public void setID(int ID) {this.ID = ID;}
    public void setBirthtime(long birthtime) {this.birthtime = birthtime;}

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
    public int getID() {return ID;}
    public long getBirthtime() {return birthtime;}
}
