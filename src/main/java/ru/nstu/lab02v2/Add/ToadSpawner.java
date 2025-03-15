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
    private int motoSpawnPeriod = 5000, carSpawnperiod = 100;
    private int motoSpawnChance = 100, carSpawnChance = 20;
    private final static Random rand = new Random();

    private Boolean paused = false;
    private Boolean started = false;

    public ToadSpawner(Pane pane){
        motos = new ArrayList<>();
        this.pane = pane;
    }

    Timer timer = new Timer("AllTheTimer");

    TimerTask spawnMoto;
    TimerTask countMillis;
    //millis saved, moto millis saved, cars millis saved
    long saveTime[] = new long[2];

    public void start(){
        started = true;
        paused = false;
        clear();
        millis = 0;
        setTasks();
        timer.scheduleAtFixedRate(countMillis, 0, 1);
        timer.scheduleAtFixedRate(spawnMoto, 0, motoSpawnPeriod);
    }
    public void end(){
        started = false;
        paused = false;
        countMillis.cancel();
        spawnMoto.cancel();
        timer.purge();
    }
    public synchronized void pause() {
        if(!paused) {
            saveTime[0] = (System.currentTimeMillis() - spawnMoto.scheduledExecutionTime());
            System.out.println(saveTime[0]);
            spawnMoto.cancel();
            countMillis.cancel();
            timer.purge();
        }
        paused = true;
    }
    public synchronized void unpause(){
        if(started && paused){
            setTasks();
            timer.scheduleAtFixedRate(countMillis, 1, 1);
            timer.scheduleAtFixedRate(spawnMoto, motoSpawnPeriod - saveTime[0], motoSpawnPeriod);
        }
      paused = false;
    }

    private void setTasks(){
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

    public Boolean isPaused(){
        return paused;
    }

    public void clear(){
        for(MotoJaba moto : motos){
            moto.die(pane);
        }
        motos.clear();
    }

    public void setPane(Pane pane) {
        this.pane = pane;
    }
    public void setPaused(Boolean state){
        this.paused = state;
    }


}
