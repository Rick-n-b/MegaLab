package ru.nstu.labs.module.modules;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import ru.nstu.labs.net.Client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ClientMenuController {
    @FXML
    private TextField serverIp;
    @FXML
    private TextField port;
    @FXML
    private TextField userName;


    @FXML
    private Button connectButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Button disconnectButton;

    private Stage stage;
    private Client client;

    private final SimpleBooleanProperty addressAndPortValid = new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty connectLocked = new SimpleBooleanProperty(false);

    public void init(Stage stage, Scene scene, Client client) {
        this.stage = stage;
        serverIp.textProperty().addListener(e -> validateAddress());
        port.textProperty().addListener(e -> validateAddress());
        userName.textProperty().addListener(e -> validateAddress());
        connectButton.disableProperty().bind(Bindings.not(addressAndPortValid).or(connectLocked));
        connectButton.setOnAction(e -> connect());
        cancelButton.setOnAction(e -> closeWindow());
        disconnectButton.setOnAction(e -> disconnect());
        disconnectButton.disableProperty().bind(Bindings.and(addressAndPortValid, Bindings.not(connectLocked)));

        this.stage.setTitle("Connect menu");
        this.stage.setScene(scene);
        this.stage.setResizable(false);
        this.client = client;
    }

    private void connect() {
        connectLocked.set(true);

        InetAddress address = null;
        try {
            address = InetAddress.getByName(serverIp.getText());
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        var port = Integer.parseInt(this.port.getText());
        try {
            client.connect(address, port, userName.getText());
        } catch (IOException e) {
            connectLocked.set(false);
            throw new RuntimeException(e);
        }
        //closeWindow();
    }

    public void disconnect() {
        client.disconnect();
        connectLocked.set(false);
    }

    private void validateAddress() {
        var addressRegex = "^((25[0-5]|(2[0-4]|1[0-9]|[1-9]|)[0-9])(\\.(?!$)|$)){4}";
        try {
            var port = Integer.parseInt(this.port.getText());
            addressAndPortValid.set((serverIp.getText().matches(addressRegex))
                    && port > 0 && port < 65536 && userName.getText() != null && !userName.getText().isBlank());
        } catch (NumberFormatException e) {
            addressAndPortValid.set(false);
        }
    }

    public void showWindow() {
        stage.show();
    }

    private void closeWindow() {
        stage.hide();
    }
}
