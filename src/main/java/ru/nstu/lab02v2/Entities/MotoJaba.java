package ru.nstu.lab02v2.Entities;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import ru.nstu.lab02v2.Add.Entity;
import ru.nstu.lab02v2.Add.SimpleAI;
import ru.nstu.lab02v2.Main;

import java.util.Objects;
import java.util.TimerTask;

public class MotoJaba extends Entity implements SimpleAI {

    public final int type = random.nextInt(motoImages.length);
    ImageView view = new ImageView(motoImages[type]);

    public MotoJaba(){
        view.resize(this.getSizeX(), this.getSizeY());
    }
    public MotoJaba(double x, double y){
        view.resize(this.getSizeX(), this.getSizeY());
        setLocation(x, y);
    }
    public MotoJaba(double x, double y, double sizeX, double sizeY){
        this.setSize(sizeX, sizeY);
        view.resize(this.getSizeX(), this.getSizeY());
        setLocation(x, y);
    }
    public MotoJaba(Pane pane){
        view.resize(this.getSizeX(), this.getSizeY());
        setRandomLocationWithBounds(pane.getWidth() - this.sizeX, pane.getHeight() - this.sizeY);
        pane.getChildren().add(view);
    }
    public MotoJaba(Pane pane, double x, double y){
        view.resize(this.getSizeX(), this.getSizeY());
        setLocation(x, y);
        pane.getChildren().add(view);
    }
    public MotoJaba(Pane pane, double x, double y, double sizeX, double sizeY){
        this.setSize(sizeX, sizeY);
        view.resize(this.getSizeX(), this.getSizeY());
        setLocation(x, y);
        pane.getChildren().add(view);
    }


    void spawn(Pane pane){
            pane.getChildren().add(view);
    }
    void die(Pane pane){
        pane.getChildren().remove(view);
    }


    public void setLocation(double x, double y){
        this.x = x;
        this.y = y;
        view.setX(x);
        view.setY(y);
    }
    public void setRandomLocation(){
        this.x = random.nextDouble(600);
        this.y = random.nextDouble(500);
        view.setX(x);
        view.setY(y);
    }
    public void setRandomLocationWithBounds(double maxX, double maxY){
        this.x = random.nextDouble(maxX);
        this.y = random.nextDouble(maxY);
        view.setX(x);
        view.setY(y);
    }
}
