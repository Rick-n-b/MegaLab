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

    public static Image[] motoImages = {
            new Image(Objects.requireNonNull(Main.class.getResourceAsStream("Motos/MotoToad1.png")), 150, 150, true, true),
            new Image(Objects.requireNonNull(Main.class.getResourceAsStream("Motos/MotoToad2.png")), 150, 150, true, true)
    };
    public final int type = random.nextInt(motoImages.length);
    private ImageView view = new ImageView(motoImages[type]);

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
    public MotoJaba(Pane pane, int id, long time){
        view.resize(this.getSizeX(), this.getSizeY());
        setRandomLocationWithBounds(pane.getWidth() - this.sizeX, pane.getHeight() - this.sizeY - 150);
        pane.getChildren().add(view);
        setID(id);
        setBirthtime(time);
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


    public void spawn(Pane pane){
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
