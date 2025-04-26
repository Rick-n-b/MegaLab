package ru.nstu.lab02v2.Add;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import ru.nstu.lab02v2.Main;

import java.util.Objects;
import java.util.Random;

public abstract class Entity {
    //hashcode equals переопределение
    //class propeties для пятой лабы(плюс искать в тг)
    protected double x = 0, y = 0;
    protected double sizeX = 150, sizeY = 150;
    @JsonIgnore
    protected static final Random random = new Random();
    @JsonIgnore
    protected static Pane pane = null;
    public int ID = 0;
    protected long birthtime = 0;
    protected double velocity = 3;
    public boolean isDirectionSet = false;
    protected ImageView view;
    @JsonIgnore
    public static Image[] motoImages = {
            new Image(Objects.requireNonNull(Main.class.getResourceAsStream("Motos/MotoToad1.png")), 150, 150, true, true),
            new Image(Objects.requireNonNull(Main.class.getResourceAsStream("Motos/MotoToad2.png")), 150, 150, true, true)
    };
    @JsonIgnore
    public static Image[] carImages = {
            new Image(Objects.requireNonNull(Main.class.getResourceAsStream("Cars/CarToad1.png")), 150, 150, true, true),
            new Image(Objects.requireNonNull(Main.class.getResourceAsStream("Cars/CarToad2.png")), 150, 150, true, true)
    };

    public void setSizeY(double sizeY) {
        this.sizeY = sizeY;
    }
    public void setSizeX(double sizeX) {
        this.sizeX = sizeX;
    }
    public void setX(double x) {
        Platform.runLater(()->{
            this.x = x;
            view.setX(x);
        });
    }
    public void setY(double y) {
        Platform.runLater(()-> {
            this.y = y;
            view.setY(y);
        });
    }
    public void setSize(double sizeX, double sizeY){
        this.sizeX = sizeX;
        this.sizeY = sizeY;
    }
    public void setID(int ID) {this.ID = ID;}
    public void setBirthtime(long birthtime) {this.birthtime = birthtime;}
    public static void setPane(Pane pane) {
        Entity.pane = pane;
    }
    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }

    public double getVelocity() {
        return velocity;
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
