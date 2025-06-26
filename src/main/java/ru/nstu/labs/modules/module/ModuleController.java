package ru.nstu.labs.modules.module;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import ru.nstu.labs.MainController;
import ru.nstu.labs.Add.ToadSpawner;

import java.net.URL;
import java.util.ResourceBundle;

public class ModuleController implements Initializable {

    public Button okButton;
    public Button cancelButton;
    private ToadSpawner toadSpawner;
    private MainController mainController;
    public void setToadSpawner(ToadSpawner toadSpawner) {
        this.toadSpawner = toadSpawner;
    }
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private TextArea infoField;

    @FXML
    private void okPressed(){
        //stop
        toadSpawner.end();
        mainController.startEnable();
        ((Stage) infoField.getScene().getWindow()).close();
    }
    @FXML
    private void cancelPressed(){
        //unpause
        toadSpawner.unpause();
        mainController.endEnable();
        ((Stage) infoField.getScene().getWindow()).close();
    }

    private String info = "";

    public void setInfo(String info){
        this.info = info;
        infoField.setText(info);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        infoField.setDisable(true);
    }
}
