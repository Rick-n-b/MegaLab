package ru.nstu.labs.Entities;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import ru.nstu.labs.Add.Entity;
import ru.nstu.labs.Add.LocationSetter;

public class CarJaba extends Entity implements LocationSetter {


    public final int type = random.nextInt(carImages.length);

    public CarJaba(){
        view = new ImageView(carImages[type]);
        view.resize(this.getSizeX(), this.getSizeY());
        view.setY(this.y);
        view.setX(this.x);
    }
    public CarJaba(double x, double y){
        view.resize(this.getSizeX(), this.getSizeY());
        setLocation(x, y);
    }
    public CarJaba(double x, double y, double sizeX, double sizeY){
        this.setSize(sizeX, sizeY);
        view.resize(this.getSizeX(), this.getSizeY());
        setLocation(x, y);
    }
    public CarJaba(Pane pane,int id,long time){
        view = new ImageView(carImages[type]);
        Entity.pane = pane;
        view.resize(this.getSizeX(), this.getSizeY());
        setRandomLocationWithBounds(pane.getWidth() - this.getSizeX(), pane.getHeight()  - this.getSizeY());
        pane.getChildren().add(view);
        setID(id);
        setBirthtime(time);

    }
    public CarJaba(Pane pane, double x, double y){
        view = new ImageView(carImages[type]);
        Entity.pane = pane;
        view.resize(this.getSizeX(), this.getSizeY());
        setLocation(x, y);
        pane.getChildren().add(view);
    }
    public CarJaba(Pane pane, double x, double y, double sizeX, double sizeY){
        view = new ImageView(carImages[type]);
        Entity.pane = pane;
        this.setSize(sizeX, sizeY);
        view.resize(this.getSizeX(), this.getSizeY());
        setLocation(x, y);
        pane.getChildren().add(view);
    }

    public void spawn(Pane pane){
        if(view == null) {
            view = new ImageView(carImages[type]);
            view.resize(this.getSizeX(), this.getSizeY());
            view.setY(this.getY());
            view.setX(this.getX());
        }
        pane.getChildren().add(view);
    }
    public void die(Pane pane){
        pane.getChildren().remove(view);
    }

    public void setLocation(double x, double y){
        this.x = x;
        this.y = y;
        view.setX(this.getX());
        view.setY(this.getY());
    }
    public void setRandomLocation(){
        this.x = random.nextDouble(600);
        this.y = random.nextDouble(500);
        view.setX(this.getX());
        view.setY(this.getY());
    }
    public void setRandomLocationWithBounds(double maxX, double maxY){
        this.x = random.nextDouble(maxX);
        this.y = random.nextDouble(maxY);
        view.setX(this.x);
        view.setY(this.y);
    }

    public static Image[] getCarImages() {
        return carImages;
    }
}
