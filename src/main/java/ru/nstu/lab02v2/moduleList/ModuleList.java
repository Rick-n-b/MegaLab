package ru.nstu.lab02v2.moduleList;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.nstu.lab02v2.Add.ToadSpawner;
import ru.nstu.lab02v2.Main;
import ru.nstu.lab02v2.MainController;

import java.io.IOException;
import java.util.Map;

public class ModuleList extends Application  {
    private ToadSpawner toadSpawner;
    private MainController mainController;
    ModuleListController moduleListController;
    public void setToadSpawner(ToadSpawner toadSpawner) {
        this.toadSpawner = toadSpawner;
    }
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    private String generateInfo(){
        StringBuilder inf = new StringBuilder("Objects alive: \n");
        for(Map.Entry<Integer,Long> entry: toadSpawner.getTimeSpawn().entrySet()){
            inf.append(entry.getKey() + " " + entry.getValue() + "\n");
        }
        return inf.toString();
        }





    public void start(Stage ownerStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("ModuleList.fxml"));
        Parent loader = fxmlLoader.load();
        moduleListController = fxmlLoader.getController();
        moduleListController.setInfo(generateInfo());
        moduleListController.setToadSpawner(toadSpawner);
        moduleListController.setMainController(mainController);
        Scene scene = new Scene(loader);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("List");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(ownerStage);
        stage.setResizable(false);
        stage.show();
    }
}
