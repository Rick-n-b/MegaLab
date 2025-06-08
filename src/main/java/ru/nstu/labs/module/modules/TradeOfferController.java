package ru.nstu.labs.module.modules;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.stage.Stage;
import ru.nstu.labs.Add.ToadSpawner;
import ru.nstu.labs.net.Client;
import ru.nstu.network.NetClient;
import ru.nstu.trades.TradeInners;
import ru.nstu.trades.Transaction;

import java.util.Objects;

public class TradeOfferController {
    @FXML
    private ListView<NetClient> others;
    @FXML
    private Button offerButton;
    @FXML
    private Button closeButton;

    private Stage stage;
    private Client client;
    private ToadSpawner toadSpawner;

    public void init(Stage stage, Scene scene, Client client) {
        this.toadSpawner = ToadSpawner.getInstance();
        this.stage = stage;
        others.setCellFactory(this::createClientsCell);
        others.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        others.getSelectionModel().selectedItemProperty().addListener(e -> validateItem());
        offerButton.setOnAction(e -> offerTrade("objects"));
        closeButton.setOnAction(e -> closeWindow());
        this.stage.setTitle("Trade offer");
        this.stage.setScene(scene);
        this.stage.setResizable(false);
        this.client = client;
    }

    private void validateItem() {
        if(others.getSelectionModel().getSelectedItems().isEmpty()){
            offerButton.setDisable(true);
        }
        else {
            offerButton.setDisable(false);
        }
    }

    private ListCell<NetClient> createClientsCell(ListView<NetClient> list) {
        return new ListCell<>(){
            @Override
            protected void updateItem(NetClient item, boolean empty) {
                super.updateItem(item, empty);

                if(empty || item == null){
                    setText(null);
                }
                else{
                    setText("Client " + item.id + " (" + item.name + ")");
                }
            }
        };
    }

    private void offerTrade(String offered) {
        offerButton.setDisable(true);
        var target = others.getSelectionModel().getSelectedItems().getFirst();
        System.out.println(target.id);
        if(target == null)
            return;
        var inners = new TradeInners(createTransaction(offered));

        client.offerTrade(target.id, inners);
        closeWindow();
        offerButton.setDisable(false);
    }

    private Transaction createTransaction(String type) {
        if(Objects.equals(type, "objects"))
            return new Transaction(type, toadSpawner.getMotos().size() + toadSpawner.getCars().size());
        return new Transaction(type, Transaction.UNKNOWN_COUNT);
    }

    private void updateClients() {
        others.getItems().clear();
        others.getItems().addAll(client.getOther());
    }

    public void showWindow() {
        offerButton.setDisable(true);
        updateClients();
        stage.show();
    }

    private void closeWindow() {
        stage.hide();
    }
}
