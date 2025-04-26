package ru.nstu.lab02v2;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import ru.nstu.lab02v2.Add.ToadSpawner;
import ru.nstu.lab02v2.module.Module;
import ru.nstu.lab02v2.moduleList.ModuleList;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    public SplitPane turboBox;//общая коробка
    @FXML
    public ComboBox<Integer> carSpawnChanceComboBox;
    @FXML
    public ComboBox<Integer> motoSpawnChanceComboBox;
    @FXML
    public TextArea carscfg;//период спавна жабамашин
    @FXML
    public Button endButton;
    @FXML
    public MenuItem endMenu;//кнопка конца симуляции (меню)
    @FXML
    public MenuItem exitMenu;//кнопка выхода в меню
    @FXML
    public AnchorPane field;//поле спавна жаб
    @FXML
    public MenuItem helpMenuButton;//
    @FXML
    public RadioButton hideTimerRB;//скрыть таймер
    @FXML
    public RadioButton showTimerRB;//показать таймер
    @FXML
    public RadioMenuItem showTimerRBMenu;//показать таймер (меню)
    @FXML
    public RadioMenuItem hideTimerRBMenu;//скрыть таймер (меню)
    @FXML
    public CheckMenuItem infoMenu;//показать сводку
    @FXML
    public AnchorPane interfacePane;//коробка меню
    @FXML
    public MenuBar mainMenu;//меню
    @FXML
    public TextArea motoscfg;//период спавна жабамотоциклов
    @FXML
    public Button startButton;//кнопка начала симуляции
    @FXML
    public MenuItem startMenu;//кнопка начала симуляции (меню)
    @FXML
    public Label timerLabel;//текстовое поле для времени
    @FXML
    public ToggleGroup timerShow;//группа радио-кнопок
    @FXML
    public CheckBox info;
    @FXML
    public MenuItem objects; //вызов окна с объектами
    @FXML
    public ComboBox<Integer> motoPrio;
    @FXML
    public ComboBox<Integer> carPrio;
    @FXML
    public RadioButton carAIRB;
    @FXML
    public RadioButton motoAIRB;

    @FXML
    public TextArea motolife; //время жизни мотожаб

    @FXML
    public TextArea carlife; //время жизнии машин

    ToadSpawner toadSpawner;
    Module module;
    ModuleList moduleList;

    @FXML
    void endSim(ActionEvent event) throws IOException {//конец симуляции
        if (infoMenu.isSelected()) {
            toadSpawner.pause();
            moduleRun();
        } else {
            startEnable();
            toadSpawner.end();
        }
    }
    @FXML
    void showList(ActionEvent event) throws IOException{ //окно с живыми объектами
        toadSpawner.pause();
        moduleListRun();
    }

    @FXML
    void exit(ActionEvent event) {//выход из приложения через меню
        toadSpawner.saveConf("C:\\Users\\nic--\\source\\Jaba\\Lab02v2\\src\\main\\resources\\ru\\nstu\\lab02v2\\AppFiles\\conf.cfg");
        Platform.exit();
        System.exit(0);
    }

    @FXML
    void startSim(ActionEvent event) throws IOException {//старт симуляции
        endEnable();
        boolean passed = true;

        toadSpawner.setCarSpawnChance(carSpawnChanceComboBox.getSelectionModel().getSelectedItem());//задание значений из комбобоксов
        toadSpawner.setMotoSpawnChance(motoSpawnChanceComboBox.getSelectionModel().getSelectedItem());

        if (!motoscfg.getText().isEmpty())//если поле не пустое
            if (motoscfg.getText().matches("^[1-9]\\d{0,4}$"))//и содержит только цифры
                if (Integer.parseInt(motoscfg.getText()) > 0)//и это число больше нуля
                    toadSpawner.setMotoSpawnPeriod(Integer.parseInt(motoscfg.getText()));//то присваиваем периоду число из поля
                else
                    toadSpawner.setMotoSpawnPeriod(Integer.MAX_VALUE);//иначе установить огромный период (ничего не появляется)
            else
                passed = false;

        if (!carscfg.getText().isEmpty())//если поле не пустое
            if (carscfg.getText().matches("^[1-9]\\d{0,4}$"))//и содержит только цифры
                if (Integer.parseInt(carscfg.getText()) > 0)//и это число больше нуля
                    toadSpawner.setCarSpawnPeriod(Integer.parseInt(carscfg.getText()));//то присваиваем периоду число из поля
                else
                    toadSpawner.setCarSpawnPeriod(Integer.MAX_VALUE);//иначе установить огромный период (ничего не появляется)
            else
                passed = false;

        if (!carlife.getText().isEmpty())
            if (carlife.getText().matches("^[1-9]\\d{0,4}$"))
                if (Integer.parseInt(carlife.getText()) > 0)
                    toadSpawner.setCarLife(Integer.parseInt(carlife.getText()));
                else
                    toadSpawner.setCarLife(Integer.MAX_VALUE);
            else
                passed = false;

        if (!motolife.getText().isEmpty())//если поле не пустое
            if (motolife.getText().matches("^[1-9]\\d{0,4}$"))//и содержит только цифры
                if (Integer.parseInt(motolife.getText()) > 0)//и это число больше нуля
                    toadSpawner.setMotoLife(Integer.parseInt(motolife.getText()));
                else
                    toadSpawner.setMotoLife(Integer.MAX_VALUE);
            else
                passed = false;

        if (passed) {
            toadSpawner.start();//если поля прошли проверку, то запускаем симуляцию, иначе - окно об ошибке
            toadSpawner.getMotoAI().setDisabled(!motoAIRB.selectedProperty().getValue());
            toadSpawner.getCarAI().setDisabled(!carAIRB.selectedProperty().getValue());
            //toadSpawner.saveConf("C:\\Users\\nic--\\source\\Jaba\\Lab02v2\\src\\main\\resources\\ru\\nstu\\lab02v2\\AppFiles\\conf.cfg");
        }
        else {
            moduleRun();
            module.setInfo("Number isn't integer or it's too big");
            module.cancelButtonDisable();
        }
    }

    @FXML
    public void setMotoPrio(ActionEvent event){
        toadSpawner.setMotoMovePrio(motoPrio.getSelectionModel().getSelectedItem());
    }
    @FXML
    public void setCarPrio(ActionEvent event){
        toadSpawner.setCarMovePrio(carPrio.getSelectionModel().getSelectedItem());
    }
    @FXML
    public void motoAction(ActionEvent event){
        if(toadSpawner.isStarted())
            toadSpawner.getMotoAI().setDisabled(!motoAIRB.selectedProperty().getValue());

    }
    @FXML
    public void carAction(ActionEvent event){
        if(toadSpawner.isStarted())
            toadSpawner.getCarAI().setDisabled(!carAIRB.selectedProperty().getValue());
    }
    //вызов модульного окна
    public void moduleRun() throws IOException {
        module = new Module();
        module.setToadSpawner(toadSpawner);
        module.setMainController(this);
        module.start((Stage) timerLabel.getScene().getWindow());
    }

    //вызов списка объектов
    public void moduleListRun() throws IOException {
        moduleList = new ModuleList();
        moduleList.setToadSpawner(toadSpawner);
        moduleList.setMainController(this);
        moduleList.start((Stage) timerLabel.getScene().getWindow());
    }
    //включает кнопку старт
    public void startEnable() {
        startButton.setDisable(false);
        startMenu.setDisable(false);
        endButton.setDisable(true);
        endMenu.setDisable(true);
        startButton.requestFocus();
    }

    //выключает кнопку старт
    public void endEnable() {
        startButton.setDisable(true);
        startMenu.setDisable(true);
        endMenu.setDisable(false);
        endButton.setDisable(false);
        endButton.requestFocus();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        toadSpawner = ToadSpawner.getInstance(field);
        toadSpawner.setPane(field);//задаём спавнеру поле, куда будут сыпаться жабы
        toadSpawner.loadConf("C:\\Users\\nic--\\source\\Jaba\\Lab02v2\\src\\main\\resources\\ru\\nstu\\lab02v2\\AppFiles\\conf.cfg");
        //биндим таймер
        timerLabel.textProperty().bind(Bindings.convert(toadSpawner.millisProperty));

        for (int i = 0; i <= 100; i += 10) {//задаём значения комбо-боксам
            carSpawnChanceComboBox.getItems().add(i);
            motoSpawnChanceComboBox.getItems().add(i);
            if(i != 0) {
                motoPrio.getItems().add(i / 10);
                carPrio.getItems().add(i / 10);
            }
        }
        //начальный выбор комбо-боксов
        carSpawnChanceComboBox.getSelectionModel().select((Integer) toadSpawner.getCarSpawnChance());
        motoSpawnChanceComboBox.getSelectionModel().select((Integer) toadSpawner.getMotoSpawnChance());

        carscfg.setPromptText(String.valueOf(toadSpawner.getCarSpawnPeriod()));
        motoscfg.setPromptText(String.valueOf(toadSpawner.getMotoSpawnPeriod()));

        motolife.setPromptText(String.valueOf(toadSpawner.getMotoLife()));
        carlife.setPromptText(String.valueOf(toadSpawner.getCarLife()));

        carscfg.setWrapText(true);
        motoscfg.setWrapText(true);
        //кнопки конца симуляции сначала недоступны
        endButton.setDisable(true);
        endMenu.setDisable(true);
        //бинд кнопок начала и конца симуляции (кнопка старт вкл -> кнопка end выкл)
        endButton.disableProperty().bindBidirectional(endMenu.disableProperty());
        startButton.disableProperty().bindBidirectional(startMenu.disableProperty());
        //бинды радио-кнопок
        hideTimerRB.selectedProperty().bindBidirectional(hideTimerRBMenu.selectedProperty());
        showTimerRB.selectedProperty().bindBidirectional(showTimerRBMenu.selectedProperty());
        //зависимость видимости текста от выбора радио-кнопки
        timerLabel.visibleProperty().bind(showTimerRB.selectedProperty());
        infoMenu.selectedProperty().bindBidirectional(info.selectedProperty());


        motoPrio.getSelectionModel().select(toadSpawner.getMotoAI().getPrio() - 1);
        carPrio .getSelectionModel().select(toadSpawner.getCarAI().getPrio() - 1);

        Tooltip ttStart = new Tooltip("press to start");
        startButton.setTooltip(ttStart);
        Tooltip ttStop = new Tooltip("press to stop");
        endButton.setTooltip(ttStop);
        Tooltip ttLabelTime = new Tooltip("enter time interval in ms");
        motoscfg.setTooltip(ttLabelTime);
        carscfg.setTooltip(ttLabelTime);
        Tooltip ttSpawn = new Tooltip("chance of spawn in %");
        motoSpawnChanceComboBox.setTooltip(ttSpawn);
        carSpawnChanceComboBox.setTooltip(ttSpawn);

        try {
            startSim(new ActionEvent());
            endSim(new ActionEvent());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.field.sceneProperty().addListener((observable, oldScene, newScene) -> { //реализация кнопок из lab1
            if (newScene != null) {
                newScene.setOnKeyPressed(new EventHandler<KeyEvent>() {
                    public void handle(KeyEvent keyEvent) {
                        switch (keyEvent.getCode()) {
                            case B:
                                try {
                                    startSim(new ActionEvent());
                                }
                                catch (IOException exception) {}
                                break;
                            case E:
                                try {
                                    endSim(new ActionEvent());
                                }
                                catch (IOException exception) {}
                                break;
                            case T:
                                showTimerRB.setSelected(!showTimerRB.isSelected());
                                hideTimerRB.setSelected(!showTimerRB.isSelected());
                                break;
                        }
                    }
                });
            }
        });
    }
}