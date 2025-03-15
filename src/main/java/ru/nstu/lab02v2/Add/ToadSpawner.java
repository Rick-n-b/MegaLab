package ru.nstu.lab02v2.Add;

import javafx.application.Platform;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.layout.Pane;
import ru.nstu.lab02v2.Entities.MotoJaba;

import java.util.*;

public class ToadSpawner {
    private ArrayList<MotoJaba> motos;//массив всех мотоциклов

    /*массив типов мотоциклов(автоматически расширяемый)
    каждому типу - ячейка массива, в которой лежит кол-во объектов этого типа*/
    private int[] motoTypeCount = new int[MotoJaba.getMotoImages().length];

    private Pane pane;//поле спавна

    public SimpleStringProperty millisProperty = new SimpleStringProperty("0");//переменная необходимая для таймера

    private long millis = 0;//время симуляции в миллисекундах
    private int motoSpawnPeriod = 5000, carSpawnperiod = 100;//периоды спавна в миллисекундах
    private int motoSpawnChance = 100, carSpawnChance = 20;//шанс спавна в процентах
    private final static Random rand = new Random();//переменная рандома

    private Boolean paused = false;//переменная, отслеживающая поставлена ли симуляция на паузу
    private Boolean started = false;//переменная, отслеживающая запущена ли симуляция

    private Timer timer = new Timer("AllTheTimer");//создание пустого таймера

    private TimerTask spawnMoto;//объявление задания спавна мотожаб
    private TimerTask countMillis;//объявление задания счёта миллисекунд

    //"сохранённое" время с последнего запуска задания у таймера: 0 - для мото, 1 - машино - жаб
    long saveTime[] = new long[2];

    public ToadSpawner(Pane pane){
        motos = new ArrayList<>();
        this.pane = pane;
    }// конструктор

    //запуск симуляции
    public void start(){
        started = true;
        paused = false;
        clear();//очистка поля
        millis = 0;//обновление таймера
        setTasks();//задание всех тасков
        timer.scheduleAtFixedRate(countMillis, 0, 1);//запуск таймера
        timer.scheduleAtFixedRate(spawnMoto, motoSpawnPeriod, motoSpawnPeriod);
    }
    //конец симуляции
    public void end(){
        started = false;
        paused = false;
        countMillis.cancel();//убийство тасков
        spawnMoto.cancel();
        timer.purge();//сгорание/очистка таймера
    }
    //остановка на "паузу"
    public synchronized void pause() {
        if(!paused) {//если не на паузе
            saveTime[0] = (System.currentTimeMillis() - spawnMoto.scheduledExecutionTime());//сохранить время последнего запуска
            System.out.println(saveTime[0]);//отладка
            spawnMoto.cancel();//убийство заданий
            countMillis.cancel();
            timer.purge();//сгорание таймера
        }
        paused = true;
    }
    //снятие с "паузы"
    public synchronized void unpause(){
        if(started && paused){
            setTasks();//задача тасков
            timer.scheduleAtFixedRate(countMillis, 1, 1);//включение таймеров
            //таймер включается с задержкой, равной недостающему времени выполнения для предыдущего задания
            //т.е.
            timer.scheduleAtFixedRate(spawnMoto, motoSpawnPeriod - saveTime[0], motoSpawnPeriod);
        }
      paused = false;
    }

    //задание всех классических тасков
    private void setTasks(){
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
                    if(rand.nextInt(100) <= motoSpawnChance){
                        motos.add(new MotoJaba(pane));
                        motoTypeCount[motos.getLast().type]++;
                    }
                });
            }
        };
    }
    //очистка поля
    public void clear(){
        for(MotoJaba moto : motos){
            moto.die(pane);
        }
        motos.clear();
    }

    /*функции геттеры и сеттеры*/
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
    public Boolean isStarted(){
        return started;
    }

    public void setPane(Pane pane) {
        this.pane = pane;
    }

}
