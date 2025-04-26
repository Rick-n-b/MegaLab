package ru.nstu.lab02v2.Add;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.layout.Pane;
import ru.nstu.lab02v2.AI.CarAI;
import ru.nstu.lab02v2.AI.MotoAI;
import ru.nstu.lab02v2.Entities.CarJaba;
import ru.nstu.lab02v2.Entities.MotoJaba;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ToadSpawner {
    @JsonIgnore
    public static ToadSpawner instance;

    private ArrayList<MotoJaba> motos;//массив всех мотоциклов
    private ArrayList<CarJaba> cars;
    private HashSet<Integer> id; //коллекция с идентификаторами
    private TreeMap<Integer, Long> timeSpawn; //с временем

    /*массив типов мотоциклов(автоматически расширяемый)
    каждому типу - ячейка массива, в которой лежит кол-во объектов этого типа*/
    private int[] motoTypeCount = new int[MotoJaba.getMotoImages().length];
    private int[] carTypeCount  = new int[CarJaba.getCarImages().length];

    private int currentId = 1; //текущий идентификатор

    private Pane pane;//поле спавна

    public SimpleStringProperty millisProperty = new SimpleStringProperty("0");//переменная необходимая для таймера

    private long millis = 0;//время симуляции в миллисекундах
    private int motoSpawnPeriod = 1000, carSpawnPeriod = 2000;//периоды спавна в миллисекундах
    private int motoSpawnChance = 70, carSpawnChance = 40;//шанс спавна в процентах
    private int motoLife = 2000, carLife = 4000; //время жизни в миллисекундах

    @JsonIgnore
    private final static Random rand = new Random();//переменная рандома

    private Boolean paused = false;//переменная, отслеживающая поставлена ли симуляция на паузу
    private Boolean started = false;//переменная, отслеживающая запущена ли симуляция

    @JsonIgnore
    private Timer timer = new Timer("AllTheTimer");//создание пустого таймера

    @JsonIgnore private TimerTask countMillis;//объявление задания счёта миллисекунд
    @JsonIgnore private TimerTask spawnMoto;//объявление задания спавна мотожаб
    @JsonIgnore private TimerTask spawnCar;
    @JsonIgnore private TimerTask clearMoto; //очистка лишних объектов
    @JsonIgnore private TimerTask clearCar;

    //"сохранённое" время с последнего запуска задания у таймера: 0 - для мото, 1 - машино - жаб
    long saveTime[] = new long[4];

    private MotoAI motoAI;
    private CarAI carAI;
    // конструктор
    public static ToadSpawner getInstance(Pane pane) {
        if(instance == null){
            instance = new ToadSpawner(pane);
            instance.carAI = new CarAI();
            instance.motoAI = new MotoAI();
        }
        return instance;
    }
    public static ToadSpawner getInstance() {
        if(instance == null){
            instance = new ToadSpawner();
            instance.carAI = new CarAI();
            instance.motoAI = new MotoAI();
        }
        return instance;
    }

    private ToadSpawner(Pane pane){
        motos = new ArrayList<>();
        cars = new ArrayList<>();
        id = new HashSet<>();
        timeSpawn = new TreeMap<>();

        this.pane = pane;
    }
    private ToadSpawner(){
        motos = new ArrayList<>();
        cars = new ArrayList<>();
        id = new HashSet<>();
        timeSpawn = new TreeMap<>();

    }


    //запуск симуляции
    public void start(){
        if(!started){
            started = true;
            paused = false;
            clear();//очистка поля
            millis = 0;//обновление таймера
            tasksSet();//задание всех тасков
            timer.scheduleAtFixedRate(countMillis, 0, 1);//запуск таймера
            timer.scheduleAtFixedRate(spawnMoto, motoSpawnPeriod, motoSpawnPeriod);
            timer.scheduleAtFixedRate(spawnCar, carSpawnPeriod, carSpawnPeriod);

            if(motoSpawnPeriod < motoLife){timer.scheduleAtFixedRate(clearMoto, 0,motoSpawnPeriod+1);} //установка минимальных значений
            else timer.scheduleAtFixedRate(clearMoto, 0, motoLife+1);                                   //для таймера удалений

            if(carSpawnPeriod < carLife){timer.scheduleAtFixedRate(clearCar, 0, carSpawnPeriod+1);}
            else timer.scheduleAtFixedRate(clearCar,0, carLife+1);


        }
    }

    //конец симуляции
    public void end(){
        if(started){
            started = false;
            paused = false;
            Arrays.fill(motoTypeCount, 0);
            Arrays.fill(carTypeCount, 0);
            tasksCancel();//убийство тасков
            timer.purge();//сгорание/очистка таймера
            currentId = 1;
            motoAI.setDisabled(true);
            carAI.setDisabled(true);
        }
    }
    //остановка на "паузу"
    public synchronized void pause() {
        if(!paused) {//если не на паузе
            saveTime[0] = (System.currentTimeMillis() - spawnMoto.scheduledExecutionTime());//сохранить время последнего запуска
            saveTime[1] = (System.currentTimeMillis() - spawnCar.scheduledExecutionTime());
            saveTime[2] = (System.currentTimeMillis() - clearMoto.scheduledExecutionTime());
            saveTime[3] = (System.currentTimeMillis() - clearCar.scheduledExecutionTime());
            motoAI.setDisabled(true);
            carAI.setDisabled(true);
            //убийство заданий
            tasksCancel();
            timer.purge();//сгорание таймера

        }
        paused = true;
    }
    //снятие с "паузы"
    public synchronized void unpause(){
        if(started && paused){
            tasksSet();//задача тасков
            timer.scheduleAtFixedRate(countMillis, 1, 1);//включение таймеров
            //таймер включается с задержкой, равной недостающему времени выполнения для предыдущего задания
            //т.е.
            timer.scheduleAtFixedRate(spawnMoto, motoSpawnPeriod - saveTime[0], motoSpawnPeriod);
            timer.scheduleAtFixedRate(spawnCar, carSpawnPeriod - saveTime[1], carSpawnPeriod);

            if(motoSpawnPeriod < motoLife){timer.scheduleAtFixedRate(clearMoto, motoSpawnPeriod - saveTime[2],motoSpawnPeriod+1);} //установка минимальных значений
            else timer.scheduleAtFixedRate(clearMoto, motoLife - saveTime[2], motoLife+1);

            if(carSpawnPeriod < carLife){timer.scheduleAtFixedRate(clearCar, carSpawnPeriod - saveTime[3], carSpawnPeriod+1);}
            else timer.scheduleAtFixedRate(clearCar,carLife - saveTime[3], carLife+1);
            if(motoAI.getDisabled())
                motoAI.setDisabled(false);
            if(carAI.getDisabled())
                carAI.setDisabled(false);
        }
      paused = false;
    }

    //обновление всех тасков
    private void tasksSet(){
        //счёт миллисекунд
        countMillis =  new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(()-> {
                            millis++;
                            millisProperty.set(String.format("%02d:%02d:%03d", millis / 60000, (millis / 1000) % 60, millis % 1000));
                        }
                );
            }
        };
        //спавн мотожаб
        spawnMoto = new TimerTask() {

            @Override
            public void run() {

                Platform.runLater(()->{
                    synchronized (motos) {
                        if (rand.nextInt(100) < motoSpawnChance) {
                            Entity motoJaba = new MotoJaba(pane, currentId, millis);
                            motos.add((MotoJaba) motoJaba);
                            motoTypeCount[motos.getLast().type]++;
                            id.add(currentId);
                            timeSpawn.put(currentId, millis);
                            currentId++;
                        }
                    }
                });
            }
        };
        spawnCar = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(()->{
                    synchronized (cars) {
                        if (rand.nextInt(100) < carSpawnChance) {
                            cars.add(new CarJaba(pane, currentId, millis));
                            carTypeCount[cars.getLast().type]++;
                            id.add(currentId);
                            timeSpawn.put(currentId, millis);
                            currentId++;
                        }
                    }
                });
            }
        };
        clearMoto = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(()->{
                    synchronized (motos) {
                        for (int i = 0; i < motos.size(); i++) {

                            int localid = motos.get(i).ID;
                            long localtime = millis;
                            if (motos.get(i).getBirthtime() + motoLife < localtime) {
                                id.remove(localid);
                                timeSpawn.remove(localid);
                                motos.get(i).die(pane);
                                motoTypeCount[motos.get(i).type]--;

                                motos.remove(i);
                                i--;
                            }
                        }
                    }
            });
        }
    };
        clearCar = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(()->{
                    synchronized (cars) {
                        for (int i = 0; i < cars.size(); i++) {
                            int localid = cars.get(i).ID;
                            long localtime = millis;
                            if (cars.get(i).getBirthtime() + carLife < localtime) {
                                id.remove(localid);
                                timeSpawn.remove(localid);
                                carTypeCount[cars.get(i).type]--;
                                cars.get(i).die(pane);
                                cars.remove(i);
                                i--;
                            }
                        }
                    }
                });
            }

        };
    }
    private void tasksCancel(){
        spawnCar.cancel();
        spawnMoto.cancel();
        countMillis.cancel();
        clearMoto.cancel();
        clearCar.cancel();
    }
    //очистка поля
    public void clear(){
        for(MotoJaba moto : motos){
            moto.die(pane);
        }
        motos.clear();

        for(CarJaba car : cars){
            car.die(pane);
        }
        cars.clear();
    }

    public synchronized void saveConf(String path){
        Properties properties = new Properties();
        properties.setProperty("motoSpawnPeriod", String.valueOf(motoSpawnPeriod));
        properties.setProperty("carSpawnPeriod", String.valueOf(carSpawnPeriod));
        properties.setProperty("motoSpawnChance", String.valueOf(motoSpawnChance));
        properties.setProperty("carSpawnChance", String.valueOf(carSpawnChance));
        properties.setProperty("motoLife", String.valueOf(motoLife));
        properties.setProperty("carLife", String.valueOf(carLife));
        properties.setProperty("motoPrio", String.valueOf(motoAI.getPrio()));
        properties.setProperty("carPrio", String.valueOf(carAI.getPrio()));
        try {
            properties.store(Files.newOutputStream(Path.of(path)), "ToadSpawner config file");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void loadConf(String path){
        Properties properties = new Properties();
        try {
            properties.load(Files.newInputStream(Path.of(path)));
            motoSpawnPeriod = Integer.parseInt(properties.getProperty("motoSpawnPeriod", String.valueOf(1000)));
            carSpawnPeriod = Integer.parseInt(properties.getProperty("carSpawnPeriod", String.valueOf(2000)));
            motoSpawnChance = Integer.parseInt(properties.getProperty("motoSpawnChance", String.valueOf(70)));
            carSpawnChance = Integer.parseInt(properties.getProperty("carSpawnChance", String.valueOf(40)));
            motoLife = Integer.parseInt(properties.getProperty("motoLife", String.valueOf(2000)));
            carLife = Integer.parseInt(properties.getProperty("motoSpawnPeriod", String.valueOf(4000)));
            motoAI.setPrio(Integer.parseInt(properties.getProperty("motoPrio", String.valueOf(5))));
            carAI.setPrio(Integer.parseInt(properties.getProperty("carPrio", String.valueOf(5))));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /*функции геттеры и сеттеры*/

    public MotoAI getMotoAI() {
        return motoAI;
    }
    public CarAI getCarAI() {
        return carAI;
    }
    public ArrayList<MotoJaba> getMotos() {
        return motos;
    }
    public ArrayList<CarJaba> getCars(){
        return cars;
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
    public int[] getCarTypeCount() {
        return carTypeCount;
    }
    public Boolean isPaused(){
        return paused;
    }
    public Boolean isStarted(){
        return started;
    }
    public int getCarSpawnChance() {
        return carSpawnChance;
    }
    public int getCarSpawnPeriod() {
        return carSpawnPeriod;
    }
    public int getMotoSpawnChance() {
        return motoSpawnChance;
    }
    public int getMotoSpawnPeriod() {
        return motoSpawnPeriod;
    }
    public int getMotoLife() {return motoLife;}
    public int getCarLife() {return carLife;}
    public HashSet<Integer> getId() {return id;}
    public TreeMap<Integer, Long> getTimeSpawn() {return timeSpawn;}
    public int getCurrentId() {return currentId;}

    public void setMotoMovePrio(int p) {
        motoAI.setPrio(p);
    }
    public void setCarMovePrio(int p){
        carAI.setPrio(p);
    }
    public void setPane(Pane pane) {
        this.pane = pane;
    }
    public void setMotoSpawnPeriod(int motoSpawnPeriod) {
        this.motoSpawnPeriod = motoSpawnPeriod;
    }
    public void setMotoSpawnChance(int motoSpawnChance) {
        this.motoSpawnChance = motoSpawnChance;
    }
    public void setCarSpawnPeriod(int carSpawnPeriod) {
        this.carSpawnPeriod = carSpawnPeriod;
    }
    public void setCarSpawnChance(int carSpawnChance) {
        this.carSpawnChance = carSpawnChance;
    }
    public void setMotoLife(int motoLife) {this.motoLife = motoLife;}
    public void setCarLife(int carLife) {this.carLife = carLife;}
    public void setId(HashSet<Integer> id) {this.id = id;}
    public void setTimeSpawn(TreeMap<Integer, Long> timeSpawn) {this.timeSpawn = timeSpawn;}
    public void setCurrentId(int currentId) {this.currentId = currentId;}
}
