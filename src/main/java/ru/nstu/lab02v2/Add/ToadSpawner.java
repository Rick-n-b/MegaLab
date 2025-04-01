package ru.nstu.lab02v2.Add;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.layout.Pane;
import ru.nstu.lab02v2.Entities.CarJaba;
import ru.nstu.lab02v2.Entities.MotoJaba;

import java.awt.desktop.SystemEventListener;
import java.util.*;

public class ToadSpawner {
    private ArrayList<MotoJaba> motos;//массив всех мотоциклов
    private ArrayList<CarJaba> cars;
    private HashSet<Integer> id; //коллекция с идентификаторами
    private TreeMap<Integer, Long> timeSpawn; //с временем

    /*массив типов мотоциклов(автоматически расширяемый)
    каждому типу - ячейка массива, в которой лежит кол-во объектов этого типа*/
    private int[] motoTypeCount = new int[MotoJaba.getMotoImages().length];
    private int[] carTypeCount = new int[CarJaba.getCarImages().length];

    private int currentId = 1; //текущий идентификатор

    private Pane pane;//поле спавна

    public SimpleStringProperty millisProperty = new SimpleStringProperty("0");//переменная необходимая для таймера

    private long millis = 0;//время симуляции в миллисекундах
    private int motoSpawnPeriod = 1000, carSpawnPeriod = 2000;//периоды спавна в миллисекундах
    private int motoSpawnChance = 70, carSpawnChance = 40;//шанс спавна в процентах
    private int motoLife = 2000, carLife = 4000; //время жизни в миллисекундах

    private final static Random rand = new Random();//переменная рандома

    private Boolean paused = false;//переменная, отслеживающая поставлена ли симуляция на паузу
    private Boolean started = false;//переменная, отслеживающая запущена ли симуляция

    private Timer timer = new Timer("AllTheTimer");//создание пустого таймера

    private TimerTask countMillis;//объявление задания счёта миллисекунд
    private TimerTask spawnMoto;//объявление задания спавна мотожаб
    private TimerTask spawnCar;
    private TimerTask clearMoto; //очистка лишних объектов
    private TimerTask clearCar;

    //"сохранённое" время с последнего запуска задания у таймера: 0 - для мото, 1 - машино - жаб
    long saveTime[] = new long[4];

    // конструктор
    public ToadSpawner(Pane pane){
        motos = new ArrayList<>();
        cars = new ArrayList<>();
        id = new HashSet<>();
        timeSpawn = new TreeMap<>();
        this.pane = pane;
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
            else timer.scheduleAtFixedRate(clearMoto, 0,motoLife+1);                                   //для таймера удалений

            if(carSpawnPeriod < carLife){timer.scheduleAtFixedRate(clearCar,0,carSpawnPeriod+1);}
            else timer.scheduleAtFixedRate(clearCar,0,carLife+1);
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
        }
    }
    //остановка на "паузу"
    public synchronized void pause() {
        if(!paused) {//если не на паузе
            saveTime[0] = (System.currentTimeMillis() - spawnMoto.scheduledExecutionTime());//сохранить время последнего запуска
            saveTime[1] = (System.currentTimeMillis() - spawnCar.scheduledExecutionTime());
            saveTime[2] = (System.currentTimeMillis() - clearMoto.scheduledExecutionTime());
            saveTime[3] = (System.currentTimeMillis() - clearCar.scheduledExecutionTime());
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

            if(motoSpawnPeriod < motoLife){timer.scheduleAtFixedRate(clearMoto, motoSpawnPeriod+1- saveTime[2] ,motoSpawnPeriod+1);} //установка минимальных значений
            else timer.scheduleAtFixedRate(clearMoto, motoLife+1- saveTime[2] ,motoLife+1);

            if(carSpawnPeriod < carLife){timer.scheduleAtFixedRate(clearCar,carSpawnPeriod+1- saveTime[3],carSpawnPeriod+1);}
            else timer.scheduleAtFixedRate(clearCar,carLife+1- saveTime[3],carLife+1);
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
                            millisProperty.set(String.format("%02d:%02d:%03d", millis / 60000, millis / 1000, millis % 1000));
                        }
                );
            }
        };
        //спавн мотожаб
        spawnMoto = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(()->{
                    if(rand.nextInt(100) < motoSpawnChance){
                        motos.add(new MotoJaba(pane,currentId, millis));
                        motoTypeCount[motos.getLast().type]++;
                        id.add(currentId);
                        timeSpawn.put(currentId,millis);
                        currentId++;
                    }
                });
            }
        };
        spawnCar = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(()->{
                    if(rand.nextInt(100) < carSpawnChance){
                        cars.add(new CarJaba(pane,currentId,millis));
                        carTypeCount[cars.getLast().type]++;
                        id.add(currentId);
                        timeSpawn.put(currentId,millis);
                        currentId++;
                    }
                });
            }
        };
        clearMoto = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(()->{
                    for(int i = 0;i < motos.size(); i++){
                        int localid = motos.get(i).ID;
                        long localtime = millis;
                        if(motos.get(i).getBirthtime() + motoLife < localtime){
                            id.remove(localid);
                            timeSpawn.remove(localid);
                            motos.get(i).die(pane);
                            motoTypeCount[motos.get(i).type]--;
                            motos.remove(i);
                            i--;
                        }
                    }
            });
        }
    };
        clearCar = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(()->{
                    for(int i = 0;i < cars.size(); i++){
                        int localid = cars.get(i).ID;
                        long localtime = millis;
                        if(cars.get(i).getBirthtime() + carLife < localtime){
                            id.remove(localid);
                            timeSpawn.remove(localid);
                            carTypeCount[cars.get(i).type]--;
                            cars.get(i).die(pane);
                            cars.remove(i);
                            i--;
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

    /*функции геттеры и сеттеры*/
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
