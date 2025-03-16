package ru.nstu.lab02v2;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import ru.nstu.lab02v2.Add.ToadSpawner;
import ru.nstu.lab02v2.module.Module;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML public SplitPane          turboBox;//общая коробка
    @FXML public ComboBox<Integer>  carSpawnChanceComboBox;
    @FXML public ComboBox<Integer>  motoSpawnChanceComboBox;
    @FXML public TextArea           carscfg;//период спавна жабамашин
    @FXML public Button             endButton;
    @FXML public MenuItem           endMenu;//кнопка конца симуляции (меню)
    @FXML public MenuItem           exitMenu;//кнопка выхода в меню
    @FXML public AnchorPane         field;//поле спавна жаб
    @FXML public MenuItem           helpMenuButton;//
    @FXML public RadioButton        hideTimerRB;//скрыть таймер
    @FXML public RadioButton        showTimerRB;//показать таймер
    @FXML public RadioMenuItem      showTimerRBMenu;//показать таймер (меню)
    @FXML public RadioMenuItem      hideTimerRBMenu;//скрыть таймер (меню)
    @FXML public CheckMenuItem      infoMenu;//показать сводку
    @FXML public AnchorPane         interfacePane;//коробка меню
    @FXML public MenuBar            mainMenu;//меню
    @FXML public TextArea           motoscfg;//период спавна жабамотоциклов
    @FXML public Button             startButton;//кнопка начала симуляции
    @FXML public MenuItem           startMenu;//кнопка начала симуляции (меню)
    @FXML public Label              timerLabel;//текстовое поле для времени
    @FXML public ToggleGroup        timerShow;//группа радио-кнопок

    ToadSpawner toadSpawner = new ToadSpawner(field);
    Module module;
    @FXML void endSim(ActionEvent event) throws IOException {//конец симуляции
        if(infoMenu.isSelected()){
            toadSpawner.pause();
            moduleRun();

        }else{
            startEnable();
            toadSpawner.end();
        }
    }
    @FXML void exit(ActionEvent event) {//выход из приложения через меню
        Platform.exit();
    }
    @FXML void startSim(ActionEvent event) throws IOException {//старт симуляции
        endEnable();
        boolean passed = true;

        toadSpawner.setCarSpawnChance(carSpawnChanceComboBox.getSelectionModel().getSelectedItem());//задание значений из комбо0боксов
        toadSpawner.setMotoSpawnChance(motoSpawnChanceComboBox.getSelectionModel().getSelectedItem());

        if(!motoscfg.getText().isEmpty())//если поле не пустое
            if(motoscfg.getText().matches("\\d+"))//и содержит только цифры
                if(Integer.parseInt(carscfg.getText()) > 0)//и это число больше нуля
                    toadSpawner.setMotoSpawnPeriod(Integer.parseInt(motoscfg.getText()));//то присваиваем периоду число из поля
                else
                    toadSpawner.setMotoSpawnPeriod(Integer.MAX_VALUE);//иначе установить огромный период (ничего не появляется)
            else
                passed = false;

        if(!carscfg.getText().isEmpty())//если поле не пустое
            if(carscfg.getText().matches("\\d+"))//и содержит только цифры
                if(Integer.parseInt(carscfg.getText()) > 0)//и это число больше нуля
                    toadSpawner.setCarSpawnPeriod(Integer.parseInt(carscfg.getText()));//то присваиваем периоду число из поля
                else
                    toadSpawner.setCarSpawnPeriod(Integer.MAX_VALUE);//иначе установить огромный период (ничего не появляется)
            else
                passed = false;

        if(passed)
            toadSpawner.start();//если поля прошли проверку, то запускаем симуляцию, иначе - окно об ошибке
        else{
            moduleRun();
            module.setInfo("Number isn't integer or it's too big");
            module.cancelButtonDisable();
        }
    }
    //вызов модульного окна
    public void moduleRun() throws IOException {
        module = new Module();
        module.setToadSpawner(toadSpawner);
        module.setMainController(this);
        module.start((Stage) timerLabel.getScene().getWindow());
    }
    //включает кнопку старт
    public void startEnable(){
        startButton.setDisable(false);
        startMenu.setDisable(false);
        endButton.setDisable(true);
        endMenu.setDisable(true);
        startButton.requestFocus();
    }
    //выключает кнопку старт
    public void endEnable(){
        startButton.setDisable(true);
        startMenu.setDisable(true);
        endMenu.setDisable(false);
        endButton.setDisable(false);
        endButton.requestFocus();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        toadSpawner.setPane(field);//задаём спавнеру поле, куда будут сыпаться жабы
        //биндим таймер
        timerLabel.textProperty().bind(Bindings.convert(toadSpawner.millisProperty));

        for(int i = 0; i <= 100; i+=10){//задаём значения комбо-боксам
            carSpawnChanceComboBox.getItems().add(i);
            motoSpawnChanceComboBox.getItems().add(i);
        }
        //начальный выбор комбо-боксов
        carSpawnChanceComboBox.getSelectionModel().select( (Integer) toadSpawner.getCarSpawnChance());
        motoSpawnChanceComboBox.getSelectionModel().select( (Integer) toadSpawner.getMotoSpawnChance());

        carscfg.setPromptText(String.valueOf(toadSpawner.getCarSpawnPeriod()));
        motoscfg.setPromptText(String.valueOf(toadSpawner.getMotoSpawnPeriod()));
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
    }
}