package ru.nstu.lab02v2;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
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

    @FXML public SplitPane          turboBox;
    @FXML public ComboBox<Integer>  carSpawnChanceComboBox;
    @FXML public ComboBox<Integer>  motoSpawnChanceComboBox;
    @FXML public TextArea           carscfg;
    @FXML public Button             endButton;
    @FXML public MenuItem           endMenu;
    @FXML public MenuItem           exitMenu;
    @FXML public AnchorPane         field;
    @FXML public MenuItem           helpMenuButton;
    @FXML public RadioButton        hideTimerRB;
    @FXML public RadioMenuItem      hideTimerRBMenu;
    @FXML public CheckMenuItem      infoMenu;
    @FXML public AnchorPane         interfacePane;
    @FXML public MenuBar            mainMenu;
    @FXML public TextArea           motoscfg;
    @FXML public RadioButton        showTimerRB;
    @FXML public RadioMenuItem      showTimerRBMenu;
    @FXML public Button             startButton;
    @FXML public MenuItem           startMenu;
    @FXML public Label              timerLabel;
    @FXML public ToggleGroup        timerShow;

    ToadSpawner toadSpawner = new ToadSpawner(field);

    Boolean show = false;
    @FXML
    void clickShow(ActionEvent event) {//показывать/не показывать статистику в конце симуляции
        show = infoMenu.isSelected();
    }
    @FXML
    void endSim(ActionEvent event) throws IOException, InterruptedException {//конец симуляции
        if(show){
            //toadSpawner.pause();
            Module module = new Module();
            module.setToadSpawner(toadSpawner);
            module.setMainController(this);
            module.start((Stage) timerLabel.getScene().getWindow());

        }else{
            startEnable();
            toadSpawner.end();
        }
    }
    @FXML
    void exit(ActionEvent event) {//выход из приложения через меню
        Platform.exit();
    }
    @FXML
    void startSim(ActionEvent event) {//старт симуляции
        endEnable();
        Platform.runLater(runLater);
    }

    Runnable runLater = new Runnable() {
        @Override
        public void run() {
            toadSpawner.start();
        }
    };

    public void startEnable(){//включет кнопку старт
        startButton.setDisable(false);
        startMenu.setDisable(false);
        endButton.setDisable(true);
        endMenu.setDisable(true);
        startButton.requestFocus();
    }
    public void endEnable(){//выключает кнопку старт
        startButton.setDisable(true);
        startMenu.setDisable(true);
        endMenu.setDisable(false);
        endButton.setDisable(false);
        endButton.requestFocus();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        timerLabel.setText("time");
        toadSpawner.setPane(field);
        timerLabel.textProperty().bind(Bindings.convert(toadSpawner.millisProperty));
        for(int i = 0; i <= 100; i+=10){
            carSpawnChanceComboBox.getItems().add(i);
            motoSpawnChanceComboBox.getItems().add(i);
        }
        carSpawnChanceComboBox.getSelectionModel().select(1);
        motoSpawnChanceComboBox.getSelectionModel().select(2);

        endButton.setDisable(true);
        endMenu.setDisable(true);

        endButton.disableProperty().bindBidirectional(endMenu.disableProperty());
        startButton.disableProperty().bindBidirectional(startMenu.disableProperty());
        //

        hideTimerRB.selectedProperty().bindBidirectional(hideTimerRBMenu.selectedProperty());
        showTimerRB.selectedProperty().bindBidirectional(showTimerRBMenu.selectedProperty());

        timerLabel.visibleProperty().bind(showTimerRB.selectedProperty());

    }
}