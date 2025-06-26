package ru.nstu.labs;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import ru.nstu.labs.Add.ToadSpawner;
import ru.nstu.labs.modules.ClientMenuController;
import ru.nstu.labs.modules.TradeOfferController;
import ru.nstu.labs.modules.TradesController;
import ru.nstu.labs.net.Client;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MW.fxml"));
        Parent loader = fxmlLoader.load();
        fxmlLoader.setRoot(new SplitPane());
        MainController mainController = fxmlLoader.getController();
        Scene scene = new Scene(loader);
        stage.setScene(scene);
        stage.setTitle("Swamp sim");
        stage.setMinWidth(loader.minWidth(-1));
        stage.setMinHeight(loader.minHeight(-1));
        stage.show();
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                ToadSpawner toadSpawner = ToadSpawner.getInstance(mainController.field);
                toadSpawner.saveConf(Objects.requireNonNull(Main.class.getResource("conf.cfg")).getPath().substring(1));
                Platform.exit();
                System.exit(0);
            }
        });

        Client client = new Client();

        FXMLLoader fxmlTradesLoader = new FXMLLoader(getClass().getResource("TW.fxml"));
        var tradesScene = new Scene(fxmlTradesLoader.load(), 600, 400);
        var tradesController = (TradesController)fxmlTradesLoader.getController();

        FXMLLoader fxmlTradeOfferLoader = new FXMLLoader(getClass().getResource("TOW.fxml"));
        var tradeOfferScene = new Scene(fxmlTradeOfferLoader.load(), 600, 400);
        var tradeOfferController = (TradeOfferController)fxmlTradeOfferLoader.getController();

        FXMLLoader fxmlConnectLoader = new FXMLLoader(getClass().getResource("CCW.fxml"));
        var connectScene = new Scene(fxmlConnectLoader.load(), 300, 125);
        var connectController = (ClientMenuController)fxmlConnectLoader.getController();

        tradesController.init(new Stage(), tradesScene, client);

        tradeOfferController.init(new Stage(), tradeOfferScene, client);

        connectController.init(new Stage(), connectScene, client);

        mainController.setNetControllers(connectController, tradesController, tradeOfferController);

    }

    public static void main(String[] args) {
        launch();
    }
}