package ru.nstu.lab02v2.Entities;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import ru.nstu.lab02v2.Add.BaseAI;
import ru.nstu.lab02v2.Add.LocationSetter;
import ru.nstu.lab02v2.Main;

import java.util.Objects;

public class CarJaba extends BaseAI implements LocationSetter {
    public static Image[] carImages = {
            new Image(Objects.requireNonNull(Main.class.getResourceAsStream("Cars/CarToad1.png")), 150, 150, true, true),
            new Image(Objects.requireNonNull(Main.class.getResourceAsStream("Cars/CarToad2.png")), 150, 150, true, true)
    };
    public final int type = random.nextInt(carImages.length);
    private ImageView view = new ImageView(carImages[type]);

    public CarJaba(){
        view.resize(this.getSizeX(), this.getSizeY());
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
        view.resize(this.getSizeX(), this.getSizeY());
        setRandomLocationWithBounds(pane.getWidth() - this.getSizeX(), pane.getHeight()  - this.getSizeY());
        pane.getChildren().add(view);
        setID(id);
        setBirthtime(time);
    }
    public CarJaba(Pane pane, double x, double y){
        view.resize(this.getSizeX(), this.getSizeY());
        setLocation(x, y);
        pane.getChildren().add(view);
    }
    public CarJaba(Pane pane, double x, double y, double sizeX, double sizeY){
        this.setSize(sizeX, sizeY);
        view.resize(this.getSizeX(), this.getSizeY());
        setLocation(x, y);
        pane.getChildren().add(view);
    }

    public void spawn(Pane pane){
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
        view.setX(this.getX());
        view.setY(y);
    }

    public static Image[] getCarImages() {
        return carImages;
    }
}
