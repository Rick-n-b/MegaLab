package ru.nstu.lab02v2;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.stage.Stage;
import ru.nstu.lab02v2.Add.ToadSpawner;

import java.io.IOException;

public class Main extends Application {
    ToadSpawner toadSpawner;
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("MW.fxml"));
        Parent root = fxmlLoader.load();
        MainController mainController = fxmlLoader.getController();

        Scene scene = new Scene(root);
        stage.setTitle("Swamp sim");
        stage.setScene(scene);
        stage.setMinWidth(root.minWidth(-1));
        stage.setMinHeight(root.minHeight(-1));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}