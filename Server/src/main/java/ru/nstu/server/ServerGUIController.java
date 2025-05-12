package ru.nstu.server;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;

public class ServerGUIController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Pane MainPane;

    @FXML
    private ListView<?> clientList;

    @FXML
    private Label portLabel;

    @FXML
    private Button startButton;

    @FXML
    private Button stopButton;

    @FXML
    private void startPressed(ActionEvent event) {

    }

    @FXML
    private void stopPressed(ActionEvent event) {

    }

    @FXML
    void initialize() {
        assert MainPane != null : "fx:id=\"MainPane\" was not injected: check your FXML file 'ServerGUI.fxml'.";
        assert clientList != null : "fx:id=\"clientList\" was not injected: check your FXML file 'ServerGUI.fxml'.";
        assert portLabel != null : "fx:id=\"portLabel\" was not injected: check your FXML file 'ServerGUI.fxml'.";
        assert startButton != null : "fx:id=\"startButton\" was not injected: check your FXML file 'ServerGUI.fxml'.";
        assert stopButton != null : "fx:id=\"stopButton\" was not injected: check your FXML file 'ServerGUI.fxml'.";

    }

}
