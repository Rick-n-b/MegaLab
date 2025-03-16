package ru.nstu.lab02v2.module;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.nstu.lab02v2.Main;
import ru.nstu.lab02v2.MainController;
import ru.nstu.lab02v2.Add.ToadSpawner;

import java.io.IOException;
import java.text.DecimalFormat;

public class Module extends Application {

    private ToadSpawner toadSpawner;
    private MainController mainController;
    public void setToadSpawner(ToadSpawner toadSpawner) {
        this.toadSpawner = toadSpawner;
    }
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    private String generateInfo(){
        StringBuilder inf = new StringBuilder("Motos: " + toadSpawner.getMotos().toArray().length + "\n");
        for(int i = 0; i < toadSpawner.getMotoTypeCount().length; i++){
            inf.append("Type").append(i + 1).append(": ").append(toadSpawner.getMotoTypeCount()[i]).append("\n");
        }

        inf.append("Cars: " + toadSpawner.getCars().toArray().length + "\n");
        for(int i = 0; i < toadSpawner.getCarTypeCount().length; i++){
            inf.append("Type").append(i + 1).append(": ").append(toadSpawner.getCarTypeCount()[i]).append("\n");
        }

        String time = String.format("Time: %02d:%02d:%03d", toadSpawner.getMillis() / 60000, toadSpawner.getMillis() / 1000, toadSpawner.getMillis() % 1000);

        inf.append(time);
        return inf.toString();
    }

    public void start(Stage ownerStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Module.fxml"));
        Parent loader = fxmlLoader.load();
        ModuleController moduleController = fxmlLoader.getController();
        moduleController.setInfo(generateInfo());
        moduleController.setToadSpawner(toadSpawner);
        moduleController.setMainController(mainController);
        Scene scene = new Scene(loader);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Info");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(ownerStage);
        stage.setResizable(false);
        stage.show();
    }

}
