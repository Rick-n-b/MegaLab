package ru.nstu.lab02v2.Add;

import javafx.beans.property.SimpleLongProperty;
import javafx.scene.layout.Pane;
import ru.nstu.lab02v2.Entities.MotoJaba;

import java.util.*;

public class ToadSpawner {
    private ArrayList<MotoJaba> motos = new ArrayList<>();
    //private ArrayList<CarJaba> cars = new ArrayList<>();

    //private HashSet<Integer> ids;
    //private TreeMap<Integer, Date> spawnTime;

    private int[] motoTypeCount = new int[MotoJaba.motoImages.length];
    private Pane pane;
    private SimpleLongProperty millisProperty;
    private long millis = 0;

    public ToadSpawner(Pane pane){
        this.pane = pane;
    }

    Timer timerCounter = new Timer();
    Timer motoTimer = new Timer();
    Timer carTimer = new Timer();

    TimerTask spawn = new TimerTask() {
        @Override
        public void run() {
            motos.add(new MotoJaba(pane));
            motoTypeCount[motos.getLast().type]++;
        }
    };
    TimerTask countMillis = new TimerTask() {
        @Override
        public void run() {
            millis++;
            millisProperty.set(millis);
        }
    };

    public void start(){
        timerCounter.scheduleAtFixedRate(countMillis, 0, 1);
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

    public SimpleLongProperty getMillisProperty() {
        return millisProperty;
    }

    public void setPane(Pane pane) {
        this.pane = pane;
    }
}
