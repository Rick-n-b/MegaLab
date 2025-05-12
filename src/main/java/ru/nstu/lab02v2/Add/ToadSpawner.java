package ru.nstu.lab02v2.Add;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.layout.Pane;
import ru.nstu.lab02v2.AI.CarAI;
import ru.nstu.lab02v2.AI.MotoAI;
import ru.nstu.lab02v2.Entities.CarJaba;
import ru.nstu.lab02v2.Entities.MotoJaba;
import ru.nstu.lab02v2.MainController;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ToadSpawner implements Serializable {
    private static final long serialVersionUID = 1L;

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

    @JsonIgnore
    private transient Pane pane;//поле спавна

    private long millis = 0;//время симуляции в миллисекундах

    //"сохранённое" время с последнего запуска задания у таймера: 0 - для мото, 1 - машино - жаб
    private Long[] saveTime = new Long[4];

    @JsonIgnore
    private transient MotoAI motoAI;
    @JsonIgnore
    private transient CarAI carAI;

    @JsonIgnore private int motoSpawnPeriod = 1000;
    @JsonIgnore private int carSpawnPeriod = 2000;//периоды спавна в миллисекундах
    @JsonIgnore private int motoSpawnChance = 70;
    @JsonIgnore private int carSpawnChance = 40;//шанс спавна в процентах
    @JsonIgnore private int motoLife = 2000;
    @JsonIgnore private int carLife = 4000; //время жизни в миллисекундах

    @JsonIgnore
    public transient SimpleStringProperty millisProperty = new SimpleStringProperty(String.valueOf(millis));//переменная необходимая для таймера

    @JsonIgnore
    private transient final static Random rand = new Random();//переменная рандома
    @JsonIgnore
    private transient Boolean paused = false;//переменная, отслеживающая поставлена ли симуляция на паузу
    @JsonIgnore
    private transient Boolean started = false;//переменная, отслеживающая запущена ли симуляция

    @JsonIgnore
    private transient Timer timer;//создание пустого таймера

    @JsonIgnore private transient TimerTask countMillis;//объявление задания счёта миллисекунд
    @JsonIgnore private transient TimerTask spawnMoto;//объявление задания спавна мотожаб
    @JsonIgnore private transient TimerTask spawnCar;
    @JsonIgnore private transient TimerTask clearMoto; //очистка лишних объектов
    @JsonIgnore private transient TimerTask clearCar;

    @JsonIgnore
    public transient MainController mainController;

    // конструктор

    public static ToadSpawner getInstance(Pane pane) {
        if(instance == null){
            instance = new ToadSpawner(pane);
            instance.carAI = new CarAI();
            instance.motoAI = new MotoAI();
        }
        return instance;
    }
    @JsonCreator
    public static ToadSpawner getInstance() {
        if(instance == null){
            instance = new ToadSpawner(null);
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
        timer = new Timer("AllTheTimer");

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
            for(Long i = 0L; i < saveTime.length; i++){
                saveTime[Math.toIntExact(i)] = 0L;
            }
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
            saveTime[0] = (System.currentTimeMillis() - spawnMoto.scheduledExecutionTime());//сохранить время последнего запуска
            saveTime[1] = (System.currentTimeMillis() - spawnCar.scheduledExecutionTime());
            saveTime[2] = (System.currentTimeMillis() - clearMoto.scheduledExecutionTime());
            saveTime[3] = (System.currentTimeMillis() - clearCar.scheduledExecutionTime());
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

            if(motoSpawnPeriod < motoLife)
                timer.scheduleAtFixedRate(clearMoto, motoSpawnPeriod - saveTime[2],motoSpawnPeriod+1); //установка минимальных значений
            else
                timer.scheduleAtFixedRate(clearMoto, motoLife - saveTime[2], motoLife+1);

            if(carSpawnPeriod < carLife)
                timer.scheduleAtFixedRate(clearCar, carSpawnPeriod - saveTime[3], carSpawnPeriod+1);
            else
                timer.scheduleAtFixedRate(clearCar,carLife - saveTime[3], carLife+1);

            if(mainController.motoAIRB.selectedProperty().getValue())
                motoAI.setDisabled(false);
            if(mainController.carAIRB.selectedProperty().getValue())
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
        try {
            if(new File(path).exists()) {
                Properties properties = new Properties();
                properties.setProperty("motoSpawnPeriod", String.valueOf(motoSpawnPeriod));
                properties.setProperty("carSpawnPeriod", String.valueOf(carSpawnPeriod));
                properties.setProperty("motoSpawnChance", String.valueOf(motoSpawnChance));
                properties.setProperty("carSpawnChance", String.valueOf(carSpawnChance));
                properties.setProperty("motoLife", String.valueOf(motoLife));
                properties.setProperty("carLife", String.valueOf(carLife));
                properties.setProperty("motoPrio", String.valueOf(motoAI.getPrio()));
                properties.setProperty("carPrio", String.valueOf(carAI.getPrio()));
                properties.store(Files.newOutputStream(Path.of(path)), "ToadSpawner config file");
            }else{
                new File(path).createNewFile();
                saveSim(path);
                //System.out.println("Can not save cfg file");
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    //переделать на java сериализацию
    public synchronized void loadConf(String path){
        Properties properties = new Properties();
        try {
            if(new File(path).exists()) {
                properties.load(Files.newInputStream(Path.of(path)));
                motoSpawnPeriod = 0 >= Integer.parseInt(properties.getProperty("motoSpawnPeriod", String.valueOf(1000))) ? 1000: Integer.parseInt(properties.getProperty("motoSpawnPeriod", String.valueOf(1000)));
                carSpawnPeriod = 0 >= Integer.parseInt(properties.getProperty("carSpawnPeriod", String.valueOf(2000))) ? 2000 : Integer.parseInt(properties.getProperty("carSpawnPeriod", String.valueOf(2000)));
                motoSpawnChance = Integer.parseInt(properties.getProperty("motoSpawnChance", String.valueOf(70)));
                carSpawnChance = Integer.parseInt(properties.getProperty("carSpawnChance", String.valueOf(40)));
                motoLife = 0 >= Integer.parseInt(properties.getProperty("motoLife", String.valueOf(2000))) ? 2000 : Integer.parseInt(properties.getProperty("motoLife", String.valueOf(2000)));
                carLife = 0 >= Integer.parseInt(properties.getProperty("carLife", String.valueOf(4000))) ? 4000 : Integer.parseInt(properties.getProperty("carLife", String.valueOf(4000)));
                motoAI.setPrio(Integer.parseInt(properties.getProperty("motoPrio", String.valueOf(5))));
                carAI.setPrio(Integer.parseInt(properties.getProperty("carPrio", String.valueOf(5))));
            }else{
                new File(path).createNewFile();
                loadConf(path);
                //System.out.println("Can not load cfg file");
            }
        } catch (IOException e) {
            System.err.println(": " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public synchronized void saveSim(String path){
        try
        {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path));
            out.writeObject(this);
            System.out.println("Данные симуляции сохранены в " + path);
        }
        catch(Exception e){
            System.err.println("Ошибка при сохранении dat данных симуляции: " + e.getMessage());
        }
    }

    public synchronized ToadSpawner loadSim(String path, Pane pane){
        try
        {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(path));
            clear();
            instance = (ToadSpawner) in.readObject(); // Десериализуем из файла
            instance.motoAI = new MotoAI();
            instance.carAI = new CarAI();
            instance.paused = true;
            instance.started = true;
            instance.pane = pane;
            instance.timer = new Timer("All the timer!");
            instance.millisProperty = new SimpleStringProperty(String.valueOf(millis));
            System.out.println(saveTime[0]);
            System.out.println(saveTime[1]);
            System.out.println(saveTime[2]);
            System.out.println(saveTime[3]);
            setPane(pane);
            System.out.println("Данные симуляции загружены из " + path);
        }
        catch(Exception e){
            System.err.println("Ошибка при загрузке dat данных симуляции: " + e.getMessage());
        }
        return instance;
    }

    public synchronized void saveJsonSim(String path){
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT); // Создаем экземпляр ObjectMapper

        try {
            mapper.writeValue(new File(path), this); // Сериализуем объект в JSON файл
            System.out.println("Данные симуляции сохранены в " + path);

        } catch (IOException e) {
            System.err.println("Ошибка при сохранении JSON данных симуляции: " + e.getMessage());
        }
    }

    public synchronized void loadJsonSim(String path, Pane pane){
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        try {
            clear();
            instance = mapper.readValue(new File(path), ToadSpawner.class); // Десериализуем из JSON файла
            instance.paused = true;
            instance.started = true;
            setPane(pane);
            System.out.println("Данные симуляции загружены из " + path);
        } catch (IOException e) {
            System.err.println("Ошибка при загрузке JSON данных симуляции: " + e.getMessage());
        }
    }

    /*геттеры и сеттеры*/
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
    public Long[] getSaveTime(){
        return saveTime;
    }

    public void setSaveTime(Long saveTime[]){
        this.saveTime = saveTime;
    }
    public void setMotoMovePrio(int p) {
        motoAI.setPrio(p);
    }
    public void setCarMovePrio(int p){
        carAI.setPrio(p);
    }

    public void setPane(Pane pane) {
        this.pane = pane;

        for(MotoJaba motoJaba : motos){
            motoJaba.spawn(pane);
           System.out.println(motoJaba.x);
        }

        for(CarJaba carJaba : cars){
            carJaba.spawn(pane);
        }

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
