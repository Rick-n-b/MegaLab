package ru.nstu.labs.modules.moduleList;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import ru.nstu.labs.MainController;
import ru.nstu.labs.Add.ToadSpawner;

import java.net.URL;
import java.util.ResourceBundle;

public class ModuleListController implements Initializable{
    public Button okButton;
    private ToadSpawner toadSpawner;
    private MainController mainController;
    public void setToadSpawner(ToadSpawner toadSpawner) {
        this.toadSpawner = toadSpawner;
    }
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private TextArea objects;

    @FXML
    private void okPress(){
        //unpause

        if(mainController.startButton.disableProperty().getValue()) {
            toadSpawner.unpause();
            mainController.endEnable();
        }
        ((Stage) objects.getScene().getWindow()).close();
    }

    private String info = "";

    public void setInfo(String info){
        this.info = info;
        objects.setText(info);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {objects.setEditable(false);}
}

