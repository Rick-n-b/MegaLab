package ru.nstu.lab02v2;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
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
import ru.nstu.lab02v2.Add.ToadSpawner;

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

    ToadSpawner toadSpawner;

    @FXML
    void clickShow(ActionEvent event) {

    }
    @FXML
    void endSim(ActionEvent event) {

    }
    @FXML
    void exit(ActionEvent event) {

    }
    @FXML
    void startSim(ActionEvent event) {

    }

    Runnable runLater = new Runnable() {
        @Override
        public void run() {
            toadSpawner = new ToadSpawner(field);
            //timerLabel.textProperty().bind(Bindings.convert(toadSpawner.getMillisProperty()));
            toadSpawner.start();
        }
    };

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        for(int i = 0; i <= 100; i+=10){
            carSpawnChanceComboBox.getItems().add(i);
            motoSpawnChanceComboBox.getItems().add(i);
        }
        carSpawnChanceComboBox.getSelectionModel().select(1);
        motoSpawnChanceComboBox.getSelectionModel().select(2);
        //
        //Platform.runLater(runLater);
    }
}