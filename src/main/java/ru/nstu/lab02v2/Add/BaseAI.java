package ru.nstu.lab02v2.Add;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import ru.nstu.lab02v2.Main;

import java.util.Objects;
import java.util.Random;

public abstract class BaseAI {
    //hashcode equals переопределение
    //class propeties для пятой лабы(плюс искать в тг)
    protected double x = 0, y = 0;
    protected double sizeX = 150, sizeY = 150;
    protected static final Random random = new Random();
    protected static Pane pane = null;
    protected int ID = 0;
    protected long birthtime = 0;
    protected int velocity = 5;
    protected ImageView view;

    public static Image[] motoImages = {
            new Image(Objects.requireNonNull(Main.class.getResourceAsStream("Motos/MotoToad1.png")), 150, 150, true, true),
            new Image(Objects.requireNonNull(Main.class.getResourceAsStream("Motos/MotoToad2.png")), 150, 150, true, true)
    };

    protected class MoveRunnable implements Runnable{
        int type = 1;
        int direction = 1;
        public MoveRunnable(int type){
            this.type = type;
            this.direction = -2 * random.nextInt(2) + 1;
        }

        @Override
        public synchronized void run() {
            Thread.yield();
            if (type == 1)//movex
                while ((x > -sizeX || x < pane.getWidth() - sizeX)) {
                    //synchronized ("JavaFX Application Thread") {
                        try {
                            x += direction * velocity;
                            view.setX(x);
                            Thread.sleep(17);
                        } catch (InterruptedException e) {
                            System.out.println(Thread.currentThread().getName() + " was interrupted!");
                        }
                   // }
                }
            else//moveY
                while ((y < pane.getHeight() + sizeY || y > -sizeY)) {
                    synchronized ("JavaFX Application Thread") {
                        try {
                            y += direction * velocity;
                            view.setY(y);
                            Thread.sleep(17);
                        } catch (InterruptedException e) {
                            System.out.println(Thread.currentThread().getName() + " was interrupted!");
                        }
                    }
                }
                Thread.currentThread().interrupt();
        }
    }

    protected Thread moveThread;
    // = new Thread(new MoveRunnable(), "move")


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
    public static void setPane(Pane pane) {
        BaseAI.pane = pane;
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
    public int getID() {return ID;}
    public long getBirthtime() {return birthtime;}
}
