package ru.nstu.labs.Entities;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import ru.nstu.labs.Add.Entity;
import ru.nstu.labs.Add.LocationSetter;

public class MotoJaba extends Entity implements LocationSetter {
    public final int type = random.nextInt(motoImages.length);

    //группировка stream groupBy - подсчёт статистики
    public MotoJaba(){
        view = new ImageView(motoImages[type]);
        view.resize(this.getSizeX(), this.getSizeY());
        view.setY(this.y);
        view.setX(this.x);
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
    public MotoJaba(Pane pane, int id, long time){
        view = new ImageView(motoImages[type]);
        this.pane = pane;
        view.resize(this.getSizeX(), this.getSizeY());
        setRandomLocationWithBounds(pane.getWidth() - this.sizeX, pane.getHeight() - this.sizeY);
        pane.getChildren().add(view);
        setID(id);
        setBirthtime(time);
    }
    public MotoJaba(Pane pane, double x, double y){
        view = new ImageView(motoImages[type]);;
        this.pane = pane;
        view.resize(this.getSizeX(), this.getSizeY());
        setLocation(x, y);
        pane.getChildren().add(view);
    }
    public MotoJaba(Pane pane, double x, double y, double sizeX, double sizeY){
        view = new ImageView(motoImages[type]);;
        this.pane = pane;
        this.setSize(sizeX, sizeY);
        view.resize(this.getSizeX(), this.getSizeY());
        setLocation(x, y);
        pane.getChildren().add(view);
    }

    public void spawn(Pane pane){
        if(view == null) {
            view = new ImageView(motoImages[type]);
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
        view.setX(this.x);
        view.setY(this.y);
    }
    public void setRandomLocation(){
        this.x = random.nextDouble(600);
        this.y = random.nextDouble(500);
        view.setX(this.x);
        view.setY(this.y);
    }
    public void setRandomLocationWithBounds(double maxX, double maxY){
        this.x = random.nextDouble(maxX);
        this.y = random.nextDouble(maxY);
        view.setX(this.x);
        view.setY(this.y);
    }

    public static Image[] getMotoImages() {
        return motoImages;
    }


}
