package ru.nstu.labs.modules;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.stage.Stage;
import ru.nstu.labs.Add.ToadSpawner;
import ru.nstu.labs.net.Client;
import ru.nstu.labs.net.ClientTrades.SmolTrade;
import ru.nstu.labs.net.ClientTrades.TradeIn;
import ru.nstu.labs.net.ClientTrades.TradeOut;
import ru.nstu.trades.Transaction;

import java.util.Objects;

public class TradesController {
    @FXML
    private ListView<SmolTrade> tradesList;
    @FXML
    private Button acceptButton;
    @FXML
    private Button rejectButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Button closeButton;

    private Stage stage;
    private Client client;
    private ToadSpawner toadSpawner;

    public void init(Stage stage, Scene scene, Client client) {
        this.stage = stage;
        toadSpawner = ToadSpawner.getInstance();
        tradesList.setCellFactory(this::createTradesCell);
        tradesList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tradesList.getSelectionModel().selectedItemProperty().addListener(this::checkSelectedTrade);
        acceptButton.setOnAction(e -> acceptTrade());
        rejectButton.setOnAction(e -> rejectTrade());
        cancelButton.setOnAction(e -> cancelTrade());
        closeButton.setOnAction(e -> closeWindow());
        this.stage.setTitle("Trades");
        this.stage.setScene(scene);
        this.stage.setResizable(false);
        this.client = client;
        client.tradesUpdated.subscribe(this::updateTrades);
    }

    private void cancelTrade() {
        client.cancelTrade((TradeOut) tradesList.getSelectionModel().getSelectedItems().getFirst());
        closeWindow();
    }

    private void rejectTrade() {
        client.rejectTrade((TradeIn) tradesList.getSelectionModel().getSelectedItems().getFirst());
        closeWindow();
    }

    private void acceptTrade() {
        var trade = (TradeIn) tradesList.getSelectionModel().getSelectedItems().getFirst();
        client.acceptTrade(trade);
        closeWindow();
        var terms = trade.getInners();
        if(Objects.equals(terms.offer().type(), "objects")) {//добавление объектов
            toadSpawner.generate(terms.offer().objectCount());
        }else
            System.out.println("Non objects requests");
        closeWindow();
    }

    private int getCount(String requested) {
        if(Objects.equals(requested, "objects"))
            return toadSpawner.getCars().size() + toadSpawner.getMotos().size();
        return Transaction.UNKNOWN_COUNT;
    }

    private ListCell<SmolTrade> createTradesCell(ListView<SmolTrade> list) {
        return new ListCell<>() {
            @Override
            protected void updateItem(SmolTrade item, boolean empty) {

                super.updateItem(item, empty);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if (empty || item == null) {
                            setText(null);
                        } else if (item instanceof TradeOut outgoing) {
                            setText(outgoing.id + " | Outgoing to "
                                    + client.findNeighbour(outgoing.targetId).getName());
                        } else if (item instanceof TradeIn incoming) {
                            setText(incoming.id + " | Incoming from "
                                    + client.findNeighbour(incoming.senderId).getName());
                        }
                    }
                });


            }
        };
    }

    private void checkSelectedTrade(Observable list) {
        var items = tradesList.getSelectionModel().getSelectedItems();
        if(items.isEmpty())
            return;
        var item = items.getFirst();
        acceptButton.setDisable(true);
        rejectButton.setDisable(true);
        cancelButton.setDisable(true);
        if(item instanceof TradeOut) {
            cancelButton.setDisable(false);
        }
        else if(item instanceof TradeIn) {
            acceptButton.setDisable(false);
            rejectButton.setDisable(false);
        }
    }

    private void updateTrades(Object sender) {
        tradesList.getItems().clear();
        tradesList.getItems().addAll(client.getTrades());
    }

    public void showWindow() {
        acceptButton.setDisable(true);
        rejectButton.setDisable(true);
        cancelButton.setDisable(true);
        stage.show();
    }

    private void closeWindow() {
        stage.hide();
    }
}
