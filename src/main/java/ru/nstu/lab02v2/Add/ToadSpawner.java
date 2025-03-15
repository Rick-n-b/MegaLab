package ru.nstu.lab02v2.Add;

import javafx.application.Platform;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.layout.Pane;
import ru.nstu.lab02v2.Entities.MotoJaba;

import java.util.*;

public class ToadSpawner {
    private ArrayList<MotoJaba> motos;

    private int[] motoTypeCount = new int[MotoJaba.getMotoImages().length];
    private Pane pane;
    public SimpleStringProperty millisProperty = new SimpleStringProperty("0");
    private long millis = 0;
    private int motoSpawnPeriod = 200, carSpawnperiod = 100;
    private int motoSpawnChance = 30, carSpawnChance = 20;
    private final static Random rand = new Random();

    public ToadSpawner(Pane pane){
        motos = new ArrayList<>();
        this.pane = pane;
    }

    Timer timer = new Timer();

    TimerTask spawnMoto;

    TimerTask countMillis;

    public void start(){
        countMillis =  new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(()-> {
                            millis++;
                            millisProperty.set(String.valueOf(millis));
                        }
                );
            }
        };
        spawnMoto = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(()->{
                    if(rand.nextInt(100) <= motoSpawnChance){
                        motos.add(new MotoJaba(pane));
                        motoTypeCount[motos.getLast().type]++;
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(countMillis, 0, 1);
        timer.scheduleAtFixedRate(spawnMoto, 0, motoSpawnPeriod);
    }
    public void end(){
        countMillis.cancel();
        spawnMoto.cancel();
        timer.purge();
    }
    public void pause() throws InterruptedException {
        wait();
    }
    public void unpause(){
       notifyAll();
    }

    public ArrayList<MotoJaba> getMotos() {
        return motos;
    }
    public Pane getPane() {
        return pane;
    }
    public long getMillis() {
        return millis;
    }
    public int[] getMotoTypeCount() {
        return motoTypeCount;
    }


    public void setPane(Pane pane) {
        this.pane = pane;
    }



}
